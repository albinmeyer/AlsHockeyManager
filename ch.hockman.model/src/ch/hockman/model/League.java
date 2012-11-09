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

import ch.hockman.model.common.HockmanException;
import ch.hockman.model.player.Nation;
import ch.hockman.model.player.NationPtrVector;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * The representation of the league to be played in this game.
 *
 * @author Albin
 *
 */
public class League {

	static final int LGU_MAXNOF_FOREIGN = 5; // max number of foreigners except for unlimited
	static final int LGU_START_YEAR = 2012;
	static final int LGU_NOF_START_YEAR = 3;
	static final int LGU_PLAYOFFGAMES = 7;
	static final int LGU_NOFNOFROUNDS = 10;
	private static final String L_NAMEOFLEAGUE = "Al's League";
	
	protected String leagueName;
	protected int startingYear;
	protected PlayerPtrVector playerVector;
	protected TeamPtrDivVector teamDivVector;
	protected NationPtrVector nationVector;
	protected Modus modus;

	public enum LeagueError {
		OK, NOTHING_EDITED, WRONG_NOF_TEAMS, WRONG_NOF_PLAYERS
	};

	public League() {
		playerVector = new PlayerPtrVector();
		teamDivVector = new TeamPtrDivVector();
		nationVector = new NationPtrVector();
		modus = new Modus();
		this.leagueName = L_NAMEOFLEAGUE;
		this.startingYear = LGU_START_YEAR;
	}

	void loadLgu(GameLeagueFile file) throws HockmanException {
		load(file, false);
	}

	void loadGam(GameLeagueFile file) throws HockmanException {
		load(file, true);
	}

	void saveLgu(GameLeagueFile file) {
		save(file, false);
	}

	void saveGam(GameLeagueFile xmlFile) {
		save(xmlFile, true);
	}

	void setLeague(String leagueName, int startingYear) {
		// called at create new league
		this.leagueName = leagueName;
		this.startingYear = startingYear;
	}

	public String getLeagueName() {
		return leagueName;
	}

	int getStartingYear() {
		return startingYear;
	}

	public PlayerPtrVector getPlayerVector() {
		return playerVector;
	}

	public TeamPtrDivVector getTeamDivVector() {
		return teamDivVector;
	}

	NationPtrVector getNationVector() {
		return nationVector;
	}

	public Modus getModus() {
		return modus;
	}

	LeagueError valid() {
		// check for lock
		if (!teamDivVector.isLocked()) {
			return LeagueError.NOTHING_EDITED;
		}

		// check nof teams
		for (int div = 1; div <= modus.getNofDivisions(); div++) {
			if (modus.getNofTeams() != teamDivVector.getNofTeams(div)) {
				// wrong nof teams for division teams (but don't check transfer
				// teams)
				return LeagueError.WRONG_NOF_TEAMS;
			}
		}

		// completeness of teams (nof players)
		for (int div = 1; div <= modus.getNofDivisions() + 1; div++) {
			int nofTeams = teamDivVector.getNofTeams(div);
			for (int i = 0; i < nofTeams; i++) {
				Team team = teamDivVector.getTeam(div, i);
				if (team.getNofPlayers() != Team.TEAM_MUSTBEINITPLAYER) {
					return LeagueError.WRONG_NOF_PLAYERS;
				}
			}
		}
		return LeagueError.OK;
	}

