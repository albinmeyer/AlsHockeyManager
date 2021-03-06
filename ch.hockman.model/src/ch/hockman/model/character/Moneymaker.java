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

import ch.hockman.model.common.Util;

/**
 * Moneymaker character of a player.
 *
 * @author Albin
 *
 */
public class Moneymaker implements PlayerCharacter {

	private static Moneymaker instance;

	@Override
	public CharID getCharID() {
		return PlayerCharacter.CharID.MONEYMAKER;
	}

	@Override
	public String getCharacterName() {
		return Util.getModelResourceBundle().getString("L_CHARACTER_MONEYMAKER");
	}

	public static PlayerCharacter instance() {
		if (instance == null) {
			instance = new Moneymaker();
		}
		return instance;
	}
}
