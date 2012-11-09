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

package ch.hockman.model.character;

/**
 * Interface of a character of a player.
 *
 * @author Albin
 *
 */
public interface PlayerCharacter {
	enum CharID { // used for load/save, also for match and motivation
		// calculation
		AGGRESSOR(0),
		AMBITIOUSMAN(1),
		FUNNYMAN(2),
		LEADER(3),
		MONEYMAKER(4),
		EXTROVERT(5),
		INTROVERT(6),
		TEAMWORKER(7);
		
		private int i;

		private CharID(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public static CharID getEnumByValue(int value) {
			for (CharID test : CharID.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No PlayerCharacter available for value " + value);
		}
	};

	CharID getCharID();

	String getCharacterName();
};
