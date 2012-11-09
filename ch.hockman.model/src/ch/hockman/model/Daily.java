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

import ch.hockman.model.match.News;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.CoachAI;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * Utility class for calculating the daily stuff.
 *
 * @author Albin
 *
 */
class Daily {

	// prevent instantiation
	private Daily() {
	}

	public static void doDaily(PlayerPtrVector ppv, TeamPtrDivVector tpdv,
			Modus modus, int round, Schedule schedule, News news) {
		// update all players
		int nofPlayers = ppv.GetNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = ppv.GetPlayer(i);
			player.updateDay(modus.getGameType() == Modus.GameType.TOURNAMENT);
		}

		// update all teams (finances, transfers, coaching)
		boolean playoff = (modus.getNofRounds() < round);
		int nofTotalRounds = modus.getNofRounds() + modus.getNofPlayoffFinals()
				* modus.getGamesPerFinal();
		for (int div = 1; div <= modus.getNofDivisions() + 1; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			for (int i = 0; i < nofTeams; i++) {
				Team team = tpdv.getTeam(div, i);
				team.updateNonLeaguePlayers(); // farm and transfer
				// team players change engergy too !
				int additionalDays = modus.getNofPlayoffFinals()
						* modus.getGamesPerFinal();
				if (modus.getNofTeams() % 2 == 1) {
					// odd number of teams: add number of no-game days
					additionalDays += modus.getNofRounds() / (modus.getNofTeams() + 1);
				}
				Daily.doFinances(team, additionalDays, modus.getNofRounds());
				if (round > 1) {
					// do transfer but not at first round
					int nofForeigners;
					if (modus.nofForeigners == Modus.maxNofForeignersExceptUnlimited + 1) {
						nofForeigners = Team.TEAM_MAXTEAMPLAYER
								+ Team.TEAM_MAXFARMPLAYER;
					} else {
						nofForeigners = modus.nofForeigners;
					}
					Daily.doTransfers(nofTotalRounds, modus.getGameType(), ppv,
							team, news, nofForeigners);
				}
			}
		}
		// coaching of all teams AFTER transfers of ALL teams ! (for
		// correct lineups after trading!)
		for (int div = 1; div <= modus.getNofDivisions() + 1; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			for (int i = 0; i < nofTeams; i++) {
				Team team = tpdv.getTeam(div, i);
				if (round <= nofTotalRounds) {
					// don't do coaching at end of season
					Team opteam = schedule.getOpTeam(round, team);
					Daily.doCoaching(team, opteam, playoff,
							modus.maxNofForeigners());
				}
			}
		}
	}

	private static void doFinances(Team team, int additionalDays, int nofRounds) {
		team.getTeamState().doFinances(team, additionalDays, nofRounds);
	}

	private static void doTransfers(int nofTotalRounds, Modus.GameType gt,
			PlayerPtrVector ppv, Team team, News news, int maxNofForeigners) {
		if (gt != Modus.GameType.TOURNAMENT) {
			team.getTeamState().doTransfers(nofTotalRounds, gt, ppv, team,
					news, maxNofForeigners);
		}
	}

	private static void doCoaching(Team thisTeam, Team opTeam, boolean playoff,
			int nofForeigners) {
		// nofForeigners: max. number of foreigners per team
		CoachAI.Analysis analysis = CoachAI.AnalyzeTeams(thisTeam, opTeam,
				playoff);
		thisTeam.getTeamState().doTraining(thisTeam, analysis);
		thisTeam.getTeamState().doTactics(thisTeam, analysis);
		thisTeam.getTeamState().doLineUp(thisTeam, analysis, nofForeigners);

	}
}
