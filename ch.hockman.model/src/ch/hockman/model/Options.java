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


/**
 * The playing options of the game.
 *
 * @author Albin
 *
 */
public class Options {
	static {
		reset();
	}

	public enum Level {
		ROOKIE, NORMAL, PROF;
		
		public static Level getEnumByValue(int value) {
			for (Level test : Level.values()) {
				if (test.ordinal() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No Level available for value " + value);
		}
	}

	public enum Playing {
		RESULT, COACHING;
		
		public static Playing getEnumByValue(int value) {
			for (Playing test : Playing.values()) {
				if (test.ordinal() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No Playing available for value " + value);
		}
	}
	
	public static Level level;
	public static Playing playing;
	public static int speed;
	public static boolean showReport;
	public static boolean showIntermissionDialog;
	public static boolean showPenaltyDialog;
	public static boolean showScoringDialog;
	public static boolean showInjuryDialog;
	public static boolean showInterruptDialog;

	public static void reset() {
		playing = Options.Playing.COACHING;
		showReport = true;
		showIntermissionDialog = true;
		showPenaltyDialog = true;
		showScoringDialog = true;
		showInjuryDialog = true;
		showInterruptDialog = false;
		level = Options.Level.NORMAL;
		speed = 10;
	}
	
	static void load(GameLeagueFile xmlFile) {
		xmlFile.parseSurroundingStartElement("Options");
		speed = xmlFile.getInt("Speed");
		level = Level.getEnumByValue(xmlFile.getInt("Level"));
		playing = Playing.getEnumByValue(xmlFile.getInt("Playing"));
		showReport = xmlFile.getBool("Report");
		showIntermissionDialog = xmlFile.getBool("Intermission");
		showPenaltyDialog = xmlFile.getBool("Penalty");
		showScoringDialog = xmlFile.getBool("Scoring");
		showInjuryDialog = xmlFile.getBool("Injury");
		showInterruptDialog = xmlFile.getBool("Interrupt");
		xmlFile.parseSurroundingEndElement();
	}

	static void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Options");
		xmlFile.write("Speed", speed);
		xmlFile.write("Level", level.ordinal());
		xmlFile.write("Playing", playing.ordinal());
		xmlFile.write("Report", showReport);
		xmlFile.write("Intermission", showIntermissionDialog);
		xmlFile.write("Penalty", showPenaltyDialog);
		xmlFile.write("Scoring", showScoringDialog);
		xmlFile.write("Injury", showInjuryDialog);
		xmlFile.write("Interrupt", showInterruptDialog);
		xmlFile.writeSurroundingEndElement();
	}
}
