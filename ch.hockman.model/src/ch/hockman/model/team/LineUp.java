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

package ch.hockman.model.team;

import ch.hockman.model.GameLeagueFile;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.position.Position;

/**
 * The lineup of a team.
 *
 * @author Albin
 *
 */
public class LineUp {
	public class G {
		public Player goal1;
		public Player goal2;
	}

	private G g;

	public class E55 {
		public Player d11, d12;
		public Player d21, d22;
		public Player d31, d32;
		public Player lw1, c1, rw1;
		public Player lw2, c2, rw2;
		public Player lw3, c3, rw3;
		public Player lw4, c4, rw4;
	}

	private E55 e55;

	public class E44 {
		public Player d11, d12, f11, f12;
		public Player d21, d22, f21, f22;
	}

	private E44 e44;

	public class E33 {
		public Player d11, d12, f1;
		public Player d21, d22, f2;
	}

	private E33 e33;

	public class PP5 {
		public Player d11, d12, lw1, c1, rw1;
		public Player d21, d22, lw2, c2, rw2;
	}

	private PP5 pp5;

	public class PP4 {
		public Player d11, d12, f11, f12;
		public Player d21, d22, f21, f22;
	}

	private PP4 pp4;

	public class PK4 {
		public Player d11, d12, f11, f12;
		public Player d21, d22, f21, f22;
	}

	private PK4 pk4;

	public class PK3 {
		public Player d11, d12, f1;
		public Player d21, d22, f2;
	}

	private PK3 pk3;

	public LineUp() {
		this.setE33(new LineUp.E33());
		this.setE44(new LineUp.E44());
		this.setE55(new LineUp.E55());
		this.setG(new LineUp.G());
		this.setPk3(new LineUp.PK3());
		this.setPk4(new LineUp.PK4());
		this.setPp4(new LineUp.PP4());
		this.setPp5(new LineUp.PP5());
		reset();
	}

	public void load(GameLeagueFile file, PlayerPtrVector ppv) {
		this.getG().goal1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getG().goal2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().d11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().d12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().d21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().d22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().d31 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().d32 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().lw1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().c1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().rw1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().lw2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().c2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().rw2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().lw3 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().c3 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().rw3 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().lw4 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().c4 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE55().rw4 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().d11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().d12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().f11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().f12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().d21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().d22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().f21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE44().f22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE33().d11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE33().d12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE33().f1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE33().d21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE33().d22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getE33().f2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().d11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().d12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().lw1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().c1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().rw1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().d21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().d22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().lw2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().c2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp5().rw2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().d11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().d12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().f11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().f12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().d21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().d22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().f21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPp4().f22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().d11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().d12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().f11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().f12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().d21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().d22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().f21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk4().f22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk3().d11 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk3().d12 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk3().f1 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk3().d21 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk3().d22 = ppv.GetIdPlayer(file.getInt("PlayerId"));
		this.getPk3().f2 = ppv.GetIdPlayer(file.getInt("PlayerId"));
	}

