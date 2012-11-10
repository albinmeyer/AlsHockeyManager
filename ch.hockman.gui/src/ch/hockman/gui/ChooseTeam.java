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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import ch.hockman.model.common.Util;
import ch.hockman.model.team.CoachedLeagueTeam;
import ch.hockman.model.team.Team;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * The mask at starting a game for choosing the managed team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class ChooseTeam implements Initializable {

	@FXML
	private ListView teamList;

	@FXML
	private Label leagueDataLbl; 

	@FXML
	private Label questionLbl; 
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if (HockmanMain.game != null) {
			Object[] messageArguments = {
					HockmanMain.game.getLeague().getLeagueName(),
					Integer.toString(HockmanMain.game.getYear()),
					HockmanMain.game.getLeague().getModus().getNofDivisions(),
					HockmanMain.game.getLeague().getModus().getNofTeams()
				};
			MessageFormat formatter = new MessageFormat("");
			formatter.applyPattern(Util.getModelResourceBundle().getString("L_LEAGUE_DATA"));
			String output = formatter.format(messageArguments);
			leagueDataLbl.setText(output);
			questionLbl.setText(Util.getModelResourceBundle().getString("L_WHICH_TEAM"));
			List<String> teamNames = new ArrayList<String>();
			for (int div = 1; div <= (HockmanMain.game.getLeague().getModus().getNofDivisions()); div++) {
				int nofTeams = HockmanMain.game.getLeague().getTeamDivVector()
						.getNofTeams(div);
				for (int i = 0; i < nofTeams; i++) {
					Team team = HockmanMain.game.getLeague().getTeamDivVector()
							.getTeam(div, i);
					teamNames.add(team.getTeamName());
				}
			}
			ObservableList<String> items = FXCollections
					.observableArrayList(teamNames);
			teamList.setItems(items);
			teamList.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent me) {
					if (me.getClickCount() < 2) {
						return;
					}
					// it's a double click
					ok(new ActionEvent());
				}
			});
			HockmanMain.setListViewCellFactory(teamList);
		}
	}

	@FXML
	private void ok(ActionEvent event) {
		try {
			if (HockmanMain.game != null) {
				// determine the chosen team
				int index = teamList.getSelectionModel().getSelectedIndex();
				if (index < 0) {
					index = 0;
				}
				int div = index
						/ (HockmanMain.game.getLeague().getModus().getNofTeams()) + 1;
				int teamNo = index
						% (HockmanMain.game.getLeague().getModus().getNofTeams());
				Team copyTeam = HockmanMain.game.getLeague().getTeamDivVector()
						.getTeam(div, teamNo);
				copyTeam.setTeamState(CoachedLeagueTeam.instance());
				HockmanMain.currManagedTeam = HockmanMain.game.getLeague()
						.getTeamDivVector().getFirstCoachedTeam();
			} else {
				HockmanMain.currManagedTeam = null;
			}
			if (HockmanMain.currManagedTeam == null) {
				HockmanMain.stageHandler.closeModalStage();
			} else {
				// everything ok
				HockmanMain.modified = false;
				HockmanMain.eofSeason = false;
				HockmanMain.stageHandler.closeModalStage();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			HockmanMain.msgBoxLbl = "An exception occured:\n" + t.getMessage();
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
	}

}
