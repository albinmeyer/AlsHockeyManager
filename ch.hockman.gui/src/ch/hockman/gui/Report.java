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

package ch.hockman.gui;

import java.net.URL;
import java.util.ResourceBundle;

import ch.hockman.model.common.Util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * Mask showing the report of one match (game).
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Report implements Initializable {

	@FXML
	private TextArea report;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		report.setEditable(false);
		String s = "";
		if (HockmanMain.currReport.getHome() != null) {
			assert (HockmanMain.currReport.getAway() != null); // see Round.EndRound()
			s = HockmanMain.currReport.getHome().getTeamName();
			s += " - ";
			s += HockmanMain.currReport.getAway().getTeamName();
			s += "  ";
			s += HockmanMain.currReport.getEndHomeScore();
			s += ":";
			s += HockmanMain.currReport.getEndAwayScore();
			s += " (";
			s += HockmanMain.currReport.getThird1HomeScore();
			s += ":";
			s += HockmanMain.currReport.getThird1AwayScore();
			s += ", ";
			s += HockmanMain.currReport.getThird2HomeScore();
			s += ":";
			s += HockmanMain.currReport.getThird2AwayScore();
			s += ", ";
			s += HockmanMain.currReport.getThird3HomeScore();
			s += ":";
			s += HockmanMain.currReport.getThird3AwayScore();
			s += ")";
			if (HockmanMain.currReport.isPenShoots()) {
				s += " Shootout";
			} else if (HockmanMain.currReport.isOvertime()) {
				s += " Overtime";
			}
			s += "\n";
			s += HockmanMain.currReport.getHome().getStadiumName();
			s += ", ";
			s += HockmanMain.currReport.getNofSpectators();
			s += Util.getModelResourceBundle().getString("L_SPECTATORS");
			s += "\n";
			s += "\nGoals:\n";
			s += HockmanMain.currReport.getGoals();
			s += "\n";
			s += Util.getModelResourceBundle().getString("L_PENALTIES");
			s += ":\n";
			s += HockmanMain.currReport.getPenHome();
			s += ":";
			s += HockmanMain.currReport.getPenAway();
			s += "\n\n";
			s += Util.getModelResourceBundle().getString("L_SHOTS");
			s += ":\n";
			s += HockmanMain.currReport.getHomeShots();
			s += ":";
			s += HockmanMain.currReport.getAwayShots();
			s += "\n\n";
			s += Util.getModelResourceBundle().getString("L_INJURIES");
			s += ":\n";
			s += HockmanMain.currReport.getInjuries();
			s += "\n\nBest Players:\n";
			if (HockmanMain.currReport.getBestHomePlayer().isMultipleName()) {
				s += HockmanMain.currReport.getBestHomePlayer().getFirstName();
				s += " ";
			}
			s += HockmanMain.currReport.getBestHomePlayer().getLastName();
			s += "\n";
			if (HockmanMain.currReport.getBestAwayPlayer().isMultipleName()) {
				s += HockmanMain.currReport.getBestAwayPlayer().getFirstName();
				s += " ";
			}
			s += HockmanMain.currReport.getBestAwayPlayer().getLastName();
			s += "\n";
		}
		report.appendText(s);
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