	void save(GameLeagueFile xmlFile) {
		write(xmlFile, this.getG().goal1);
		write(xmlFile, this.getG().goal2);
		write(xmlFile, this.getE55().d11);
		write(xmlFile, this.getE55().d12);
		write(xmlFile, this.getE55().d21);
		write(xmlFile, this.getE55().d22);
		write(xmlFile, this.getE55().d31);
		write(xmlFile, this.getE55().d32);
		write(xmlFile, this.getE55().lw1);
		write(xmlFile, this.getE55().c1);
		write(xmlFile, this.getE55().rw1);
		write(xmlFile, this.getE55().lw2);
		write(xmlFile, this.getE55().c2);
		write(xmlFile, this.getE55().rw2);
		write(xmlFile, this.getE55().lw3);
		write(xmlFile, this.getE55().c3);
		write(xmlFile, this.getE55().rw3);
		write(xmlFile, this.getE55().lw4);
		write(xmlFile, this.getE55().c4);
		write(xmlFile, this.getE55().rw4);
		write(xmlFile, this.getE44().d11);
		write(xmlFile, this.getE44().d12);
		write(xmlFile, this.getE44().f11);
		write(xmlFile, this.getE44().f12);
		write(xmlFile, this.getE44().d21);
		write(xmlFile, this.getE44().d22);
		write(xmlFile, this.getE44().f21);
		write(xmlFile, this.getE44().f22);
		write(xmlFile, this.getE33().d11);
		write(xmlFile, this.getE33().d12);
		write(xmlFile, this.getE33().f1);
		write(xmlFile, this.getE33().d21);
		write(xmlFile, this.getE33().d22);
		write(xmlFile, this.getE33().f2);
		write(xmlFile, this.getPp5().d11);
		write(xmlFile, this.getPp5().d12);
		write(xmlFile, this.getPp5().lw1);
		write(xmlFile, this.getPp5().c1);
		write(xmlFile, this.getPp5().rw1);
		write(xmlFile, this.getPp5().d21);
		write(xmlFile, this.getPp5().d22);
		write(xmlFile, this.getPp5().lw2);
		write(xmlFile, this.getPp5().c2);
		write(xmlFile, this.getPp5().rw2);
		write(xmlFile, this.getPp4().d11);
		write(xmlFile, this.getPp4().d12);
		write(xmlFile, this.getPp4().f11);
		write(xmlFile, this.getPp4().f12);
		write(xmlFile, this.getPp4().d21);
		write(xmlFile, this.getPp4().d22);
		write(xmlFile, this.getPp4().f21);
		write(xmlFile, this.getPp4().f22);
		write(xmlFile, this.getPk4().d11);
		write(xmlFile, this.getPk4().d12);
		write(xmlFile, this.getPk4().f11);
		write(xmlFile, this.getPk4().f12);
		write(xmlFile, this.getPk4().d21);
		write(xmlFile, this.getPk4().d22);
		write(xmlFile, this.getPk4().f21);
		write(xmlFile, this.getPk4().f22);
		write(xmlFile, this.getPk3().d11);
		write(xmlFile, this.getPk3().d12);
		write(xmlFile, this.getPk3().f1);
		write(xmlFile, this.getPk3().d21);
		write(xmlFile, this.getPk3().d22);
		write(xmlFile, this.getPk3().f2);
	}

	private void write(GameLeagueFile xmlFile, Player player) {
		if (player != null) {
			xmlFile.write("PlayerId", player.getId());
		} else {
			xmlFile.write("PlayerId", -1);
		}
	}

	void reset() {
		this.getG().goal1 = null;
		this.getG().goal2 = null;
		this.getE55().d11 = null;
		this.getE55().d12 = null;
		this.getE55().d21 = null;
		this.getE55().d22 = null;
		this.getE55().d31 = null;
		this.getE55().d32 = null;
		this.getE55().lw1 = null;
		this.getE55().c1 = null;
		this.getE55().rw1 = null;
		this.getE55().lw2 = null;
		this.getE55().c2 = null;
		this.getE55().rw2 = null;
		this.getE55().lw3 = null;
		this.getE55().c3 = null;
		this.getE55().rw3 = null;
		this.getE55().lw4 = null;
		this.getE55().c4 = null;
		this.getE55().rw4 = null;
		this.getE44().d11 = null;
		this.getE44().d12 = null;
		this.getE44().f11 = null;
		this.getE44().f12 = null;
		this.getE44().d21 = null;
		this.getE44().d22 = null;
		this.getE44().f21 = null;
		this.getE44().f22 = null;
		this.getE33().d11 = null;
		this.getE33().d12 = null;
		this.getE33().f1 = null;
		this.getE33().d21 = null;
		this.getE33().d22 = null;
		this.getE33().f2 = null;
		this.getPp5().d11 = null;
		this.getPp5().d12 = null;
		this.getPp5().lw1 = null;
		this.getPp5().c1 = null;
		this.getPp5().rw1 = null;
		this.getPp5().d21 = null;
		this.getPp5().d22 = null;
		this.getPp5().lw2 = null;
		this.getPp5().c2 = null;
		this.getPp5().rw2 = null;
		this.getPp4().d11 = null;
		this.getPp4().d12 = null;
		this.getPp4().f11 = null;
		this.getPp4().f12 = null;
		this.getPp4().d21 = null;
		this.getPp4().d22 = null;
		this.getPp4().f21 = null;
		this.getPp4().f22 = null;
		this.getPk4().d11 = null;
		this.getPk4().d12 = null;
		this.getPk4().f11 = null;
		this.getPk4().f12 = null;
		this.getPk4().d21 = null;
		this.getPk4().d22 = null;
		this.getPk4().f21 = null;
		this.getPk4().f22 = null;
		this.getPk3().d11 = null;
		this.getPk3().d12 = null;
		this.getPk3().f1 = null;
		this.getPk3().d21 = null;
		this.getPk3().d22 = null;
		this.getPk3().f2 = null;
	}

