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

import ch.hockman.model.Modus;
import ch.hockman.model.common.Util;
import ch.hockman.model.team.Team;

/**
 * One match (game) between two teams during a round.
 * An instance of this class is valid before, during and after playing.
 * During match, a PlayingMatch instance must be active.
 *
 * @author Albin
 *
 */
public class Match {
	public static final double MATCH_TICKET_PRICE = 0.03; // unit is K
	public static final int MATCH_EXPENSES = 5;

	private Team home;
	private Team away;
	private int endHomeScore;
	private int endAwayScore;
	private boolean overTime;
	private PlayingMatch playingMatch;
	
	public Match() {
		this.home = null;
		this.away = null;
		this.overTime = false;
		this.endHomeScore = 0;
		this.endAwayScore = 0;
		this.playingMatch = null;
	}

	public void begin(Modus modus, Report report, boolean playoff) {
		// alloc PlayingMatch instance
		// calc nof spectators (therefore "playoff" param is needed)
		if (home != null && away != null) {
			endHomeScore = 0;
			endAwayScore = 0;
			playingMatch = new PlayingMatch(report, home, away, modus.isAdvHome());
			// nof spectators
			int homeRank = home.getStatistics().getRank();
			int awayRank = away.getStatistics().getRank();
			if (homeRank == 0 && awayRank == 0) {
				// begin of season
				homeRank = modus.getNofTeams() / 2;
				awayRank = modus.getNofTeams() / 2;
			}
			int nofSpec = (int) ((1 - (float) (homeRank + awayRank)
					/ (2 * modus.getNofTeams()) / 2) * home.getCapacity());
			nofSpec += Util.random(home.getCapacity()
					/ (Math.abs(homeRank - awayRank) + 1));
			if (!playoff) {
				nofSpec -= Util.random(home.getCapacity() / 5);
			}
			if (nofSpec < 0) {
				nofSpec = 0;
			}
			if ((int) nofSpec > home.getCapacity()) {
				nofSpec = home.getCapacity();
			}
			report.setNofSpectators(nofSpec);
			home.getFinances().setMatch((int) (MATCH_TICKET_PRICE * nofSpec),
					MATCH_EXPENSES);
			away.getFinances().setMatch(0, MATCH_EXPENSES);
		}
	}

	public void doSecond(MatchTime mt) {
		doSecond(mt, false);
	}

	public void doSecond(MatchTime mt, boolean shootout) {
		// here the goals are calculated
		if (playingMatch != null) {
			// there is a match !
			playingMatch.doSecond(mt, shootout);
			endHomeScore = playingMatch.currHomeScore;
			endAwayScore = playingMatch.currAwayScore;
		}
	}

	public void justFinished() {
		// just finished match, but others may still be in overtime/penalty
		if (playingMatch != null) {
			// there is a match !
			playingMatch.justFinished();
		}
	}

	public void end() {
		// free PlayingMatch instance
		if (playingMatch != null) {
			this.overTime = playingMatch.overTime();			
			playingMatch = null;
			home.getLineUp().incPlayerNofMatches(home);
			away.getLineUp().incPlayerNofMatches(away);
		}
	}

	public boolean tie() {
		return home != null && away != null && endHomeScore == endAwayScore;
	}

	public void init(Team home, Team away) {
		init(home, away, 0, 0);
	}

	public void init(Team home, Team away, int homeScore, int awayScore) {
		// can be called several times, for playoffs reinit
		// => check for already played
		assert (home == null && away == null || this.home == null
				&& this.away == null);
		this.home = home;
		this.away = away;
		this.endHomeScore = homeScore;
		this.endAwayScore = awayScore;
	}

	public PlayingMatch getPlayingMatch() {
		return this.playingMatch;
	}

	public Team getResultHome() {
		return this.home;
	}

	public int getResultEndHomeScore() {
		return this.endHomeScore;
	}

	public int getResultEndAwayScore() {
		return this.endAwayScore;
	}

	public Team getResultAway() {
		return this.away;
	}
	
	public boolean getOverTime() {
		return this.overTime;
	}
}
