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

import ch.hockman.model.match.MatchTime;
import ch.hockman.model.match.News;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * A game the player is playing with one particular managed team.
 *
 * @author Albin
 *
 */
public class Game {
	private int year;
	private int round;
	private League league;
	private Schedule schedule;
	private News news;

	public Game(League league) {
		// called by GameCreator
		news = new News(league.getModus().getNofDivisions()
				* ((league.getModus().getNofTeams() + 1) / 2));
		// called by GameCreator
		this.league = league;
		this.year = league.getStartingYear();
		this.round = 1;
		this.schedule = null;
	}

	void loadGam(GameLeagueFile file) {
		assert (schedule == null);
		this.schedule = new Schedule(league.getModus());
		this.schedule.load(file, this.league.getTeamDivVector());
		this.year = file.getInt("Year");
		this.round = file.getInt("Round");
		this.news.load(file, this.league.getTeamDivVector(),
				this.league.getPlayerVector());
	}

	void saveGam(GameLeagueFile xmlFile) {
		assert (this.schedule != null);
		this.schedule.save(xmlFile);
		xmlFile.write("Year", this.year);
		xmlFile.write("Round", this.round);
		this.news.save(xmlFile);
	}

	public void newSeason() {
		assert (schedule == null);
		round = 1;
		this.schedule = new Schedule(league.getModus());
		this.schedule.create(league.getTeamDivVector());
		news.reset();

		// init teams
		int nofDiv = league.getModus().getNofDivisions();
		TeamPtrDivVector tpdv = league.getTeamDivVector();
		for (int div = 1; div <= nofDiv + 1; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			for (int i = 0; i < nofTeams; i++) {
				Team team = tpdv.getTeam(div, i);
				team.initSeason();
			}
		}

		// init players
		PlayerPtrVector ppv = this.getLeague().getPlayerVector();
		int nofPlayers = ppv.GetNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = ppv.GetPlayer(i);
			player.initSeason(year);
		}

		// do daily stuff
		Daily.doDaily(league.getPlayerVector(), league.getTeamDivVector(),
				league.getModus(), round, schedule, news);
	}

	public League getLeague() {
		return league;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public News getNews() {
		return news;
	}

	public int getYear() {
		return year;
	}

	public int getRound() {
		return round;
	}

	public void afterLastRound() {
		// before end of season
		round = 1;
	}

	public void eoSeason() {
		// end of season
		assert (round == 1); // Game.AfterLastRound() must have been called.
		schedule = null;
		year++;
		news.reset();

		// update teams
		int nofDiv = league.getModus().getNofDivisions();
		TeamPtrDivVector tpdv = league.getTeamDivVector();
		for (int div = 1; div <= nofDiv + 1; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			for (int i = 0; i < nofTeams; i++) {
				Team team = tpdv.getTeam(div, i);
				team.eofSeason(this);
			}
		}

		// update players
		PlayerPtrVector ppv = this.getLeague().getPlayerVector();

		for (int i = 0; i < ppv.GetNofPlayers(); i++) {
			Player player;
			do {
				player = ppv.GetPlayer(i);
			} while (player.updateAfterSeason(this));
		}

	}

	public void doResult() {
		// do match
		int nofMatches = schedule.beginRound(round, news, league.getModus());
		MatchTime mt = new MatchTime();
		boolean done = false;
		while (!done) {
			done = schedule.doRoundSecond(round, news, nofMatches, mt);
			mt.tick();
		}
		// distribute points/playoffWins done in Round.EndRound()
		schedule.endRound(round, news, nofMatches, league.getModus().isTwoPoints());

	}

	public void afterMatch() {
		// calc Team ranks, call Daily.DoDaily()
		// calculate the table
		int nofDiv = league.getModus().getNofDivisions();
		TeamPtrDivVector tpdv = league.getTeamDivVector();
		for (int div = 1; div <= nofDiv; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			int i, j, goaldiff;
			for (i = 0; i < nofTeams; i++) {
				Team teami = tpdv.getTeam(div, i);
				Statistics teamiStatistics = teami.getStatistics();
				if (round <= league.getModus().getNofRounds()) {
					// regular season
					// calc team ranks:
					teamiStatistics.setRank(nofTeams);
					for (j = 0; j < nofTeams; j++) {
						Team teamj = tpdv.getTeam(div, j);
						Statistics teamjStatistics = teamj.getStatistics();
						if (teamiStatistics.getPoints() > teamjStatistics
								.getPoints()) {
							teamiStatistics.decreaseRank();
						} else if (teamiStatistics.getPoints() == teamjStatistics
								.getPoints()) {
							int fori = teamiStatistics.getGoalsFor();
							int forj = teamjStatistics.getGoalsFor();
							int againsti = teamiStatistics.getGoalsAgainst();
							int againstj = teamjStatistics.getGoalsAgainst();
							goaldiff = fori - againsti - forj + againstj;
							if (goaldiff > 0) {
								teamiStatistics.decreaseRank();
							} else if ((fori > forj) && (goaldiff == 0)) {
								teamiStatistics.decreaseRank();
							}
						}
					}
					for (j = 0; j < i; j++) {
						Team teamj = tpdv.getTeam(div, j);
						if (teamiStatistics.getRank() == teamj.getStatistics()
								.getRank()) {
							teamiStatistics.decreaseRank();
						}
					}
				} else {
					// playoffs: who is out ?
					if (teamiStatistics.playoffGameWins > league.getModus().getGamesPerFinal() / 2) {
						teamiStatistics.setPlayoffBeatenBy(null);
					} else {
						teamiStatistics.setInPlayoff(false);
					}
				}
			}
		}

		// do the daily stuff after having played the matches
		round++;
		Daily.doDaily(league.getPlayerVector(), league.getTeamDivVector(),
				league.getModus(), round, schedule, news);

	}

	public void beforeMatch() {
		// reset the news and match income
		news.reset();
		// reset the messages
		int nofDiv = league.getModus().getNofDivisions();
		TeamPtrDivVector tpdv = league.getTeamDivVector();
		for (int div = 1; div <= nofDiv; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			for (int i = 0; i < nofTeams; i++) {
				Team team = tpdv.getTeam(div, i);
				team.getMessages().reset();
				team.getFinances().setMatch(0, 0); // reset match income/expenses
			}
		}
	}

	public Team getChampion() {
		Team champion = null;
		int nofDiv = league.getModus().getNofDivisions();
		TeamPtrDivVector tpdv = league.getTeamDivVector();
		for (int div = 1; div <= nofDiv; div++) {
			int nofTeams = tpdv.getNofTeams(div);
			for (int i = 0; i < nofTeams; i++) {
				Team team = tpdv.getTeam(div, i);
				if (team.getStatistics().getPlayoffBeatenBy() == null
						&& team.getStatistics().playoffGameWins > 0) {
					assert (champion == null); // only one champion !!
					champion = team;
				}
			}
		}
		assert (champion != null); // there is a champion !!
		return champion;
	}
}
