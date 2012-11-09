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

import ch.hockman.model.common.Util;
import ch.hockman.model.match.Match;
import ch.hockman.model.player.Player;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamState;

/**
 * Handling all the finances (income, expenses) of a team.
 *
 * @author Albin
 *
 */
public class Finances {

	private static class Income {
		public int transfer, match, sponsor, commercials;

		public int Sum() {
			return transfer + match + sponsor + commercials;
		}
	}

	private static class Expenses {
		public int transfer;
		public int match, youth, material, wages, interests;

		public int sum() {
			return transfer + match + youth + material + wages + interests;
		}
	}
	
	static final int FIN_CREDIT_INTEREST = 10;

	private Income income;
	private Expenses expenses;
	private int youthSaved; // saved youth expenses
	private int previousTotal;
	private int currTotal;

	public Finances() {
		income = new Income();
		income.transfer = 0;
		income.match = 0;
		income.sponsor = 0;
		income.commercials = 0;
		expenses = new Expenses();
		expenses.transfer = 0;
		expenses.match = 0;
		expenses.youth = 10;
		expenses.material = 0;
		expenses.wages = 0;
		expenses.interests = 0;
		youthSaved = 0;
		previousTotal = 500;
		currTotal = 500;
	}

	public void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Finances");
		xmlFile.write("IncomeTransfer", income.transfer);
		xmlFile.write("IncomeMatch", income.match);
		xmlFile.write("IncomeSponsor", income.sponsor);
		xmlFile.write("IncomeCommercials", income.commercials);
		xmlFile.write("ExpTransfer", expenses.transfer);
		xmlFile.write("ExpMatch", expenses.match);
		xmlFile.write("ExpYouth", expenses.youth);
		xmlFile.write("ExpMat", expenses.material);
		xmlFile.write("ExpWages", expenses.wages);
		xmlFile.write("ExpInterests", expenses.interests);
		xmlFile.write("Youth", youthSaved);
		xmlFile.write("PrevTotal", previousTotal);
		xmlFile.write("CurrTotal", currTotal);
		xmlFile.writeSurroundingEndElement();
	}

	public void financesKI(Team team) {
		// calc youth
		expenses.youth = currTotal / 50 + Util.random(5) - Util.random(5);
		if (expenses.youth < 0) {
			expenses.youth = 0;
		}
	}

	public void doFinances(Team team, int additionalDays, int nofRounds) {
		youthSaved += expenses.youth;
		Team.Sponsoring sponsoring = team.getSponsoring();
		// 12/21/98 more cash => less sponsoring
		int ran;
		if (sponsoring == Team.Sponsoring.GOOD) {
			ran = 200 - currTotal / 10;
			if (ran < 1) {
				ran = 1;
			}
		} else if (sponsoring == Team.Sponsoring.OK) {
			ran = 160 - currTotal / 10;
			if (ran < 1) {
				ran = 1;
			}
		} else {
			assert (sponsoring == Team.Sponsoring.POOR);
			ran = 120 - currTotal / 10;
			if (ran < 1) {
				ran = 1;
			}
		}
		income.sponsor = Util.random(ran);
		if (team.getTeamState().getTeamStateID() == TeamState.TeamStateID.COACHED) {
			// game level
			if (Options.level == Options.Level.ROOKIE) {
				income.sponsor += 10;
			} else if (Options.level == Options.Level.PROF
					&& income.sponsor >= 10) {
				income.sponsor -= 10;
			}
		}
		income.transfer = 0; // reset
		income.commercials = Util.random(30 + 4 * additionalDays);
		if (team.getTeamState().getTeamStateID() == TeamState.TeamStateID.NONLEAGUE) {
			// don't reset income.match and expenses.match for non-transfer teams !
			income.match = (int) (team.getCapacity() * Match.MATCH_TICKET_PRICE / 2);
			// div 2 because average of a round
			expenses.match = Match.MATCH_EXPENSES;
		}
		expenses.material = 5;
		expenses.transfer = 0; // reset
		expenses.wages = 0; // calc wage
		int nofPlayers = team.getNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = team.getPlayer(i);
			expenses.wages += player.getContracts().getCurrContrWage();
		}
		if (currTotal >= 0) {
			expenses.interests = 0;
		} else {
			expenses.interests = 1 - currTotal / FIN_CREDIT_INTEREST
					/ nofRounds;
		}
		previousTotal = currTotal;
		calcCurrTotal();
	}

	public void setYouth(int youth) {
		// for GUI of managed team
		if (youth >= 0 && youth <= 100) {
			expenses.youth = youth;
		} else {
			expenses.youth = 10;
		}
		calcCurrTotal();
	}

	public int getAndResetSavedYouth() {
		// saved youth expenses
		int save = youthSaved;
		youthSaved = 0;
		return save;
	}

	void setTransfer(int transferIn, int transferOut) {
		income.transfer += transferIn;
		expenses.transfer += transferOut;
		calcCurrTotal();
	}

	public void setMatch(int matchIn, int matchOut) {
		income.match = matchIn;
		expenses.match = matchOut;
	}

	public int getTransferIn() {
		return this.income.transfer;
	}

	public int getMatchIn() {
		return this.income.match;
	}

	public int getSponsor() {
		return this.income.sponsor;
	}

	public int getCommercials() {
		return this.income.commercials;
	}

	public int getTransferOut() {
		return this.expenses.transfer;
	}

	public int getMatchOut() {
		return this.expenses.match;
	}

	public int getYouth() {
		return this.expenses.youth;
	}

	public int getMaterial() {
		return this.expenses.material;
	}

	public int getWages() {
		return this.expenses.wages;
	}

	public int getInterests() {
		return this.expenses.interests;
	}

	public int getPreviousTotal() {
		return this.previousTotal;
	}

	public int getCurrTotal() {
		return this.currTotal;
	}

	public int calcCurrTotal() {
		assert ((long) previousTotal + income.Sum() - expenses.sum() <= Integer.MAX_VALUE);
		currTotal = previousTotal + income.Sum() - expenses.sum();
		return currTotal;
	}

	public void load(GameLeagueFile file) {
		file.parseSurroundingStartElement("Finances");
		income = new Income();
		income.transfer = file.getInt("IncomeTransfer");
		income.match = file.getInt("IncomeMatch");
		income.sponsor = file.getInt("IncomeSponsor");
		income.commercials = file.getInt("IncomeCommercials");
		expenses = new Expenses();
		expenses.transfer = file.getInt("ExpTransfer");
		expenses.match = file.getInt("ExpMatch");
		expenses.youth = file.getInt("ExpYouth");
		expenses.material = file.getInt("ExpMat");
		expenses.wages = file.getInt("ExpWages");
		expenses.interests = file.getInt("ExpInterests");
		youthSaved = file.getInt("Youth");
		previousTotal = file.getInt("PrevTotal");
		currTotal = file.getInt("CurrTotal");
		file.parseSurroundingEndElement();
	}
}
