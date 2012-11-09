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

package ch.hockman.model;

import ch.hockman.model.common.Util;
import ch.hockman.model.match.Match;
import ch.hockman.model.match.MatchTime;
import ch.hockman.model.match.News;
import ch.hockman.model.match.PlayingMatch;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * The schedule of a season.
 *
 * @author Albin
 *
 */
public class Schedule {

	static public class Result {
		public Team homeTeam;
		public Team awayTeam;
		public int homeScore;
		public int awayScore;
		public PlayingMatch pm;
		public boolean overTime;
	}

	static class RegularSchedule {
		RegularSchedule(int nofRounds, int nofTeams, int nofDiv) {
			rounds = new Round[nofRounds];
			for (int i = 0; i < nofRounds; i++) {
				rounds[i] = new Round();
				rounds[i]
						.init(((nofTeams % 2 == 0) ? nofTeams : (nofTeams + 1))
								* nofDiv / 2);
			}
		}

		Round[] rounds;
	};

	static class PlayoffSchedule {
		PlayoffSchedule(int nofPlayoffFinals, int gamesPerFinal) {
			rounds = new Round[nofPlayoffFinals * gamesPerFinal];
			for (int i = 0; i < nofPlayoffFinals * gamesPerFinal; i++) {
				rounds[i] = new Round();
				rounds[i].init((int) Math.pow(2, nofPlayoffFinals - 1));
			}
		}

		Round[] rounds;
	};

	static public class TRound {
		static public class TGamePair {
			public int home;
			public int away;
		};

		TGamePair[] gamePair;

		void init(int nofGamesPerRound) {
			gamePair = new TGamePair[nofGamesPerRound];
			for (int i = 0; i < nofGamesPerRound; i++) {
				gamePair[i] = new TGamePair();
			}
		}

		void deInit() {
			gamePair = null;
		}
	};

	static public class TClub {
		private boolean[] playedagainst;
		private boolean[] playing;
		private int currplayedagainst;

		void init(int nofTeams, int nofRounds) {
			currplayedagainst = -1;
			playing = new boolean[nofRounds];

			for (int round = 0; round < nofRounds; round++) {
				playing[round] = false;
			}
			playedagainst = new boolean[nofTeams];
			for (int team = 0; team < nofTeams; team++) {
				playedagainst[team] = false;
			}
		}

		void deInit() {
			playing = null;
			playedagainst = null;
		}
	};

	private TRound[] rounds;
	private TClub[] club;

	private RegularSchedule rs;
	private PlayoffSchedule ps;
	// private boolean created;
	final int m_nofTeams; // number of teams in a division
	final int m_nofRounds;
	final int m_nofDiv;
	final boolean m_acrossDiv;
	final int m_ownDiv;
	final int m_ownConf;
	final int m_otherConf;
	final int m_nofPlayoffFinals;
	final int m_gamesPerFinal;
	final boolean m_acrossDivFinals;

	public Schedule(Modus modus) {
		// create schedule for the according modus
		rs = new RegularSchedule(modus.getNofRounds(), modus.getNofTeams(),
				modus.getNofDivisions());
		ps = new PlayoffSchedule(modus.getNofPlayoffFinals(), modus.getGamesPerFinal());
		m_nofTeams = modus.getNofTeams() % 2 != 0 ? modus.getNofTeams() + 1
				: modus.getNofTeams();
		m_nofRounds = modus.getNofRounds();
		m_nofDiv = modus.getNofDivisions();
		m_acrossDiv = modus.nofMatchInOwnConf > 0
				|| modus.nofMatchInOtherConf > 0;
		m_ownDiv = modus.nofMatchInOwnDiv;
		m_ownConf = modus.nofMatchInOwnConf;
		m_otherConf = modus.nofMatchInOtherConf;
		m_nofPlayoffFinals = modus.getNofPlayoffFinals();
		m_gamesPerFinal = modus.getGamesPerFinal();
		m_acrossDivFinals = modus.acrossDivFinals;
		// create schedule for the according modus, alloc Match instances
		// created= false;
		assert (modus.nofMatchInOwnDiv + modus.nofMatchInOwnDiv
				/ modus.getNofTeams() >= modus.nofMatchInOwnConf);
		assert (2 * modus.nofMatchInOwnConf >= modus.nofMatchInOtherConf);
	}

