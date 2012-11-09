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
import ch.hockman.model.Offer;
import ch.hockman.model.Transfer;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * The mask for making a transfer offer for a player.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class MakeOffer implements Initializable {

	@FXML
	private Label contractLbl;
	
	@FXML
	private Label wageLbl;

	@FXML
	private Label tradingAgainstLbl;

	@FXML
	private Label offerText;

	@FXML
	private Label feeLbl;

	@FXML
	private Label nextTxt;

	@FXML
	private Label immTxt;

	@FXML
	private RadioButton immediatelyRadio;

	@FXML
	private RadioButton nextSeasonRadio;

	@FXML
	private ToggleGroup transferTime;

	@FXML
	private ChoiceBox contractChoice;

	@FXML
	private ChoiceBox wageChoice;

	@FXML
	private ChoiceBox tradeChoice;

	@FXML
	private VBox cashGroupBox;

	@FXML
	private VBox tradingGroupBox;

	private boolean tradeEnabled;

	private PlayerPtrVector allPlayers;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.allPlayers = HockmanMain.game.getLeague().getPlayerVector();
		this.tradeEnabled = HockmanMain.game.getLeague().getModus().getGameType() == Modus.GameType.TRADE
				&& HockmanMain.currPlayer.getContracts().getCurrContr().getTeam() != null;
				// free agent cannot be traded;

		// called only at opening the dialog !!
		// (don't call it at updating the dialog while being opened)

		// default: next season transfer with cash
		this.cashGroupBox.setDisable(false);
		this.tradingGroupBox.setDisable(true);
		contractLbl.setText(Util.getModelResourceBundle().getString("L_CONTRACT_TXT"));
		wageLbl.setText(Util.getModelResourceBundle().getString("L_WAGE_TXT"));
		tradingAgainstLbl.setText(Util.getModelResourceBundle().getString("L_TRADING_AGAINST"));
		nextTxt.setText(Util.getModelResourceBundle().getString("L_NEXT_CASH"));
		immTxt.setText(Util.getModelResourceBundle().getString("L_THIS_CASH"));
		if (tradeEnabled) {
			immTxt.setText(Util.getModelResourceBundle().getString("L_THIS_TRADE"));
			// show all my players in the combo
			int nofPlayers = HockmanMain.currManagedTeam.getNofPlayers();
			List<String> tradeItems = new ArrayList<String>();
			for (int i = 0; i < nofPlayers; i++) {
				// more info for trade player
				Player player = HockmanMain.currManagedTeam.getPlayer(i);
				String s = player.getLastName();
				s += " (";
				s += player.getPosition().posName();
				s += ", ";
				s += player.getAge();
				s += " y, ";
				s += player.getTotalStrengthWithoutHealth();
				s += " strength)";
				tradeItems.add(s);
			}
			ObservableList<String> items = FXCollections
					.observableArrayList(tradeItems);
			tradeChoice.setItems(items);
			tradeChoice.getSelectionModel().select(0);
		}
		contractChoice.getSelectionModel().select(0);

		String s = Util.getModelResourceBundle().getString("L_OFFER_TO");
		s += HockmanMain.currPlayer.getLastName();
		ch.hockman.model.team.Team team = HockmanMain.currPlayer.getContracts().getCurrContr()
				.getTeam();
		if (team != null) {
			s += Util.getModelResourceBundle().getString("L_OF");
			s += team.getTeamName();
		} else {
			s += Util.getModelResourceBundle().getString("L_FREE_AGENT");
		}
		this.offerText.setText(s);
		s = Util.getModelResourceBundle().getString("L_FEE");
		s += HockmanMain.currPlayer.getFee();
		this.feeLbl.setText(s);

		List<String> wageItems = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			wageItems.add(Integer.toString(i));
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(wageItems);
		wageChoice.setItems(items);
		wageChoice.getSelectionModel().select(
				HockmanMain.currPlayer.getInitWage());

		transferTime.selectToggle(transferTime.getToggles().get(1)); // next
																		// season
		transferTime.selectedToggleProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				RadioButton rb = ((RadioButton) transferTime
						.getSelectedToggle());
				String s = Util.getModelResourceBundle().getString("L_FEE");
				if (rb.equals(immediatelyRadio)) {
					if (tradeEnabled) {
						tradingGroupBox.setDisable(false);
						cashGroupBox.setDisable(true);
						s += '0';
					} else {
						s += HockmanMain.currPlayer.getFee()
								* Transfer.TRANS_IMMEDIATELY_FACTOR;
					}
				} else if (rb.equals(nextSeasonRadio)) {
					if (tradeEnabled) {
						tradingGroupBox.setDisable(true);
						cashGroupBox.setDisable(false);
					}
					s += HockmanMain.currPlayer.getFee();
				}
				feeLbl.setText(s);
			}
		});

	}

	@FXML
	private void ok(ActionEvent event) {
		Offer offer = new Offer();
		RadioButton rb = ((RadioButton) transferTime.getSelectedToggle());
		if (rb.equals(this.immediatelyRadio)) {
			offer.setImmediately(true);
			if (tradeEnabled) {
				offer.setTrade(HockmanMain.currManagedTeam.getPlayer(tradeChoice
						.getSelectionModel().getSelectedIndex()));
			} else {
				offer.setTrade(null);
			}
		} else {
			offer.setImmediately(false);
			offer.setTrade(null);
		}
		offer.setWage(this.wageChoice.getSelectionModel().getSelectedIndex() + 1);
		offer.setFee(HockmanMain.currPlayer.getFee());
		offer.setYears(this.contractChoice.getSelectionModel()
				.getSelectedIndex() + 1);
		offer.setPlayer(HockmanMain.currPlayer);
		offer.setSeller(HockmanMain.currPlayer.getContracts().getCurrContr()
				.getTeam());
		offer.setBuyer(HockmanMain.currManagedTeam);
		int fee = offer.getFee();
		int currTotal = HockmanMain.currManagedTeam.getFinances()
				.calcCurrTotal();
		if (!offer.isImmediately() && fee > currTotal || !tradeEnabled
				&& offer.isImmediately()
				&& fee * Transfer.TRANS_IMMEDIATELY_FACTOR > currTotal) {
			// money check of managed team
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_NOT_ENOUGH_CASH");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		} else if (!offer.getPlayer().getContracts().isNoContractThisRound()
				&& (offer.getTrade() == null || offer.getTrade().getContracts().getNextContr()
						.getTeam() == null)) {
			ch.hockman.model.match.News news = HockmanMain.game.getNews();
			Transfer.MakingOffer mo = HockmanMain.currManagedTeam.getTransfer()
					.makeOffer(offer, allPlayers, news);
			if (mo == Transfer.MakingOffer.FAILED) {
				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_FAILED_OFFER");
				HockmanMain.stageHandler
						.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			} else if (mo == Transfer.MakingOffer.ACCEPTED) {
				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_ACCEPTED_OFFER");
				HockmanMain.stageHandler
						.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			} else if (mo == Transfer.MakingOffer.ACCEPTED_AND_DONE) {
				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_ACCEPTED_AND_DONE");
				HockmanMain.stageHandler
						.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			} else {
				assert (mo == Transfer.MakingOffer.SUCCEED);
				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_OFFER_DONE");
				HockmanMain.stageHandler
						.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			}
		} else {
			if (offer.getPlayer().getContracts().isNoContractThisRound()) {
				// free agent already tried
				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_FAILED_OFFER_ALREADY_TRIED");
				HockmanMain.stageHandler
						.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			} else {
				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_FAILED_OFFER_INVALID_TRADE");
				HockmanMain.stageHandler
						.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			}
		}
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void cancel(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
