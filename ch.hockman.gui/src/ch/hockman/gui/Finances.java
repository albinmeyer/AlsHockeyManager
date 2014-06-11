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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * The mask showing the finances.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Finances implements Initializable {

	@FXML
	private Label expensesLbl;

	@FXML
	private Label incomeLbl;

	@FXML
	private Label noteLbl;

	@FXML
	private Label recommendLbl;
	
	@FXML
	private Label cashBeforeLbl;

	@FXML
	private Label transferIncLbl;

	@FXML
	private Label gameIncLbl;

	@FXML
	private Label sponsorLbl;

	@FXML
	private Label commercialsLbl;

	@FXML
	private Label transferExpLbl;

	@FXML
	private Label gameExpLbl;

	@FXML
	private Label materialLbl;

	@FXML
	private Label wagesLbl;

	@FXML
	private Label interestsLbl;

	@FXML
	private Label cashNowLbl;

	@FXML
	private ChoiceBox<String> youthDropDown;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		expensesLbl.setText(Util.getModelResourceBundle().getString("L_EXPENSES"));
		incomeLbl.setText(Util.getModelResourceBundle().getString("L_INCOME"));
		noteLbl.setText(Util.getModelResourceBundle().getString("L_NOTE_CASH"));
		recommendLbl.setText(Util.getModelResourceBundle().getString("L_RECOMMEND_YOUTH"));
		ch.hockman.model.Finances finances = HockmanMain.currManagedTeam
				.getFinances();
		cashBeforeLbl.setText(Integer.toString(finances.getPreviousTotal()));
		transferIncLbl.setText(Integer.toString(finances.getTransferIn()));
		gameIncLbl.setText(Integer.toString(finances.getMatchIn()));
		sponsorLbl.setText(Integer.toString(finances.getSponsor()));
		commercialsLbl.setText(Integer.toString(finances.getCommercials()));
		transferExpLbl.setText(Integer.toString(finances.getTransferOut()));
		gameExpLbl.setText(Integer.toString(finances.getMatchOut()));
		materialLbl.setText(Integer.toString(finances.getMaterial()));
		wagesLbl.setText(Integer.toString(finances.getWages()));
		interestsLbl.setText(Integer.toString(finances.getInterests()));
		cashNowLbl.setText(Integer.toString(finances.getCurrTotal()));
		List<String> youthItems = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			youthItems.add(Integer.toString(i));
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(youthItems);
		youthDropDown.setItems(items);
		youthDropDown.getSelectionModel().select(finances.getYouth());
	}

	@FXML
	private void ok(ActionEvent event) {
		ch.hockman.model.Finances finances = HockmanMain.currManagedTeam
				.getFinances();
		finances.setYouth(youthDropDown.getSelectionModel().getSelectedIndex());
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void cancel(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
