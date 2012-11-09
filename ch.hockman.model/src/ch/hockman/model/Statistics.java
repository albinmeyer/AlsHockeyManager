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

import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * The stats of a team.
 * 
 * @author Albin
 *
 */
public class Statistics {
	private Team playoffBeatenBy; // needed for playoff schedule calculation
	int playoffGameWins;
	private int rank;
	private int goalFor;
	private int goalAgainst;
	private int points;
	private int vic, vicOt, lost, lostOt;
	private boolean inPlayoff;

	public Statistics() {
		reset();
	}

	public void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("TeamStats");
		xmlFile.write("Rank", rank);
		xmlFile.write("GoalFor", goalFor);
		xmlFile.write("GoalAgainst", goalAgainst);
		xmlFile.write("Points", points);
		xmlFile.write("Vic", vic);
		xmlFile.write("VicOt", vicOt);
		xmlFile.write("Lost", lost);
		xmlFile.write("LostOt", lostOt);		
		if (getPlayoffBeatenBy() != null) {
			xmlFile.write("PlayoffBeatenby", getPlayoffBeatenBy().getId());
		} else {
			xmlFile.write("PlayoffBeatenby", -1);
		}
		xmlFile.write("PlayoffGameWins", playoffGameWins);
		xmlFile.write("InPlayoff", isInPlayoff());		
		xmlFile.writeSurroundingEndElement();
	}

	public void reset() {
		rank = 0;
		goalFor = 0;
		goalAgainst = 0;
		points = 0;
		vic = 0;
		vicOt = 0;
		lost = 0;
		lostOt = 0;		
		setPlayoffBeatenBy(null);
		playoffGameWins = 0;
		setInPlayoff(false);
	}

	void increaseGoals(int goalFor, int goalAgainst) {
		this.goalFor += goalFor;
		this.goalAgainst += goalAgainst;
	}

	void increasePoints(int points) {
		this.points += points;
	}

	void increaseVicLost(int v, int vot, int l, int lot) {
		vic += v;
		vicOt += vot;
		lost += l;
		lostOt += lot;
	}

	void setRank(int rank) {
		this.rank = rank;
	}

	void decreaseRank() {
		this.rank--;
	}

	public int getGoalsFor() {
		return this.goalFor;
	}

	public int getGoalsAgainst() {
		return this.goalAgainst;
	}

	public int getPoints() {
		return this.points;
	}

	public int getVic() {
		return this.vic;
	}

	public int getVicOt() {
		return this.vicOt;
	}

	public int getLost() {
		return this.lost;
	}

	public int getLostOt() {
		return this.lostOt;
	}
	
	public int getRank() {
		return this.rank;
	}

	public void load(GameLeagueFile file, TeamPtrDivVector tpdv) {
		file.parseSurroundingStartElement("TeamStats");
		rank = file.getInt("Rank");
		goalFor = file.getInt("GoalFor");
		goalAgainst = file.getInt("GoalAgainst");
		points = file.getInt("Points");
		vic = file.getInt("Vic");
		vicOt = file.getInt("VicOt");
		lost = file.getInt("Lost");
		lostOt = file.getInt("LostOt");		
		setPlayoffBeatenBy(tpdv.getIdTeam(file.getInt("PlayoffBeatenby")));
		playoffGameWins = file.getInt("PlayoffGameWins");
		setInPlayoff(file.getInt("InPlayoff") == 1);		
		file.parseSurroundingEndElement();
	}

	public Team getPlayoffBeatenBy() {
		return playoffBeatenBy;
	}

	public void setPlayoffBeatenBy(Team playoffBeatenBy) {
		this.playoffBeatenBy = playoffBeatenBy;
	}

	public boolean isInPlayoff() {
		return inPlayoff;
	}

	public void setInPlayoff(boolean inPlayoff) {
		this.inPlayoff = inPlayoff;
	}
}