	private void load(GameLeagueFile file, boolean gam) throws HockmanException {
		file.parseSurroundingStartElement("League");

		// get nations
		NationPtrVector npv = this.getNationVector();
		int nofNations = file.getInt("NofNations");
		for (int i = 0; i < nofNations; i++) {
			file.parseSurroundingStartElement("Nation");
			int id = file.getInt("Id");
			if (id >= Nation.NAT_MAXNOFNAT) {
				throw new HockmanException("Nation id to high");
			}
			String s = file.getString("Name");
			file.parseSurroundingEndElement();
			Nation nation = new Nation(id, s);
			npv.insertNation(nation);
		}

		// get modus
		if (!this.getModus().load(file, npv)) {
			throw new HockmanException(this.getModus().toString());
		}

		// get league info
		String leagueName = file.getString("LeagueName");
		int startingYear = file.getInt("StartingYear");
		this.setLeague(leagueName, startingYear);

		// fill PlayerPtrVector
		PlayerPtrVector ppv = this.getPlayerVector();
		int nofPlayers = file.getInt("NofPlayers");
		if (gam) {
			for (int i = 0; i < nofPlayers; i++) {
				Player player = new Player(startingYear);
				player.loadGam(file, npv);
				player.calcFee();
				ppv.InsertPlayer(player);
			}
		} else {
			for (int i = 0; i < nofPlayers; i++) {
				Player player = new Player(startingYear);
				player.loadLgu(file, npv);
				ppv.InsertPlayer(player);
			}
		}

		// fill TeamPtrVector
		int nofTeams;
		TeamPtrDivVector tpdv = this.getTeamDivVector();
		int nofDiv = file.getInt("NofDiv");
		tpdv.lockDivisions(nofDiv);
		for (int div = 1; div <= nofDiv + 1; div++) {
			file.parseSurroundingStartElement("Division");
			nofTeams = file.getInt("NofTeams");
			String divName = file.getString("DivName");
			tpdv.setDivName(divName, div);
			if (gam) {
				for (int i = 0; i < nofTeams; i++) {
					Team team = new Team(div);
					team.loadGam(file, npv, ppv);
					tpdv.insertDivTeam(team, div);
				}
			} else {
				for (int i = 0; i < nofTeams; i++) {
					Team team = new Team(div);
					team.loadLgu(file, npv, ppv);
					tpdv.insertDivTeam(team, div);
				}
			}
			file.parseSurroundingEndElement();
		}

		// Update contracts of players
		for (int i = 0; i < nofPlayers; i++) {
			Player player = ppv.GetPlayer(i);
			player.updateContrTeams(tpdv);
		}
		if (gam) {
			// Update statistics/transfers of teams
			for (int div = 1; div <= nofDiv + 1; div++) {
				nofTeams = tpdv.getNofTeams(div);
				for (int i = 0; i < nofTeams; i++) {
					Team team = tpdv.getTeam(div, i);
					team.loadGamUpdate(file, tpdv, ppv);
				}
			}
		}
		file.parseSurroundingEndElement(); // League
	}

	private void save(GameLeagueFile xmlFile, boolean gam) {
		// save nations
		xmlFile.writeSurroundingStartElement("League");
		NationPtrVector npv = this.getNationVector();
		int nofNations = npv.getNofNations();
		xmlFile.write("NofNations", nofNations, "List of Nations referenced by Player and Team");
		for (int i = 0; i < nofNations; i++) {
			Nation nation = npv.getNation(i);
			xmlFile.writeSurroundingStartElement("Nation");
			xmlFile.write("Id", nation.getId());
			xmlFile.write("Name", nation.getName());
			xmlFile.writeSurroundingEndElement();
		}

		// save Modus
		this.getModus().save(xmlFile);

		// save league info
		xmlFile.write("LeagueName", this.getLeagueName());
		xmlFile.write("StartingYear", this.getStartingYear());

		// save PlayerPtrVector
		PlayerPtrVector ppv = this.getPlayerVector();
		int nofPlayers = ppv.GetNofPlayers();
		xmlFile.write("NofPlayers", nofPlayers);
		if (gam) {
			for (int i = 0; i < nofPlayers; i++) {
				Player player = ppv.GetPlayer(i);
				player.saveGam(xmlFile);
			}
		} else {
			for (int i = 0; i < nofPlayers; i++) {
				Player player = ppv.GetPlayer(i);
				player.saveLgu(xmlFile);
			}
		}

		// save TeamPtrVector
		TeamPtrDivVector tpdv = this.getTeamDivVector();
		int nofDiv = this.getModus().getNofDivisions();
		xmlFile.write("NofDiv", nofDiv, "Number of league division. There is one more division for transfer teams");
		for (int div = 1; div <= nofDiv + 1; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			xmlFile.writeSurroundingStartElement("Division");
			xmlFile.write("NofTeams", nofTeams);
			xmlFile.write("DivName", tpdv.getDivName(div));
			if (gam) {
				for (int i = 0; i < nofTeams; i++) {
					Team team = tpdv.getTeam(div, i);
					team.saveGam(xmlFile);
				}
			} else {
				for (int i = 0; i < nofTeams; i++) {
					Team team = tpdv.getTeam(div, i);
					team.saveLgu(xmlFile);
				}
			}
			xmlFile.writeSurroundingEndElement();			
		}
		if (gam) {
			tpdv = this.getTeamDivVector();
			nofDiv = this.getModus().getNofDivisions();
			for (int div = 1; div <= nofDiv + 1; div++) {
				int nofTeams = tpdv.getNofTeams(div);
				for (int i = 0; i < nofTeams; i++) {
					Team team = tpdv.getTeam(div, i);
					team.saveGamUpdate(xmlFile);
				}
			}
		}
		xmlFile.writeSurroundingEndElement();
	}
}
