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

import ch.hockman.model.team.Team;
import ch.hockman.model.Transfer;
import ch.hockman.model.Options;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Contracts;
import ch.hockman.model.player.Motivation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * The mask for offering a new contract to a player of the managed team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class NewContract implements Initializable {

	@FXML
	private Label prolongueLbl;

	@FXML
	private ChoiceBox<Integer> yearsChoice;

	@FXML
	private ChoiceBox<String> wageChoice;

	@FXML
	private Button okBtn;

	@FXML
	private Label counterContractYearsLbl;

	@FXML
	private Label counterWageLbl;

	@FXML
	private Label contractYearsTxtLbl;

	@FXML
	private Label wageTxtLbl;

	@FXML
	private Label counterContractYearsTxtLbl;

	@FXML
	private Label counterWageTxtLbl;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		contractYearsTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_CONTRACT_YEARS"));
		wageTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_WAGE_K"));
		counterContractYearsTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_CONTRACT_YEARS"));
		counterWageTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_WAGE_K"));
		counterContractYearsLbl.setText("");
		counterWageLbl.setText("");
		okBtn.setDisable(true);
		prolongueLbl.setText("Prolongue Contract of "
				+ HockmanMain.currPlayer.getFirstName() + " "
				+ HockmanMain.currPlayer.getLastName());
		List<String> wageItems = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			wageItems.add(Integer.toString(i));
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(wageItems);
		wageChoice.setItems(items);
		wageChoice.getSelectionModel().select(
				HockmanMain.currPlayer.getInitWage());
		yearsChoice.getSelectionModel().select(0);
	}

	@FXML
	private void makeOffer(ActionEvent event) {
		int years = yearsChoice.getSelectionModel().getSelectedIndex() + 1;
		int wage = yearsChoice.getSelectionModel().getSelectedIndex() + 1;
		// follow the proposal of the manager
		if (wage + Util.random(3) < HockmanMain.currPlayer.getInitWage()
				+ years - 1
				|| Util.random(2) == 0) {
			years = Util.random(5) + 1;
			if (years == 5 || HockmanMain.currPlayer.getAge() > 30
					&& Util.random(3) != 0) {
				// 1 year contracts are most likely
				years = 1;
			}
			wage = HockmanMain.currPlayer.getInitWage() + years - 1
					+ Util.random(2);
		}
		if (wage < 1) {
			wage = 1;
		}
		if (HockmanMain.currPlayer.getCharacter().getCharID() == ch.hockman.model.character.PlayerCharacter.CharID.MONEYMAKER) {
			// moneymakers want more wage
			wage += Util.random(2);
		}

		//TODO player AI for contracts

		// don't allow to modify contracts to managers players
		if (HockmanMain.currPlayer.getContracts().getNextContr().getTeam() == null
				&& HockmanMain.currPlayer.getContracts().getCurrContrYears() == 1
				&& !HockmanMain.currPlayer.getContracts().isNoContractThisRound()
				&& Transfer.nofFuturePlayers(HockmanMain.currPlayer
						.getContracts().getCurrContr().getTeam(), HockmanMain.game
						.getLeague().getPlayerVector()) < Team.TEAM_MAXTEAMPLAYER
						+ Team.TEAM_MAXFARMPLAYER) {
			// player has no contract for next year with another team
			// game level
			int ran = 0;
			if (Options.level == Options.Level.ROOKIE) {
				ran = 1 + Util.random(2);
			} else if (Options.level == Options.Level.NORMAL) {
				ran = 2;
			} else if (Options.level == Options.Level.PROF) {
				ran = 3;
			} else {
				assert (false);
			}
			// happy players are more likely to stay
			Motivation.MotivationValue mv = HockmanMain.currPlayer
					.getMotivation().getMotVal();
			if (ran > 1
					&& (mv == Motivation.MotivationValue.GOOD || mv == Motivation.MotivationValue.GREAT)) {
				ran--;
			}
			if (Util.random(ran) == 0) {
				counterContractYearsLbl.setText(Integer.toString(years));
				counterWageLbl.setText(Integer.toString(wage));
				this.okBtn.setDisable(false);
			} else {
				counterContractYearsLbl.setText(Util.getModelResourceBundle().getString("L_CONTRACT_REJECTED"));
				counterWageLbl.setText("");
				HockmanMain.currPlayer.getContracts().setNoContractThisRound(true);
				this.okBtn.setDisable(true);
			}
		} else if (HockmanMain.currPlayer.getContracts().isNoContractThisRound()) {
			// player cannot sign another contract this round
			counterContractYearsLbl.setText(Util.getModelResourceBundle().getString("L_CONTRACT_NOT_POSSIBLE_THIS_ROUND"));
			counterWageLbl.setText("");
		} else {
			// player cannot sign another contract
			counterContractYearsLbl.setText(Util.getModelResourceBundle().getString("L_CONTRACT_NOT_POSSIBLE"));
			counterWageLbl.setText("");
		}

	}

	@FXML
	private void ok(ActionEvent event) {
		Contracts contracts = HockmanMain.currPlayer.getContracts();
		contracts.setNextContr(new Contracts.TContract(
				contracts.getCurrContr().getTeam()));
		contracts.setNextContrWage(Integer.parseInt(this.counterWageLbl
				.getText()));
		contracts.setNextContrYears(Integer
				.parseInt(this.counterContractYearsLbl.getText()));
		// increase motivation after prolonge contract
		HockmanMain.currPlayer.getMotivation().calculate(
				HockmanMain.currPlayer, Util.random(40), true);
		String s = HockmanMain.game.getNews().getTransfers();
		s += HockmanMain.currPlayer.getLastName();
		s += Util.getModelResourceBundle().getString("L_STAYS_AT");
		s += contracts.getCurrContr().getTeam().getTeamName();
		s += "\n";
		HockmanMain.game.getNews().setTransfers(s);
		contracts.getNextContr().getTeam().getTransfer()
				.rejectAllOffersOfPlayer(HockmanMain.currPlayer);

		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void cancel(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
