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

package ch.hockman.model.player;

/**
 * Representation of the birthday of a player.
 *
 * @author Albin
 *
 */
public class Birthday {
	// default values for birthday of players
	static final int PLR_BIRTHDAY = 19;
	static final int PLR_BIRTHMONTH = 9;
	static final int PLR_BIRTHYEAR = 1990;

	public Birthday() {
		setDay(PLR_BIRTHDAY);
		setMonth(PLR_BIRTHMONTH);
		setYear(PLR_BIRTHYEAR);
	}

	private int day;
	private int month;
	private int year;

	@Override
	public String toString() {
		// TODO localization
		return String.valueOf(getDay()) + "." + String.valueOf(getMonth()) + "."
				+ String.valueOf(getYear());
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
