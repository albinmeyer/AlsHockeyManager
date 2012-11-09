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

import ch.hockman.model.common.HockmanException;
import ch.hockman.model.player.Player;

/**
 * Utility class for creating a game.
 *
 * @author Albin
 *
 */
public class GameCreator {
	
	static GameCreator instance;
	
	private GameLeagueFile xmlFile; // xml file format

	public GameCreator() {
		xmlFile = new GameLeagueFile();
	}

	public static GameCreator instance() {
		if (instance == null) {
			instance = new GameCreator();
		}
		return instance;
	}

	GameLeagueFile getFile() {
		return xmlFile;
	}

	String getCurrFileName() {
		return xmlFile.getCurrFileName();
	}

	public void setCurrFileName(String fileName) {
		xmlFile.setCurrFileName(fileName);		
	}

	static void free() {
		if (instance != null) {
			instance = null;
		}
	}

	public Game loadGam() throws HockmanException {
		// alloc schedule, Game;
		League league = null;
		if (xmlFile.openForXMLRead()) {
			xmlFile.parseSurroundingStartElement("Game");			
			// create league
			try {
				league = new League();
				Player.initIdBitSet();				
				league.loadGam(xmlFile);
			} catch (HockmanException e) {
				xmlFile.close();
				// deinit all
				LeagueCreator.instance().deInit(league);
				throw e;
			}
		} else {
			// if invalid file, return null
			return null;
		}
		// alloc Game;
		// calls LeagueCreator.LoadGam() and Game()
		Game game = null;
		assert (league != null);
		assert (xmlFile.opened()); // already opened by LeagueCreator.LoadGam()
		try {
			game = new Game(league);
			game.loadGam(xmlFile);
			Options.load(xmlFile);
			xmlFile.parseSurroundingEndElement();			
		} catch (Throwable t) {
			xmlFile.close();
			GameCreator.instance().deInit(game);
			throw new HockmanException(t.getMessage());
		}
		xmlFile.close();
		return game;

	}
	
	public boolean saveGam(Game game) {
		assert (game != null);
		if (xmlFile.openForXMLWrite()) {
			xmlFile.writeSurroundingStartElement("Game");
			game.getLeague().saveGam(xmlFile);
			game.saveGam(xmlFile);
			Options.save(xmlFile);
			xmlFile.writeSurroundingEndElement();
			xmlFile.close();
			return true;
		} else {
			return false;
		}
	}

	public Game newGame(League league) {
		// alloc schedule, Game;
		assert (league != null);
		Game game = new Game(league);
		game.newSeason();
		return game;
	}

	public void deInit(Game game) {
		// free Game
		assert (game != null);
		LeagueCreator.instance().deInit(game.getLeague());
	}
}
