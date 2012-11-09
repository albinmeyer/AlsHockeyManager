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

/**
 * The state of a team (nonleague, coached, ai).
 *
 * @author Albin
 *
 */
public interface TeamState {
 
	public enum TeamStateID { // needed for load/save
		NONLEAGUE(0), COACHED(1), KI(2);
		private int i;

		private TeamStateID(int i) {
			this.i = i;
		}

		int getValue() {
			return this.i;
		}

		public static TeamStateID getEnumByValue(int value) {
			for (TeamStateID test : TeamStateID.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No TeamState available for value " + value);
		}
	};

	TeamStateID getTeamStateID();

	void doFinances(Team team, int additionalDays, int nofRounds);

	void doTraining(Team thisTeam, CoachAI.Analysis analysis);

	void doTactics(Team thisTeam, CoachAI.Analysis analysis, int goaldiff,
			int second);

	void doTactics(Team thisTeam, CoachAI.Analysis analysis);

	void doLineUp(Team thisTeam, CoachAI.Analysis analysis, int nofForeigners,
			int goaldiff, int second);

	void doLineUp(Team thisTeam, CoachAI.Analysis analysis, int nofForeigners);

	void doTransfers(int nofTotalRounds, Modus.GameType gt,
			PlayerPtrVector ppv, Team team, News news, int maxNofForeigners);
}