	void load(GameLeagueFile file, TeamPtrDivVector tpdv) {
		int nofMatchs;
		if ((m_nofTeams % 2) == 0) {
			nofMatchs = m_nofDiv * m_nofTeams / 2;
		} else {
			nofMatchs = m_nofDiv * (m_nofTeams + 1) / 2;
		}
		file.parseSurroundingStartElement("Schedule");
		for (int i = 0; i < m_nofRounds; i++) {
			file.parseSurroundingStartElement("SeasonRound");			
			for (int match = 0; match < nofMatchs; match++) {
				file.parseSurroundingStartElement("SeasonMatch");				
				Team home;
				Team away;
				int endHomeScore, endAwayScore;
				home = tpdv.getIdTeam(file.getInt("HomeId"));
				away = tpdv.getIdTeam(file.getInt("AwayId"));
				endHomeScore = file.getInt("EndHomeScore");
				endAwayScore = file.getInt("EndAwayScore");
				this.rs.rounds[i].getMatches()[match].init(home, away, endHomeScore,
						endAwayScore);
				file.parseSurroundingEndElement();
			}
			file.parseSurroundingEndElement();			
		}
		for (int i = 0; i < m_nofPlayoffFinals * m_gamesPerFinal; i++) {
			file.parseSurroundingStartElement("PlayoffRound");			
			for (int match = 0; match < Math.pow(2, m_nofPlayoffFinals - 1); match++) {
				file.parseSurroundingStartElement("PlayoffMatch");				
				Team home;
				Team away;
				int endHomeScore, endAwayScore;
				home = tpdv.getIdTeam(file.getInt("HomeId"));
				away = tpdv.getIdTeam(file.getInt("AwayId"));
				endHomeScore = file.getInt("EndHomeScore");
				endAwayScore = file.getInt("EndAwayScore");
				this.ps.rounds[i].getMatches()[match].init(home, away, endHomeScore,
						endAwayScore);
				file.parseSurroundingEndElement();				
			}
			file.parseSurroundingEndElement();			
		}
		file.parseSurroundingEndElement();		
	}

	void save(GameLeagueFile xmlFile) {
		int nofMatchs;
		if ((m_nofTeams % 2) == 0) {
			nofMatchs = m_nofDiv * m_nofTeams / 2;
		} else {
			nofMatchs = m_nofDiv * (m_nofTeams + 1) / 2;
		}
		xmlFile.writeSurroundingStartElement("Schedule");
		for (int i = 0; i < m_nofRounds; i++) {
			xmlFile.writeSurroundingStartElement("SeasonRound");
			for (int match = 0; match < nofMatchs; match++) {
				xmlFile.writeSurroundingStartElement("SeasonMatch");
				Match m = this.rs.rounds[i].getMatches()[match];
				Team home = m.getResultHome();
				Team away = m.getResultAway();
				int endHomeScore = m.getResultEndHomeScore();
				int endAwayScore = m.getResultEndAwayScore();
				if (home == null) {
					xmlFile.write("HomeId", -1);
				} else {
					xmlFile.write("HomeId", home.getId());
				}
				if (away == null) {
					xmlFile.write("AwayId", -1);
				} else {
					xmlFile.write("AwayId", away.getId());
				}
				xmlFile.write("EndHomeScore", endHomeScore);
				xmlFile.write("EndAwayScore", endAwayScore);
				xmlFile.writeSurroundingEndElement();
			}
			xmlFile.writeSurroundingEndElement();
		}
		for (int i = 0; i < m_nofPlayoffFinals * m_gamesPerFinal; i++) {
			xmlFile.writeSurroundingStartElement("PlayoffRound");
			for (int match = 0; match < Math.pow(2, m_nofPlayoffFinals - 1); match++) {
				xmlFile.writeSurroundingStartElement("PlayoffMatch");
				Match m = this.ps.rounds[i].getMatches()[match];
				Team home = m.getResultHome();
				Team away = m.getResultAway();
				int endHomeScore = m.getResultEndHomeScore();
				int endAwayScore = m.getResultEndAwayScore();
				if (home == null) {
					xmlFile.write("HomeId", -1);
				} else {
					xmlFile.write("HomeId", home.getId());
				}
				if (away == null) {
					xmlFile.write("AwayId", -1);
				} else {
					xmlFile.write("AwayId", away.getId());
				}
				xmlFile.write("EndHomeScore", endHomeScore);
				xmlFile.write("EndAwayScore", endAwayScore);
				xmlFile.writeSurroundingEndElement();
			}
			xmlFile.writeSurroundingEndElement();
		}
		xmlFile.writeSurroundingEndElement(); // Schedule
	}

