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
import java.util.ResourceBundle;

import ch.hockman.model.common.Util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;

/**
 * Mask showing the options of the whole game.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Options implements Initializable {

	@FXML
	private Slider speedSlider;

	@FXML
	private RadioButton rookieRadio;

	@FXML
	private RadioButton normalRadio;

	@FXML
	private RadioButton professionalRadio;

	@FXML
	private ToggleGroup levelToggleGroup;

	@FXML
	private RadioButton resultsRadio;

	@FXML
	private RadioButton coachingRadio;

	@FXML
	private ToggleGroup playingToggleGroup;

	@FXML
	private CheckBox reportsCheckBox;

	@FXML
	private CheckBox intermissionCheckBox;

	@FXML
	private CheckBox penaltyCheckBox;

	@FXML
	private CheckBox scoringCheckBox;

	@FXML
	private CheckBox injuryCheckBox;

	@FXML
	private CheckBox interruptCheckBox;
	
	@FXML
	private Label gameSpeedLbl;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		gameSpeedLbl.setText(Util.getModelResourceBundle().getString("L_LBL_GAMESPEED"));
		resultsRadio.setText(Util.getModelResourceBundle().getString("L_RADIO_RESULTSONLY"));
		coachingRadio.setText(Util.getModelResourceBundle().getString("L_RADIO_COACHING"));
		
		// speed of game is a value between 0 and 10
		speedSlider.setMin(0);
		speedSlider.setMax(10);
		speedSlider.setValue(ch.hockman.model.Options.speed);

		levelToggleGroup.selectToggle(levelToggleGroup.getToggles().get(
				ch.hockman.model.Options.level.ordinal()));
		playingToggleGroup.selectToggle(playingToggleGroup.getToggles().get(
				ch.hockman.model.Options.playing.ordinal()));

		reportsCheckBox.selectedProperty().set(
				ch.hockman.model.Options.showReport);
		intermissionCheckBox.selectedProperty().set(
				ch.hockman.model.Options.showIntermissionDialog);
		penaltyCheckBox.selectedProperty().set(
				ch.hockman.model.Options.showPenaltyDialog);
		scoringCheckBox.selectedProperty().set(
				ch.hockman.model.Options.showScoringDialog);
		injuryCheckBox.selectedProperty().set(
				ch.hockman.model.Options.showInjuryDialog);
		interruptCheckBox.selectedProperty().set(
				ch.hockman.model.Options.showInterruptDialog);
	}

	@FXML
	private void ok(ActionEvent event) {
		ch.hockman.model.Options.speed = (int) speedSlider.getValue();

		RadioButton rb = ((RadioButton) levelToggleGroup.getSelectedToggle());
		if (rb.equals(rookieRadio)) {
			ch.hockman.model.Options.level = ch.hockman.model.Options.Level.ROOKIE;
		} else if (rb.equals(normalRadio)) {
			ch.hockman.model.Options.level = ch.hockman.model.Options.Level.NORMAL;
		} else {
			ch.hockman.model.Options.level = ch.hockman.model.Options.Level.PROF;
		}

		rb = ((RadioButton) playingToggleGroup.getSelectedToggle());
		if (rb.equals(resultsRadio)) {
			ch.hockman.model.Options.playing = ch.hockman.model.Options.Playing.RESULT;
		} else {
			ch.hockman.model.Options.playing = ch.hockman.model.Options.Playing.COACHING;
		}

		ch.hockman.model.Options.showReport = reportsCheckBox
				.selectedProperty().get();
		ch.hockman.model.Options.showIntermissionDialog = intermissionCheckBox
				.selectedProperty().get();
		ch.hockman.model.Options.showPenaltyDialog = penaltyCheckBox
				.selectedProperty().get();
		ch.hockman.model.Options.showScoringDialog = scoringCheckBox
				.selectedProperty().get();
		ch.hockman.model.Options.showInjuryDialog = injuryCheckBox
				.selectedProperty().get();
		ch.hockman.model.Options.showInterruptDialog = interruptCheckBox
				.selectedProperty().get();

		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void cancel(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