	public void farmAndFirstTeamKI(Team thisTeam, CoachAI.Analysis analysis,
			int maxNofForeigners) {
		// replace worse team players with better farm players
		int nofNonForeignFarmPlayers = 0;
		int i = 0;
		while (i < thisTeam.getNofFarmPlayers()) {
			thisTeam.sortPlayerWeakness();
			int nofGoalies = 0;
			int nofDefenders = 0;
			int nofLeftWings = 0;
			int nofCenters = 0;
			int nofRightWings = 0;
			int nofTeamPlayers = thisTeam.getNofTeamPlayers();
			for (int j = 0; j < nofTeamPlayers; j++) {
				Position.PosID pos = thisTeam.getPlayer(j).getPosition()
						.getPosID();
				if (pos == Position.PosID.GOALIE) {
					nofGoalies++;
				} else if (pos == Position.PosID.DEFENDER) {
					nofDefenders++;
				} else if (pos == Position.PosID.LEFTWING) {
					nofLeftWings++;
				} else if (pos == Position.PosID.CENTER) {
					nofCenters++;
				} else if (pos == Position.PosID.RIGHTWING) {
					nofRightWings++;
				} else {
					assert (false);
				}
			}
			Player weakestTeamPlayer = null;
			int j = 0;
			Player farmPlayer = thisTeam.getFarmPlayer(i);
			Position.PosID farmPos = farmPlayer.getPosition().getPosID();
			while (j < nofTeamPlayers) {
				weakestTeamPlayer = thisTeam.getTeamPlayer(j);
				Position.PosID weakestPos = weakestTeamPlayer.getPosition()
						.getPosID();
				if (weakestTeamPlayer.getHealth().getInjury() > 0) {
					break;
				} else if (weakestPos == Position.PosID.GOALIE
						&& farmPos != Position.PosID.GOALIE && nofGoalies < 3) {
					j++;
				} else if (weakestPos == Position.PosID.DEFENDER
						&& farmPos != Position.PosID.DEFENDER
						&& nofDefenders < 7) {
					j++;
				} else if (weakestPos == Position.PosID.LEFTWING
						&& farmPos != Position.PosID.LEFTWING
						&& nofLeftWings < 5) {
					j++;
				} else if (weakestPos == Position.PosID.CENTER
						&& farmPos != Position.PosID.CENTER && nofCenters < 5) {
					j++;
				} else if (weakestPos == Position.PosID.RIGHTWING
						&& farmPos != Position.PosID.RIGHTWING
						&& nofRightWings < 5) {
					j++;
				} else {
					break;
				}
			}
			if (j == nofTeamPlayers) {
				// too few players on all positions => take REAL weakest player
				weakestTeamPlayer = thisTeam.getTeamPlayer(0);
			}
			assert (weakestTeamPlayer != null);
			Position.PosID weakestPos = weakestTeamPlayer.getPosition()
					.getPosID();
			if (farmPlayer.getHealth().getInjury() == 0
					&& (weakestTeamPlayer.getTotalStrength() < farmPlayer
							.getTotalStrength()
							|| farmPos == Position.PosID.GOALIE
							&& weakestPos != Position.PosID.GOALIE
							&& nofGoalies < 2
							|| farmPos == Position.PosID.DEFENDER
							&& weakestPos != Position.PosID.DEFENDER
							&& nofDefenders < 6
							|| farmPos == Position.PosID.LEFTWING
							&& weakestPos != Position.PosID.LEFTWING
							&& nofLeftWings < 4
							|| farmPos == Position.PosID.CENTER
							&& weakestPos != Position.PosID.CENTER
							&& nofCenters < 4 || farmPos == Position.PosID.RIGHTWING
							&& weakestPos != Position.PosID.RIGHTWING
							&& nofRightWings < 4)) {
				// swap
				thisTeam.removeTeamPlayer(weakestTeamPlayer);
				thisTeam.removeFarmPlayer(farmPlayer);
				thisTeam.addFarmPlayer(weakestTeamPlayer);
				thisTeam.addTeamPlayer(farmPlayer);
				i = 0;
				nofNonForeignFarmPlayers = 0;
			} else {
				if (farmPlayer.getNation() == thisTeam.getNation()) {
					nofNonForeignFarmPlayers++;
				}
				i++;
			}
		}

		// check for nof foreigners
		int nofForeigners1 = 0;
		thisTeam.sortPlayerStrength();
		int nofTeamPlayers = thisTeam.getNofTeamPlayers();
		i = 0;
		while (i < nofTeamPlayers) {
			Player thisPlayer = thisTeam.getTeamPlayer(i);
			if (thisPlayer.getNation() != thisTeam.getNation()) {
				nofForeigners1++;
				if (nofForeigners1 > maxNofForeigners
						&& nofNonForeignFarmPlayers > 0) {
					Player farmPlayer;
					int j = 0;
					// replace the foreigner with a farm player of same position
					int nofFarmPlayers = thisTeam.getNofFarmPlayers();
					do {
						farmPlayer = thisTeam.getFarmPlayer(j);
						if (farmPlayer.getNation() != thisTeam.getNation()
								|| farmPlayer.getPosition() != thisPlayer
										.getPosition()) {
							j++;
						} else {
							break;
						}
					} while (j < nofFarmPlayers);
					if (j == nofFarmPlayers) {
						// there is no farm own nation player with same position
						// as the foreigner
						j = 0;
						do {
							farmPlayer = thisTeam.getFarmPlayer(j);
							j++;
						} while (farmPlayer.getNation() != thisTeam.getNation());
					}
					thisTeam.removeTeamPlayer(thisPlayer);
					thisTeam.removeFarmPlayer(farmPlayer);
					thisTeam.addFarmPlayer(thisPlayer);
					thisTeam.addTeamPlayer(farmPlayer);
					i = 0;
					nofForeigners1 = 0;
					nofNonForeignFarmPlayers--;
				} else {
					i++;
				}
			} else {
				i++;
			}
		}
	}