	void create(TeamPtrDivVector tpdv) {
		// calculates a new schedule (at new season)
		// created= true;
		club = new TClub[m_nofTeams];
		for (int j = 0; j < m_nofTeams; j++) {
			assert (club.length > j);
			club[j] = new TClub();
			club[j].init(m_nofTeams, m_nofRounds); // cannot do it in Ctor of
			// TClub
		}
		rounds = new TRound[m_nofRounds];
		for (int j = 0; j < m_nofRounds; j++) {
			assert (rounds.length > j);
			rounds[j] = new TRound();
			rounds[j].init(((m_nofTeams % 2 == 0) ? m_nofTeams
					: (m_nofTeams + 1)) / 2 * m_nofDiv); // cannot do it in Ctor
			// of TRound
		}
		for (int round = 0; round < ((m_nofTeams % 2 == 0) ? m_nofTeams - 1
				: m_nofTeams); round++) {
			if (!calculatePrototype(0, round)) {
				assert (false);
			}
		}
		calculateWhole();
		// now the schedule is in "rounds"

		// randomize schedule
		int[] rnd = new int[m_nofTeams];
		// randomize();
		for (int i = 0; i < m_nofTeams; i++) {
			boolean again;
			do {
				again = false;
				rnd[i] = Util.random(m_nofTeams);
				for (int a = 0; a < i; a++) {
					if (rnd[a] == rnd[i])
						again = true;
				}
			} while (again);
		}

		// Copy it to match/rounds
		for (int j = 0; j < m_nofRounds; j++) {
			for (int match = 0; match < m_nofTeams * m_nofDiv / 2; match++) {
				int modHome = rounds[j].gamePair[match].home % m_nofTeams;
				int modAway = rounds[j].gamePair[match].away % m_nofTeams;
				int noHome = rounds[j].gamePair[match].home - modHome
						+ rnd[modHome];
				int noAway = rounds[j].gamePair[match].away - modAway
						+ rnd[modAway];
				int divHome = noHome / m_nofTeams + 1;
				int divAway = noAway / m_nofTeams + 1;
				int teamHome = noHome % m_nofTeams;
				int teamAway = noAway % m_nofTeams;
				Team home = null;
				Team away = null;
				if (tpdv.getNofTeams(divHome) > teamHome) {
					home = tpdv.getTeam(divHome, teamHome);
				}
				if (tpdv.getNofTeams(divAway) > teamAway) {
					away = tpdv.getTeam(divAway, teamAway);
				}
				rs.rounds[j].getMatches()[match].init(home, away);
			}
		}
	}

	public int beginRound(int round, News news, Modus modus) {
		// returns number of matches
		// returns number of matches
		assert (round > 0 && round <= m_nofRounds + m_nofPlayoffFinals
				* m_gamesPerFinal);
		if (round <= m_nofRounds) {
			return rs.rounds[round - 1].beginRound(news, modus, false);
		} else {
			return ps.rounds[round - 1 - m_nofRounds].beginRound(news, modus,
					true);
		}
	}

	public boolean doRoundSecond(int round, News news, int nofMatches,
			MatchTime mt) {
		// returns done, if the match can be finished
		// returns done, if the match can be finished
		assert (round > 0 && round <= m_nofRounds + m_nofPlayoffFinals
				* m_gamesPerFinal);
		if (round <= m_nofRounds) {
			return rs.rounds[round - 1].doRoundSecond(news, nofMatches, mt,
					false);
		} else {
			return ps.rounds[round - 1 - m_nofRounds].doRoundSecond(news,
					nofMatches, mt, true);
		}
	}

	public void endRound(int round, News news, int nofMatches, boolean twoPoints) {
		assert (round > 0 && round <= m_nofRounds + m_nofPlayoffFinals
				* m_gamesPerFinal);
		if (round <= m_nofRounds) {
			rs.rounds[round - 1].endRound(news, nofMatches, false, twoPoints);
		} else {
			ps.rounds[round - 1 - m_nofRounds].endRound(news, nofMatches, true,
					twoPoints);
		}
	}

