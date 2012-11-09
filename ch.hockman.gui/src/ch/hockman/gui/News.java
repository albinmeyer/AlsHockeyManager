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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ch.hockman.model.Modus;
import ch.hockman.model.common.Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

/**
 * Mask showing the news of this round.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class News implements Initializable {

	@FXML
	private TextArea transferList;

	@FXML
	private ListView matchList;

	@FXML
	private Label nextRoundLbl;

	private ch.hockman.model.match.News news;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		news = HockmanMain.game.getNews();
		transferList.setEditable(false);
		transferList.clear();
		transferList.appendText(news.getTransfers());
		Modus modus = HockmanMain.game.getLeague().getModus();
		int round = HockmanMain.game.getRound();
		if (round <= modus.getNofRounds()) {
			// regular season
			String s = Util.getModelResourceBundle().getString("L_NEXT_ROUND");
			s += "Regular " + Integer.toString(round);
			nextRoundLbl.setText(s);
		} else {
			// playoff
			String s = Util.getModelResourceBundle().getString("L_NEXT_ROUND");
			s += "Playoff ";
			s += 1 + (round - modus.getNofRounds() - 1) / modus.getGamesPerFinal();
			s += ", Game ";
			s += 1 + (round - modus.getNofRounds() - 1) % modus.getGamesPerFinal();
			nextRoundLbl.setText(s);
		}

		List<String> matchListStrings = new ArrayList<String>();
		for (int i = 0; i < news.getNofReports(); i++) {
			String s;
			if (news.getReports()[i].getHome() != null) {
				assert (news.getReports()[i].getAway() != null); // see Round.EndRound()
				if (news.getReports()[i].getHome().equals(HockmanMain.currManagedTeam)
						|| news.getReports()[i].getAway()
								.equals(HockmanMain.currManagedTeam)) {
					s = "!!! ";
				} else {
					s = "    ";
				}
				s += news.getReports()[i].getHome().getTeamName();
				s += " - ";
				s += news.getReports()[i].getAway().getTeamName();
				s += "  ";
				s += news.getReports()[i].getEndHomeScore();
				s += ":";
				s += news.getReports()[i].getEndAwayScore();
				if (news.getReports()[i].isPenShoots()) {
					s += " SO";
				} else if (news.getReports()[i].isOvertime()) {
					s += " OT";
				}
			} else {
				s = "";
			}
			matchListStrings.add(s);
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(matchListStrings);
		matchList.setItems(items);
		matchList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if (me.getClickCount() < 2) {
					return;
				}
				// it's a double click
				matchbericht(new ActionEvent());
			}
		});
		HockmanMain.setListViewCellFactory(matchList);

	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void matchbericht(ActionEvent event) {
		int index = matchList.getSelectionModel().getSelectedIndex();
		if (index < 0) {
			index = 0;
		}
		HockmanMain.currReport = news.getReports()[index];
		HockmanMain.stageHandler.showModalStageAndWait("Report.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_REPORT"));
	}

}
