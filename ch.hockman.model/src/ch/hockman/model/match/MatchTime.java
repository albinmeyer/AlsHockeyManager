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

package ch.hockman.model.match;

/**
 * The timer used in one match (game team against team).
 *
 * @author Albin
 *
 */
public class MatchTime {
	private int sec;
	
	public MatchTime() {
		sec = 0;
	}

	public void reset() {
		sec = 0;
	}

	public void tick() {
		sec++;
	}

	public int getMin() {
		return sec / 60;
	}

	public int getMinSec() {
		return sec % 60;
	}

	public int getTotalSec() {
		return sec;
	}
}
