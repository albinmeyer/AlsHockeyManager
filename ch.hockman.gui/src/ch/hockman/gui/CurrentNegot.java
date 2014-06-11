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

import ch.hockman.model.Offer;
import ch.hockman.model.Transfer;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Player;
import ch.hockman.model.team.CoachAI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * The mask showing current negotiations for transfers.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class CurrentNegot implements Initializable {

	@FXML
	private Label offersToYouLbl;
	
	@FXML
	private Label buyingNegLbl;
	
	@FXML
	private ListView<String> offersList;

	@FXML
	private ListView<String> buyingList;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		showForm();
	}

	private void showForm() {
		offersToYouLbl.setText(Util.getModelResourceBundle().getString("L_OFFERS_TO_YOU"));
		buyingNegLbl.setText(Util.getModelResourceBundle().getString("L_BUYING_NEG"));
		int nofOffers;
		Offer offer;
		Player player;
		List<String> buyingItems = new ArrayList<String>();
		nofOffers = HockmanMain.currManagedTeam.getTransfer()
				.getNofBuyNegOffers();
		for (int i = 0; i < nofOffers; i++) {
			String s = "";
			offer = HockmanMain.currManagedTeam.getTransfer().getBuyNegOffer(i);
			player = offer.getPlayer();
			if (player.isMultipleName()) {
				s += player.getFirstName();
				s += " ";
			}
			s += player.getLastName();
			s += " (";
			s += offer.getSeller().getTeamName();
			s += ")";
			s += Util.getModelResourceBundle().getString("L_FOR");
			if (offer.getTrade() != null) {
				if (offer.getTrade().isMultipleName()) {
					s += offer.getTrade().getFirstName();
					s += " ";
				}
				s += offer.getTrade().getLastName();
				// more info for trade player
				s += " (";
				s += offer.getTrade().getPosition().posName();
				s += ", ";
				s += offer.getTrade().getAge();
				s += " y, ";
				s += offer.getTrade().getTotalStrengthWithoutHealth();
				s += " strength)";
			} else {
				if (offer.isImmediately()) {
					s += offer.getFee() * Transfer.TRANS_IMMEDIATELY_FACTOR;
				} else {
					s += offer.getFee();
				}
				s += "K";
			}
			s += " (";
			s += Util.getModelResourceBundle().getString("L_WAGE");
			s += ": ";
			s += offer.getWage();
			s += "K)";
			if (offer.isImmediately()) {
				s += Util.getModelResourceBundle().getString("L_IMMEDIATELY");
			} else {
				s += Util.getModelResourceBundle().getString("L_NEXT_SEASON");
			}
			s += " ";
			s += offer.getYears();
			s += Util.getModelResourceBundle().getString("L_YEARS");
			buyingItems.add(s);
		}

		ObservableList<String> items = FXCollections
				.observableArrayList(buyingItems);
		buyingList.setItems(items);

		List<String> sellingItems = new ArrayList<String>();
		nofOffers = HockmanMain.currManagedTeam.getTransfer()
				.getNofSellNegOffers();
		for (int i = 0; i < nofOffers; i++) {
			String s;
			offer = HockmanMain.currManagedTeam.getTransfer()
					.getSellNegOffer(i);
			player = offer.getPlayer();
			s = offer.getBuyer().getTeamName();
			s += Util.getModelResourceBundle().getString("L_WANTS");
			if (player.isMultipleName()) {
				s += player.getFirstName();
				s += " ";
			}
			s += player.getLastName();
			s += Util.getModelResourceBundle().getString("L_FOR");
			if (offer.getTrade() != null) {
				if (offer.getTrade().isMultipleName()) {
					s += offer.getTrade().getFirstName();
					s += " ";
				}
				s += offer.getTrade().getLastName();
				// more info for trade player
				s += " (";
				s += offer.getTrade().getPosition().posName();
				s += ", ";
				s += offer.getTrade().getAge();
				s += " y, ";
				s += offer.getTrade().getTotalStrengthWithoutHealth();
				s += " strength)";
			} else {
				if (offer.isImmediately()) {
					s += offer.getFee() * Transfer.TRANS_IMMEDIATELY_FACTOR;
				} else {
					s += offer.getFee();
				}
				s += "K";
			}
			if (offer.isImmediately()) {
				s += Util.getModelResourceBundle().getString("L_IMMEDIATELY");
			} else {
				s += Util.getModelResourceBundle().getString("L_NEXT_SEASON");
			}
			sellingItems.add(s);
		}
		items = FXCollections.observableArrayList(sellingItems);
		offersList.setItems(items);
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void rejectOffer(ActionEvent event) {
		if (offersList.getSelectionModel().getSelectedIndex() >= 0) {
			HockmanMain.currManagedTeam.getTransfer().rejectOffer(
					HockmanMain.currManagedTeam.getTransfer().getSellNegOffer(
							offersList.getSelectionModel().getSelectedIndex()));
			showForm();
		}
	}

	@FXML
	private void acceptOffer(ActionEvent event) {
		if (offersList.getSelectionModel().getSelectedIndex() >= 0) {
			Offer offer = HockmanMain.currManagedTeam.getTransfer()
					.getSellNegOffer(
							offersList.getSelectionModel().getSelectedIndex());
			if (!offer.isImmediately()
					&& offer.getPlayer().getFee() <= offer.getBuyer().getFinances()
							.calcCurrTotal()
					|| offer.isImmediately()
					&& (offer.getTrade() != null || offer.getPlayer().getFee()
							* Transfer.TRANS_IMMEDIATELY_FACTOR <= offer.getBuyer()
							.getFinances().calcCurrTotal())) {
				int nofPlayers = Transfer.buyerNofPlayers(offer,
						HockmanMain.game.getLeague().getPlayerVector());
				if (nofPlayers < ch.hockman.model.team.Team.TEAM_MAXTEAMPLAYER
						+ ch.hockman.model.team.Team.TEAM_MAXFARMPLAYER) {
					// do lineup of AI buyer after immediate buying from managed
					// team
					if (offer.isImmediately()) {
						ch.hockman.model.team.Team buyer = offer.getBuyer();
						HockmanMain.currManagedTeam.getTransfer().acceptOffer(
								offer, HockmanMain.game.getNews());
						CoachAI.Analysis dummy = new CoachAI.Analysis();
						buyer.getLineUp().farmAndFirstTeamKI(
								buyer,
								dummy,
								HockmanMain.game.getLeague().getModus()
										.maxNofForeigners());
						buyer.getLineUp().lineUpAI(
								buyer,
								dummy,
								HockmanMain.game.getLeague().getModus()
										.maxNofForeigners());
					} else {
						HockmanMain.currManagedTeam.getTransfer().acceptOffer(
								offer, HockmanMain.game.getNews());
					}
				} else {
					HockmanMain.currManagedTeam.getTransfer().rejectOffer(
							HockmanMain.currManagedTeam.getTransfer()
									.getSellNegOffer(
											offersList.getSelectionModel()
													.getSelectedIndex()));
					HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_OTHER_NOT_ENOUGH_SPACE");
					HockmanMain.stageHandler
							.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
				}
			} else {
				// buyer has not enough money
				HockmanMain.currManagedTeam.getTransfer().rejectOffer(
						HockmanMain.currManagedTeam.getTransfer()
								.getSellNegOffer(
										offersList.getSelectionModel()
												.getSelectedIndex()));
				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_OTHER_NOT_ENOUGH_CASH");
				HockmanMain.stageHandler
						.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			}
			showForm();
		}

	}

	@FXML
	private void cancelOffer(ActionEvent event) {
		if (buyingList.getSelectionModel().getSelectedIndex() >= 0) {
			HockmanMain.currManagedTeam.getTransfer().cancelOffer(
					HockmanMain.currManagedTeam.getTransfer().getBuyNegOffer(
							buyingList.getSelectionModel().getSelectedIndex()));
			showForm();
		}
	}

}
