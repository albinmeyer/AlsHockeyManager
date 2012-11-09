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

package ch.hockman.model.position;

/**
 * Position interface of a player.
 *
 * @author Albin
 *
 */
public interface Position {
	
	enum PosID {
		GOALIE(0),
		DEFENDER(1),
		LEFTWING(2),
		CENTER(3),
		RIGHTWING(4);
		
		private int i;

		private PosID(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public static PosID getEnumByValue(int value) {
			for (PosID test : PosID.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No Position available for value " + value);
		}
	};

	public PosID getPosID();

	public String posName();
}
