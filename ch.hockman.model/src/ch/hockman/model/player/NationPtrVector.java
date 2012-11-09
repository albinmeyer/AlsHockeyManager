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

import java.util.ArrayList;
import java.util.List;

import ch.hockman.model.LeagueCreator;

/**
 * A container for nations of a league.
 *
 * @author Albin
 *
 */
public class NationPtrVector {
	private List<Nation> natVector;
	
	public NationPtrVector() {
		this.natVector = new ArrayList<Nation>();
	}

	public void insertNation(Nation nation) {
		this.natVector.add(nation);
	}

	public void removeNation(Nation nation) {
		// remove (but not delete) nation
		this.natVector.remove(nation);
	}

	public int getNofNations() {
		return this.natVector.size();
	}

	public void update() {
		// removes (and deletes) all obsolete nations
		int i = 0;
		while (i < getNofNations()) {
			// note: must call GetNofNations() every iteration,
			// because the return value changes after having deleted a nation
			Nation nat = natVector.get(i);
			if (nat.noReferences()) {
				nat.freeId();
				LeagueCreator.instance().delNation(nat, this);
			} else {
				i++;
			}
		}
	}

	public Nation getNation(int i) {
		// for league editor nation menu of player/team
		assert (getNofNations() > i);
		return natVector.get(i);
	}

	Nation getNation(String name) {
		// for adding player/team from lgu file
		int i = 0;
		final int nofNat = getNofNations();

		while (i < nofNat) {
			if (natVector.get(i).getName().equals(name)) {
				return natVector.get(i);
			}
			i++;
		}
		return null;
	}

	public Nation getIdNation(int id) {
		// for creating league/Game from file
		int i = 0;
		final int nofNat = getNofNations();

		while (i < nofNat) {
			// as an alternative we could also iterate with an iterator !!
			if (natVector.get(i).getId() == id) {
				return natVector.get(i);
			}
			i++;
		}
		return null;
	}
};
