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
 * Factory class to create a character of a player.
 *
 * @author Albin
 *
 */
public class CharacterFactory {
	public static PlayerCharacter instance(PlayerCharacter.CharID charId) {
		switch (charId) {
		case AGGRESSOR:
			return Aggressor.instance();
		case AMBITIOUSMAN:
			return Ambitiousman.instance();
		case FUNNYMAN:
			return Funnyman.instance();
		case LEADER:
			return Leader.instance();
		case MONEYMAKER:
			return Moneymaker.instance();
		case EXTROVERT:
			return Extrovert.instance();
		case INTROVERT:
			return Introvert.instance();
		case TEAMWORKER:
			return Teamworker.instance();
		}
		return null;
	}
};
