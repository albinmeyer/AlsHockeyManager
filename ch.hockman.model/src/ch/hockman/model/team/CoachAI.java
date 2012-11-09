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

import java.util.BitSet;

/**
 * The ai of a coach.
 * For training, tactics, lineup.
 *
 * @author Albin
 *
 */
public class CoachAI {

	public enum AnalysisBit { // properties of teams and match
		HOME(0),
		PLAYOFF(1),
		ENERGY(2),
		MOTIVATION(3),
		STAMINA(4),
		POWER(5),
		SKILL(6),
		SHOTS(7),
		DEFENSE(8),
		OFFENSE(9),
		EVENBLOCKS(10),
		POWERBLOCK(11);

		private AnalysisBit(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		private int value;
	};

	public static class Analysis {
		public BitSet analysis;

		public Analysis() {
			analysis = new BitSet(AnalysisBit.POWERBLOCK.getValue() + 1);
		}
	};

	public static Analysis AnalyzeTeams(Team myTeam, Team opTeam,
			boolean playoff) {
		Analysis analysis = new Analysis();
		if (myTeam.getHome()) {
			analysis.analysis.set(AnalysisBit.HOME.getValue());
		}
		if (playoff) {
			analysis.analysis.set(AnalysisBit.PLAYOFF.getValue());
		}

		// TODO implement analyzing teams for ai coaches

		return analysis;
	}
};
