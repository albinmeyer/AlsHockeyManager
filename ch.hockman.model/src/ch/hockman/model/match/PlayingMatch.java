/*
 * This file is part of Al's Hockey Manager
 * Copyright (C) 1998-2012 Albin Meyer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.hockman.model.match;

import java.util.BitSet;

import ch.hockman.model.Options;
import ch.hockman.model.Tactics;
import ch.hockman.model.character.PlayerCharacter;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Health;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.position.Position;
import ch.hockman.model.team.CoachAI;
import ch.hockman.model.team.LineUp;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamState;

/**
 * The representation of a currently played match (game between two teams).
 *
 * @author Albin
 *
 */
public class PlayingMatch {
	
	public static class OnIce {
		// invariant:
		// there are no injured players on ice
		// (but hurt ones can! and injured players can be in lineup!)
		OnIce() {
			homePlayer = new PlayerPtrVector();
			awayPlayer = new PlayerPtrVector();
		}

		public PlayerPtrVector homePlayer;
		public PlayerPtrVector awayPlayer;
	};

	public static class PenaltyBox { // aggregate of PlayingMatch
		PenaltyBox() {
			homePlayer = new PlayerPtrVector();
			awayPlayer = new PlayerPtrVector();
		}

		public PlayerPtrVector homePlayer;
		public PlayerPtrVector awayPlayer;
	};

	public static class Injuries { // aggregate of PlayingMatch
		Injuries() {
			homePlayer = new PlayerPtrVector();
			awayPlayer = new PlayerPtrVector();
		}

		public PlayerPtrVector homePlayer;
		public PlayerPtrVector awayPlayer;
	};

	// Events during a match
	public static final int FINISH = 0;
	public static final int GOAL_HOME = 1;
	public static final int GOAL_AWAY = 2;
	public static final int INTERMISSION = 3;
	public static final int MINOR_PENALTY_HOME = 4;
	public static final int MINOR_PENALTY_AWAY = 5;
	public static final int MAJOR_PENALTY_HOME = 6;
	public static final int MAJOR_PENALTY_AWAY = 7;
	static final int RETURN_FROM_PEN = 8;
	public static final int INJURY = 9;
	public static final int INTERRUPT = 10;

	static public class Event {
		public BitSet bitSet;

		public Event() {
			bitSet = new BitSet(INTERRUPT + 1);
		}
	}

	private Event event;
	private int homeShots;
	private int awayShots;
	private OnIce onIce;
	private PenaltyBox penBox;
	private Injuries injuries;
	private Report report;
	private Team home;
	private Team away;
	int currHomeScore;
	int currAwayScore;
	private boolean advHome;

	PlayingMatch(Report rep, Team home, Team away, boolean advHome) {
		this.report = rep;
		this.home = home;
		this.away = away;
		this.advHome = advHome;
		this.onIce = new OnIce();
		this.penBox = new PenaltyBox();
		this.injuries = new Injuries();
		this.setEvent(new Event());
		currHomeScore = 0;
		currAwayScore = 0;
		setHomeShots(0);
		setAwayShots(0);

		// init the onice with first blocks
		this.onIce.homePlayer.InsertPlayer(home.getLineUp().getG().goal1);
		this.onIce.homePlayer.InsertPlayer(home.getLineUp().getE55().d11);
		this.onIce.homePlayer.InsertPlayer(home.getLineUp().getE55().d12);
		this.onIce.homePlayer.InsertPlayer(home.getLineUp().getE55().lw1);
		this.onIce.homePlayer.InsertPlayer(home.getLineUp().getE55().c1);
		this.onIce.homePlayer.InsertPlayer(home.getLineUp().getE55().rw1);
		this.onIce.awayPlayer.InsertPlayer(away.getLineUp().getG().goal1);
		this.onIce.awayPlayer.InsertPlayer(away.getLineUp().getE55().d11);
		this.onIce.awayPlayer.InsertPlayer(away.getLineUp().getE55().d12);
		this.onIce.awayPlayer.InsertPlayer(away.getLineUp().getE55().lw1);
		this.onIce.awayPlayer.InsertPlayer(away.getLineUp().getE55().c1);
		this.onIce.awayPlayer.InsertPlayer(away.getLineUp().getE55().rw1);
	}

	private int getPlayerStrength(Player player) {
		if (player == null) {
			return 0;
		} else {
			return player.getTotalStrengthWithMot();
		}
	}

	private int getPlayerEnergy(Player player) {
		if (player == null) {
			return 0;
		} else {
			return player.getEnergy();
		}
	}

	public OnIce getOnIce() {
		return this.onIce;
	}

	public PenaltyBox getPenBox() {
		return this.penBox;
	}

	public Injuries getInjuries() {
		return this.injuries;
	}

	public Event getEvent() {
		return this.event;
	}

