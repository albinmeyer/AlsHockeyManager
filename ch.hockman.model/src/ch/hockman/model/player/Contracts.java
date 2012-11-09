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

import ch.hockman.model.team.Team;

/**
 * The contracts of a player (current, next season).
 *
 * @author Albin
 *
 */
public class Contracts {
	
	public static interface Contract {
		String getTeamName();

		int getId();

		Team getTeam();
	}

	static class IdContract implements Contract {
		public IdContract(int id) {
			this.id = id;
		}

		public int id;

		@Override
		public String getTeamName() {
			return "";
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public Team getTeam() {
			return null;
		}
	}

	public static class TContract implements Contract {
		public TContract(Team t) {
			this.t = t;
		}

		public Team t;

		@Override
		public String getTeamName() {
			return t.getTeamName();
		}

		@Override
		public int getId() {
			return t.getId();
		}

		@Override
		public Team getTeam() {
			return t;
		}
	}

	private Contract currContr;
	private Contract nextContr;
	private int currContrYears;
	private int nextContrYears;
	private int currContrWage;
	private int nextContrWage;
	private int fee;
	private boolean noContractThisRound; // GUI team should not try to contract same player more than once a round
	
	public Contracts() {
		setCurrContr(new IdContract(-1));
		setNextContr(new IdContract(-1));
		setCurrContrYears(1);
		setNextContrYears(0);
		setCurrContrWage(0);
		setNextContrWage(0);
		setFee(0);
		setNoContractThisRound(false);
	}

	public Contract getCurrContr() {
		return currContr;
	}

	public void setCurrContr(Contract currContr) {
		this.currContr = currContr;
	}

	public Contract getNextContr() {
		return nextContr;
	}

	public void setNextContr(Contract nextContr) {
		this.nextContr = nextContr;
	}

	public int getCurrContrYears() {
		return currContrYears;
	}

	public void setCurrContrYears(int currContrYears) {
		this.currContrYears = currContrYears;
	}

	public int getNextContrYears() {
		return nextContrYears;
	}

	public void setNextContrYears(int nextContrYears) {
		this.nextContrYears = nextContrYears;
	}

	public int getCurrContrWage() {
		return currContrWage;
	}

	public void setCurrContrWage(int currContrWage) {
		this.currContrWage = currContrWage;
	}

	public int getNextContrWage() {
		return nextContrWage;
	}

	public void setNextContrWage(int nextContrWage) {
		this.nextContrWage = nextContrWage;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public boolean isNoContractThisRound() {
		return noContractThisRound;
	}

	public void setNoContractThisRound(boolean noContractThisRound) {
		this.noContractThisRound = noContractThisRound;
	}
}