	void lineUpAI(Team thisTeam, CoachAI.Analysis analysis, int nofForeigners,
			int goaldiff, int second) {
		// opTeam may be null !!
		thisTeam.sortPlayerStrength();
		reset();

		// NYI
		// analysis not yet used ...

		// Goalies
		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getG().goal1 = thisTeam.getFirstGoalie();
		this.getG().goal2 = thisTeam.getNextGoalie();
		if (this.getG().goal1 == null || this.getG().goal2 == null) {
			// insert weakest players as substitute for goalies
			thisTeam.sortPlayerWeakness();
			thisTeam.initNonPlaying();
			if (this.getG().goal1 == null)
				this.getG().goal1 = thisTeam.getNextNonPlaying();
			if (this.getG().goal2 == null)
				this.getG().goal2 = thisTeam.getNextNonPlaying();
			thisTeam.sortPlayerStrength();
		}

		// even plays
		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getE55().d11 = thisTeam.getFirstDefender();
		this.getE55().d12 = thisTeam.getNextDefender();
		this.getE55().d21 = thisTeam.getNextDefender();
		this.getE55().d22 = thisTeam.getNextDefender();
		this.getE55().d31 = thisTeam.getNextDefender();
		this.getE55().d32 = thisTeam.getNextDefender();
		this.getE55().lw1 = thisTeam.getFirstLeftWing();
		this.getE55().c1 = thisTeam.getFirstCenter();
		this.getE55().rw1 = thisTeam.getFirstRightWing();
		this.getE55().lw2 = thisTeam.getNextLeftWing();
		this.getE55().c2 = thisTeam.getNextCenter();
		this.getE55().rw2 = thisTeam.getNextRightWing();
		this.getE55().lw3 = thisTeam.getNextLeftWing();
		this.getE55().c3 = thisTeam.getNextCenter();
		this.getE55().rw3 = thisTeam.getNextRightWing();
		this.getE55().lw4 = thisTeam.getNextLeftWing();
		this.getE55().c4 = thisTeam.getNextCenter();
		this.getE55().rw4 = thisTeam.getNextRightWing();
		thisTeam.initNonPlaying();
		if (this.getE55().d11 == null)
			this.getE55().d11 = thisTeam.getNextNonPlaying();
		if (this.getE55().d12 == null)
			this.getE55().d12 = thisTeam.getNextNonPlaying();
		if (this.getE55().d21 == null)
			this.getE55().d21 = thisTeam.getNextNonPlaying();
		if (this.getE55().d22 == null)
			this.getE55().d22 = thisTeam.getNextNonPlaying();
		if (this.getE55().d31 == null)
			this.getE55().d31 = thisTeam.getNextNonPlaying();
		if (this.getE55().d32 == null)
			this.getE55().d32 = thisTeam.getNextNonPlaying();
		if (this.getE55().lw1 == null)
			this.getE55().lw1 = thisTeam.getNextNonPlaying();
		if (this.getE55().c1 == null)
			this.getE55().c1 = thisTeam.getNextNonPlaying();
		if (this.getE55().rw1 == null)
			this.getE55().rw1 = thisTeam.getNextNonPlaying();
		if (this.getE55().lw2 == null)
			this.getE55().lw2 = thisTeam.getNextNonPlaying();
		if (this.getE55().c2 == null)
			this.getE55().c2 = thisTeam.getNextNonPlaying();
		if (this.getE55().rw2 == null)
			this.getE55().rw2 = thisTeam.getNextNonPlaying();
		if (this.getE55().lw3 == null)
			this.getE55().lw3 = thisTeam.getNextNonPlaying();
		if (this.getE55().c3 == null)
			this.getE55().c3 = thisTeam.getNextNonPlaying();
		if (this.getE55().rw3 == null)
			this.getE55().rw3 = thisTeam.getNextNonPlaying();
		if (this.getE55().lw4 == null)
			this.getE55().lw4 = thisTeam.getNextNonPlaying();
		if (this.getE55().c4 == null)
			this.getE55().c4 = thisTeam.getNextNonPlaying();
		if (this.getE55().rw4 == null)
			this.getE55().rw4 = thisTeam.getNextNonPlaying();

		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getE44().d11 = thisTeam.getFirstDefender();
		this.getE44().d12 = thisTeam.getNextDefender();
		this.getE44().f11 = thisTeam.getFirstCenter();
		this.getE44().f12 = thisTeam.getFirstRightWing();
		this.getE44().d21 = thisTeam.getNextDefender();
		this.getE44().d22 = thisTeam.getNextDefender();
		this.getE44().f21 = thisTeam.getFirstLeftWing();
		this.getE44().f22 = thisTeam.getNextCenter();
		thisTeam.initNonPlaying();
		if (this.getE44().d11 == null)
			this.getE44().d11 = thisTeam.getNextNonPlaying();
		if (this.getE44().d12 == null)
			this.getE44().d12 = thisTeam.getNextNonPlaying();
		if (this.getE44().f11 == null)
			this.getE44().f11 = thisTeam.getNextNonPlaying();
		if (this.getE44().f12 == null)
			this.getE44().f12 = thisTeam.getNextNonPlaying();
		if (this.getE44().d21 == null)
			this.getE44().d21 = thisTeam.getNextNonPlaying();
		if (this.getE44().d22 == null)
			this.getE44().d22 = thisTeam.getNextNonPlaying();
		if (this.getE44().f21 == null)
			this.getE44().f21 = thisTeam.getNextNonPlaying();
		if (this.getE44().f22 == null)
			this.getE44().f22 = thisTeam.getNextNonPlaying();

		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getE33().d11 = thisTeam.getFirstDefender();
		this.getE33().d12 = thisTeam.getNextDefender();
		this.getE33().f1 = thisTeam.getFirstCenter();
		this.getE33().d21 = thisTeam.getNextDefender();
		this.getE33().d22 = thisTeam.getNextDefender();
		this.getE33().f2 = thisTeam.getNextCenter();
		thisTeam.initNonPlaying();
		if (this.getE33().d11 == null)
			this.getE33().d11 = thisTeam.getNextNonPlaying();
		if (this.getE33().d12 == null)
			this.getE33().d12 = thisTeam.getNextNonPlaying();
		if (this.getE33().f1 == null)
			this.getE33().f1 = thisTeam.getNextNonPlaying();
		if (this.getE33().d21 == null)
			this.getE33().d21 = thisTeam.getNextNonPlaying();
		if (this.getE33().d22 == null)
			this.getE33().d22 = thisTeam.getNextNonPlaying();
		if (this.getE33().f2 == null)
			this.getE33().f2 = thisTeam.getNextNonPlaying();

		// power plays
		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getPp5().d11 = thisTeam.getFirstDefender();
		this.getPp5().d12 = thisTeam.getNextDefender();
		this.getPp5().lw1 = thisTeam.getFirstLeftWing();
		this.getPp5().c1 = thisTeam.getFirstCenter();
		this.getPp5().rw1 = thisTeam.getFirstRightWing();
		this.getPp5().d21 = thisTeam.getNextDefender();
		this.getPp5().d22 = thisTeam.getNextDefender();
		this.getPp5().lw2 = thisTeam.getNextLeftWing();
		this.getPp5().c2 = thisTeam.getNextCenter();
		this.getPp5().rw2 = thisTeam.getNextRightWing();
		thisTeam.initNonPlaying();
		if (this.getPp5().d11 == null)
			this.getPp5().d11 = thisTeam.getNextNonPlaying();
		if (this.getPp5().d12 == null)
			this.getPp5().d12 = thisTeam.getNextNonPlaying();
		if (this.getPp5().lw1 == null)
			this.getPp5().lw1 = thisTeam.getNextNonPlaying();
		if (this.getPp5().c1 == null)
			this.getPp5().c1 = thisTeam.getNextNonPlaying();
		if (this.getPp5().rw1 == null)
			this.getPp5().rw1 = thisTeam.getNextNonPlaying();
		if (this.getPp5().d21 == null)
			this.getPp5().d21 = thisTeam.getNextNonPlaying();
		if (this.getPp5().d22 == null)
			this.getPp5().d22 = thisTeam.getNextNonPlaying();
		if (this.getPp5().lw2 == null)
			this.getPp5().lw2 = thisTeam.getNextNonPlaying();
		if (this.getPp5().c2 == null)
			this.getPp5().c2 = thisTeam.getNextNonPlaying();
		if (this.getPp5().rw2 == null)
			this.getPp5().rw2 = thisTeam.getNextNonPlaying();

		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getPp4().d11 = thisTeam.getFirstDefender();
		this.getPp4().d12 = thisTeam.getNextDefender();
		this.getPp4().f11 = thisTeam.getFirstCenter();
		this.getPp4().f12 = thisTeam.getFirstRightWing();
		this.getPp4().d21 = thisTeam.getNextDefender();
		this.getPp4().d22 = thisTeam.getNextDefender();
		this.getPp4().f21 = thisTeam.getFirstLeftWing();
		this.getPp4().f22 = thisTeam.getNextCenter();
		thisTeam.initNonPlaying();
		if (this.getPp4().d11 == null)
			this.getPp4().d11 = thisTeam.getNextNonPlaying();
		if (this.getPp4().d12 == null)
			this.getPp4().d12 = thisTeam.getNextNonPlaying();
		if (this.getPp4().f11 == null)
			this.getPp4().f11 = thisTeam.getNextNonPlaying();
		if (this.getPp4().f12 == null)
			this.getPp4().f12 = thisTeam.getNextNonPlaying();
		if (this.getPp4().d21 == null)
			this.getPp4().d21 = thisTeam.getNextNonPlaying();
		if (this.getPp4().d22 == null)
			this.getPp4().d22 = thisTeam.getNextNonPlaying();
		if (this.getPp4().f21 == null)
			this.getPp4().f21 = thisTeam.getNextNonPlaying();
		if (this.getPp4().f22 == null)
			this.getPp4().f22 = thisTeam.getNextNonPlaying();

		// penalty killing
		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getPk4().d11 = thisTeam.getFirstDefender();
		this.getPk4().d12 = thisTeam.getNextDefender();
		this.getPk4().f11 = thisTeam.getFirstCenter();
		this.getPk4().f12 = thisTeam.getFirstRightWing();
		this.getPk4().d21 = thisTeam.getNextDefender();
		this.getPk4().d22 = thisTeam.getNextDefender();
		this.getPk4().f21 = thisTeam.getFirstLeftWing();
		this.getPk4().f22 = thisTeam.getNextCenter();
		thisTeam.initNonPlaying();
		if (this.getPk4().d11 == null)
			this.getPk4().d11 = thisTeam.getNextNonPlaying();
		if (this.getPk4().d12 == null)
			this.getPk4().d12 = thisTeam.getNextNonPlaying();
		if (this.getPk4().f11 == null)
			this.getPk4().f11 = thisTeam.getNextNonPlaying();
		if (this.getPk4().f12 == null)
			this.getPk4().f12 = thisTeam.getNextNonPlaying();
		if (this.getPk4().d21 == null)
			this.getPk4().d21 = thisTeam.getNextNonPlaying();
		if (this.getPk4().d22 == null)
			this.getPk4().d22 = thisTeam.getNextNonPlaying();
		if (this.getPk4().f21 == null)
			this.getPk4().f21 = thisTeam.getNextNonPlaying();
		if (this.getPk4().f22 == null)
			this.getPk4().f22 = thisTeam.getNextNonPlaying();

		thisTeam.resetGetPositionPlayer(nofForeigners);
		this.getPk3().d11 = thisTeam.getFirstDefender();
		this.getPk3().d12 = thisTeam.getNextDefender();
		this.getPk3().f1 = thisTeam.getFirstCenter();
		this.getPk3().d21 = thisTeam.getNextDefender();
		this.getPk3().d22 = thisTeam.getNextDefender();
		this.getPk3().f2 = thisTeam.getNextCenter();
		thisTeam.initNonPlaying();
		if (this.getPk3().d11 == null)
			this.getPk3().d11 = thisTeam.getNextNonPlaying();
		if (this.getPk3().d12 == null)
			this.getPk3().d12 = thisTeam.getNextNonPlaying();
		if (this.getPk3().f1 == null)
			this.getPk3().f1 = thisTeam.getNextNonPlaying();
		if (this.getPk3().d21 == null)
			this.getPk3().d21 = thisTeam.getNextNonPlaying();
		if (this.getPk3().d22 == null)
			this.getPk3().d22 = thisTeam.getNextNonPlaying();
		if (this.getPk3().f2 == null)
			this.getPk3().f2 = thisTeam.getNextNonPlaying();
	}

