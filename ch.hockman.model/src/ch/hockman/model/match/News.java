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
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * The news of a round, containing a list of match reports,
 * injuries, transfers.
 *
 * @author Albin
 *
 */
public class News {
	private String transfers;
	private Report[] reports;
	private final int nofReports;

	public News(int nofRep) {
		nofReports = nofRep;
		setReports(new Report[nofRep]);
		for (int i = 0; i < nofRep; i++) {
			getReports()[i] = new Report();
		}
		setTransfers("");
	}

	public void reset() {
		for (int i = 0; i < getNofReports(); i++) {
			getReports()[i].reset();
		}
		setTransfers("");
	}

	public void load(GameLeagueFile file, TeamPtrDivVector tpdv, PlayerPtrVector ppv) {
		for (int i = 0; i < getNofReports(); i++) {
			getReports()[i].load(file, tpdv, ppv);
		}
		setTransfers(file.getString("Transfers"));
	}

	public void save(GameLeagueFile xmlFile) {
		for (int i = 0; i < getNofReports(); i++) {
			getReports()[i].save(xmlFile);
		}
		xmlFile.write("Transfers", getTransfers());
	}

	public String getTransfers() {
		return transfers;
	}

	public void setTransfers(String transfers) {
		this.transfers = transfers;
	}

	public Report[] getReports() {
		return reports;
	}

	public void setReports(Report[] reports) {
		this.reports = reports;
	}

	public int getNofReports() {
		return nofReports;
	}
}
