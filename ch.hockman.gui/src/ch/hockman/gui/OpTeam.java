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

import ch.hockman.model.common.Util;
import ch.hockman.model.player.Contracts;
import ch.hockman.model.player.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * Mask showing the roster of an opponent's team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class OpTeam implements Initializable {

	private boolean mainTeamListActive = true;

	@FXML
	private ImageView teamImg;

	@FXML
	private Label totalStrengthLbl;

	@FXML
	private Label gamePlayLbl;

	@FXML
	private Label effortLbl;

	@FXML
	private Label defenseLbl;

	@FXML
	private Label offenseLbl;

	@FXML
	private Label blockSelLbl;

	@FXML
	private Label sponsoringLbl;

	@FXML
	private ListView<String> mainTeam;

	@FXML
	private ListView<String> farmTeam;

	@FXML
	private Label mainTeamLabel;

	@FXML
	private Label farmTeamLabel;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String s;
		mainTeamLabel.setText(HockmanMain.lineupTeam.getTeamName());
		farmTeamLabel.setText(HockmanMain.lineupTeam.getFarmName());

		Image image;
		try {
			String url = "file:" + HockmanMain.lineupTeam.getPicPath();
			image = new Image(url);
			if (image.isError()) {
				url = Manager.class.getResource(Manager.L_TEAMPICPATH)
						.toString();
				image = new Image(url);
			}
		} catch (IllegalArgumentException ex) {
			String url = Manager.class.getResource(Manager.L_TEAMPICPATH)
					.toString();
			image = new Image(url);
		}
		teamImg.setImage(image);

		totalStrengthLbl.setText(Integer.toString(HockmanMain.lineupTeam
				.getStrengthWithMot()));
		ch.hockman.model.Tactics tactics = HockmanMain.lineupTeam.getTactics();
		gamePlayLbl.setText(tactics.getGamePlay().getName());
		effortLbl.setText(tactics.getEffort().getName());
		defenseLbl.setText(tactics.getDefense().getName());
		offenseLbl.setText(tactics.getOffense().getName());
		blockSelLbl.setText(tactics.getBlockSelection().getName());
		sponsoringLbl.setText(HockmanMain.lineupTeam.getSponsoring().getName());

		HockmanMain.lineupTeam.sortPlayerPos();
		int nofPlayers = HockmanMain.lineupTeam.getNofTeamPlayers();
		List<String> mainPlayers = new ArrayList<String>();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
			s = player.getLastName();
			s += " ";
			s += player.getFirstName();
			while (s.length() < 20) {
				s += ' ';
			}
			s += " (";
			s += player.getPosition().posName();
			s += ", ";
			s += player.getNation().getName();
			s += ", ";
			Contracts contract = player.getContracts();
			if (contract.getNextContr().getTeam() == null) {
				s += contract.getCurrContrYears();
				s += "y";
			} else if (contract.getNextContr().getTeam().equals(
					contract.getCurrContr().getTeam())) {
				s += Util.getModelResourceBundle().getString("L_PROL");
			} else {
				s += Util.getModelResourceBundle().getString("L_CANC");
			}
			if (player.getHealth().getInjury() > 0) {
				s += ", ";
				s += Util.getModelResourceBundle().getString("L_INJ");
			}
			s += ")";
			mainPlayers.add(s);
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(mainPlayers);
		mainTeam.setItems(items);
		mainTeam.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mainTeamListActive = true;
				if (me.getClickCount() < 2) {
					return;
				}
				// it's a double click
				playerDetail(new ActionEvent());
			}
		});
		HockmanMain.setListViewCellFactory(mainTeam);

		List<String> farmPlayers = new ArrayList<String>();
		nofPlayers = HockmanMain.lineupTeam.getNofFarmPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = HockmanMain.lineupTeam.getFarmPlayer(i);
			s = player.getLastName();
			s = player.getLastName();
			s += " ";
			s += player.getFirstName();
			while (s.length() < 20) {
				s += ' ';
			}
			s += " (";
			s += player.getPosition().posName();
			s += ", ";
			s += player.getNation().getName();
			s += ", ";
			Contracts contract = player.getContracts();
			if (contract.getNextContr().getTeam() == null) {
				s += contract.getCurrContrYears();
				s += "y";
			} else if (contract.getNextContr().getTeam().equals(
					contract.getCurrContr().getTeam())) {
				s += Util.getModelResourceBundle().getString("L_PROL");
			} else {
				s += Util.getModelResourceBundle().getString("L_CANC");
			}
			if (player.getHealth().getInjury() > 0) {
				s += ", ";
				s += Util.getModelResourceBundle().getString("L_INJ");
			}
			s += ")";
			farmPlayers.add(s);
		}
		items = FXCollections.observableArrayList(farmPlayers);
		farmTeam.setItems(items);
		farmTeam.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				mainTeamListActive = false;
				if (me.getClickCount() < 2) {
					return;
				}
				playerDetail(new ActionEvent());
			}
		});
		HockmanMain.setListViewCellFactory(farmTeam);

	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void playerDetail(ActionEvent event) {
		initPlayer();
		HockmanMain.stageHandler.showModalStageAndWait("PlayerDetails.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_PLAYERDETAILS"));
	}

	private void initPlayer() {
		int index;
		if (mainTeamListActive) {
			index = mainTeam.getSelectionModel().getSelectedIndex();
		} else {
			index = farmTeam.getSelectionModel().getSelectedIndex();
		}
		if (index < 0) {
			index = 0;
		}
		if (mainTeamListActive) {
			HockmanMain.currPlayer = HockmanMain.lineupTeam
					.getTeamPlayer(index);
		} else {
			HockmanMain.currPlayer = HockmanMain.lineupTeam
					.getFarmPlayer(index);
		}
	}

}
