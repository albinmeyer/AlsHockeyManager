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

package ch.hockman.model.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A container of teams.
 *
 * @author Albin
 *
 */
public class TeamPtrDivVector {

	private static final String L_DIVISION_NAME = "Division Name";
	private static final String L_TRANSFER = "Transfer Teams";

	private List<List<Team>> divVector; // array with nofDivision + 1(transfer
	// teams) elements
	private List<String> divName; // array with nofDivision + 1(transfer teams)
	// elements

	private int nofDivision;
	private boolean locked;
	
	public TeamPtrDivVector() {
		locked = false;
		nofDivision = 0;
		divVector = new ArrayList<List<Team>>();
		divName = new ArrayList<String>();
	}

	public void lockDivisions(int nofDivision) {
		assert (divVector.size() == 0 && divName.size() == 0);
		for (int i = 0; i < nofDivision + 1; i++) {
			// league divisions plus transfer-division
			divName.add(L_DIVISION_NAME);
			divVector.add(new ArrayList<Team>());
		}
		divName.add(L_TRANSFER);
		locked = true;
		this.nofDivision = nofDivision;
	}

	public boolean isLocked() {
		return locked;
	}

	public void insertDivTeam(Team team, int division) {
		// division is 1,2,3,4,...
		// assert division being in the range of nofDivision + 1 (additional
		// transfer team division)
		assert (division > 0 && division <= nofDivision + 1);
		divVector.get(division - 1).add(team); // division is > 0
	}

	public void setDivName(String name, int division) {
		// assert division being in the range of nofDivision + 1 (additional
		// transfer team division)
		assert (division > 0 && division <= nofDivision + 1);
		divName.set(division - 1, name);
	}

	public void removeDivTeam(Team team) {
		// remove, but not delete
		int division = team.getDivision();
		// assert division being in the range of nofDivision + 1 (additional
		// transfer team division)
		assert (division <= nofDivision + 1);
		assert (divVector.size() != 0);
		divVector.get(division - 1).remove(team);
	}

	public int getNofTeams(int division) {
		// assert division being in the range of nofDivision + 1 (additional
		// transfer team division)
		if (divVector.size() == 0) {
			assert (!locked); // was not locked
			return 0;
		}
		assert (division > 0 && division <= nofDivision + 1);
		return divVector.get(division - 1).size();
	}

	/**
	 * Gets the i'th team in the division which is still in playoff,
	 * starting i by 0. This method assumes that the team lists
	 * are sorted by rank! So SortByTeamRank() must have been called before.
	 * @param division
	 * @param i
	 * @return the team in playoff
	 */
	public Team getPlayoffTeam(int division, int i) {
		// assert division being in the range of nofDivision
		assert (division > 0 && division <= nofDivision);
		assert (divVector.size() != 0 && getNofTeams(division) > i);
		List<Team> teamList = divVector.get(division - 1);
		int count = 0;
		for(Iterator<Team> it = teamList.iterator(); it.hasNext();) {
			Team team = it.next();
			if(team.notYetKnockedOutInPlayoff()) {
				if(count == i) {
					return team;
				}
				count++;
			}
		}
		assert(false); // should never come to here
		return null;
	}
	
	public Team getTeam(int division, int i) {
		// assert division being in the range of nofDivision + 1 (additional
		// transfer team division)
		assert (division > 0 && division <= nofDivision + 1);
		assert (divVector.size() != 0 && getNofTeams(division) > i);
		return divVector.get(division - 1).get(i);
	}

	public String getDivName(int division) {
		// assert division being in the range of nofDivision + 1 (additional
		// transfer team division)
		assert (division > 0 && division <= nofDivision + 1);
		return divName.get(division - 1);
	}

	public Team getIdTeam(int id) {
		for (int div = 1; div <= nofDivision + 1; div++) {
			int i = 0;
			final int nofTeams = getNofTeams(div);

			while (i < nofTeams) {
				if (divVector.get(div - 1).get(i).getId() == id) {
					return divVector.get(div - 1).get(i);
				}
				i++;
			}
		}
		return null;
	}

	public Team getFirstCoachedTeam() {
		Team found = null;
		for (int div = 1; div <= nofDivision + 1; div++) {
			int i = 0;
			final int nofTeams = getNofTeams(div);

			while (i < nofTeams) {
				// as an alternative we could also iterate with an iterator !!
				if (divVector.get(div - 1).get(i).getTeamState()
						.getTeamStateID() == TeamState.TeamStateID.COACHED) {
					if (found != null) {
						// more than one coached team found.
						throw new IllegalStateException(
								"More than one team coaching!");
					}
					found = divVector.get(div - 1).get(i);
				}
				i++;
			}
		}
		return found;
	}

	public void sortByTeamRank() {
		for (int i = 0; i < nofDivision; i++) {
			Collections.sort(divVector.get(i), new Comparator<Team>() {

				@Override
				public int compare(Team t1, Team t2) {
					return t1.getStatistics().getRank()
							- t2.getStatistics().getRank();
				}
			});
		}
	}
}
