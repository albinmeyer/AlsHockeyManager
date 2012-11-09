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

import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * A transfer offer to a player of another team.
 *
 * @author Albin
 *
 */
public class Offer {
	private Player player;
	private int years;
	private int wage;
	private int fee;
	private boolean immediately;
	private Player trade;
	private Team buyer;
	private Team seller;

	public Offer() {
		setPlayer(null);
		setYears(0);
		setWage(0);
		setFee(0);
		setImmediately(false);
		setTrade(null);
		setBuyer(null);
		setSeller(null);
	}

	void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Offer");
		xmlFile.write("PlayerId", getPlayer().getId());
		xmlFile.write("Years", getYears());
		xmlFile.write("Wage", getWage());
		xmlFile.write("Fee", getFee());
		xmlFile.write("Immediately", isImmediately());
		if (getTrade() != null) {
			xmlFile.write("TradeId", getTrade().getId());
		} else {
			xmlFile.write("TradeId", -1);
		}
		xmlFile.write("BuyerId", getBuyer().getId());
		if (getSeller() != null) {
			xmlFile.write("SellerId", getSeller().getId());
		} else {
			xmlFile.write("SellerId", -1);
		}
		xmlFile.writeSurroundingEndElement();
	}

	public void load(GameLeagueFile file, TeamPtrDivVector tpdv,
			PlayerPtrVector ppv) {
		file.parseSurroundingStartElement("Offer");
		setPlayer(ppv.GetIdPlayer(file.getInt("PlayerId")));
		setYears(file.getInt("Years"));
		setWage(file.getInt("Wage"));
		setFee(file.getInt("Fee"));
		setImmediately(file.getInt("Immediately") > 0);
		setTrade(ppv.GetIdPlayer(file.getInt("TradeId")));
		setBuyer(tpdv.getIdTeam(file.getInt("BuyerId")));
		setSeller(tpdv.getIdTeam(file.getInt("SellerId")));
		file.parseSurroundingEndElement();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getYears() {
		return years;
	}

	public void setYears(int years) {
		this.years = years;
	}

	public int getWage() {
		return wage;
	}

	public void setWage(int wage) {
		this.wage = wage;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	public boolean isImmediately() {
		return immediately;
	}

	public void setImmediately(boolean immediately) {
		this.immediately = immediately;
	}

	public Player getTrade() {
		return trade;
	}

	public void setTrade(Player trade) {
		this.trade = trade;
	}

	public Team getBuyer() {
		return buyer;
	}

	public void setBuyer(Team buyer) {
		this.buyer = buyer;
	}

	public Team getSeller() {
		return seller;
	}

	public void setSeller(Team seller) {
		this.seller = seller;
	}
}
