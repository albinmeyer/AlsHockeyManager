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
 * LeftWing position of a player.
 *
 * @author Albin
 *
 */
public class LeftWing implements Position {
	private static LeftWing instance;

	@Override
	public PosID getPosID() {
		return Position.PosID.LEFTWING;
	}

	@Override
	public String posName() {
		return "LW";
	}

	public static Position instance() {
		if (instance == null) {
			instance = new LeftWing();
		}
		return instance;
	}
}