	public void createNextPlayOffFinals(TeamPtrDivVector tpdv, int playoffRound) {
		// Note: one playoffRound is one PlayoffMatch !
		assert (playoffRound > 0 && playoffRound <= m_nofPlayoffFinals
				* m_gamesPerFinal);

		if (((playoffRound - 1) % m_gamesPerFinal) == 0) {
			// begin of new playoff series
			tpdv.sortByTeamRank();
			int finalNo = 1 + (playoffRound - 1) / m_gamesPerFinal;
			final int nofPlayoffTeamsPerDiv = (int) (Math.pow(2,
					m_nofPlayoffFinals) / m_nofDiv);
			final int nofMatchPerDiv = (int) (nofPlayoffTeamsPerDiv / 2 / Math
					.pow(2, finalNo - 1));
			for (int i = playoffRound - 1; i < playoffRound - 1
					+ m_gamesPerFinal; i++) {
				for (int match = 0; match < Math.pow(2, m_nofPlayoffFinals - 1); match++) {
					Team home = null;
					Team away = null;
					switch (m_nofDiv) {
						case 1:
							if (match < nofMatchPerDiv) {
								home = tpdv.getPlayoffTeam(1, match);
								away = tpdv.getPlayoffTeam(1, 2 * nofMatchPerDiv - 1
										- match);
							} else {
								home = null;
								away = null;
							}
							break;
						case 2:
							if (match < 2 * nofMatchPerDiv) {
								if (match < nofMatchPerDiv) {
									// 1st div
									home = tpdv.getPlayoffTeam(1, match);
									away = tpdv.getPlayoffTeam(m_acrossDivFinals ? 2 : 1,
											2 * nofMatchPerDiv - 1 - match);
								} else {
									// 2nd div
									home = tpdv.getPlayoffTeam(2, match - nofMatchPerDiv);
									away = tpdv.getPlayoffTeam(m_acrossDivFinals ? 1 : 2,
											3 * nofMatchPerDiv - 1 - match);
								}
							} else if (finalNo == m_nofPlayoffFinals && match == 0) {
								// final: winner of 1st div against winner of 2nd
								// div
								home = tpdv.getTeam(1, 0);
								away = tpdv.getTeam(2, 0);
							} else {
								home = null;
								away = null;
							}
							break;
						case 4:
							if (match < 4 * nofMatchPerDiv) {
								if (match < nofMatchPerDiv) {
									// 1st div
									home = tpdv.getPlayoffTeam(1, match);
									away = tpdv.getPlayoffTeam(m_acrossDivFinals ? 2 : 1,
											2 * nofMatchPerDiv - 1 - match);
								} else if (match < 2 * nofMatchPerDiv) {
									// 2nd div
									home = tpdv.getPlayoffTeam(2, match - nofMatchPerDiv);
									away = tpdv.getPlayoffTeam(m_acrossDivFinals ? 1 : 2,
											3 * nofMatchPerDiv - 1 - match);
								} else if (match < 3 * nofMatchPerDiv) {
									// 3rd div
									home = tpdv.getPlayoffTeam(3, match - 2
											* nofMatchPerDiv);
									away = tpdv.getPlayoffTeam(m_acrossDivFinals ? 4 : 3,
											4 * nofMatchPerDiv - 1 - match);
								} else {
									// 4th div
									home = tpdv.getPlayoffTeam(4, match - 3
											* nofMatchPerDiv);
									away = tpdv.getPlayoffTeam(m_acrossDivFinals ? 3 : 4,
											5 * nofMatchPerDiv - 1 - match);
								}
							} else if (finalNo == m_nofPlayoffFinals && match == 0) {
								// final: winner of 1st div against winner of 2nd
								// div
								home = tpdv.getTeam(1, 0);
								away = tpdv.getTeam(3, 0);
							} else if (finalNo == m_nofPlayoffFinals - 1
									&& (match == 0 || match == 1)) {
								// semifinals: winners of divisions
								home = tpdv.getTeam(1 + 2 * match, 0);
								away = tpdv.getTeam(2 + 2 * match, 0);
							} else {
								home = null;
								away = null;
							}
							break;
						default:
							assert (false);
					}
					if (home != null && away != null) {
						home = getCorrectTeam(home);
						away = getCorrectTeam(away);
						Statistics homeStatistics = home.getStatistics();
						Statistics awayStatistics = away.getStatistics();
						homeStatistics.playoffGameWins = 0;
						awayStatistics.playoffGameWins = 0;
						homeStatistics.setPlayoffBeatenBy(away);
						awayStatistics.setPlayoffBeatenBy(home);
						homeStatistics.setInPlayoff(true);
						awayStatistics.setInPlayoff(true);
						if (homeStatistics.getRank() > awayStatistics.getRank()
								&& ((i % m_gamesPerFinal) % 2) == 0
								|| homeStatistics.getRank() <= awayStatistics
										.getRank()
								&& ((i % m_gamesPerFinal) % 2) != 0) {
							// swap home/away
							Team swap = home;
							home = away;
							away = swap;
						}
					}
					ps.rounds[i].getMatches()[match].init(home, away);
				}
			}
		} else {
			// during playoff, check if a team already qualified
			for (int match = 0; match < Math.pow(2, m_nofPlayoffFinals - 1); match++) {
				Team home, away;
				Match m = ps.rounds[playoffRound - 1].getMatches()[match];
				home = m.getResultHome();
				away = m.getResultAway();

				if (home != null
						&& away != null
						&& (home.getStatistics().getPlayoffBeatenBy() == null || away
								.getStatistics().getPlayoffBeatenBy() == null)) {
					for (int i = playoffRound - 1; i < playoffRound - 1
							+ m_gamesPerFinal; i++) {
						if (i < m_nofPlayoffFinals * m_gamesPerFinal) {
							ps.rounds[i].getMatches()[match].init(null, null);
						}
					}
				}
			}
		}

	}

