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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.hockman.model.common.Util;

/**
 * The messages to the manager.
 *
 * @author Albin
 *
 */
public class TeamMessages {
	private List<String> msgVec;
	private Iterator<String> it;

	// messages shown on the manager main window
	public TeamMessages() {
		msgVec = new ArrayList<String>();
		add(Util.getModelResourceBundle().getString("L_NOMESSAGES"));
	}

	public void load(GameLeagueFile file) {
		file.parseSurroundingStartElement("TeamMessages");
		reset();
		int counter = file.getInt("Counter");
		int i = 0;
		while (i < counter) {
			add(file.getString("Message"));
			i++;
		}
		file.parseSurroundingEndElement();
	}

	public void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("TeamMessages");
		int counter = 0;
		setBoV();
		while (!eoV()) {
			getCurrSetNext();
			counter++;
		}
		xmlFile.write("Counter", counter);
		setBoV();
		while (!eoV()) {
			xmlFile.write("Message", getCurrSetNext());
		}
		xmlFile.writeSurroundingEndElement();
	}

	public void reset() {
		msgVec.clear();
	}

	public void add(String s) {
		msgVec.add(s);
	}

	public void setBoV() {
		// set begin of vector
		it = msgVec.iterator();
	}

	public String getCurrSetNext() {
		assert (it.hasNext());
		return it.next();
	}

	public boolean eoV() {
		// end of vector
		return !it.hasNext();
	}
}