	void updatePlayers(MatchTime mt) {
		int nofPlayers = onIce.homePlayer.GetNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = onIce.homePlayer.GetPlayer(i);
			player.updateMatchSecond(Player.MatchPlayerEvent.ONICE, 0);
			int ranRange = 49550 + (player.getEnergy() << 3)
					- (player.getHealth().getHurt() << 10);
			if (Util.random(ranRange) == 0) {
				if (player.getPosition().getPosID() != Position.PosID.GOALIE
						|| Util.random(4) == 0) {
					// goalies are 4 times more on ice
					Health health = player.getHealth();
					assert (health.getInjury() == 0); // no injured players on
					// ice
					if (Util.random(2) > 0) {
						// injury
						health.setInjury();
						health.setHurt(0);
						injuries.homePlayer.InsertPlayer(player);
						if (player.isMultipleName()) {
							this.report.setInjuries(this.report
									.getInjuries() + player.getFirstName());
							this.report.setInjuries(this.report
									.getInjuries() + " ");
						}
						this.report.setInjuries(this.report
								.getInjuries() + player.getLastName());
						this.report.setInjuries(this.report
								.getInjuries() + "\n");
						String s = Util.getModelResourceBundle().getString("L_INJURY_MATCH");
						if (player.isMultipleName()) {
							s += player.getFirstName();
							s += " ";
						}
						s += player.getLastName();
						home.getMessages().add(s);
						getEvent().bitSet.set(INJURY);

					} else {
						// condition necessary for lineup injured players (!)
						// hurt
						health.setHurt();
					}
				}
			}
		}
		nofPlayers = onIce.awayPlayer.GetNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = onIce.awayPlayer.GetPlayer(i);
			player.updateMatchSecond(Player.MatchPlayerEvent.ONICE, 0);
			int ranRange = 49550 + (player.getEnergy() << 3)
					- (player.getHealth().getHurt() << 10);
			if (Util.random(ranRange) == 0) {
				if (player.getPosition().getPosID() != Position.PosID.GOALIE
						|| Util.random(3) == 0) {
					// goalies are 3 times more on ice
					Health health = player.getHealth();
					assert (health.getInjury() == 0); // no injured players on
					// ice
					if (Util.random(2) > 0) {
						// injury
						health.setInjury();
						health.setHurt(0);
						injuries.awayPlayer.InsertPlayer(player);
						if (player.isMultipleName()) {
							this.report.setInjuries(this.report
									.getInjuries() + player.getFirstName());
							this.report.setInjuries(this.report
									.getInjuries() + " ");
						}
						this.report.setInjuries(this.report
								.getInjuries() + player.getLastName());
						this.report.setInjuries(this.report
								.getInjuries() + "\n");
						String s = Util.getModelResourceBundle().getString("L_INJURY_MATCH");
						if (player.isMultipleName()) {
							s += player.getFirstName();
							s += " ";
						}
						s += player.getLastName();
						away.getMessages().add(s);
						getEvent().bitSet.set(INJURY);
					} else {
						// condition necessary for lineup injured players (!)
						// hurt
						health.setHurt();
					}
				}
			}
		}
		if (getEvent().bitSet.get(INJURY)) {
			// if new injury, do new put on ice
			putOnIce(mt);
		}

	}

	void doSecond(MatchTime mt, boolean shootout) {
		// calc a match second:
		// event/line changes, penalties, goals, injuries, report updates
		getEvent().bitSet.clear();
		if (home != null && away != null) {
			updatePlayers(mt);
			calcCoaching(mt);
			emptyPenBox(); // return from pen box onto ice
			if (Util.random(60) == 30) {
				// every 60 seconds an interrupt
				getEvent().bitSet.set(INTERRUPT);
				calcNewPen(); // new penalties
				putOnIce(mt); // put players onto ice
			} else if (Util.random(4) == 0) {
				calcGoal(mt, shootout);
			}
		}
		if (mt.getTotalSec() == 1200 || mt.getTotalSec() == 2400
				|| mt.getTotalSec() == 3600) {
			getEvent().bitSet.set(INTERMISSION);
		}
		updateReport(mt); // update match report
	}

	void doSecond(MatchTime mt) {
		doSecond(mt, false);
	}

	void justFinished() {
		// just finished match, but others may still be in overtime/penalty
		getEvent().bitSet.clear();
		getEvent().bitSet.set(FINISH);
	}

	void calcCoaching(MatchTime mt) {
		// called at event
		boolean playoff = false; // NYI
		home.setHome(true);
		CoachAI.Analysis analysis = CoachAI.AnalyzeTeams(home, away, playoff);
		home.getTeamState().doTactics(home, analysis,
				currHomeScore - currAwayScore, mt.getTotalSec());

		away.setHome(false);
		analysis = CoachAI.AnalyzeTeams(away, home, playoff);
		away.getTeamState().doTactics(away, analysis,
				currAwayScore - currHomeScore, mt.getTotalSec());

		// NYI

	}

	void emptyPenBox() {
		assert (home != null && away != null);
		// empty the penalty box
		int i = 0;
		while (i < penBox.homePlayer.GetNofPlayers()) {
			Player player = penBox.homePlayer.GetPlayer(i);
			player.decPenSec();
			if (player.getPenSec() == 0) {
				// player goes from penbox onto ice
				getEvent().bitSet.set(RETURN_FROM_PEN);
				penBox.homePlayer.RemovePlayer(player);
				if (penBox.homePlayer.GetNofPlayers() < 2) {
					// if 2 or more in pen box, still 3 on ice !
					onIce.homePlayer.InsertPlayer(player);
				}
			} else {
				i++;
			}
		}
		i = 0;
		while (i < penBox.awayPlayer.GetNofPlayers()) {
			Player player = penBox.awayPlayer.GetPlayer(i);
			player.decPenSec();
			if (player.getPenSec() == 0) {
				// player goes from penbox onto ice
				getEvent().bitSet.set(RETURN_FROM_PEN);
				penBox.awayPlayer.RemovePlayer(player);
				if (penBox.awayPlayer.GetNofPlayers() < 2) {
					// if 2 or more in pen box, still 3 on ice !
					onIce.awayPlayer.InsertPlayer(player);
				}
			} else {
				i++;
			}
		}

	}

	void calcNewPen() {
		// new penalties
		assert (home != null && away != null);
		// calc new penalties
		// (remove penalties is done at goal calc and at every second)
		Tactics.Effort effort = home.getTactics().getEffort();
		int homeEff;
		if (effort == Tactics.Effort.FULL) {
			homeEff = 0;
		} else if (effort == Tactics.Effort.EASY) {
			homeEff = 2;
		} else {
			assert (effort == Tactics.Effort.NORMAL_EFF);
			homeEff = 1;
		}
		effort = away.getTactics().getEffort();
		int awayEff;
		if (effort == Tactics.Effort.FULL) {
			awayEff = 0;
		} else if (effort == Tactics.Effort.EASY) {
			awayEff = 2;
		} else {
			assert (effort == Tactics.Effort.NORMAL_EFF);
			awayEff = 1;
		}
		if (Util.random(3 + homeEff + awayEff) == 2) {
			boolean home = false;
			boolean away = false;
			// every 5 min a penalty
			if (Util.random(8) == 5) {
				// both teams
				home = true;
				away = true;
			} else if (Util.random(2) > 0) {
				home = true;
			} else {
				away = true;
			}
			if (home && onIce.homePlayer.GetNofPlayers() > 1) {
				// home team
				// if nofplayers<=3, the team has too less players in lineup
				int i = Util.random(onIce.homePlayer.GetNofPlayers() - 1) + 1;
				Player player = onIce.homePlayer.GetPlayer(i);
				Player goalie = null;
				if (Util.random(50) == 5) {
					// goalie gets penalty
					goalie = onIce.homePlayer.GetPlayer(0);
				}
				if (goalie != null
						&& goalie.getCharacter().getCharID() != PlayerCharacter.CharID.AGGRESSOR
						|| goalie == null
						&& player.getCharacter().getCharID() != PlayerCharacter.CharID.AGGRESSOR) {
					// assure Agressors get more penalties than others
					i = Util.random(onIce.homePlayer.GetNofPlayers() - 1) + 1;
					player = onIce.homePlayer.GetPlayer(i);
					goalie = null;
					if (Util.random(50) == 5) {
						// goalie gets penalty
						goalie = onIce.homePlayer.GetPlayer(0);
					}
				}
				penBox.homePlayer.InsertPlayer(player);
				if (away && Util.random(4) == 2) {
					// five minutes
					getEvent().bitSet.set(MAJOR_PENALTY_HOME);
					this.report
							.setPenHome(this.report.getPenHome() + 5);
					player.setPenMin(5);
					if (goalie != null) {
						player = goalie;
					}
					player.incPenalty(5);
				} else {
					// two minutes
					getEvent().bitSet.set(MINOR_PENALTY_HOME);
					this.report
							.setPenHome(this.report.getPenHome() + 2);
					player.setPenMin(2);
					if (goalie != null) {
						player = goalie;
					}
					player.incPenalty(2);
				}
			}
			if (away && onIce.awayPlayer.GetNofPlayers() > 1) {
				// away team
				// if nofplayers<=3, the team has too less players in lineup
				int i = Util.random(onIce.awayPlayer.GetNofPlayers() - 1) + 1;
				Player player = onIce.awayPlayer.GetPlayer(i);
				Player goalie = null;
				if (Util.random(50) == 5) {
					// goalie gets penalty
					goalie = onIce.awayPlayer.GetPlayer(0);
				}
				if (goalie != null
						&& goalie.getCharacter().getCharID() != PlayerCharacter.CharID.AGGRESSOR
						|| goalie == null
						&& player.getCharacter().getCharID() != PlayerCharacter.CharID.AGGRESSOR) {
					// assure Agressors get more penalties than others
					i = Util.random(onIce.awayPlayer.GetNofPlayers() - 1) + 1;
					player = onIce.awayPlayer.GetPlayer(i);
					goalie = null;
					if (Util.random(50) == 5) {
						// goalie gets penalty
						goalie = onIce.awayPlayer.GetPlayer(0);
					}
				}
				penBox.awayPlayer.InsertPlayer(player);
				if (home && Util.random(4) == 2) {
					// five minutes
					getEvent().bitSet.set(MAJOR_PENALTY_AWAY);
					this.report
							.setPenAway(this.report.getPenAway() + 5);
					player.setPenMin(5);
					if (goalie != null) {
						player = goalie;
					}
					player.incPenalty(5);
				} else {
					// two minutes
					getEvent().bitSet.set(MINOR_PENALTY_AWAY);
					this.report
							.setPenAway(this.report.getPenAway() + 2);
					player.setPenMin(2);
					if (goalie != null) {
						player = goalie;
					}
					player.incPenalty(2);
				}
			}
		}
	}

	void putOnIce(MatchTime mt) {
		// put players onto ice
		assert (home != null && away != null);
		LineUp homeLineUp = home.getLineUp();
		LineUp awayLineUp = away.getLineUp();
		int block;
		int strength1 = 0;
		int strength2 = 0;
		int strength3 = 0;
		Tactics.BlockSelection homeBlockSelection = home.getTactics()
				.getBlockSelection();
		Tactics.BlockSelection awayBlockSelection = away.getTactics()
				.getBlockSelection();

		// NYI use homeBlockSelection and awayBlockSelection

		// put players onto ice
		int nofHomeFieldPlayers = 5 - penBox.homePlayer.GetNofPlayers();
		if (nofHomeFieldPlayers < 3) {
			// at least 3 field players
			nofHomeFieldPlayers = 3;
		}
		int nofAwayFieldPlayers = 5 - penBox.awayPlayer.GetNofPlayers();
		if (nofAwayFieldPlayers < 3) {
			// at least 3 field players
			nofAwayFieldPlayers = 3;
		}
		this.onIce.homePlayer.RemoveAllPlayers();
		this.onIce.awayPlayer.RemoveAllPlayers();

		// goalies
		Player goalie = homeLineUp.getG().goal1;
		if (goalie == null || goalie.getHealth().getInjury() > 0) {
			goalie = homeLineUp.getG().goal2;
		}
		this.onIce.homePlayer.InsertPlayer(goalie);
		goalie = awayLineUp.getG().goal1;
		if (goalie == null || goalie.getHealth().getInjury() > 0) {
			goalie = awayLineUp.getG().goal2;
		}
		this.onIce.awayPlayer.InsertPlayer(goalie);

		// even play / powerplay ?
		if (nofHomeFieldPlayers == nofAwayFieldPlayers) {
			// even play
			if (nofHomeFieldPlayers == 5) {
				// away put on ice
				if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
					strength1 = getPlayerStrength(awayLineUp.getE55().d11)
							+ getPlayerStrength(awayLineUp.getE55().d12)
							+ getPlayerStrength(awayLineUp.getE55().lw1)
							+ getPlayerStrength(awayLineUp.getE55().c1)
							+ getPlayerStrength(awayLineUp.getE55().rw1);
					strength2 = getPlayerStrength(awayLineUp.getE55().d21)
							+ getPlayerStrength(awayLineUp.getE55().d22)
							+ getPlayerStrength(awayLineUp.getE55().lw2)
							+ getPlayerStrength(awayLineUp.getE55().c2)
							+ getPlayerStrength(awayLineUp.getE55().rw2);
					strength3 = getPlayerStrength(awayLineUp.getE55().d31)
							+ getPlayerStrength(awayLineUp.getE55().d32)
							+ getPlayerStrength(awayLineUp.getE55().lw3)
							+ getPlayerStrength(awayLineUp.getE55().c3)
							+ getPlayerStrength(awayLineUp.getE55().rw3);
				} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
					strength1 = getPlayerEnergy(awayLineUp.getE55().d11)
							+ getPlayerEnergy(awayLineUp.getE55().d12)
							+ getPlayerEnergy(awayLineUp.getE55().lw1)
							+ getPlayerEnergy(awayLineUp.getE55().c1)
							+ getPlayerEnergy(awayLineUp.getE55().rw1);
					strength2 = getPlayerEnergy(awayLineUp.getE55().d21)
							+ getPlayerEnergy(awayLineUp.getE55().d22)
							+ getPlayerEnergy(awayLineUp.getE55().lw2)
							+ getPlayerEnergy(awayLineUp.getE55().c2)
							+ getPlayerEnergy(awayLineUp.getE55().rw2);
					strength3 = getPlayerEnergy(awayLineUp.getE55().d31)
							+ getPlayerEnergy(awayLineUp.getE55().d32)
							+ getPlayerEnergy(awayLineUp.getE55().lw3)
							+ getPlayerEnergy(awayLineUp.getE55().c3)
							+ getPlayerEnergy(awayLineUp.getE55().rw3);
				} else {
					assert (false); // away team cannot do SAME/AGAINST
				}
				block = Util.random(6); // 0..5
				if (strength1 >= strength2 && strength1 >= strength3) {
					if (strength2 >= strength3) {
						// 1,2,3
						if (block > 2) {
							block = 1;
						} else if (block > 0) {
							block = 2;
						} else {
							block = 3;
						}
					} else {
						// 1,3,2
						if (block > 2) {
							block = 1;
						} else if (block > 0) {
							block = 3;
						} else {
							block = 2;
						}
					}
				} else if (strength2 >= strength3 && strength2 > strength1) {
					if (strength1 >= strength3) {
						// 2,1,3
						if (block > 2) {
							block = 2;
						} else if (block > 0) {
							block = 1;
						} else {
							block = 3;
						}
					} else {
						// 2,3,1
						if (block > 2) {
							block = 2;
						} else if (block > 0) {
							block = 3;
						} else {
							block = 1;
						}
					}
				} else {
					assert (strength3 > strength2 && strength3 > strength1);
					if (strength1 >= strength2) {
						// 3,1,2
						if (block > 2) {
							block = 3;
						} else if (block > 0) {
							block = 1;
						} else {
							block = 2;
						}
					} else {
						// 3,2,1
						if (block > 2) {
							block = 3;
						} else if (block > 0) {
							block = 2;
						} else {
							block = 1;
						}
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (awayLineUp.getE55().d11 == null
								|| awayLineUp.getE55().d12 == null
								|| awayLineUp.getE55().lw1 == null
								|| awayLineUp.getE55().c1 == null || awayLineUp.getE55().rw1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (awayLineUp.getE55().d21 == null
								|| awayLineUp.getE55().d22 == null
								|| awayLineUp.getE55().lw2 == null
								|| awayLineUp.getE55().c2 == null || awayLineUp.getE55().rw2 == null)) {
					block = 3;
				}
				if (block == 3
						&& (awayLineUp.getE55().d31 == null
								|| awayLineUp.getE55().d32 == null
								|| awayLineUp.getE55().lw3 == null
								|| awayLineUp.getE55().c3 == null || awayLineUp.getE55().rw3 == null)) {
					block = 1;
				}
				switch (block) {
				case 1:
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().d11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().d12);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().lw1);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().c1);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().rw1);
					break;
				case 2:
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().d21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().d22);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().lw2);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().c2);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().rw2);
					break;
				case 3:
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().d31);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().d32);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().lw3);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().c3);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE55().rw3);
					break;
				default:
					assert (false);
				}

				// home put on ice
				if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
					if (block == 1) {
						block = 2;
					} else if (block == 2) {
						block = 1;
					}

					// NYI 4th block

				} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
					// do nothing ("block" remains the same)
				} else {
					if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(homeLineUp.getE55().d11)
								+ getPlayerStrength(homeLineUp.getE55().d12)
								+ getPlayerStrength(homeLineUp.getE55().lw1)
								+ getPlayerStrength(homeLineUp.getE55().c1)
								+ getPlayerStrength(homeLineUp.getE55().rw1);
						strength2 = getPlayerStrength(homeLineUp.getE55().d21)
								+ getPlayerStrength(homeLineUp.getE55().d22)
								+ getPlayerStrength(homeLineUp.getE55().lw2)
								+ getPlayerStrength(homeLineUp.getE55().c2)
								+ getPlayerStrength(homeLineUp.getE55().rw2);
						strength3 = getPlayerStrength(homeLineUp.getE55().d31)
								+ getPlayerStrength(homeLineUp.getE55().d32)
								+ getPlayerStrength(homeLineUp.getE55().lw3)
								+ getPlayerStrength(homeLineUp.getE55().c3)
								+ getPlayerStrength(homeLineUp.getE55().rw3);
					} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(homeLineUp.getE55().d11)
								+ getPlayerEnergy(homeLineUp.getE55().d12)
								+ getPlayerEnergy(homeLineUp.getE55().lw1)
								+ getPlayerEnergy(homeLineUp.getE55().c1)
								+ getPlayerEnergy(homeLineUp.getE55().rw1);
						strength2 = getPlayerEnergy(homeLineUp.getE55().d21)
								+ getPlayerEnergy(homeLineUp.getE55().d22)
								+ getPlayerEnergy(homeLineUp.getE55().lw2)
								+ getPlayerEnergy(homeLineUp.getE55().c2)
								+ getPlayerEnergy(homeLineUp.getE55().rw2);
						strength3 = getPlayerEnergy(homeLineUp.getE55().d31)
								+ getPlayerEnergy(homeLineUp.getE55().d32)
								+ getPlayerEnergy(homeLineUp.getE55().lw3)
								+ getPlayerEnergy(homeLineUp.getE55().c3)
								+ getPlayerEnergy(homeLineUp.getE55().rw3);
					}
					block = Util.random(6); // 0..5
					if (strength1 >= strength2 && strength1 >= strength3) {
						if (strength2 >= strength3) {
							// 1,2,3
							if (block > 2) {
								block = 1;
							} else if (block > 0) {
								block = 2;
							} else {
								block = 3;
							}
						} else {
							// 1,3,2
							if (block > 2) {
								block = 1;
							} else if (block > 0) {
								block = 3;
							} else {
								block = 2;
							}
						}
					} else if (strength2 >= strength3 && strength2 > strength1) {
						if (strength1 >= strength3) {
							// 2,1,3
							if (block > 2) {
								block = 2;
							} else if (block > 0) {
								block = 1;
							} else {
								block = 3;
							}
						} else {
							// 2,3,1
							if (block > 2) {
								block = 2;
							} else if (block > 0) {
								block = 3;
							} else {
								block = 1;
							}
						}
					} else {
						assert (strength3 > strength2 && strength3 > strength1);
						if (strength1 >= strength2) {
							// 3,1,2
							if (block > 2) {
								block = 3;
							} else if (block > 0) {
								block = 1;
							} else {
								block = 2;
							}
						} else {
							// 3,2,1
							if (block > 2) {
								block = 3;
							} else if (block > 0) {
								block = 2;
							} else {
								block = 1;
							}
						}
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (homeLineUp.getE55().d11 == null
								|| homeLineUp.getE55().d12 == null
								|| homeLineUp.getE55().lw1 == null
								|| homeLineUp.getE55().c1 == null || homeLineUp.getE55().rw1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (homeLineUp.getE55().d21 == null
								|| homeLineUp.getE55().d22 == null
								|| homeLineUp.getE55().lw2 == null
								|| homeLineUp.getE55().c2 == null || homeLineUp.getE55().rw2 == null)) {
					block = 3;
				}
				if (block == 3
						&& (homeLineUp.getE55().d31 == null
								|| homeLineUp.getE55().d32 == null
								|| homeLineUp.getE55().lw3 == null
								|| homeLineUp.getE55().c3 == null || homeLineUp.getE55().rw3 == null)) {
					block = 1;
				}
				switch (block) {
				case 1:
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().d11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().d12);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().lw1);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().c1);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().rw1);
					break;
				case 2:
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().d21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().d22);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().lw2);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().c2);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().rw2);
					break;
				case 3:
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().d31);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().d32);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().lw3);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().c3);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE55().rw3);
					break;
				default:
					assert (false);
				}
			} else if (nofHomeFieldPlayers == 4) {
				// away put on ice
				if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
					strength1 = getPlayerStrength(awayLineUp.getE44().d11)
							+ getPlayerStrength(awayLineUp.getE44().d12)
							+ getPlayerStrength(awayLineUp.getE44().f11)
							+ getPlayerStrength(awayLineUp.getE44().f12);
					strength2 = getPlayerStrength(awayLineUp.getE44().d21)
							+ getPlayerStrength(awayLineUp.getE44().d22)
							+ getPlayerStrength(awayLineUp.getE44().f21)
							+ getPlayerStrength(awayLineUp.getE44().f22);
				} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
					strength1 = getPlayerEnergy(awayLineUp.getE44().d11)
							+ getPlayerEnergy(awayLineUp.getE44().d12)
							+ getPlayerEnergy(awayLineUp.getE44().f11)
							+ getPlayerEnergy(awayLineUp.getE44().f12);
					strength2 = getPlayerEnergy(awayLineUp.getE44().d21)
							+ getPlayerEnergy(awayLineUp.getE44().d22)
							+ getPlayerEnergy(awayLineUp.getE44().f21)
							+ getPlayerEnergy(awayLineUp.getE44().f22);
				} else {
					assert (false); // away team cannot do SAME/AGAINST
				}
				block = Util.random(3); // 0..2
				if (strength1 >= strength2) {
					// 1,2
					if (block > 1) {
						block = 2;
					} else {
						block = 1;
					}
				} else {
					// 2, 1
					if (block > 1) {
						block = 1;
					} else {
						block = 2;
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (awayLineUp.getE44().d11 == null
								|| awayLineUp.getE44().d12 == null
								|| awayLineUp.getE44().f11 == null || awayLineUp.getE44().f12 == null)) {
					block = 2;
				}
				if (block == 2
						&& (awayLineUp.getE44().d21 == null
								|| awayLineUp.getE44().d22 == null
								|| awayLineUp.getE44().f21 == null || awayLineUp.getE44().f22 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().d11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().d12);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().f11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().f12);
				} else {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().d21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().d22);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().f21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE44().f22);
				}
				// home put on ice
				if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
					if (block == 1) {
						block = 2;
					} else if (block == 2) {
						block = 1;
					}
				} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
					// do nothing ("block" remains the same)
				} else {
					if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(homeLineUp.getE44().d11)
								+ getPlayerStrength(homeLineUp.getE44().d12)
								+ getPlayerStrength(homeLineUp.getE44().f11)
								+ getPlayerStrength(homeLineUp.getE44().f12);
						strength2 = getPlayerStrength(homeLineUp.getE44().d21)
								+ getPlayerStrength(homeLineUp.getE44().d22)
								+ getPlayerStrength(homeLineUp.getE44().f21)
								+ getPlayerStrength(homeLineUp.getE44().f22);
					} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(homeLineUp.getE44().d11)
								+ getPlayerEnergy(homeLineUp.getE44().d12)
								+ getPlayerEnergy(homeLineUp.getE44().f11)
								+ getPlayerEnergy(homeLineUp.getE44().f12);
						strength2 = getPlayerEnergy(homeLineUp.getE44().d21)
								+ getPlayerEnergy(homeLineUp.getE44().d22)
								+ getPlayerEnergy(homeLineUp.getE44().f21)
								+ getPlayerEnergy(homeLineUp.getE44().f22);
					}
					block = Util.random(3); // 0..2
					if (strength1 >= strength2) {
						// 1,2
						if (block > 1) {
							block = 2;
						} else {
							block = 1;
						}
					} else {
						// 2, 1
						if (block > 1) {
							block = 1;
						} else {
							block = 2;
						}
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (homeLineUp.getE44().d11 == null
								|| homeLineUp.getE44().d12 == null
								|| homeLineUp.getE44().f11 == null || homeLineUp.getE44().f12 == null)) {
					block = 2;
				}
				if (block == 2
						&& (homeLineUp.getE44().d21 == null
								|| homeLineUp.getE44().d22 == null
								|| homeLineUp.getE44().f21 == null || homeLineUp.getE44().f22 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().d11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().d12);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().f11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().f12);
				} else {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().d21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().d22);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().f21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE44().f22);
				}
			} else {
				assert (nofHomeFieldPlayers == 3);
				// away put on ice
				if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
					strength1 = getPlayerStrength(awayLineUp.getE33().d11)
							+ getPlayerStrength(awayLineUp.getE33().d12)
							+ getPlayerStrength(awayLineUp.getE33().f1);
					strength2 = getPlayerStrength(awayLineUp.getE33().d21)
							+ getPlayerStrength(awayLineUp.getE33().d22)
							+ getPlayerStrength(awayLineUp.getE33().f2);
				} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
					strength1 = getPlayerEnergy(awayLineUp.getE33().d11)
							+ getPlayerEnergy(awayLineUp.getE33().d12)
							+ getPlayerEnergy(awayLineUp.getE33().f1);
					strength2 = getPlayerEnergy(awayLineUp.getE33().d21)
							+ getPlayerEnergy(awayLineUp.getE33().d22)
							+ getPlayerEnergy(awayLineUp.getE33().f2);
				} else {
					assert (false); // away team cannot do SAME/AGAINST
				}
				block = Util.random(3); // 0..2
				if (strength1 >= strength2) {
					// 1,2
					if (block > 1) {
						block = 2;
					} else {
						block = 1;
					}
				} else {
					// 2, 1
					if (block > 1) {
						block = 1;
					} else {
						block = 2;
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (awayLineUp.getE33().d11 == null
								|| awayLineUp.getE33().d12 == null || awayLineUp.getE33().f1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (awayLineUp.getE33().d21 == null
								|| awayLineUp.getE33().d22 == null || awayLineUp.getE33().f2 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE33().d11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE33().d12);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE33().f1);
				} else {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE33().d21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE33().d22);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getE33().f2);
				}
				// home put on ice
				if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
					if (block == 1) {
						block = 2;
					} else if (block == 2) {
						block = 1;
					}
				} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
					// do nothing ("block" remains the same)
				} else {
					if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(homeLineUp.getE33().d11)
								+ getPlayerStrength(homeLineUp.getE33().d12)
								+ getPlayerStrength(homeLineUp.getE33().f1);
						strength2 = getPlayerStrength(homeLineUp.getE33().d21)
								+ getPlayerStrength(homeLineUp.getE33().d22)
								+ getPlayerStrength(homeLineUp.getE33().f2);
					} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(homeLineUp.getE33().d11)
								+ getPlayerEnergy(homeLineUp.getE33().d12)
								+ getPlayerEnergy(homeLineUp.getE33().f1);
						strength2 = getPlayerEnergy(homeLineUp.getE33().d21)
								+ getPlayerEnergy(homeLineUp.getE33().d22)
								+ getPlayerEnergy(homeLineUp.getE33().f2);
					}
					block = Util.random(3); // 0..2
					if (strength1 >= strength2) {
						// 1,2
						if (block > 1) {
							block = 2;
						} else {
							block = 1;
						}
					} else {
						// 2, 1
						if (block > 1) {
							block = 1;
						} else {
							block = 2;
						}
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (homeLineUp.getE33().d11 == null
								|| homeLineUp.getE33().d12 == null || homeLineUp.getE33().f1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (homeLineUp.getE33().d21 == null
								|| homeLineUp.getE33().d22 == null || homeLineUp.getE33().f2 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE33().d11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE33().d12);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE33().f1);
				} else {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE33().d21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE33().d22);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getE33().f2);
				}
			}
		} else if (nofHomeFieldPlayers > nofAwayFieldPlayers) {
			// powerplay home
			if (nofHomeFieldPlayers == 5) {
				// away put on ice
				if (nofAwayFieldPlayers == 4) {
					if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(awayLineUp.getPk4().d11)
								+ getPlayerStrength(awayLineUp.getPk4().d12)
								+ getPlayerStrength(awayLineUp.getPk4().f11)
								+ getPlayerStrength(awayLineUp.getPk4().f12);
						strength2 = getPlayerStrength(awayLineUp.getPk4().d21)
								+ getPlayerStrength(awayLineUp.getPk4().d22)
								+ getPlayerStrength(awayLineUp.getPk4().f21)
								+ getPlayerStrength(awayLineUp.getPk4().f22);
					} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(awayLineUp.getPk4().d11)
								+ getPlayerEnergy(awayLineUp.getPk4().d12)
								+ getPlayerEnergy(awayLineUp.getPk4().f11)
								+ getPlayerEnergy(awayLineUp.getPk4().f12);
						strength2 = getPlayerEnergy(awayLineUp.getPk4().d21)
								+ getPlayerEnergy(awayLineUp.getPk4().d22)
								+ getPlayerEnergy(awayLineUp.getPk4().f21)
								+ getPlayerEnergy(awayLineUp.getPk4().f22);
					} else {
						assert (false); // away team cannot do SAME/AGAINST
					}
					block = Util.random(3); // 0..2
					if (strength1 >= strength2) {
						// 1,2
						if (block > 1) {
							block = 2;
						} else {
							block = 1;
						}
					} else {
						// 2, 1
						if (block > 1) {
							block = 1;
						} else {
							block = 2;
						}
					}
					// don't take incomplete blocks
					if (block == 1
							&& (awayLineUp.getPk4().d11 == null
									|| awayLineUp.getPk4().d12 == null
									|| awayLineUp.getPk4().f11 == null || awayLineUp.getPk4().f12 == null)) {
						block = 2;
					}
					if (block == 2
							&& (awayLineUp.getPk4().d21 == null
									|| awayLineUp.getPk4().d22 == null
									|| awayLineUp.getPk4().f21 == null || awayLineUp.getPk4().f22 == null)) {
						block = 1;
					}
					if (block == 1) {
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().d11);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().d12);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().f11);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().f12);
					} else {
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().d21);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().d22);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().f21);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk4().f22);
					}
				} else {
					assert (nofAwayFieldPlayers == 3);
					if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(awayLineUp.getPk3().d11)
								+ getPlayerStrength(awayLineUp.getPk3().d12)
								+ getPlayerStrength(awayLineUp.getPk3().f1);
						strength2 = getPlayerStrength(awayLineUp.getPk3().d21)
								+ getPlayerStrength(awayLineUp.getPk3().d22)
								+ getPlayerStrength(awayLineUp.getPk3().f2);
					} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(awayLineUp.getPk3().d11)
								+ getPlayerEnergy(awayLineUp.getPk3().d12)
								+ getPlayerEnergy(awayLineUp.getPk3().f1);
						strength2 = getPlayerEnergy(awayLineUp.getPk3().d21)
								+ getPlayerEnergy(awayLineUp.getPk3().d22)
								+ getPlayerEnergy(awayLineUp.getPk3().f2);
					} else {
						assert (false); // away team cannot do SAME/AGAINST
					}
					block = Util.random(3); // 0..2
					if (strength1 >= strength2) {
						// 1,2
						if (block > 1) {
							block = 2;
						} else {
							block = 1;
						}
					} else {
						// 2, 1
						if (block > 1) {
							block = 1;
						} else {
							block = 2;
						}
					}
					// don't take incomplete blocks
					if (block == 1
							&& (awayLineUp.getPk3().d11 == null
									|| awayLineUp.getPk3().d12 == null || awayLineUp.getPk3().f1 == null)) {
						block = 2;
					}
					if (block == 2
							&& (awayLineUp.getPk3().d21 == null
									|| awayLineUp.getPk3().d22 == null || awayLineUp.getPk3().f2 == null)) {
						block = 1;
					}
					if (block == 1) {
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d11);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d12);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().f1);
					} else {
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d21);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d22);
						this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().f2);
					}
				}
				// home put on ice
				if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
					if (block == 1) {
						block = 2;
					} else if (block == 2) {
						block = 1;
					}
				} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
					// do nothing ("block" remains the same)
				} else {
					if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(homeLineUp.getPp5().d11)
								+ getPlayerStrength(homeLineUp.getPp5().d12)
								+ getPlayerStrength(homeLineUp.getPp5().lw1)
								+ getPlayerStrength(homeLineUp.getPp5().c1)
								+ getPlayerStrength(homeLineUp.getPp5().rw1);
						strength2 = getPlayerStrength(homeLineUp.getPp5().d21)
								+ getPlayerStrength(homeLineUp.getPp5().d22)
								+ getPlayerStrength(homeLineUp.getPp5().lw2)
								+ getPlayerStrength(homeLineUp.getPp5().c2)
								+ getPlayerStrength(homeLineUp.getPp5().rw2);
					} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(homeLineUp.getPp5().d11)
								+ getPlayerEnergy(homeLineUp.getPp5().d12)
								+ getPlayerEnergy(homeLineUp.getPp5().lw1)
								+ getPlayerEnergy(homeLineUp.getPp5().c1)
								+ getPlayerEnergy(homeLineUp.getPp5().rw1);
						strength2 = getPlayerEnergy(homeLineUp.getPp5().d21)
								+ getPlayerEnergy(homeLineUp.getPp5().d22)
								+ getPlayerEnergy(homeLineUp.getPp5().lw2)
								+ getPlayerEnergy(homeLineUp.getPp5().c2)
								+ getPlayerEnergy(homeLineUp.getPp5().rw2);
					}
					block = Util.random(3); // 0..2
					if (strength1 >= strength2) {
						// 1,2
						if (block > 1) {
							block = 2;
						} else {
							block = 1;
						}
					} else {
						// 2, 1
						if (block > 1) {
							block = 1;
						} else {
							block = 2;
						}
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (homeLineUp.getPp5().d11 == null
								|| homeLineUp.getPp5().d12 == null
								|| homeLineUp.getPp5().lw1 == null
								|| homeLineUp.getPp5().c1 == null || homeLineUp.getPp5().rw1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (homeLineUp.getPp5().d21 == null
								|| homeLineUp.getPp5().d22 == null
								|| homeLineUp.getPp5().lw2 == null
								|| homeLineUp.getPp5().c2 == null || homeLineUp.getPp5().rw2 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().d11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().d12);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().lw1);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().c1);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().rw1);
				} else {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().d21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().d22);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().lw2);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().c2);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp5().rw2);
				}
			} else {
				assert (nofHomeFieldPlayers == 4);
				assert (nofAwayFieldPlayers == 3);
				// away put on ice
				if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
					strength1 = getPlayerStrength(awayLineUp.getPk3().d11)
							+ getPlayerStrength(awayLineUp.getPk3().d12)
							+ getPlayerStrength(awayLineUp.getPk3().f1);
					strength2 = getPlayerStrength(awayLineUp.getPk3().d21)
							+ getPlayerStrength(awayLineUp.getPk3().d22)
							+ getPlayerStrength(awayLineUp.getPk3().f2);
				} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
					strength1 = getPlayerEnergy(awayLineUp.getPk3().d11)
							+ getPlayerEnergy(awayLineUp.getPk3().d12)
							+ getPlayerEnergy(awayLineUp.getPk3().f1);
					strength2 = getPlayerEnergy(awayLineUp.getPk3().d21)
							+ getPlayerEnergy(awayLineUp.getPk3().d22)
							+ getPlayerEnergy(awayLineUp.getPk3().f2);
				} else {
					assert (false); // away team cannot do SAME/AGAINST
				}
				block = Util.random(3); // 0..2
				if (strength1 >= strength2) {
					// 1,2
					if (block > 1) {
						block = 2;
					} else {
						block = 1;
					}
				} else {
					// 2, 1
					if (block > 1) {
						block = 1;
					} else {
						block = 2;
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (awayLineUp.getPk3().d11 == null
								|| awayLineUp.getPk3().d12 == null || awayLineUp.getPk3().f1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (awayLineUp.getPk3().d21 == null
								|| awayLineUp.getPk3().d22 == null || awayLineUp.getPk3().f2 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d12);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().f1);
				} else {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().d22);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPk3().f2);
				}
				// home put on ice
				if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
					if (block == 1) {
						block = 2;
					} else if (block == 2) {
						block = 1;
					}
				} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
					// do nothing ("block" remains the same)
				} else {
					if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(homeLineUp.getPp4().d11)
								+ getPlayerStrength(homeLineUp.getPp4().d12)
								+ getPlayerStrength(homeLineUp.getPp4().f11)
								+ getPlayerStrength(homeLineUp.getPp4().f12);
						strength2 = getPlayerStrength(homeLineUp.getPp4().d21)
								+ getPlayerStrength(homeLineUp.getPp4().d22)
								+ getPlayerStrength(homeLineUp.getPp4().f21)
								+ getPlayerStrength(homeLineUp.getPp4().f22);
					} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(homeLineUp.getPp4().d11)
								+ getPlayerEnergy(homeLineUp.getPp4().d12)
								+ getPlayerEnergy(homeLineUp.getPp4().f11)
								+ getPlayerEnergy(homeLineUp.getPp4().f12);
						strength2 = getPlayerEnergy(homeLineUp.getPp4().d21)
								+ getPlayerEnergy(homeLineUp.getPp4().d22)
								+ getPlayerEnergy(homeLineUp.getPp4().f21)
								+ getPlayerEnergy(homeLineUp.getPp4().f22);
					}
					block = Util.random(3); // 0..2
					if (strength1 >= strength2) {
						// 1,2
						if (block > 1) {
							block = 2;
						} else {
							block = 1;
						}
					} else {
						// 2, 1
						if (block > 1) {
							block = 1;
						} else {
							block = 2;
						}
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (homeLineUp.getPp4().d11 == null
								|| homeLineUp.getPp4().d12 == null
								|| homeLineUp.getPp4().f11 == null || homeLineUp.getPp4().f12 == null)) {
					block = 2;
				}
				if (block == 2
						&& (homeLineUp.getPp4().d21 == null
								|| homeLineUp.getPp4().d22 == null
								|| homeLineUp.getPp4().f21 == null || homeLineUp.getPp4().f22 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().d11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().d12);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().f11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().f12);
				} else {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().d21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().d22);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().f21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPp4().f22);
				}
			}
		} else {
			// powerplay away
			assert (nofHomeFieldPlayers < nofAwayFieldPlayers);
			if (nofAwayFieldPlayers == 5) {
				// away put on ice
				if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
					strength1 = getPlayerStrength(awayLineUp.getPp5().d11)
							+ getPlayerStrength(awayLineUp.getPp5().d12)
							+ getPlayerStrength(awayLineUp.getPp5().lw1)
							+ getPlayerStrength(awayLineUp.getPp5().c1)
							+ getPlayerStrength(awayLineUp.getPp5().rw1);
					strength2 = getPlayerStrength(awayLineUp.getPp5().d21)
							+ getPlayerStrength(awayLineUp.getPp5().d22)
							+ getPlayerStrength(awayLineUp.getPp5().lw2)
							+ getPlayerStrength(awayLineUp.getPp5().c2)
							+ getPlayerStrength(awayLineUp.getPp5().rw2);
				} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
					strength1 = getPlayerEnergy(awayLineUp.getPp5().d11)
							+ getPlayerEnergy(awayLineUp.getPp5().d12)
							+ getPlayerEnergy(awayLineUp.getPp5().lw1)
							+ getPlayerEnergy(awayLineUp.getPp5().c1)
							+ getPlayerEnergy(awayLineUp.getPp5().rw1);
					strength2 = getPlayerEnergy(awayLineUp.getPp5().d21)
							+ getPlayerEnergy(awayLineUp.getPp5().d22)
							+ getPlayerEnergy(awayLineUp.getPp5().lw2)
							+ getPlayerEnergy(awayLineUp.getPp5().c2)
							+ getPlayerEnergy(awayLineUp.getPp5().rw2);
				} else {
					assert (false); // away team cannot do SAME/AGAINST
				}
				block = Util.random(3); // 0..2
				if (strength1 >= strength2) {
					// 1,2
					if (block > 1) {
						block = 2;
					} else {
						block = 1;
					}
				} else {
					// 2, 1
					if (block > 1) {
						block = 1;
					} else {
						block = 2;
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (awayLineUp.getPp5().d11 == null
								|| awayLineUp.getPp5().d12 == null
								|| awayLineUp.getPp5().lw1 == null
								|| awayLineUp.getPp5().c1 == null || awayLineUp.getPp5().rw1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (awayLineUp.getPp5().d21 == null
								|| awayLineUp.getPp5().d22 == null
								|| awayLineUp.getPp5().lw2 == null
								|| awayLineUp.getPp5().c2 == null || awayLineUp.getPp5().rw2 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().d11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().d12);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().lw1);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().c1);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().rw1);
				} else {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().d21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().d22);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().lw2);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().c2);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp5().rw2);
				}
				// home put on ice
				if (nofHomeFieldPlayers == 4) {
					if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
						if (block == 1) {
							block = 2;
						} else if (block == 2) {
							block = 1;
						}
					} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
						// do nothing ("block" remains the same)
					} else {
						if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
							strength1 = getPlayerStrength(homeLineUp.getPk4().d11)
									+ getPlayerStrength(homeLineUp.getPk4().d12)
									+ getPlayerStrength(homeLineUp.getPk4().f11)
									+ getPlayerStrength(homeLineUp.getPk4().f12);
							strength2 = getPlayerStrength(homeLineUp.getPk4().d21)
									+ getPlayerStrength(homeLineUp.getPk4().d22)
									+ getPlayerStrength(homeLineUp.getPk4().f21)
									+ getPlayerStrength(homeLineUp.getPk4().f22);
						} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
							strength1 = getPlayerEnergy(homeLineUp.getPk4().d11)
									+ getPlayerEnergy(homeLineUp.getPk4().d12)
									+ getPlayerEnergy(homeLineUp.getPk4().f11)
									+ getPlayerEnergy(homeLineUp.getPk4().f12);
							strength2 = getPlayerEnergy(homeLineUp.getPk4().d21)
									+ getPlayerEnergy(homeLineUp.getPk4().d22)
									+ getPlayerEnergy(homeLineUp.getPk4().f21)
									+ getPlayerEnergy(homeLineUp.getPk4().f22);
						}
						block = Util.random(3); // 0..2
						if (strength1 >= strength2) {
							// 1,2
							if (block > 1) {
								block = 2;
							} else {
								block = 1;
							}
						} else {
							// 2, 1
							if (block > 1) {
								block = 1;
							} else {
								block = 2;
							}
						}
					}
					// don't take incomplete blocks
					if (block == 1
							&& (homeLineUp.getPk4().d11 == null
									|| homeLineUp.getPk4().d12 == null
									|| homeLineUp.getPk4().f11 == null || homeLineUp.getPk4().f12 == null)) {
						block = 2;
					}
					if (block == 2
							&& (homeLineUp.getPk4().d21 == null
									|| homeLineUp.getPk4().d22 == null
									|| homeLineUp.getPk4().f21 == null || homeLineUp.getPk4().f22 == null)) {
						block = 1;
					}
					if (block == 1) {
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().d11);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().d12);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().f11);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().f12);
					} else {
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().d21);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().d22);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().f21);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk4().f22);
					}
				} else {
					assert (nofHomeFieldPlayers == 3);
					if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
						if (block == 1) {
							block = 2;
						} else if (block == 2) {
							block = 1;
						}
					} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
						// do nothing ("block" remains the same)
					} else {
						if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
							strength1 = getPlayerStrength(homeLineUp.getPk3().d11)
									+ getPlayerStrength(homeLineUp.getPk3().d12)
									+ getPlayerStrength(homeLineUp.getPk3().f1);
							strength2 = getPlayerStrength(homeLineUp.getPk3().d21)
									+ getPlayerStrength(homeLineUp.getPk3().d22)
									+ getPlayerStrength(homeLineUp.getPk3().f2);
						} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
							strength1 = getPlayerEnergy(homeLineUp.getPk3().d11)
									+ getPlayerEnergy(homeLineUp.getPk3().d12)
									+ getPlayerEnergy(homeLineUp.getPk3().f1);
							strength2 = getPlayerEnergy(homeLineUp.getPk3().d21)
									+ getPlayerEnergy(homeLineUp.getPk3().d22)
									+ getPlayerEnergy(homeLineUp.getPk3().f2);
						}
						block = Util.random(3); // 0..2
						if (strength1 >= strength2) {
							// 1,2
							if (block > 1) {
								block = 2;
							} else {
								block = 1;
							}
						} else {
							// 2, 1
							if (block > 1) {
								block = 1;
							} else {
								block = 2;
							}
						}
					}
					// don't take incomplete blocks
					if (block == 1
							&& (homeLineUp.getPk3().d11 == null
									|| homeLineUp.getPk3().d12 == null || homeLineUp.getPk3().f1 == null)) {
						block = 2;
					}
					if (block == 2
							&& (homeLineUp.getPk3().d21 == null
									|| homeLineUp.getPk3().d22 == null || homeLineUp.getPk3().f2 == null)) {
						block = 1;
					}
					if (block == 1) {
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d11);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d12);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().f1);
					} else {
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d21);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d22);
						this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().f2);
					}
				}
			} else {
				assert (nofHomeFieldPlayers == 3);
				assert (nofAwayFieldPlayers == 4);
				// away put on ice
				if (awayBlockSelection == Tactics.BlockSelection.STRONGER) {
					strength1 = getPlayerStrength(awayLineUp.getPp4().d11)
							+ getPlayerStrength(awayLineUp.getPp4().d12)
							+ getPlayerStrength(awayLineUp.getPp4().f11)
							+ getPlayerStrength(awayLineUp.getPp4().f12);
					strength2 = getPlayerStrength(awayLineUp.getPp4().d21)
							+ getPlayerStrength(awayLineUp.getPp4().d22)
							+ getPlayerStrength(awayLineUp.getPp4().f21)
							+ getPlayerStrength(awayLineUp.getPp4().f22);
				} else if (awayBlockSelection == Tactics.BlockSelection.LESSTIRED) {
					strength1 = getPlayerEnergy(awayLineUp.getPp4().d11)
							+ getPlayerEnergy(awayLineUp.getPp4().d12)
							+ getPlayerEnergy(awayLineUp.getPp4().f11)
							+ getPlayerEnergy(awayLineUp.getPp4().f12);
					strength2 = getPlayerEnergy(awayLineUp.getPp4().d21)
							+ getPlayerEnergy(awayLineUp.getPp4().d22)
							+ getPlayerEnergy(awayLineUp.getPp4().f21)
							+ getPlayerEnergy(awayLineUp.getPp4().f22);
				}
				block = Util.random(3); // 0..2
				if (strength1 >= strength2) {
					// 1,2
					if (block > 1) {
						block = 2;
					} else {
						block = 1;
					}
				} else {
					// 2, 1
					if (block > 1) {
						block = 1;
					} else {
						block = 2;
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (awayLineUp.getPp4().d11 == null
								|| awayLineUp.getPp4().d12 == null
								|| awayLineUp.getPp4().f11 == null || awayLineUp.getPp4().f12 == null)) {
					block = 2;
				}
				if (block == 2
						&& (awayLineUp.getPp4().d21 == null
								|| awayLineUp.getPp4().d22 == null
								|| awayLineUp.getPp4().f21 == null || awayLineUp.getPp4().f22 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().d11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().d12);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().f11);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().f12);
				} else {
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().d21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().d22);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().f21);
					this.onIce.awayPlayer.InsertPlayer(awayLineUp.getPp4().f22);
				}
				// home put on ice
				if (homeBlockSelection == Tactics.BlockSelection.AGAINST) {
					if (block == 1) {
						block = 2;
					} else if (block == 2) {
						block = 1;
					}
				} else if (homeBlockSelection == Tactics.BlockSelection.SAME) {
					// do nothing ("block" remains the same)
				} else {
					if (homeBlockSelection == Tactics.BlockSelection.STRONGER) {
						strength1 = getPlayerStrength(homeLineUp.getPk3().d11)
								+ getPlayerStrength(homeLineUp.getPk3().d12)
								+ getPlayerStrength(homeLineUp.getPk3().f1);
						strength2 = getPlayerStrength(homeLineUp.getPk3().d21)
								+ getPlayerStrength(homeLineUp.getPk3().d22)
								+ getPlayerStrength(homeLineUp.getPk3().f2);
					} else if (homeBlockSelection == Tactics.BlockSelection.LESSTIRED) {
						strength1 = getPlayerEnergy(homeLineUp.getPk3().d11)
								+ getPlayerEnergy(homeLineUp.getPk3().d12)
								+ getPlayerEnergy(homeLineUp.getPk3().f1);
						strength2 = getPlayerEnergy(homeLineUp.getPk3().d21)
								+ getPlayerEnergy(homeLineUp.getPk3().d22)
								+ getPlayerEnergy(homeLineUp.getPk3().f2);
					}
					block = Util.random(3); // 0..2
					if (strength1 >= strength2) {
						// 1,2
						if (block > 1) {
							block = 2;
						} else {
							block = 1;
						}
					} else {
						// 2, 1
						if (block > 1) {
							block = 1;
						} else {
							block = 2;
						}
					}
				}
				// don't take incomplete blocks
				if (block == 1
						&& (homeLineUp.getPk3().d11 == null
								|| homeLineUp.getPk3().d12 == null || homeLineUp.getPk3().f1 == null)) {
					block = 2;
				}
				if (block == 2
						&& (homeLineUp.getPk3().d21 == null
								|| homeLineUp.getPk3().d22 == null || homeLineUp.getPk3().f2 == null)) {
					block = 1;
				}
				if (block == 1) {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d11);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d12);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().f1);
				} else {
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d21);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().d22);
					this.onIce.homePlayer.InsertPlayer(homeLineUp.getPk3().f2);
				}
			}
		}
		// if player in penbox or injured, remove from ice and replace with
		// other player
		int i = 0;
		while (i < onIce.homePlayer.GetNofPlayers()) {
			Player player = onIce.homePlayer.GetPlayer(i);
			if (penBox.homePlayer.GetIdPlayer(player.getId()) != null
					|| player.getHealth().getInjury() > 0 // there are no injured players on ice !
			) {
				onIce.homePlayer.RemovePlayer(player);
				// TODO get least tired player from the lineups
				player = homeLineUp.getE55().c4;
				if (player == null
						|| player.getHealth().getInjury() > 0
						|| penBox.homePlayer.GetIdPlayer(player.getId()) != null
						|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
					player = homeLineUp.getE55().lw4;
					if (player == null
							|| player.getHealth().getInjury() > 0
							|| penBox.homePlayer.GetIdPlayer(player.getId()) != null
							|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
						player = homeLineUp.getE55().rw4;
						if (player == null
								|| player.getHealth().getInjury() > 0
								|| penBox.homePlayer
										.GetIdPlayer(player.getId()) != null
								|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
							player = homeLineUp.getG().goal2;
							if (player == null
									|| player.getHealth().getInjury() > 0
									|| penBox.homePlayer.GetIdPlayer(player
											.getId()) != null
									|| onIce.homePlayer.GetIdPlayer(player
											.getId()) != null) {
								player = null;
							}

							// NYI

						}
					}
				}
				if (player != null) {
					onIce.homePlayer.InsertPlayer(player);
				}
				i = 0;
			} else {
				i++;
			}
		}
		i = 0;
		while (i < onIce.awayPlayer.GetNofPlayers()) {
			Player player = onIce.awayPlayer.GetPlayer(i);
			if (penBox.awayPlayer.GetIdPlayer(player.getId()) != null
					|| player.getHealth().getInjury() > 0 // there are no injured players on ice !
			) {
				onIce.awayPlayer.RemovePlayer(player);
				// NYI get least tired player from the lineups
				player = awayLineUp.getE55().c4;
				if (player == null
						|| player.getHealth().getInjury() > 0
						|| penBox.awayPlayer.GetIdPlayer(player.getId()) != null
						|| onIce.awayPlayer.GetIdPlayer(player.getId()) != null) {
					player = awayLineUp.getE55().lw4;
					if (player == null
							|| player.getHealth().getInjury() > 0
							|| penBox.awayPlayer.GetIdPlayer(player.getId()) != null
							|| onIce.awayPlayer.GetIdPlayer(player.getId()) != null) {
						player = awayLineUp.getE55().rw4;
						if (player == null
								|| player.getHealth().getInjury() > 0
								|| penBox.awayPlayer
										.GetIdPlayer(player.getId()) != null
								|| onIce.awayPlayer.GetIdPlayer(player.getId()) != null) {
							player = awayLineUp.getG().goal2;
							if (player == null
									|| player.getHealth().getInjury() > 0
									|| penBox.awayPlayer.GetIdPlayer(player
											.getId()) != null
									|| onIce.awayPlayer.GetIdPlayer(player
											.getId()) != null) {
								player = null;
							}

							// NYI

						}
					}
				}
				if (player != null) {
					onIce.awayPlayer.InsertPlayer(player);
				}
				i = 0;
			} else {
				i++;
			}
		}

		if (mt.getTotalSec() > 3540 && currHomeScore + 1 == currAwayScore
				|| mt.getTotalSec() > 3480 && mt.getTotalSec() <= 3540
				&& currHomeScore + 2 == currAwayScore) {
			// pull/push goalie
			// pull goalie
			Player player = onIce.homePlayer.GetPlayer(0);
			onIce.homePlayer.RemovePlayer(player);
			// NYI get least tired player from the lineups
			player = homeLineUp.getE55().c4;
			if (player == null || player.getHealth().getInjury() > 0
					|| penBox.homePlayer.GetIdPlayer(player.getId()) != null
					|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
				player = homeLineUp.getE55().lw4;
				if (player == null
						|| player.getHealth().getInjury() > 0
						|| penBox.homePlayer.GetIdPlayer(player.getId()) != null
						|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
					player = homeLineUp.getE55().rw4;
					if (player == null
							|| player.getHealth().getInjury() > 0
							|| penBox.homePlayer.GetIdPlayer(player.getId()) != null
							|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
						player = homeLineUp.getG().goal2;
						if (player == null
								|| player.getHealth().getInjury() > 0
								|| penBox.homePlayer
										.GetIdPlayer(player.getId()) != null
								|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
							player = null;
						}

						// NYI

					}
				}
			}
			if (player != null) {
				onIce.homePlayer.InsertPlayer(player);
			}
		}
		if (mt.getTotalSec() > 3540 && currAwayScore + 1 == currHomeScore
				|| mt.getTotalSec() > 3480 && mt.getTotalSec() <= 3540
				&& currAwayScore + 2 == currHomeScore) {
			// pull goalie
			Player player = onIce.awayPlayer.GetPlayer(0);
			onIce.awayPlayer.RemovePlayer(player);
			// NYI get least tired player from the lineups
			player = awayLineUp.getE55().c4;
			if (player == null || player.getHealth().getInjury() > 0
					|| penBox.awayPlayer.GetIdPlayer(player.getId()) != null
					|| onIce.awayPlayer.GetIdPlayer(player.getId()) != null) {
				player = awayLineUp.getE55().lw4;
				if (player == null
						|| player.getHealth().getInjury() > 0
						|| penBox.awayPlayer.GetIdPlayer(player.getId()) != null
						|| onIce.awayPlayer.GetIdPlayer(player.getId()) != null) {
					player = awayLineUp.getE55().rw4;
					if (player == null
							|| player.getHealth().getInjury() > 0
							|| penBox.awayPlayer.GetIdPlayer(player.getId()) != null
							|| onIce.awayPlayer.GetIdPlayer(player.getId()) != null) {
						player = awayLineUp.getG().goal2;
						if (player == null
								|| player.getHealth().getInjury() > 0
								|| penBox.homePlayer
										.GetIdPlayer(player.getId()) != null
								|| onIce.homePlayer.GetIdPlayer(player.getId()) != null) {
							player = null;
						}

						// NYI

					}
				}
			}
			if (player != null) {
				onIce.awayPlayer.InsertPlayer(player);
			}
		}

	}

	void calcGoal(MatchTime mt, boolean shootout) {
		assert (home != null && away != null);
		Tactics.GamePlay homeGamePlay = home.getTactics().getGamePlay();
		Tactics.Effort homeEffort = home.getTactics().getEffort();
		Tactics.Defense homeDefense = home.getTactics().getDefense();
		Tactics.Offense homeOffense = home.getTactics().getOffense();
		Tactics.GamePlay awayGamePlay = away.getTactics().getGamePlay();
		Tactics.Effort awayEffort = away.getTactics().getEffort();
		Tactics.Defense awayDefense = away.getTactics().getDefense();
		Tactics.Offense awayOffense = away.getTactics().getOffense();

		// calc goals
		boolean homeWasShooting = false;
		int strength;
		int ranRange;
		int tune; // controller according tactics:
		// the greater tune, the less goals

		int nofHomePlayers = onIce.homePlayer.GetNofPlayers();
		int nofAwayPlayers = onIce.awayPlayer.GetNofPlayers();

		// calc strength of home team
		int homeStrength = 0;
		int collectPos = 0; // check for having correct positions
		for (int i = 0; i < nofHomePlayers; i++) {
			Player player = onIce.homePlayer.GetPlayer(i);
			Position.PosID posID = player.getPosition().getPosID();
			if (posID != Position.PosID.GOALIE) {
				// defense/offense tactics
				int playerStrength = player.getTotalStrengthWithMot();
				playerStrength *= 10; // 20 bytes code, ?? cycles
				if (homeDefense == Tactics.Defense.ZONE) {
					playerStrength += (player.getSkill() + player.getDefense()) / 2;
				} else {
					assert (homeDefense == Tactics.Defense.MARK);
					playerStrength += player.getPower();
				}
				if (homeOffense == Tactics.Offense.SHOOT) {
					playerStrength += player.getShooting();
				} else {
					assert (homeOffense == Tactics.Offense.PASS);
					playerStrength += (player.getPassing() + player
							.getOffense()) / 2;
				}
				playerStrength = playerStrength / 12;
				homeStrength += playerStrength;
				if (posID == Position.PosID.DEFENDER) {
					collectPos += 100;
				} else if (posID == Position.PosID.LEFTWING) {
					collectPos += 25;
				} else if (posID == Position.PosID.RIGHTWING) {
					collectPos -= 25;
				} else {
					assert (posID == Position.PosID.CENTER);
				}
			}
		}
		if (collectPos != 200) {
			// check for having correct positions
			homeStrength -= homeStrength / 10;
			if (collectPos < 150 || collectPos > 250) {
				homeStrength -= homeStrength / 10;
				if (collectPos < 100 || collectPos > 300) {
					homeStrength -= homeStrength / 10;
				}
			}
		}
		homeStrength /= 5;
		assert (homeStrength < 120 && homeStrength >= 0); // 6 field players,
		// div through 5
		if (home.getTeamState().getTeamStateID() == TeamState.TeamStateID.COACHED) {
			// game level
			if (Options.level == Options.Level.PROF && homeStrength > 0) {
				homeStrength--;
			} else if (Options.level == Options.Level.ROOKIE) {
				homeStrength++;
			}
		}

		// calc strength of away team
		int awayStrength = 0;
		collectPos = 0; // check for having correct positions
		for (int i = 0; i < nofAwayPlayers; i++) {
			Player player = onIce.awayPlayer.GetPlayer(i);
			Position.PosID posID = player.getPosition().getPosID();
			if (posID != Position.PosID.GOALIE) {
				// defense/offense tactics
				int playerStrength = player.getTotalStrengthWithMot();
				playerStrength *= 10;
				if (awayDefense == Tactics.Defense.ZONE) {
					playerStrength += (player.getSkill() + player.getDefense()) / 2;
				} else {
					assert (awayDefense == Tactics.Defense.MARK);
					playerStrength += player.getPower();
				}
				if (awayOffense == Tactics.Offense.SHOOT) {
					playerStrength += player.getShooting();
				} else {
					assert (awayOffense == Tactics.Offense.PASS);
					playerStrength += (player.getPassing() + player
							.getOffense()) / 2;
				}
				playerStrength = playerStrength / 12;
				awayStrength += playerStrength;
				if (posID == Position.PosID.DEFENDER) {
					collectPos += 100;
				} else if (posID == Position.PosID.LEFTWING) {
					collectPos += 25;
				} else if (posID == Position.PosID.RIGHTWING) {
					collectPos -= 25;
				} else {
					assert (posID == Position.PosID.CENTER);
				}
			}
		}
		if (collectPos != 200) {
			// check for having correct positions
			awayStrength -= awayStrength / 10;
			if (collectPos < 150 || collectPos > 250) {
				awayStrength -= awayStrength / 10;
				if (collectPos < 100 || collectPos > 300) {
					awayStrength -= awayStrength / 10;
				}
			}
		}
		awayStrength /= 5;
		assert (awayStrength < 120 && awayStrength >= 0); // 6 field players,
		// div through 5
		if (away.getTeamState().getTeamStateID() == TeamState.TeamStateID.COACHED) {
			// game level
			if (Options.level == Options.Level.PROF && awayStrength > 0) {
				awayStrength--;
			} else if (Options.level == Options.Level.ROOKIE) {
				awayStrength++;
			}
		}

		// calc shots/goals of away team
		// normalize strength
		float normalizeFactor = (float) 100 / (homeStrength + awayStrength);

		// Calc Shots/Goals: Consider opponents strength too.
		strength = (int) ((2 * homeStrength - awayStrength) * normalizeFactor);
		tune = 18; // normal tune
		if (homeGamePlay == Tactics.GamePlay.OFFENSIVE) {
			tune--;
		} else if (homeGamePlay == Tactics.GamePlay.DEFENSIVE) {
			tune++;
		} else {
			assert (homeGamePlay == Tactics.GamePlay.NORMAL_GP);
		}
		if (awayGamePlay == Tactics.GamePlay.OFFENSIVE) {
			tune--;
		} else if (awayGamePlay == Tactics.GamePlay.DEFENSIVE) {
			tune++;
		} else {
			assert (awayGamePlay == Tactics.GamePlay.NORMAL_GP);
		}
		if (homeEffort == Tactics.Effort.FULL) {
			tune--;
		} else if (homeEffort == Tactics.Effort.EASY) {
			tune++;
		} else {
			assert (homeEffort == Tactics.Effort.NORMAL_EFF);
		}
		if (awayEffort == Tactics.Effort.FULL) {
			tune++;
		} else if (awayEffort == Tactics.Effort.EASY) {
			tune--;
		} else {
			assert (awayEffort == Tactics.Effort.NORMAL_EFF);
		}
		ranRange = 3 * tune + 50 - strength;
		if (Util.random(ranRange > 0 ? ranRange : 1) == 0 && nofHomePlayers > 1) {
			homeWasShooting = true;
			setHomeShots(getHomeShots() + 1);
			this.report.setHomeShots(this.report.getHomeShots() + 1);
			Player goaliePlayer = onIce.awayPlayer.GetPlayer(0);
			if (goaliePlayer == null
					|| Util.random((goaliePlayer.getTotalStrength() + 50) / 20) == 0
					|| goaliePlayer.getPosition().getPosID() != Position.PosID.GOALIE
			// goalie replaced by field player
			) {
				// goalie could not save the shot
				currHomeScore++;
				getEvent().bitSet.set(GOAL_HOME);
				// goal
				if (shootout) {
					// shoot out after overtime
					// calculation should be improved ...
					// NYI
					this.report.setGoals(this.report.getGoals()
							+ home.getTeamName());
					this.report.setGoals(this.report.getGoals()
							+ Util.getModelResourceBundle().getString("L_WINS_SHOOTOUT"));
					this.report.setGoals(this.report.getGoals() + "\n");
				} else {
					Player goalPlayer = this.onIce.homePlayer.GetPlayer(Util
							.random(nofHomePlayers - 1) + 1);
					if (goalPlayer.getPosition().getPosID() == Position.PosID.DEFENDER
							|| goalPlayer.getPosition().getPosID() == Position.PosID.GOALIE
							|| goalPlayer.getTotalStrength() < strength) {
						// calc again to give forwards and strong players a
						// greater chance
						goalPlayer = this.onIce.homePlayer.GetPlayer(Util
								.random(nofHomePlayers - 1) + 1);
					}
					goalPlayer.updateMatchSecond(
							Player.MatchPlayerEvent.GOAL_SHOT, 0);
					// a goal against the goalie (but only for REAL goalies!)
					if (goaliePlayer != null
							&& goaliePlayer.getPosition().getPosID() == Position.PosID.GOALIE) {
						goaliePlayer.updateMatchSecond(
								Player.MatchPlayerEvent.GOAL_RECEIVED, 0);
					}
					this.report.setGoals(this.report.getGoals()
							+ Integer.toString(mt.getMin() + 1));
					this.report.setGoals(this.report.getGoals() + ". ");
					this.report.setGoals(this.report.getGoals()
							+ Integer.toString(currHomeScore));
					this.report.setGoals(this.report.getGoals() + ":");
					this.report.setGoals(this.report.getGoals()
							+ Integer.toString(currAwayScore));
					this.report.setGoals(this.report.getGoals() + " ");
					if (goalPlayer.isMultipleName()) {
						this.report.setGoals(this.report.getGoals()
								+ goalPlayer.getFirstName());
						this.report.setGoals(this.report.getGoals()
								+ " ");
					}
					this.report.setGoals(this.report.getGoals()
							+ goalPlayer.getLastName());
					// 1st assist
					Player assistPlayer = this.onIce.homePlayer.GetPlayer(Util
							.random(nofHomePlayers - 1) + 1);
					if (assistPlayer.getPosition().getPosID() == Position.PosID.DEFENDER
							|| assistPlayer.getPosition().getPosID() == Position.PosID.GOALIE) {
						// more points for forwards
						assistPlayer = this.onIce.homePlayer.GetPlayer(Util
								.random(nofHomePlayers - 1) + 1);
					}
					if (assistPlayer != goalPlayer) {
						assistPlayer.updateMatchSecond(
								Player.MatchPlayerEvent.ASSIST, 0);
						this.report.setGoals(this.report.getGoals()
								+ " (");
						if (assistPlayer.isMultipleName()) {
							this.report.setGoals(this.report.getGoals()
									+ assistPlayer.getFirstName());
							this.report.setGoals(this.report.getGoals()
									+ " ");
						}
						this.report.setGoals(this.report.getGoals()
								+ assistPlayer.getLastName());
						Player assistPlayer2 = this.onIce.homePlayer
								.GetPlayer(Util.random(nofHomePlayers - 1) + 1);
						if (assistPlayer2 != goalPlayer
								&& assistPlayer2 != assistPlayer) {
							// 2nd assist
							assistPlayer2.updateMatchSecond(
									Player.MatchPlayerEvent.ASSIST, 0);
							this.report.setGoals(this.report.getGoals()
									+ ", ");
							if (assistPlayer2.isMultipleName()) {
								this.report.setGoals(this.report
										.getGoals() + assistPlayer2
												.getFirstName());
								this.report.setGoals(this.report
										.getGoals() + " ");
							}
							this.report.setGoals(this.report.getGoals()
									+ assistPlayer2.getLastName());
						}
						this.report.setGoals(this.report.getGoals()
								+ ")");
					}
					if (nofHomePlayers > nofAwayPlayers) {
						this.report.setGoals(this.report.getGoals()
								+ " PP");
					} else if (nofHomePlayers < nofAwayPlayers) {
						this.report.setGoals(this.report.getGoals()
								+ " SH");
					}
					if (goaliePlayer.getPosition().getPosID() != Position.PosID.GOALIE) {
						this.report.setGoals(this.report.getGoals()
								+ " EN"); // empty net info
					} else if (onIce.homePlayer.GetPlayer(0).getPosition()
							.getPosID() != Position.PosID.GOALIE) {
						this.report.setGoals(this.report.getGoals()
								+ " WG"); // without goalie
					} else if (assistPlayer == goalPlayer
							&& Util.random(50) == 25) {
						this.report.setGoals(this.report.getGoals()
								+ " PEN"); // penalty shot
					}
					this.report.setGoals(this.report.getGoals() + "\n");
					for (int i = 0; i < nofHomePlayers; i++) {
						onIce.homePlayer.GetPlayer(i).updateMatchSecond(
								Player.MatchPlayerEvent.NOTHING, 1);
					}
					for (int i = 0; i < nofAwayPlayers; i++) {
						onIce.awayPlayer.GetPlayer(i).updateMatchSecond(
								Player.MatchPlayerEvent.NOTHING, -1);
					}
					// return first player from penalty box (if any)
					if (nofHomePlayers > nofAwayPlayers
							&& penBox.awayPlayer.GetNofPlayers() > 0) {
						Player player = penBox.awayPlayer.GetPlayer(0);
						player.setPenMin(0);
						// player goes from penbox onto ice
						getEvent().bitSet.set(RETURN_FROM_PEN);
						penBox.awayPlayer.RemovePlayer(player);
						onIce.awayPlayer.InsertPlayer(player);
					}
					putOnIce(mt); // new lines
				}
			} else {
				// a save for the goalie (but only for REAL goalies!)
				if (goaliePlayer.getPosition().getPosID() == Position.PosID.GOALIE) {
					goaliePlayer.updateMatchSecond(
							Player.MatchPlayerEvent.SAVE, 0);
				}
			}
		}

		// calc shots/goals of away team
		// normalize strength
		// Calc Shots/Goals: Consider opponents strength too.
		strength = (int) ((2 * awayStrength - homeStrength) * normalizeFactor);
		tune = (advHome ? 22 : 18); // normal tune
		if (awayGamePlay == Tactics.GamePlay.OFFENSIVE) {
			tune--;
		} else if (awayGamePlay == Tactics.GamePlay.DEFENSIVE) {
			tune++;
		} else {
			assert (awayGamePlay == Tactics.GamePlay.NORMAL_GP);
		}
		if (homeGamePlay == Tactics.GamePlay.OFFENSIVE) {
			tune--;
		} else if (homeGamePlay == Tactics.GamePlay.DEFENSIVE) {
			tune++;
		} else {
			assert (homeGamePlay == Tactics.GamePlay.NORMAL_GP);
		}
		if (awayEffort == Tactics.Effort.FULL) {
			tune--;
		} else if (awayEffort == Tactics.Effort.EASY) {
			tune++;
		} else {
			assert (awayEffort == Tactics.Effort.NORMAL_EFF);
		}
		if (homeEffort == Tactics.Effort.FULL) {
			tune++;
		} else if (homeEffort == Tactics.Effort.EASY) {
			tune--;
		} else {
			assert (homeEffort == Tactics.Effort.NORMAL_EFF);
		}
		ranRange = 3 * tune + 50 - strength;
		if (!homeWasShooting && Util.random(ranRange > 0 ? ranRange : 1) == 0
				&& nofAwayPlayers > 1) {
			setAwayShots(getAwayShots() + 1);
			this.report.setAwayShots(this.report.getAwayShots() + 1);
			Player goaliePlayer = onIce.homePlayer.GetPlayer(0);
			if (goaliePlayer == null
					|| Util.random((goaliePlayer.getTotalStrength() + 50) / 20) == 0
					|| goaliePlayer.getPosition().getPosID() != Position.PosID.GOALIE
			// goalie replaced by field player
			) {
				// goalie could not save the shot
				currAwayScore++;
				getEvent().bitSet.set(GOAL_AWAY);
				// goal
				if (shootout) {
					// shoot out after overtime
					// calculation should be improved ...
					// NYI
					this.report.setGoals(this.report.getGoals()
							+ away.getTeamName());
					this.report.setGoals(this.report.getGoals()
							+ Util.getModelResourceBundle().getString("L_WINS_SHOOTOUT"));
					this.report.setGoals(this.report.getGoals() + "\n");
				} else {
					Player goalPlayer = this.onIce.awayPlayer.GetPlayer(Util
							.random(nofAwayPlayers - 1) + 1);
					if (goalPlayer.getPosition().getPosID() == Position.PosID.DEFENDER
							|| goalPlayer.getPosition().getPosID() == Position.PosID.GOALIE
							|| goalPlayer.getTotalStrength() < strength) {
						// calc again to give forwards and strong players a
						// greater chance
						goalPlayer = this.onIce.awayPlayer.GetPlayer(Util
								.random(nofAwayPlayers - 1) + 1);
					}
					goalPlayer.updateMatchSecond(
							Player.MatchPlayerEvent.GOAL_SHOT, 0); // goal
					// scorer
					// a goal against the goalie (but only for REAL goalies!)
					if (goaliePlayer != null
							&& goaliePlayer.getPosition().getPosID() == Position.PosID.GOALIE) {
						goaliePlayer.updateMatchSecond(
								Player.MatchPlayerEvent.GOAL_RECEIVED, 0); // goalie
						// received
					}
					this.report.setGoals(this.report.getGoals()
							+ Integer.toString(mt.getMin() + 1));
					this.report.setGoals(this.report.getGoals() + ". ");
					this.report.setGoals(this.report.getGoals()
							+ Integer.toString(currHomeScore));
					this.report.setGoals(this.report.getGoals() + ":");
					this.report.setGoals(this.report.getGoals()
							+ Integer.toString(currAwayScore));
					this.report.setGoals(this.report.getGoals() + " ");
					if (goalPlayer.isMultipleName()) {
						this.report.setGoals(this.report.getGoals()
								+ goalPlayer.getFirstName());
						this.report.setGoals(this.report.getGoals()
								+ " ");
					}
					this.report.setGoals(this.report.getGoals()
							+ goalPlayer.getLastName());
					// assist
					Player assistPlayer = this.onIce.awayPlayer.GetPlayer(Util
							.random(nofAwayPlayers - 1) + 1);
					if (assistPlayer.getPosition().getPosID() == Position.PosID.DEFENDER
							|| assistPlayer.getPosition().getPosID() == Position.PosID.GOALIE) {
						// more points for forwards
						assistPlayer = this.onIce.awayPlayer.GetPlayer(Util
								.random(nofAwayPlayers - 1) + 1);
					}
					if (assistPlayer != goalPlayer) {
						assistPlayer.updateMatchSecond(
								Player.MatchPlayerEvent.ASSIST, 0);
						this.report.setGoals(this.report.getGoals()
								+ " (");
						if (assistPlayer.isMultipleName()) {
							this.report.setGoals(this.report.getGoals()
									+ assistPlayer.getFirstName());
							this.report.setGoals(this.report.getGoals()
									+ " ");
						}
						this.report.setGoals(this.report.getGoals()
								+ assistPlayer.getLastName());
						Player assistPlayer2 = this.onIce.awayPlayer
								.GetPlayer(Util.random(nofAwayPlayers - 1) + 1);
						if (assistPlayer2 != goalPlayer
								&& assistPlayer2 != assistPlayer) {
							// 2nd assist
							assistPlayer2.updateMatchSecond(
									Player.MatchPlayerEvent.ASSIST, 0);
							this.report.setGoals(this.report.getGoals()
									+ ", ");
							if (assistPlayer2.isMultipleName()) {
								this.report.setGoals(this.report
										.getGoals() + assistPlayer2
												.getFirstName());
								this.report.setGoals(this.report
										.getGoals() + " ");
							}
							this.report.setGoals(this.report.getGoals()
									+ assistPlayer2.getLastName());
						}
						this.report.setGoals(this.report.getGoals()
								+ ")");
					}
					if (nofHomePlayers < nofAwayPlayers) {
						this.report.setGoals(this.report.getGoals()
								+ " PP");
					} else if (nofHomePlayers > nofAwayPlayers) {
						this.report.setGoals(this.report.getGoals()
								+ " SH");
					}
					if (goaliePlayer.getPosition().getPosID() != Position.PosID.GOALIE) {
						this.report.setGoals(this.report.getGoals()
								+ " EN"); // empty net info
					} else if (onIce.awayPlayer.GetPlayer(0).getPosition()
							.getPosID() != Position.PosID.GOALIE) {
						this.report.setGoals(this.report.getGoals()
								+ " WG"); // without goalie
					} else if (assistPlayer == goalPlayer
							&& Util.random(50) == 25) {
						this.report.setGoals(this.report.getGoals()
								+ " PEN"); // penalty shot
					}
					this.report.setGoals(this.report.getGoals() + "\n");
					for (int i = 0; i < nofAwayPlayers; i++) {
						onIce.awayPlayer.GetPlayer(i).updateMatchSecond(
								Player.MatchPlayerEvent.NOTHING, 1);
					}
					for (int i = 0; i < nofHomePlayers; i++) {
						onIce.homePlayer.GetPlayer(i).updateMatchSecond(
								Player.MatchPlayerEvent.NOTHING, -1);
					}
					// return first player from penalty box (if any)
					if (nofHomePlayers < nofAwayPlayers
							&& penBox.homePlayer.GetNofPlayers() > 0) {
						Player player = penBox.homePlayer.GetPlayer(0);
						player.setPenMin(0);
						// player goes from penbox onto ice
						getEvent().bitSet.set(RETURN_FROM_PEN);
						penBox.homePlayer.RemovePlayer(player);
						onIce.homePlayer.InsertPlayer(player);
					}
					putOnIce(mt); // new lines
				}
			} else {
				// a save for the goalie (but only for REAL goalies!)
				if (goaliePlayer.getPosition().getPosID() == Position.PosID.GOALIE) {
					goaliePlayer.updateMatchSecond(
							Player.MatchPlayerEvent.SAVE, 0);
				}
			}
		}
	}

	void updateReport(MatchTime mt) {
		// update match report
		// update reports break scores
		if (mt.getTotalSec() == 1199) {
			this.report.setThird1HomeScore(currHomeScore);
			this.report.setThird1AwayScore(currAwayScore);
		}
		if (mt.getTotalSec() == 2399) {
			this.report.setThird2HomeScore(currHomeScore
					- this.report.getThird1HomeScore());
			this.report.setThird2AwayScore(currAwayScore
					- this.report.getThird1AwayScore());
		}
		if (mt.getTotalSec() == 3599) {
			this.report.setThird3HomeScore(currHomeScore
					- this.report.getThird1HomeScore() - this.report.getThird2HomeScore());
			this.report.setThird3AwayScore(currAwayScore
					- this.report.getThird1AwayScore() - this.report.getThird2AwayScore());
		}
	}

	public boolean overTime() {
		return this.report.isOvertime();
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public int getHomeShots() {
		return homeShots;
	}

	public void setHomeShots(int homeShots) {
		this.homeShots = homeShots;
	}

	public int getAwayShots() {
		return awayShots;
	}

	public void setAwayShots(int awayShots) {
		this.awayShots = awayShots;
	}
}