	public Result getResult(int round, int match) {
		assert (round > 0); // round: 1..n, match: 0..n
		assert (round <= m_nofRounds + m_nofPlayoffFinals * m_gamesPerFinal && match < m_nofTeams
				* m_nofDiv / 2);
		Match m;
		if (round <= m_nofRounds) {
			m = rs.rounds[round - 1].getMatches()[match];
		} else {
			m = ps.rounds[round - 1 - m_nofRounds].getMatches()[match];
		}
		Result res = new Result();
		res.homeTeam = m.getResultHome();
		res.homeScore = m.getResultEndHomeScore();
		res.awayTeam = m.getResultAway();
		res.awayScore = m.getResultEndAwayScore();
		res.overTime = m.getOverTime();
		res.pm = m.getPlayingMatch();
		return res;
	}

	public Team getOpTeam(int round, Team team) {
		Team homeTeam, awayTeam;
		if (round <= m_nofRounds) {
			// regular
			for (int match = 0; match < ((m_nofTeams % 2 == 0) ? m_nofTeams
					: (m_nofTeams + 1)) * m_nofDiv / 2; match++) {
				Match m = rs.rounds[round - 1].getMatches()[match];
				homeTeam = m.getResultHome();
				awayTeam = m.getResultAway();
				if (team.equals(homeTeam)) {
					if (awayTeam != null) {
						team.setHome(true);
					}
					return awayTeam;
				}
				if (team.equals(awayTeam)) {
					if (homeTeam != null) {
						team.setHome(false);
						return homeTeam;
					}
				}
			}
		} else {
			// playoff
			for (int match = 0; match < Math.pow(2, m_nofPlayoffFinals - 1); match++) {
				Match theMatch = ps.rounds[round - 1 - m_nofRounds].getMatches()[match];
				homeTeam = theMatch.getResultHome();
				awayTeam = theMatch.getResultAway();
				if (team.equals(homeTeam)) {
					team.setHome(true);
					return awayTeam;
				}
				if (team.equals(awayTeam)) {
					team.setHome(false);
					return homeTeam;
				}
			}
		}
		return null;
	}

	private Team getCorrectTeam(Team team) {
		// needed for playoff schedule generation over multiple divisions:
		// get the team who has won over the given team.
		while (team.getStatistics().getPlayoffBeatenBy() != null) {
			if (team.getStatistics().getPlayoffBeatenBy().getStatistics().getPlayoffBeatenBy() != team) {
				team = team.getStatistics().getPlayoffBeatenBy();
			} else {
				break;
			}
		}
		return team;
	}

	boolean playedHome(int round, int teamNo) {
		// needed for regular season schedule calculating
		if (round < 0) {
			round += m_nofTeams - 1;
			return playedAway(round, teamNo);
		}
		for (int match = 0; match < m_nofTeams / 2; match++) {
			assert (this.rounds.length > round);
			assert (this.rounds[round].gamePair.length > match);
			if (this.rounds[round].gamePair[match].home == teamNo) {
				return true;
			}
		}
		return false;
	}

	boolean playedAway(int round, int teamNo) {
		// needed for regular season schedule calculating
		if (round < 0) {
			round += m_nofTeams - 1;
			return playedHome(round, teamNo);
		}
		for (int match = 0; match < m_nofTeams / 2; match++) {
			assert (this.rounds.length > round);
			assert (this.rounds[round].gamePair.length > match);
			if (this.rounds[round].gamePair[match].away == teamNo) {
				return true;
			}
		}
		return false;
	}

