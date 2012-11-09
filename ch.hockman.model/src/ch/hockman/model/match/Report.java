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

package ch.hockman.model.match;

import ch.hockman.model.GameLeagueFile;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * The report of one match (game between two teams).
 *
 * @author Albin
 *
 */
public class Report {
	private Team home;
	private Team away;
	private int nofSpectators; // also used for calculating finances !
	private int third1HomeScore;
	private int third2HomeScore;
	private int third3HomeScore;
	private int third1AwayScore;
	private int third2AwayScore;
	private int third3AwayScore;
	private int endHomeScore;
	private int endAwayScore;
	private int homeShots;
	private int awayShots;
	private boolean penShoots;
	private boolean overtime;
	private String goals;
	private String injuries;
	private Player bestHomePlayer;
	private Player bestAwayPlayer;
	private int penHome;
	private int penAway;

	public Report() {
		reset();
	}

	void reset() {
		setHome(null);
		setAway(null);
		setThird1HomeScore(0);
		setThird2HomeScore(0);
		setThird3HomeScore(0);
		setThird1AwayScore(0);
		setThird2AwayScore(0);
		setThird3AwayScore(0);
		setEndHomeScore(0);
		setEndAwayScore(0);
		setPenShoots(false);
		setOvertime(false);
		setGoals("");
		setInjuries("");
		setPenHome(0);
		setPenAway(0);
		setBestHomePlayer(null);
		setBestAwayPlayer(null);
		setHomeShots(0);
		setAwayShots(0);
		setNofSpectators(0);
	}

	void load(GameLeagueFile file, TeamPtrDivVector tpdv, PlayerPtrVector ppv) {
		file.parseSurroundingStartElement("Report");		
		setHome(tpdv.getIdTeam(file.getInt("HomeId")));
		setAway(tpdv.getIdTeam(file.getInt("AwayId")));
		setThird1HomeScore(file.getInt("Third1HomeScore"));
		setThird2HomeScore(file.getInt("Third2HomeScore"));
		setThird3HomeScore(file.getInt("Third3HomeScore"));
		setThird1AwayScore(file.getInt("Third1AwayScore"));
		setThird2AwayScore(file.getInt("Third2AwayScore"));
		setThird3AwayScore(file.getInt("Third3AwayScore"));
		setEndHomeScore(file.getInt("EndHomeScore"));
		setEndAwayScore(file.getInt("EndAwayScore"));
		setPenShoots(file.getInt("PenShoots") > 0);
		setOvertime(file.getInt("Overtime") > 0);
		setGoals(file.getString("Goals"));
		setInjuries(file.getString("Injuries"));
		setPenHome(file.getInt("PenHome"));
		setPenAway(file.getInt("PenAway"));
		setBestHomePlayer(ppv.GetIdPlayer(file.getInt("BestHomePlayerId")));
		setBestAwayPlayer(ppv.GetIdPlayer(file.getInt("BestAwayPlayerId")));
		setHomeShots(file.getInt("HomeShots"));
		setAwayShots(file.getInt("AwayShots"));
		setNofSpectators(file.getInt("NofSpectators"));
		file.parseSurroundingEndElement();		
	}

