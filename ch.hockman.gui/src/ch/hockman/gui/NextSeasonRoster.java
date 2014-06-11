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

import ch.hockman.model.League;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * Mask showing the roster of the managed team of next year.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class NextSeasonRoster implements Initializable {

	@FXML
	private ListView<String> nextRoster;

	private League league;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		league = HockmanMain.game.getLeague();
		league.getPlayerVector().SortPlayerPos();
		Player player = HockmanMain.currManagedTeam.getTransfer()
				.getNextSeasFirst(league.getPlayerVector(),
						HockmanMain.currManagedTeam);
		List<String> listItems = new ArrayList<String>();
		while (player != null) {
			String s = player.getLastName();
			s += " ";
			s += player.getFirstName();
			while (s.length() < 30) {
				s += ' ';
			}
			s += " (";
			s += player.getPosition().posName();
			s += ", ";
			s += player.getAge();
			s += " y)";
			listItems.add(s);
			player = HockmanMain.currManagedTeam.getTransfer().getNextSeasNext(
					league.getPlayerVector(), HockmanMain.currManagedTeam);
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(listItems);
		nextRoster.setItems(items);
		nextRoster.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if (me.getClickCount() < 2) {
					return;
				}
				// it's a double click
				playerDetails(new ActionEvent());
			}
		});
		HockmanMain.setListViewCellFactory(nextRoster);

	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void playerDetails(ActionEvent event) {
		Player player = HockmanMain.currManagedTeam.getTransfer()
				.getNextSeasFirst(league.getPlayerVector(),
						HockmanMain.currManagedTeam);
		int i = 0;
		while (i < nextRoster.getSelectionModel().getSelectedIndex()) {
			i++;
			player = HockmanMain.currManagedTeam.getTransfer().getNextSeasNext(
					league.getPlayerVector(), HockmanMain.currManagedTeam);
		}
		if (player != null) {
			HockmanMain.currPlayer = player;
			HockmanMain.stageHandler
					.showModalStageAndWait("PlayerDetails.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_PLAYERDETAILS"));
		}
	}

}