	boolean calculatePrototype(int match, int round) {
		// needed for regular season schedule calculating
		int i = 0;
		int attempt = 0;
		int ok = 0;

		while (i < m_nofTeams) {
			assert (club.length > i);
			if (!club[i].playing[round]) {
				int j = 0;
				while (j < m_nofTeams) {
					assert (club.length > i);
					assert (club.length > j);
					assert (club[j].playedagainst.length > i);
					assert (club[i].playedagainst.length > j);
					assert (club[j].playing.length > round);
					assert (club[i].playing.length > round);
					if (club[j] != club[i] && !club[j].playedagainst[i]
							&& club[j].currplayedagainst != (int) i
							&& club[i].currplayedagainst != (int) j
							&& !club[j].playing[round]) {
						if (ok > 0) {
							ok--;
						} else {
							club[j].currplayedagainst = i;
							club[i].currplayedagainst = j;
							club[i].playing[round] = true;
							club[j].playing[round] = true;
							if (match + 1 < m_nofTeams / 2) {
								if (!calculatePrototype(match + 1, round)) {
									club[j].currplayedagainst = -1;
									club[i].currplayedagainst = -1;
									club[i].playing[round] = false;
									club[j].playing[round] = false;
									attempt++;
									ok = attempt;
									i = -1;
									break;
								} else {
									return true;
								}
							} else {
								int matchno = 0;
								for (int j1 = 0; j1 < m_nofTeams; j1++) {
									assert (club[j1].playedagainst.length > club[j1].currplayedagainst);
									assert (club[club[j1].currplayedagainst].playedagainst.length > j1);
									assert (club[j1].playing[round]);
									assert (club[j1].currplayedagainst >= 0);
									if (!club[j1].playedagainst[club[j1].currplayedagainst]) {
										club[j1].playedagainst[club[j1].currplayedagainst] = true;
										club[club[j1].currplayedagainst].playedagainst[j1] = true;
										setPair(round, matchno, j1,
												club[j1].currplayedagainst);
										club[j1].currplayedagainst = -1;
										// TODO why was this line in the old c++ version of hockeymanager?
										// the index is -1! this could not have worked!
										// 			club[club[j1].currplayedagainst].currplayedagainst=-1;
										matchno++;
									}
								}
								return true;
							}
						}
					}
					j++;
				} // while j
			}
			i++;
		} // while i
		return false;
	}

