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

import ch.hockman.model.match.Match;
import ch.hockman.model.match.MatchTime;
import ch.hockman.model.match.News;
import ch.hockman.model.team.Team;

/**
 * One round of a season.
 *
 * @author Albin
 *
 */
public class Round {
	private boolean penalty; // penalty shots after 20 Min. overtime in playoff
	private Match[] matches;

	public Round() {
		setMatches(null);
		penalty = false;
	}

	public void init(int nofMatch) {
		assert (getMatches() == null);
		setMatches(new Match[nofMatch]);
		for (int i = 0; i < nofMatch; i++) {
			getMatches()[i] = new Match();
		}
	}

	public boolean doRoundSecond(News news, int nofMatches, MatchTime mt,
			boolean playoff) {
		// returns done, if the match can be finished
		boolean done = true;
		for (int i = 0; i < nofMatches; i++) {
			if (mt.getTotalSec() < 3600
					|| getMatches()[i].tie()
					&& (mt.getTotalSec() < 3900 || playoff
							&& (mt.getTotalSec() < 4800 || !penalty))) {
				if (mt.getTotalSec() >= 3600) {
					news.getReports()[i].setOvertime(true);
				}
				getMatches()[i].doSecond(mt); // calculate goals
				done = false;
				// } else if(matches[i].Tie() && playoff) {
			} else if (getMatches()[i].tie()) {
				// penalty shots (shootout)
				news.getReports()[i].setPenShoots(true);
				while (getMatches()[i].tie()) {
					getMatches()[i].doSecond(mt, true);
				}
			} else {
				getMatches()[i].justFinished();
			}
		}
		return done;
	}

	public int beginRound(News news, Modus modus, boolean playoff) {
		// calls Match.Begin(), returns number of matches
		int nofMatches = modus.nofMatches(playoff);
		for (int i = 0; i < nofMatches; i++) {
			getMatches()[i].begin(modus, news.getReports()[i], playoff);
		}
		penalty = modus.penalties;
		return nofMatches;
	}

	public void endRound(News news, int nofMatches, boolean playoff,
			boolean twoPoints) {
		for (int i = 0; i < nofMatches; i++) {
			getMatches()[i].end();
			Team home, away;
			int endHomeScore, endAwayScore;
			home = getMatches()[i].getResultHome();
			away = getMatches()[i].getResultAway();
			endHomeScore = getMatches()[i].getResultEndHomeScore();
			endAwayScore = getMatches()[i].getResultEndAwayScore();
			if (home != null && away != null) {
				// fill news
				news.getReports()[i].setHome(home);
				news.getReports()[i].setAway(away);
				news.getReports()[i].setEndHomeScore(endHomeScore);
				news.getReports()[i].setEndAwayScore(endAwayScore);
				news.getReports()[i].setBestHomePlayer(home.bestMatchPlayer());
				news.getReports()[i].setBestAwayPlayer(away.bestMatchPlayer());
				// take care of odd nof team modus
				if (!playoff) {
					// regular season: distribute points
					home.getStatistics().increaseGoals(endHomeScore,
							endAwayScore);
					away.getStatistics().increaseGoals(endAwayScore,
							endHomeScore);
					if (endHomeScore > endAwayScore) {
						if (twoPoints) {
							home.getStatistics().increasePoints(2);
							if (news.getReports()[i].isOvertime()) {
								away.getStatistics().increasePoints(1);
								home.getStatistics().increaseVicLost(0, 1, 0, 0);
								away.getStatistics().increaseVicLost(0, 0, 0, 1);
							} else {
								home.getStatistics().increaseVicLost(1, 0, 0, 0);
								away.getStatistics().increaseVicLost(0, 0, 1, 0);
							}
						} else {
							if (news.getReports()[i].isOvertime()) {
								home.getStatistics().increasePoints(2);
								away.getStatistics().increasePoints(1);
								home.getStatistics().increaseVicLost(0, 1, 0, 0);
								away.getStatistics().increaseVicLost(0, 0, 0, 1);
							} else {
								home.getStatistics().increasePoints(3);
								home.getStatistics().increaseVicLost(1, 0, 0, 0);
								away.getStatistics().increaseVicLost(0, 0, 1, 0);
							}
						}
					} else if (endHomeScore < endAwayScore) {
						if (twoPoints) {
							away.getStatistics().increasePoints(2);
							if (news.getReports()[i].isOvertime()) {
								home.getStatistics().increasePoints(1);
								away.getStatistics().increaseVicLost(0, 1, 0, 0);
								home.getStatistics().increaseVicLost(0, 0, 0, 1);
							} else {
								away.getStatistics().increaseVicLost(1, 0, 0, 0);
								home.getStatistics().increaseVicLost(0, 0, 1, 0);
							}
						} else { 
							if (news.getReports()[i].isOvertime()) {
								away.getStatistics().increasePoints(2);
								home.getStatistics().increasePoints(1);
								away.getStatistics().increaseVicLost(0, 1, 0, 0);
								home.getStatistics().increaseVicLost(0, 0, 0, 1);
							} else {
								away.getStatistics().increasePoints(3);								
								away.getStatistics().increaseVicLost(1, 0, 0, 0);
								home.getStatistics().increaseVicLost(0, 0, 1, 0);
							}
						}
					} else {
						assert(false); // no tie possible
					}
					// calc rank done in Game.NextMatch()
				} else {
					// playoff
					if (endHomeScore > endAwayScore) {
						home.getStatistics().playoffGameWins++;
					} else if (endHomeScore < endAwayScore) {
						away.getStatistics().playoffGameWins++;
					} else {
						assert (false); // there must be a winner !
					}
				}
			}
		}
	}

	void deInit() {
		assert (getMatches() != null);
		setMatches(null);
	}

	public Match[] getMatches() {
		return matches;
	}

	public void setMatches(Match[] matches) {
		this.matches = matches;
	}
}
