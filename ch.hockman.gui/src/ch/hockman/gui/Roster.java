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

import ch.hockman.model.Modus.GameType;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Contracts;
import ch.hockman.model.player.Player;
import ch.hockman.model.team.CoachAI;

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
 * Mask showing the roster (farm/first) of the managed team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Roster implements Initializable {

	private boolean mainTeamListActive = true;

	@FXML
	private ListView mainTeam;

	@FXML
	private ListView farmTeam;

	@FXML
	private Label mainTeamLabel;

	@FXML
	private Label farmTeamLabel;

	@FXML
	private Label helpLabel;
	
	@FXML
	private Button newContractBtn;

	@FXML
	private Button minimumFeeBtn;

	@FXML
	private Button normalFeeBtn;

	@FXML
	private Button nextSeasonsBtn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		newContractBtn.setText(Util.getModelResourceBundle().getString("L_BTN_NEWCONTRACT"));
		minimumFeeBtn.setText(Util.getModelResourceBundle().getString("L_BTN_MINIMUMFEE"));
		normalFeeBtn.setText(Util.getModelResourceBundle().getString("L_BTN_NORMALFEE"));
		nextSeasonsBtn.setText(Util.getModelResourceBundle().getString("L_BTN_NEXTSEASONROSTER"));
		showForm();
	}

	private void showForm() {
		String s;
		mainTeamLabel.setText(HockmanMain.currManagedTeam.getTeamName() + " --- " + Util.getModelResourceBundle().getString("L_ROSTER_FARM_HELP"));
		helpLabel.setText(Util.getModelResourceBundle().getString("L_ROSTER_HELP"));
		farmTeamLabel.setText(HockmanMain.currManagedTeam.getFarmName());

		HockmanMain.currManagedTeam.sortPlayerPos();
		int nofPlayers = HockmanMain.currManagedTeam.getNofTeamPlayers();
		List<String> mainPlayers = new ArrayList<String>();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = HockmanMain.currManagedTeam.getTeamPlayer(i);
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
		nofPlayers = HockmanMain.currManagedTeam.getNofFarmPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = HockmanMain.currManagedTeam.getFarmPlayer(i);
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

		if (HockmanMain.game.getLeague().getModus().getGameType() == GameType.TOURNAMENT) {
			newContractBtn.setDisable(true);
			minimumFeeBtn.setDisable(true);
			normalFeeBtn.setDisable(true);
			nextSeasonsBtn.setDisable(true);
		} else {
			newContractBtn.setDisable(false);
			minimumFeeBtn.setDisable(false);
			normalFeeBtn.setDisable(false);
			nextSeasonsBtn.setDisable(false);
		}
	}

	@FXML
	private void newContract(ActionEvent event) {
		initPlayer();
		HockmanMain.stageHandler.showModalStageAndWait("NewContract.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_NEWCONTRACT"));
		showForm();
	}

	@FXML
	private void minimumFee(ActionEvent event) {
		Player player = null;
		if (mainTeamListActive) {
			int index = mainTeam.getSelectionModel().getSelectedIndex();
			if (index >= 0) {
				player = HockmanMain.currManagedTeam.getTeamPlayer(index);
			}
		} else {
			int index = farmTeam.getSelectionModel().getSelectedIndex();
			if (index >= 0) {
				player = HockmanMain.currManagedTeam.getFarmPlayer(index);
			}
		}
		if (player != null) {
			player.calcFee(true);
			String s = player.getLastName();
			s += Util.getModelResourceBundle().getString("L_FOR");
			s += Util.getModelResourceBundle().getString("L_LOW_FEE");
			HockmanMain.msgBoxLbl = s;
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
	}

	@FXML
	private void normalFee(ActionEvent event) {
		Player player = null;
		if (mainTeamListActive) {
			int index = mainTeam.getSelectionModel().getSelectedIndex();
			if (index >= 0) {
				player = HockmanMain.currManagedTeam.getTeamPlayer(index);
			}
		} else {
			int index = farmTeam.getSelectionModel().getSelectedIndex();
			if (index >= 0) {
				player = HockmanMain.currManagedTeam.getFarmPlayer(index);
			}
		}
		if (player != null) {
			player.calcFee(false);
			String s = player.getLastName();
			s += Util.getModelResourceBundle().getString("L_FOR");
			s += Util.getModelResourceBundle().getString("L_NORMAL_FEE");
			HockmanMain.msgBoxLbl = s;
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
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
			HockmanMain.currPlayer = HockmanMain.currManagedTeam
					.getTeamPlayer(index);
		} else {
			HockmanMain.currPlayer = HockmanMain.currManagedTeam
					.getFarmPlayer(index);
		}
	}

	@FXML
	private void playerDetail(ActionEvent event) {
		initPlayer();
		HockmanMain.stageHandler.showModalStageAndWait("PlayerDetails.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_PLAYERDETAILS"));
	}

	@FXML
	private void nextSeasonsRoster(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("NextSeasonRoster.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_NEXTSEASONROSTER"));
	}

	@FXML
	private void toFarm(ActionEvent event) {
		if (ch.hockman.model.Options.playing == ch.hockman.model.Options.Playing.RESULT) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_NO_MOVE_RES_ONLY");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		} else if (mainTeam.getSelectionModel().getSelectedIndex() >= 0
				&& HockmanMain.currManagedTeam.getNofFarmPlayers() < ch.hockman.model.team.Team.TEAM_MAXFARMPLAYER) {
			Player player = HockmanMain.currManagedTeam.getTeamPlayer(mainTeam
					.getSelectionModel().getSelectedIndex());
			HockmanMain.currManagedTeam.removeTeamPlayer(player);
			HockmanMain.currManagedTeam.addFarmPlayer(player);
			CoachAI.Analysis dummy = new CoachAI.Analysis();
			HockmanMain.currManagedTeam.getLineUp().lineUpAI(
					HockmanMain.currManagedTeam, dummy,
					HockmanMain.game.getLeague().getModus().maxNofForeigners());
			showForm();
		} else if (mainTeam.getSelectionModel().getSelectedIndex() < 0) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_SELECT_PLAYER");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		} else {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_FARM_FULL");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
	}

	@FXML
	private void toTeam(ActionEvent event) {
		if (ch.hockman.model.Options.playing == ch.hockman.model.Options.Playing.RESULT) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_NO_MOVE_RES_ONLY");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		} else if (farmTeam.getSelectionModel().getSelectedIndex() >= 0
				&& HockmanMain.currManagedTeam.getNofTeamPlayers() < ch.hockman.model.team.Team.TEAM_MAXTEAMPLAYER) {
			Player player = HockmanMain.currManagedTeam.getFarmPlayer(farmTeam
					.getSelectionModel().getSelectedIndex());
			HockmanMain.currManagedTeam.removeFarmPlayer(player);
			HockmanMain.currManagedTeam.addTeamPlayer(player);
			CoachAI.Analysis dummy = new CoachAI.Analysis();
			HockmanMain.currManagedTeam.getLineUp().lineUpAI(
					HockmanMain.currManagedTeam, dummy,
					HockmanMain.game.getLeague().getModus().maxNofForeigners());
			showForm();
		} else if (farmTeam.getSelectionModel().getSelectedIndex() < 0) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_SELECT_PLAYER");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		} else {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_FIRST_FULL");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
