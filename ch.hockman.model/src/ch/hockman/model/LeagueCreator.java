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
 * Utility class to create a league.
 *
 * @author Albin
 *
 */
public class LeagueCreator {
	static private LeagueCreator instance;
	
	private GameLeagueFile xmlFile; // new xml format

	private LeagueCreator() {
		xmlFile = new GameLeagueFile();
	}

	public static LeagueCreator instance() {
		if (instance == null) {
			instance = new LeagueCreator();
		}
		return instance;
	}

	GameLeagueFile getFile() {
		return xmlFile;
	}

	String getCurrFileName() {
		return xmlFile.getCurrFileName();
	}

	public void setCurrFileName(String fileName) {
		xmlFile.setCurrFileName(fileName);
	}

	static void free() {
		if (instance != null) {
			instance = null;
		}
	}

	public League loadLguXML() throws HockmanException {
		// alloc nation, teams, players, league
		if (xmlFile.openForXMLRead()) {
			// create league
			League league = null;
			try {
				league = new League();
				Player.initIdBitSet();
				league.loadLgu(xmlFile);
				xmlFile.close(); // don't close, GameCreator.LoadGam() is called afterwards
				return league;
			} catch(HockmanException e) {
				xmlFile.close();
				deInit(league);
				throw e;
			}
		} else {
			// if invalid file, return null
			return null;
		}
	}

	public boolean saveLgu(League league) {
		return save(league, false);
	}

	void delPlayer(Player player, Team team, League league) {
		// free player and remove from team and league playerlist
		// also remove and free nation if belonging to noone anymore
		// free player and remove from team and league playerlist
		// also remove and free nation if belonging to noone anymore
		PlayerPtrVector ppv = league.getPlayerVector();
		ppv.RemovePlayer(player);
		if (team != null) {
			team.removePlayer(player);
		}
		league.getNationVector().update();
	}

	void delTeam(Team team, League league) {
		// free and remove team and its players from appropriate lists (with
		// nations)
		Player player;
		PlayerPtrVector ppv = league.getPlayerVector();
		while (team.getNofTeamPlayers() > 0) {
			player = team.getTeamPlayer(0);
			team.removeTeamPlayer(player);
			ppv.RemovePlayer(player);
		}
		while (team.getNofFarmPlayers() > 0) {
			player = team.getFarmPlayer(0);
			team.removeFarmPlayer(player);
			ppv.RemovePlayer(player);
		}
		while (team.getNofRookiePlayers() > 0) {
			player = team.getRookiePlayer(0);
			team.removeRookiePlayer(player);
		}
		league.getTeamDivVector().removeDivTeam(team);
		league.getNationVector().update();

	}

	Nation newNation(NationPtrVector npv) {
		// alloc nation and insert in nationlist
		Nation nation = new Nation();
		npv.insertNation(nation);
		return nation;
	}

	public void delNation(Nation nation, NationPtrVector npv) {
		// free nation and remove from nationlist
		npv.removeNation(nation);
	}

	void deInit(League league) {
		// free nation, teams, players, league and remove from appropriate lists
		assert (league != null);
		xmlFile.setCurrFileName(new String());

		// free teams (including transfer teams)
		TeamPtrDivVector tpdv = league.getTeamDivVector();
		int nofdiv = league.getModus().getNofDivisions();
		for (int div = 1; div <= nofdiv + 1; div++) {
			while (tpdv.getNofTeams(div) > 0) {
				delTeam(tpdv.getTeam(div, 0), league);
				// free team and its players
			}
		}

		// free players (in error case at load lgu: there could be players not
		// in a team)
		PlayerPtrVector ppv = league.getPlayerVector();
		while (ppv.GetNofPlayers() > 0) {
			Player player = ppv.GetPlayer(0);
			delPlayer(player, null, league);
		}

		// free nations
		NationPtrVector npv = league.getNationVector();
		npv.update(); // should remove all nations except basic one (if one)
		while (npv.getNofNations() > 0) {
			// in normal case, there should be 0 or 1 nation left (basic one)
			// in error case (load invalid lgu file), may be more
			delNation(npv.getNation(0), npv);
		}
	}

	boolean save(League league, boolean gam) {
		if (xmlFile.openForXMLWrite()) {
			if (gam) {
				league.saveGam(xmlFile);
			} else {
				league.saveLgu(xmlFile);
			}
			xmlFile.close();
			return true;
		} else {
			return false;
		}
	}
}
