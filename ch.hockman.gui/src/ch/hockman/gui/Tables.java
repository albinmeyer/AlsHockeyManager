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
import ch.hockman.model.Statistics;
import ch.hockman.model.common.Util;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * Mask showing the standings (team tables) of the current season.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Tables implements Initializable {

	@FXML
	private Button btnDiv1;

	@FXML
	private Button btnDiv2;

	@FXML
	private Button btnDiv3;

	@FXML
	private Button btnDiv4;

	@FXML
	private Label lblDiv;

	@FXML
	private ListView table;

	private int currDiv;

	private TeamPtrDivVector tpdv;

	private int nofPlayoffRanks;

	private int nofDiv;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.tpdv = HockmanMain.game.getLeague().getTeamDivVector();
		Modus m = HockmanMain.game.getLeague().getModus();
		this.nofDiv = m.getNofDivisions();
		this.nofPlayoffRanks = (int) (Math.pow(2, m.getNofPlayoffFinals()) / m.getNofDivisions());
		currDiv = HockmanMain.currManagedTeam.getDivision();
		tpdv.sortByTeamRank();
		show();
	}

	private void show() {
		String s = Integer.toString(currDiv);
		s += " (";
		s += tpdv.getDivName(currDiv);
		s += ")";
		lblDiv.setText(s);

		switch (nofDiv) {
		case 1:
			btnDiv1.setDisable(false);
			btnDiv2.setDisable(true);
			btnDiv3.setDisable(true);
			btnDiv4.setDisable(true);
			break;
		case 2:
			btnDiv1.setDisable(false);
			btnDiv2.setDisable(false);
			btnDiv3.setDisable(true);
			btnDiv4.setDisable(true);
			break;
		case 3:
			btnDiv1.setDisable(false);
			btnDiv2.setDisable(false);
			btnDiv3.setDisable(false);
			btnDiv4.setDisable(true);
			break;
		case 4:
			btnDiv1.setDisable(false);
			btnDiv2.setDisable(false);
			btnDiv3.setDisable(false);
			btnDiv4.setDisable(false);
			break;
		default:
			assert (false);
		}
		int nofTeams = tpdv.getNofTeams(currDiv);
		List<String> tableItems = new ArrayList<String>();
		for (int i = 0; i < nofTeams; i++) {
			Team team = tpdv.getTeam(currDiv, i);
			Statistics statistics = team.getStatistics();
			String s1;
			if (HockmanMain.currManagedTeam.equals(team)) {
				s1 = "!!! ";
			} else {
				s1 = "    ";
			}
			if (nofPlayoffRanks >= i + 1) {
				s1 += "*** ";
			} else {
				s1 += "    ";
			}
			s1 += Integer.toString(statistics.getRank()) + ". ";
			while (s1.length() < 9) {
				s1 += ' ';
			}
			s1 += team.getTeamName();
			while (s1.length() < 29) {
				s1 += ' ';
			}
			int vic, vicOt, lost, lostOt;
			vic = statistics.getVic();
			vicOt = statistics.getVicOt();
			lost = statistics.getLost();
			lostOt = statistics.getLostOt();			
			s1 += Integer.toString(vic + vicOt + lost + lostOt) + "   "
					+ Integer.toString(vic) + " " + Integer.toString(vicOt) + " "
					+ Integer.toString(lostOt) + " " + Integer.toString(lost) + "  ";
			while (s1.length() < 47) {
				s1 += ' ';
			}
			int goalFor = statistics.getGoalsFor();
			int goalAgainst = statistics.getGoalsAgainst();
			s1 += Integer.toString(goalFor) + ":"
					+ Integer.toString(goalAgainst) + " ";
			while (s1.length() < 57) {
				s1 += ' ';
			}
			s1 += Integer.toString(statistics.getPoints());
			tableItems.add(s1);
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(tableItems);
		table.setItems(items);
		table.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if (me.getClickCount() < 2) {
					return;
				}
				// it's a double click
				teamDetails(new ActionEvent());
			}
		});

		HockmanMain.setListViewCellFactory(table);
	}

	@FXML
	private void btnDiv1Clicked(ActionEvent event) {
		currDiv = 1;
		show();
	}

	@FXML
	private void btnDiv2Clicked(ActionEvent event) {
		currDiv = 2;
		show();
	}

	@FXML
	private void btnDiv3Clicked(ActionEvent event) {
		currDiv = 3;
		show();
	}

	@FXML
	private void btnDiv4Clicked(ActionEvent event) {
		currDiv = 4;
		show();
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void teamDetails(ActionEvent event) {
		// determine the chosen team
		int index = table.getSelectionModel().getSelectedIndex();
		if (index < 0) {
			index = 0;
		}
		HockmanMain.lineupTeam = tpdv.getTeam(currDiv, index);
		HockmanMain.stageHandler.showModalStageAndWait("OpTeam.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_OPTEAM"));
	}

}
