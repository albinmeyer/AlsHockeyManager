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

import ch.hockman.model.Modus;
import ch.hockman.model.match.News;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.CoachAI.Analysis;

/**
 * If a team is coached by AI, it has this team state.
 *
 * @author Albin
 *
 */
public class AILeagueTeam implements TeamState {

	static AILeagueTeam instance;

	@Override
	public TeamStateID getTeamStateID() {
		return TeamStateID.KI;
	}

	@Override
	public void doFinances(Team team, int additionalDays, int nofRounds) {
		team.getFinances().financesKI(team);
		team.getFinances().doFinances(team, additionalDays, nofRounds);
	}

	@Override
	public void doTraining(Team thisTeam, Analysis analysis) {
		thisTeam.getTraining().trainingAI(thisTeam, analysis);
		thisTeam.getTraining().doTraining(thisTeam);
	}

	@Override
	public void doTactics(Team thisTeam, Analysis analysis, int goaldiff,
			int second) {
		thisTeam.getTactics().tacticsAI(thisTeam, analysis, goaldiff, second);
	}

	@Override
	public void doTactics(Team thisTeam, Analysis analysis) {
		doTactics(thisTeam, analysis, 0, 0);
	}

	@Override
	public void doLineUp(Team thisTeam, Analysis analysis, int nofForeigners,
			int goaldiff, int second) {
		thisTeam.getLineUp().farmAndFirstTeamKI(thisTeam, analysis,
				nofForeigners);
		thisTeam.getLineUp().lineUpAI(thisTeam, analysis, nofForeigners,
				goaldiff, second);
	}

	@Override
	public void doLineUp(Team thisTeam, Analysis analysis, int nofForeigners) {
		doLineUp(thisTeam, analysis, nofForeigners, 0, 0);
	}

	@Override
	public void doTransfers(int nofTotalRounds, Modus.GameType gt,
			PlayerPtrVector ppv, Team team, News news, int maxNofForeigners) {
		team.getTransfer().transferAI(nofTotalRounds, gt, ppv, team, news,
				maxNofForeigners);
	}

	public static TeamState instance() {
		if (instance == null) {
			instance = new AILeagueTeam();
		}
		return instance;
	}

}