	void calculateWhole() {
		// schedule generated for rounds[0 to nofTeams-1]
		// gamePair[0 to nofTeams/2]
		// now generate the rest
		int genRounds; // real rounds
		int genOwnDiv = 0; // a round having played once against all teams
		// inside div
		int genOwnConf = 0; // a round having played once against all teams
		// inside conf
		int genOtherConf = 0; // a round having played once against all teams of
		// other conf

		// fill up the first m_nofTeams-1 rounds for all divisions
		if (m_nofDiv == 2) {
			for (int round = 0; round < m_nofTeams - 1; round++) {
				for (int match = 0; match < m_nofTeams / 2; match++) {
					assert (this.rounds.length > round);
					assert (this.rounds[round].gamePair.length > match
							+ m_nofTeams / 2);
					this.rounds[round].gamePair[match + m_nofTeams / 2].home = this.rounds[round].gamePair[match].home
							+ m_nofTeams;
					this.rounds[round].gamePair[match + m_nofTeams / 2].away = this.rounds[round].gamePair[match].away
							+ m_nofTeams;
				}
			}
		} else if (m_nofDiv == 4) {
			for (int round = 0; round < m_nofTeams - 1; round++) {
				for (int match = 0; match < m_nofTeams / 2; match++) {
					assert (this.rounds.length > round);
					assert (this.rounds[round].gamePair.length > match
							+ m_nofTeams / 2);
					this.rounds[round].gamePair[match + m_nofTeams / 2].home = this.rounds[round].gamePair[match].home
							+ m_nofTeams;
					this.rounds[round].gamePair[match + m_nofTeams].home = this.rounds[round].gamePair[match].home
							+ m_nofTeams * 2;
					this.rounds[round].gamePair[match + m_nofTeams * 3 / 2].home = this.rounds[round].gamePair[match].home
							+ m_nofTeams * 3;
					this.rounds[round].gamePair[match + m_nofTeams / 2].away = this.rounds[round].gamePair[match].away
							+ m_nofTeams;
					this.rounds[round].gamePair[match + m_nofTeams].away = this.rounds[round].gamePair[match].away
							+ m_nofTeams * 2;
					this.rounds[round].gamePair[match + m_nofTeams * 3 / 2].away = this.rounds[round].gamePair[match].away
							+ m_nofTeams * 3;
				}
			}
		}

		// now we have the first round inside divisions
		genOwnDiv = 1;
		genRounds = m_nofTeams - 1;

		// back round inside divisions
		if (genOwnDiv * (m_nofTeams - 1) < m_ownDiv) {
			for (int round = genRounds; round < genRounds + m_nofTeams - 1; round++) {
				for (int match = 0; match < m_nofTeams * m_nofDiv / 2; match++) {
					assert (this.rounds.length > round);
					assert (this.rounds[round].gamePair.length > match);
					assert (this.rounds.length > round - genRounds);
					assert (this.rounds[round - genRounds].gamePair.length > match);
					this.rounds[round].gamePair[match].home = this.rounds[round
							- genRounds].gamePair[match].away;
					this.rounds[round].gamePair[match].away = this.rounds[round
							- genRounds].gamePair[match].home;
				}
			}
			genRounds += m_nofTeams - 1;
			genOwnDiv++;
		}

		// further rounds
		while (genRounds < m_nofRounds) {
			// playing across divisions
			if (m_acrossDiv) {
				if (m_nofDiv == 2) {
					if (genOwnConf * m_nofTeams < m_ownConf) {
						int times = ((genOwnConf + 1) * m_nofTeams < m_ownConf) ? 2
								: 1;
						for (int round = genRounds; round < genRounds + times
								* (m_nofTeams - 1); round++) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > round);
								assert (this.rounds[round].gamePair.length > match);
								assert (this.rounds.length > round - genRounds);
								assert (this.rounds[round - genRounds].gamePair.length > match);
								this.rounds[round].gamePair[match].home = this.rounds[round
										- genRounds].gamePair[match].home;
								int away = (this.rounds[round - genRounds].gamePair[match]).away;
								if (away < (int) m_nofTeams) {
									away += m_nofTeams;
								} else {
									away -= m_nofTeams;
								}
								this.rounds[round].gamePair[match].away = away;
							}
						}
						genRounds += times * (m_nofTeams - 1);
						for (int match = 0; match < m_nofTeams * m_nofDiv / 2; match++) {
							assert (this.rounds.length > genRounds);
							assert (this.rounds[genRounds].gamePair.length > match);
							this.rounds[genRounds].gamePair[match].home = match;
							this.rounds[genRounds].gamePair[match].away = match
									+ m_nofTeams;
						}
						genRounds++;
						if (times == 2) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > genRounds);
								assert (this.rounds[genRounds].gamePair.length > match);
								this.rounds[genRounds].gamePair[match].home = match
										+ m_nofTeams;
								this.rounds[genRounds].gamePair[match].away = match;
							}
							genRounds++;
						}
						genOwnConf += times;
					}
				} else if (m_nofDiv == 4) {
					if (genOwnConf * m_nofTeams < m_ownConf) {
						int times = ((genOwnConf + 1) * m_nofTeams < m_ownConf) ? 2
								: 1;
						for (int round = genRounds; round < genRounds + times
								* (m_nofTeams - 1); round++) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > round);
								assert (this.rounds[round].gamePair.length > match);
								assert (this.rounds.length > round - genRounds);
								assert (this.rounds[round - genRounds].gamePair.length > match);
								this.rounds[round].gamePair[match].home = this.rounds[round
										- genRounds].gamePair[match].home;
								int away = this.rounds[round - genRounds].gamePair[match].away;
								if (away < (int) m_nofTeams
										|| away >= 2 * (int) m_nofTeams
										&& away < 3 * (int) m_nofTeams) {
									away += m_nofTeams;
								} else {
									away -= m_nofTeams;
								}
								this.rounds[round].gamePair[match].away = away;
							}
						}
						genRounds += times * (m_nofTeams - 1);
						for (int match = 0; match < m_nofTeams * m_nofDiv / 2; match++) {
							assert (this.rounds.length > genRounds);
							assert (this.rounds[genRounds].gamePair.length > match);
							if (match < m_nofTeams) {
								this.rounds[genRounds].gamePair[match].home = match;
								this.rounds[genRounds].gamePair[match].away = match
										+ m_nofTeams;
							} else {
								this.rounds[genRounds].gamePair[match].home = match
										+ m_nofTeams;
								this.rounds[genRounds].gamePair[match].away = match
										+ 2 * m_nofTeams;
							}
						}
						genRounds++;
						if (times == 2) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > genRounds);
								assert (this.rounds[genRounds].gamePair.length > match);
								if (match < m_nofTeams) {
									this.rounds[genRounds].gamePair[match].home = match
											+ m_nofTeams;
									this.rounds[genRounds].gamePair[match].away = match;
								} else {
									this.rounds[genRounds].gamePair[match].home = match
											+ 2 * m_nofTeams;
									this.rounds[genRounds].gamePair[match].away = match
											+ m_nofTeams;
								}
							}
							genRounds++;
						}
						genOwnConf += times;
					}
					if (genOtherConf * (m_nofTeams * 2) < m_otherConf) {
						int times = ((genOtherConf + 1) * (m_nofTeams * 2) < m_otherConf) ? 2
								: 1;
						for (int round = genRounds; round < genRounds + times
								* (m_nofTeams - 1); round++) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > round);
								assert (this.rounds[round].gamePair.length > match);
								assert (this.rounds.length > round - genRounds);
								assert (this.rounds[round - genRounds].gamePair.length > match);
								this.rounds[round].gamePair[match].home = this.rounds[round
										- genRounds].gamePair[match].home;
								int away = this.rounds[round - genRounds].gamePair[match].away;
								if (away < 2 * (int) m_nofTeams) {
									away += 2 * m_nofTeams;
								} else {
									away -= 2 * m_nofTeams;
								}
								this.rounds[round].gamePair[match].away = away;
							}
						}
						genRounds += times * (m_nofTeams - 1);
						for (int round = genRounds; round < genRounds + times
								* (m_nofTeams - 1); round++) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > round);
								assert (this.rounds[round].gamePair.length > match);
								assert (this.rounds.length > round - genRounds);
								assert (this.rounds[round - genRounds].gamePair.length > match);
								this.rounds[round].gamePair[match].home = this.rounds[round
										- genRounds].gamePair[match].home;
								int away = this.rounds[round - genRounds].gamePair[match].away;
								if (away < (int) m_nofTeams) {
									away += 3 * m_nofTeams;
								} else if (away < 2 * (int) m_nofTeams) {
									away += m_nofTeams;
								} else if (away < 3 * (int) m_nofTeams) {
									away -= m_nofTeams;
								} else {
									away -= 3 * m_nofTeams;
								}
								this.rounds[round].gamePair[match].away = away;
							}
						}
						genRounds += times * (m_nofTeams - 1);
						for (int match = 0; match < m_nofTeams * m_nofDiv / 2; match++) {
							assert (this.rounds.length > genRounds);
							assert (this.rounds[genRounds].gamePair.length > match);
							this.rounds[genRounds].gamePair[match].home = match;
							this.rounds[genRounds].gamePair[match].away = match
									+ 2 * m_nofTeams;
						}
						genRounds++;
						if (times == 2) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > genRounds);
								assert (this.rounds[genRounds].gamePair.length > match);
								this.rounds[genRounds].gamePair[match].home = match
										+ 2 * m_nofTeams;
								this.rounds[genRounds].gamePair[match].away = match;
							}
							genRounds++;
						}
						for (int match = 0; match < m_nofTeams * m_nofDiv / 2; match++) {
							assert (this.rounds.length > genRounds);
							assert (this.rounds[genRounds].gamePair.length > match);
							if (match < m_nofTeams) {
								this.rounds[genRounds].gamePair[match].home = match;
								this.rounds[genRounds].gamePair[match].away = match
										+ 3 * m_nofTeams;
							} else {
								this.rounds[genRounds].gamePair[match].home = match;
								this.rounds[genRounds].gamePair[match].away = match
										+ m_nofTeams;
							}
						}
						genRounds++;
						if (times == 2) {
							for (int match = 0; match < m_nofTeams * m_nofDiv
									/ 2; match++) {
								assert (this.rounds.length > genRounds);
								assert (this.rounds[genRounds].gamePair.length > match);
								if (match < m_nofTeams) {
									this.rounds[genRounds].gamePair[match].home = match
											+ 3 * m_nofTeams;
									this.rounds[genRounds].gamePair[match].away = match;
								} else {
									this.rounds[genRounds].gamePair[match].home = match
											+ m_nofTeams;
									this.rounds[genRounds].gamePair[match].away = match;
								}
							}
							genRounds++;
						}
						genOtherConf += times;
					}
				}
			}
			// playing inside divisions
			// forth and back
			if (genOwnDiv * (m_nofTeams - 1) < m_ownDiv) {
				for (int round = genRounds; round < genRounds + 2
						* (m_nofTeams - 1); round++) {
					for (int match = 0; match < m_nofTeams * m_nofDiv / 2; match++) {
						assert (this.rounds.length > round);
						assert (this.rounds[round].gamePair.length > match);
						assert (this.rounds.length > round - genRounds);
						assert (this.rounds[round - genRounds].gamePair.length > match);
						this.rounds[round].gamePair[match].home = this.rounds[round
								- genRounds].gamePair[match].home;
						this.rounds[round].gamePair[match].away = this.rounds[round
								- genRounds].gamePair[match].away;
					}
				}
				genRounds += 2 * (m_nofTeams - 1);
				genOwnDiv += 2;
			}
		}

	}

	void setPair(int round, int match, int teamNoi, int teamNoj) {
		// needed for regular season schedule calculating
		assert (this.rounds.length > round);
		assert (this.rounds[round].gamePair.length > match);
		if (playedHome(round - 1, teamNoi)
				&& (playedHome(round - 2, teamNoi) || playedAway(round - 1,
						teamNoj)) || playedAway(round - 1, teamNoj)
				&& playedAway(round - 2, teamNoj)) {
			this.rounds[round].gamePair[match].home = teamNoj;
			this.rounds[round].gamePair[match].away = teamNoi;
		} else {
			this.rounds[round].gamePair[match].home = teamNoi;
			this.rounds[round].gamePair[match].away = teamNoj;
		}
	}
}
