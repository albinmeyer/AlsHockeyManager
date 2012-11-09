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

import java.util.BitSet;

/**
 * The nation a player belongs to. This is relevant for leagues,
 * where the number of foreigners is restricted.
 *
 * @author Albin
 *
 */
public class Nation {
	private static final String L_NATIONNAME = "Al's Nation";
	public final static int NAT_MAXNOFNAT = 100; // if modified, modify TEAM_MAXNOFTEAMS too !

	private int id;
	private static BitSet idBar = new BitSet(NAT_MAXNOFNAT); // managing used IDs
	private int nofReferences;
	private String nationName;
	
	public Nation() {
		// create new nation in league editor
		this.nationName = L_NATIONNAME;
		createNewId();
		this.nofReferences = 0;
	}

	public Nation(int id, String nationName) {
		// load a nation from file
		this.nationName = nationName;
		setId(id);
		this.nofReferences = 0;
	}

	void setName(String nationName) {
		this.nationName = nationName;
	}

	public int getId() {
		return id;
	}

	public void increaseReferences() {
		nofReferences++;
	}

	public void decreaseReferences() {
		nofReferences--;
	}

	boolean noReferences() {
		return nofReferences == 0;
	}

	private void setId(int i) {
		id = i;
		assert (!idBar.get(id)); // id should not already be used
		idBar.set(id);
	}

	private void createNewId() {
		id = 0;
		while (id < NAT_MAXNOFNAT) {
			if (!idBar.get(id)) {
				idBar.set(id);
				break;
			}
			id++;
		}
		assert (id < NAT_MAXNOFNAT); // must have breaked in "while" above
	}

	public void freeId() {
		idBar.clear(id);
	}

	public String getName() {
		return nationName;
	}

}
