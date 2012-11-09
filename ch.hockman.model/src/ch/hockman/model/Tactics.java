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
import ch.hockman.model.team.CoachAI;
import ch.hockman.model.team.Team;

/**
 * The tactics of a team, including AI.
 * blockSelection can be SAME or AGAINST only if team plays home.
 * @author Albin
 *
 */
public class Tactics {

	private GamePlay gamePlay;
	private Effort effort;
	private Defense defense;
	private Offense offense;
	private BlockSelection blockSelection;
	boolean pullGoalie;

	public enum GamePlay {
		OFFENSIVE(0), NORMAL_GP(1), DEFENSIVE(2);
		private int i;

		private GamePlay(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public String getName() {
			switch(i) {
				case 0:
					return Util.getModelResourceBundle().getString("L_GP_OFFENSIVE");
				case 1:
					return Util.getModelResourceBundle().getString("L_GP_NORMAL");
				case 2:
					return Util.getModelResourceBundle().getString("L_GP_DEFENSIVE");
				default:
					return "";
			}			
		}
		
		public static GamePlay getEnumByValue(int value) {
			for (GamePlay test : GamePlay.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No GamePlay available for value " + value);
		}
	};

	public enum Effort {
		FULL(0), NORMAL_EFF(1), EASY(2);
		private int i;

		private Effort(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public String getName() {
			switch(i) {
				case 0:
					return Util.getModelResourceBundle().getString("L_EFF_FULL");
				case 1:
					return Util.getModelResourceBundle().getString("L_EFF_NORMAL");
				case 2:
					return Util.getModelResourceBundle().getString("L_EFF_EASY");
				default:
					return "";
			}			
		}
		
		public static Effort getEnumByValue(int value) {
			for (Effort test : Effort.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException("No Effort available for value "
					+ value);
		}
	};

	public enum Defense {
		ZONE(0), MARK(1);
		private int i;

		private Defense(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public String getName() {
			switch(i) {
				case 0:
					return Util.getModelResourceBundle().getString("L_DEF_ZONE");
				case 1:
					return Util.getModelResourceBundle().getString("L_DEF_MARK");
				default:
					return "";
			}			
		}
		
		public static Defense getEnumByValue(int value) {
			for (Defense test : Defense.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No Defense available for value " + value);
		}
	};

	public enum Offense {
		SHOOT(0), PASS(1);
		private int i;

		private Offense(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public String getName() {
			switch(i) {
				case 0:
					return Util.getModelResourceBundle().getString("L_OFF_SHOOT");
				case 1:
					return Util.getModelResourceBundle().getString("L_OFF_PASS");
				default:
					return "";
			}			
		}
		
		public static Offense getEnumByValue(int value) {
			for (Offense test : Offense.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No Offense available for value " + value);
		}
	};

	public enum BlockSelection {
		STRONGER(0), LESSTIRED(1), SAME(2), AGAINST(3);
		private int i;

		private BlockSelection(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public String getName() {
			switch(i) {
				case 0:
					return Util.getModelResourceBundle().getString("L_BLOCK_STRONGER");
				case 1:
					return Util.getModelResourceBundle().getString("L_BLOCK_LESSTIRED");
				case 2:
					return Util.getModelResourceBundle().getString("L_BLOCK_SAME");
				case 3:
					return Util.getModelResourceBundle().getString("L_BLOCK_AGAINST");
				default:
					return "";
			}			
		}
		
		public static BlockSelection getEnumByValue(int value) {
			for (BlockSelection test : BlockSelection.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No BlockSelection available for value " + value);
		}
	};

	public Tactics() {
		gamePlay = GamePlay.NORMAL_GP;
		effort = Effort.NORMAL_EFF;
		defense = Defense.ZONE;
		offense = Offense.SHOOT;
		blockSelection = BlockSelection.STRONGER;
		pullGoalie = true;
	}

	public void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Tactics");
		xmlFile.write("GamePlay", gamePlay.getValue());
		xmlFile.write("Effort", effort.getValue());
		xmlFile.write("Defense", defense.getValue());
		xmlFile.write("Offense", offense.getValue());
		xmlFile.write("BlockSelection", blockSelection.getValue());
		xmlFile.write("PullGoalie", pullGoalie);
		xmlFile.writeSurroundingEndElement();
	}

	public void blockSelectionCheck(Team thisTeam, CoachAI.Analysis analysis) {
		// checks for managed team, if playing away, correct block selection is
		// switched on
		if (!analysis.analysis.get(CoachAI.AnalysisBit.HOME.getValue())
				&& blockSelection != BlockSelection.STRONGER
				&& blockSelection != BlockSelection.LESSTIRED) {
			// can select only one of the first 2 block Selections
			blockSelection = BlockSelection.getEnumByValue(Util.random(2));
		}
	}

	public void tacticsAI(Team thisTeam, CoachAI.Analysis analysis) {
		tacticsAI(thisTeam, analysis, 0, 0);
	}

	public void tacticsAI(Team thisTeam, CoachAI.Analysis analysis,
			int goaldiff, int second) {
		// analysis not yet used

		boolean change = (second == 0 || Util.random(600) == 0); // change
		// tactics only
		// every 10 min

		if (Math.abs(goaldiff) < 3 && second > 3300) {
			effort = Tactics.Effort.FULL;
		} else {
			if (change) {
				effort = Effort.getEnumByValue(Util.random(3));
			}
		}
		if (goaldiff < 0) {
			// behind
			if (second < 3300 || goaldiff < -2) {
				if (change) {
					gamePlay = GamePlay.getEnumByValue(Util.random(3));
				}
			} else {
				gamePlay = Tactics.GamePlay.OFFENSIVE;
			}
		} else if (goaldiff > 0) {
			// advance
			if (second < 3300 || goaldiff > 2) {
				if (change) {
					gamePlay = GamePlay.getEnumByValue(Util.random(3));
				}
			} else {
				gamePlay = Tactics.GamePlay.DEFENSIVE;
			}
		} else {
			if (change) {
				gamePlay = GamePlay.getEnumByValue(Util.random(3));
			}
		}
		if (change) {
			defense = Defense.getEnumByValue(Util.random(2));
			offense = Offense.getEnumByValue(Util.random(2));
			if (analysis.analysis.get(CoachAI.AnalysisBit.HOME.getValue())) {
				// can select one of all 4 block Selection
				blockSelection = BlockSelection.getEnumByValue(Util.random(4));
			} else {
				// can select only one of the first 2 block Selections
				blockSelection = BlockSelection.getEnumByValue(Util.random(2));
			}
		}
		pullGoalie = true;
	}

	public void setTactics(GamePlay gamePlay, Effort effort, Defense defense,
			Offense offense, BlockSelection blockSelection, boolean pullGoalie) {
		this.gamePlay = gamePlay;
		this.effort = effort;
		this.defense = defense;
		this.offense = offense;
		this.blockSelection = blockSelection;
		this.pullGoalie = pullGoalie;
	}

	public GamePlay getGamePlay() {
		return gamePlay;
	}

	public Effort getEffort() {
		return effort;
	}

	public Defense getDefense() {
		return defense;
	}

	public Offense getOffense() {
		return offense;
	}

	public BlockSelection getBlockSelection() {
		return blockSelection;
	}

	public boolean getPullGoalie() {
		return this.pullGoalie;
	}

	public void load(GameLeagueFile file) {
		file.parseSurroundingStartElement("Tactics");
		gamePlay = GamePlay.getEnumByValue(file.getInt("GamePlay"));
		effort = Effort.getEnumByValue(file.getInt("Effort"));
		defense = Defense.getEnumByValue(file.getInt("Defense"));
		offense = Offense.getEnumByValue(file.getInt("Offense"));
		blockSelection = BlockSelection.getEnumByValue(file
				.getInt("BlockSelection"));
		pullGoalie = file.getInt("PullGoalie") > 0;
		file.parseSurroundingEndElement();
	}
}
