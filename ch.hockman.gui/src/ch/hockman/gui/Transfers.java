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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Contracts;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;

/**
 * Mask for choosing a player to transfer from another team or free agent.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Transfers implements Initializable {

	@FXML
	private Label cashAvailableLbl;

	@FXML
	private ListView<String> playerList;

	private PlayerPtrVector ppv;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.ppv = HockmanMain.game.getLeague().getPlayerVector();
		showForm();
	}

	private void showForm() {
		cashAvailableLbl.setText("Cash available: "
				+ HockmanMain.currManagedTeam.getFinances().calcCurrTotal());
		List<String> items = new ArrayList<String>();
		int nofPlayers = ppv.GetNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = ppv.GetPlayer(i);
			String s = player.getLastName();
			while (s.length() < 18) {
				s += ' ';
			}
			s += player.getPosition().posName();
			while (s.length() < 22) {
				s += ' ';
			}
			s += player.getNation().getName();
			while (s.length() < 34) {
				s += ' ';
			}
			Contracts contracts = player.getContracts();
			if (contracts.getCurrContr().getTeam() != null) {
				s += contracts.getCurrContr().getTeam().getTeamName();
			} else {
				s += Util.getModelResourceBundle().getString("L_FREEAGENT");
			}
			while (s.length() < 46) {
				s += ' ';
			}
			s += Integer.toString(contracts.getCurrContrYears());
			while (s.length() < 50) {
				s += ' ';
			}
			if (contracts.getNextContr().getTeam() != null) {
				s += contracts.getNextContr().getTeam().getTeamName();
			} else {
				s += '-';
			}
			while (s.length() < 62) {
				s += ' ';
			}
			s += Integer.toString(contracts.getFee());
			while (s.length() < 68) {
				s += ' ';
			}
			s += Integer.toString(contracts.getCurrContrWage());
			items.add(s);
		}
		ObservableList<String> obsItems = FXCollections
				.observableArrayList(items);
		playerList.setItems(obsItems);
		playerList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if (me.getClickCount() < 2) {
					return;
				}
				// it's a double click
				playerDetails(new ActionEvent());
			}
		});
		HockmanMain.setListViewCellFactory(playerList);

	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void makeOffer(ActionEvent event) {
		initPlayer();
		HockmanMain.stageHandler.showModalStageAndWait("MakeOffer.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MAKEOFFER"));
	}

	@FXML
	private void playerDetails(ActionEvent event) {
		initPlayer();
		HockmanMain.stageHandler.showModalStageAndWait("PlayerDetails.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_PLAYERDETAILS"));
	}

	private void initPlayer() {
		int index = this.playerList.getSelectionModel().getSelectedIndex();
		HockmanMain.currPlayer = ppv.GetPlayer(index);
	}

	@FXML
	private void currentNegotiations(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("CurrentNegot.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_CURRENTNEGOT"));
	}

	@FXML
	private void sortName(ActionEvent event) {
		ppv.SortPlayerLastName();
		showForm();
	}

	@FXML
	private void sortPos(ActionEvent event) {
		ppv.SortPlayerPos();
		showForm();
	}

	@FXML
	private void sortNat(ActionEvent event) {
		ppv.SortPlayerNation();
		showForm();
	}

	@FXML
	private void sortOwner(ActionEvent event) {
		ppv.SortPlayerOwner();
		showForm();
	}

	@FXML
	private void sortContract(ActionEvent event) {
		ppv.SortPlayerContract();
		showForm();
	}

	@FXML
	private void sortNextSeason(ActionEvent event) {
		ppv.SortPlayerNextSeason();
		showForm();
	}

	@FXML
	private void sortFee(ActionEvent event) {
		ppv.SortPlayerFee();
		showForm();
	}

	@FXML
	private void sortWage(ActionEvent event) {
		ppv.SortPlayerWage();
		showForm();
	}
}