	public void lineUpAI(Team thisTeam, CoachAI.Analysis analysis,
			int nofForeigners) {
		lineUpAI(thisTeam, analysis, nofForeigners, 0, 0);
	}

	public void incPlayerNofMatches(Team team) {
		if (this.getG().goal1 != null) {
			this.getG().goal1.incNofMatches();
		}
		int nofPlayers = team.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = team.getTeamPlayer(i);
			if (player.getHealth().getInjury() == 0 && player != this.getG().goal1
					&& player != this.getG().goal2
					&& player.getPosition().getPosID() != Position.PosID.GOALIE) {
				player.incNofMatches();
			}
		}
	}

	void removePlayer(Player player) {
		if (player.equals(this.getG().goal1))
			this.getG().goal1 = null;
		if (player.equals(this.getG().goal2))
			this.getG().goal2 = null;
		if (player.equals(this.getE55().d11))
			this.getE55().d11 = null;
		if (player.equals(this.getE55().d12))
			this.getE55().d12 = null;
		if (player.equals(this.getE55().d21))
			this.getE55().d21 = null;
		if (player.equals(this.getE55().d22))
			this.getE55().d22 = null;
		if (player.equals(this.getE55().d31))
			this.getE55().d31 = null;
		if (player.equals(this.getE55().d32))
			this.getE55().d32 = null;
		if (player.equals(this.getE55().lw1))
			this.getE55().lw1 = null;
		if (player.equals(this.getE55().c1))
			this.getE55().c1 = null;
		if (player.equals(this.getE55().rw1))
			this.getE55().rw1 = null;
		if (player.equals(this.getE55().lw2))
			this.getE55().lw2 = null;
		if (player.equals(this.getE55().c2))
			this.getE55().c2 = null;
		if (player.equals(this.getE55().rw2))
			this.getE55().rw2 = null;
		if (player.equals(this.getE55().lw3))
			this.getE55().lw3 = null;
		if (player.equals(this.getE55().c3))
			this.getE55().c3 = null;
		if (player.equals(this.getE55().rw3))
			this.getE55().rw3 = null;
		if (player.equals(this.getE55().lw4))
			this.getE55().lw4 = null;
		if (player.equals(this.getE55().c4))
			this.getE55().c4 = null;
		if (player.equals(this.getE55().rw4))
			this.getE55().rw4 = null;
		if (player.equals(this.getE44().d11))
			this.getE44().d11 = null;
		if (player.equals(this.getE44().d12))
			this.getE44().d12 = null;
		if (player.equals(this.getE44().f11))
			this.getE44().f11 = null;
		if (player.equals(this.getE44().f12))
			this.getE44().f12 = null;
		if (player.equals(this.getE44().d21))
			this.getE44().d21 = null;
		if (player.equals(this.getE44().d22))
			this.getE44().d22 = null;
		if (player.equals(this.getE44().f21))
			this.getE44().f21 = null;
		if (player.equals(this.getE44().f22))
			this.getE44().f22 = null;
		if (player.equals(this.getE33().d11))
			this.getE33().d11 = null;
		if (player.equals(this.getE33().d12))
			this.getE33().d12 = null;
		if (player.equals(this.getE33().f1))
			this.getE33().f1 = null;
		if (player.equals(this.getE33().d21))
			this.getE33().d21 = null;
		if (player.equals(this.getE33().d22))
			this.getE33().d22 = null;
		if (player.equals(this.getE33().f2))
			this.getE33().f2 = null;
		if (player.equals(this.getPp5().d11))
			this.getPp5().d11 = null;
		if (player.equals(this.getPp5().d12))
			this.getPp5().d12 = null;
		if (player.equals(this.getPp5().lw1))
			this.getPp5().lw1 = null;
		if (player.equals(this.getPp5().c1))
			this.getPp5().c1 = null;
		if (player.equals(this.getPp5().rw1))
			this.getPp5().rw1 = null;
		if (player.equals(this.getPp5().d21))
			this.getPp5().d21 = null;
		if (player.equals(this.getPp5().d22))
			this.getPp5().d22 = null;
		if (player.equals(this.getPp5().lw2))
			this.getPp5().lw2 = null;
		if (player.equals(this.getPp5().c2))
			this.getPp5().c2 = null;
		if (player.equals(this.getPp5().rw2))
			this.getPp5().rw2 = null;
		if (player.equals(this.getPp4().d11))
			this.getPp4().d11 = null;
		if (player.equals(this.getPp4().d12))
			this.getPp4().d12 = null;
		if (player.equals(this.getPp4().f11))
			this.getPp4().f11 = null;
		if (player.equals(this.getPp4().f12))
			this.getPp4().f12 = null;
		if (player.equals(this.getPp4().d21))
			this.getPp4().d21 = null;
		if (player.equals(this.getPp4().d22))
			this.getPp4().d22 = null;
		if (player.equals(this.getPp4().f21))
			this.getPp4().f21 = null;
		if (player.equals(this.getPp4().f22))
			this.getPp4().f22 = null;
		if (player.equals(this.getPk4().d11))
			this.getPk4().d11 = null;
		if (player.equals(this.getPk4().d12))
			this.getPk4().d12 = null;
		if (player.equals(this.getPk4().f11))
			this.getPk4().f11 = null;
		if (player.equals(this.getPk4().f12))
			this.getPk4().f12 = null;
		if (player.equals(this.getPk4().d21))
			this.getPk4().d21 = null;
		if (player.equals(this.getPk4().d22))
			this.getPk4().d22 = null;
		if (player.equals(this.getPk4().f21))
			this.getPk4().f21 = null;
		if (player.equals(this.getPk4().f22))
			this.getPk4().f22 = null;
		if (player.equals(this.getPk3().d11))
			this.getPk3().d11 = null;
		if (player.equals(this.getPk3().d12))
			this.getPk3().d12 = null;
		if (player.equals(this.getPk3().f1))
			this.getPk3().f1 = null;
		if (player.equals(this.getPk3().d21))
			this.getPk3().d21 = null;
		if (player.equals(this.getPk3().d22))
			this.getPk3().d22 = null;
		if (player.equals(this.getPk3().f2))
			this.getPk3().f2 = null;
	}

	public G getG() {
		return g;
	}

	public void setG(G g) {
		this.g = g;
	}

	public E55 getE55() {
		return e55;
	}

	public void setE55(E55 e55) {
		this.e55 = e55;
	}

	public E44 getE44() {
		return e44;
	}

	public void setE44(E44 e44) {
		this.e44 = e44;
	}

	public E33 getE33() {
		return e33;
	}

	public void setE33(E33 e33) {
		this.e33 = e33;
	}

	public PP5 getPp5() {
		return pp5;
	}

	public void setPp5(PP5 pp5) {
		this.pp5 = pp5;
	}

	public PP4 getPp4() {
		return pp4;
	}

	public void setPp4(PP4 pp4) {
		this.pp4 = pp4;
	}

	public PK4 getPk4() {
		return pk4;
	}

	public void setPk4(PK4 pk4) {
		this.pk4 = pk4;
	}

	public PK3 getPk3() {
		return pk3;
	}

	public void setPk3(PK3 pk3) {
		this.pk3 = pk3;
	}
}