	void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Report");
		if (getHome() != null && getAway() != null) {
			xmlFile.write("HomeId", getHome().getId());
			xmlFile.write("AwayId", getAway().getId());
		} else {
			xmlFile.write("HomeId", -1);
			xmlFile.write("AwayId", -1);
		}
		xmlFile.write("Third1HomeScore", getThird1HomeScore());
		xmlFile.write("Third2HomeScore", getThird2HomeScore());
		xmlFile.write("Third3HomeScore", getThird3HomeScore());
		xmlFile.write("Third1AwayScore", getThird1AwayScore());
		xmlFile.write("Third2AwayScore", getThird2AwayScore());
		xmlFile.write("Third3AwayScore", getThird3AwayScore());
		xmlFile.write("EndHomeScore", getEndHomeScore());
		xmlFile.write("EndAwayScore", getEndAwayScore());
		xmlFile.write("PenShoots", isPenShoots());
		xmlFile.write("Overtime", isOvertime());
		xmlFile.write("Goals", getGoals());
		xmlFile.write("Injuries", getInjuries());
		xmlFile.write("PenHome", getPenHome());
		xmlFile.write("PenAway", getPenAway());
		if (getBestHomePlayer() != null && getBestAwayPlayer() != null) {
			xmlFile.write("BestHomePlayerId", getBestHomePlayer().getId());
			xmlFile.write("BestAwayPlayerId", getBestAwayPlayer().getId());
		} else {
			xmlFile.write("BestHomePlayerId", -1);
			xmlFile.write("BestAwayPlayerId", -1);
		}
		xmlFile.write("HomeShots", getHomeShots());
		xmlFile.write("AwayShots", getAwayShots());
		xmlFile.write("NofSpectators", getNofSpectators());
		xmlFile.writeSurroundingEndElement(); // Report
	}

	public Team getHome() {
		return home;
	}

	public void setHome(Team home) {
		this.home = home;
	}

	public Team getAway() {
		return away;
	}

	public void setAway(Team away) {
		this.away = away;
	}

	public int getNofSpectators() {
		return nofSpectators;
	}

	public void setNofSpectators(int nofSpectators) {
		this.nofSpectators = nofSpectators;
	}

	public int getThird1HomeScore() {
		return third1HomeScore;
	}

	public void setThird1HomeScore(int third1HomeScore) {
		this.third1HomeScore = third1HomeScore;
	}

	public int getThird2HomeScore() {
		return third2HomeScore;
	}

	public void setThird2HomeScore(int third2HomeScore) {
		this.third2HomeScore = third2HomeScore;
	}

	public int getThird3HomeScore() {
		return third3HomeScore;
	}

	public void setThird3HomeScore(int third3HomeScore) {
		this.third3HomeScore = third3HomeScore;
	}

	public int getThird1AwayScore() {
		return third1AwayScore;
	}

	public void setThird1AwayScore(int third1AwayScore) {
		this.third1AwayScore = third1AwayScore;
	}

	public int getThird2AwayScore() {
		return third2AwayScore;
	}

	public void setThird2AwayScore(int third2AwayScore) {
		this.third2AwayScore = third2AwayScore;
	}

	public int getThird3AwayScore() {
		return third3AwayScore;
	}

	public void setThird3AwayScore(int third3AwayScore) {
		this.third3AwayScore = third3AwayScore;
	}

	public int getEndHomeScore() {
		return endHomeScore;
	}

	public void setEndHomeScore(int endHomeScore) {
		this.endHomeScore = endHomeScore;
	}

	public int getEndAwayScore() {
		return endAwayScore;
	}

	public void setEndAwayScore(int endAwayScore) {
		this.endAwayScore = endAwayScore;
	}

	public int getHomeShots() {
		return homeShots;
	}

	public void setHomeShots(int homeShots) {
		this.homeShots = homeShots;
	}

	public int getAwayShots() {
		return awayShots;
	}

	public void setAwayShots(int awayShots) {
		this.awayShots = awayShots;
	}

	public boolean isPenShoots() {
		return penShoots;
	}

	public void setPenShoots(boolean penShoots) {
		this.penShoots = penShoots;
	}

	public boolean isOvertime() {
		return overtime;
	}

	public void setOvertime(boolean overtime) {
		this.overtime = overtime;
	}

	public String getGoals() {
		return goals;
	}

	public void setGoals(String goals) {
		this.goals = goals;
	}

	public String getInjuries() {
		return injuries;
	}

	public void setInjuries(String injuries) {
		this.injuries = injuries;
	}

	public Player getBestHomePlayer() {
		return bestHomePlayer;
	}

	public void setBestHomePlayer(Player bestHomePlayer) {
		this.bestHomePlayer = bestHomePlayer;
	}

	public Player getBestAwayPlayer() {
		return bestAwayPlayer;
	}

	public void setBestAwayPlayer(Player bestAwayPlayer) {
		this.bestAwayPlayer = bestAwayPlayer;
	}

	public int getPenHome() {
		return penHome;
	}

	public void setPenHome(int penHome) {
		this.penHome = penHome;
	}

	public int getPenAway() {
		return penAway;
	}

	public void setPenAway(int penAway) {
		this.penAway = penAway;
	}
}
