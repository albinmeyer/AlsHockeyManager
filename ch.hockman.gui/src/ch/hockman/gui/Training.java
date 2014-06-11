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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import ch.hockman.model.Training.Effort;
import ch.hockman.model.common.Util;

/**
 * Mask for defining the training of the managed team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Training implements Initializable {

	@FXML
	private ChoiceBox<String> shootingDropDown;

	@FXML
	private ChoiceBox<String> staminaDropDown;

	@FXML
	private ChoiceBox<String> skillDropDown;

	@FXML
	private ChoiceBox<String> passingDropDown;

	@FXML
	private ChoiceBox<String> powerDropDown;

	@FXML
	private ChoiceBox<String> offensivePlayDropDown;

	@FXML
	private ChoiceBox<String> defensivePlayDropDown;

	@FXML
	private ChoiceBox<String> mentalDropDown;

	@FXML
	private Label avShootingLbl;

	@FXML
	private Label avStaminaLbl;

	@FXML
	private Label avSkillLbl;

	@FXML
	private Label avPassingLbl;

	@FXML
	private Label avPowerLbl;

	@FXML
	private Label avOffensiveLbl;

	@FXML
	private Label avDefensiveLbl;

	@FXML
	private Label helpLbl;
	
	@FXML
	private ToggleGroup effortToggleGroup;

	@FXML
	private RadioButton fullEffortRadio;

	@FXML
	private RadioButton normalEffortRadio;

	@FXML
	private RadioButton lowEffortRadio;

	@FXML
	private Label shootingLbl;

	@FXML
	private Label staminaLbl;

	@FXML
	private Label skillLbl;

	@FXML
	private Label passingLbl;

	@FXML
	private Label powerLbl;

	@FXML
	private Label offplayLbl;

	@FXML
	private Label defplayLbl;

	@FXML
	private Label mentalTrainingLbl;
	
	@FXML
	private Label effortLbl;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		helpLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_HELP"));
		shootingLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_SHOOTING"));
		staminaLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_STAMINA"));
		skillLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_SKILL"));
		passingLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_PASSING"));
		powerLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_POWER"));
		offplayLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_OFFPLAY"));
		defplayLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_DEFPLAY"));
		mentalTrainingLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TRAINING_MENTALTRAINING"));
		effortLbl.setText(Util.getModelResourceBundle().getString("L_LBL_EFFORT"));
		fullEffortRadio.setText(Util.getModelResourceBundle().getString("L_EFF_FULL"));
		normalEffortRadio.setText(Util.getModelResourceBundle().getString("L_EFF_NORMAL"));		
		lowEffortRadio.setText(Util.getModelResourceBundle().getString("L_EFF_EASY"));
		
		List<String> percItems = new ArrayList<String>();
		for (int i = 0; i <= 100; i++) {
			percItems.add(Integer.toString(i));
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(percItems);
		ch.hockman.model.Training training = HockmanMain.currManagedTeam
				.getTraining();
		shootingDropDown.setItems(items);
		shootingDropDown.getSelectionModel().select(training.getShooting());

		staminaDropDown.setItems(items);
		staminaDropDown.getSelectionModel().select(training.getStamina());

		skillDropDown.setItems(items);
		skillDropDown.getSelectionModel().select(training.getSkill());

		passingDropDown.setItems(items);
		passingDropDown.getSelectionModel().select(training.getPassing());

		powerDropDown.setItems(items);
		powerDropDown.getSelectionModel().select(training.getPower());

		offensivePlayDropDown.setItems(items);
		offensivePlayDropDown.getSelectionModel().select(
				training.getOffensive());

		defensivePlayDropDown.setItems(items);
		defensivePlayDropDown.getSelectionModel().select(
				training.getDefensive());

		mentalDropDown.setItems(items);
		mentalDropDown.getSelectionModel().select(training.getMental());

		avShootingLbl
				.setText(Util.getModelResourceBundle().getString("L_TEAM_AVERAGE")
						+ Integer.toString(HockmanMain.currManagedTeam
								.getShootingAv()));
		avStaminaLbl.setText(Util.getModelResourceBundle().getString("L_TEAM_AVERAGE")
				+ Integer.toString(HockmanMain.currManagedTeam.getStaminaAv()));
		avSkillLbl.setText(Util.getModelResourceBundle().getString("L_TEAM_AVERAGE")
				+ Integer.toString(HockmanMain.currManagedTeam.getSkillAv()));
		avPassingLbl.setText(Util.getModelResourceBundle().getString("L_TEAM_AVERAGE")
				+ Integer.toString(HockmanMain.currManagedTeam.getPassingAv()));
		avPowerLbl.setText(Util.getModelResourceBundle().getString("L_TEAM_AVERAGE")
				+ Integer.toString(HockmanMain.currManagedTeam.getPowerAv()));
		avOffensiveLbl
				.setText(Util.getModelResourceBundle().getString("L_TEAM_AVERAGE")
						+ Integer.toString(HockmanMain.currManagedTeam
								.getOffensiveAv()));
		avDefensiveLbl
				.setText(Util.getModelResourceBundle().getString("L_TEAM_AVERAGE")
						+ Integer.toString(HockmanMain.currManagedTeam
								.getDefensiveAv()));

		effortToggleGroup.selectToggle(effortToggleGroup.getToggles().get(
				training.getEffort().ordinal()));
	}

	@FXML
	private void ok(ActionEvent event) {
		ch.hockman.model.Training training = HockmanMain.currManagedTeam
				.getTraining();
		Effort effort;
		RadioButton rb = ((RadioButton) effortToggleGroup.getSelectedToggle());
		if (rb.equals(fullEffortRadio)) {
			effort = Effort.FULL;
		} else if (rb.equals(normalEffortRadio)) {
			effort = Effort.NORMAL;
		} else {
			effort = Effort.EASY;
		}
		int shooting = shootingDropDown.getSelectionModel().getSelectedIndex();
		int stamina = staminaDropDown.getSelectionModel().getSelectedIndex();
		int skill = skillDropDown.getSelectionModel().getSelectedIndex();
		int passing = passingDropDown.getSelectionModel().getSelectedIndex();
		int power = powerDropDown.getSelectionModel().getSelectedIndex();
		int offensive = offensivePlayDropDown.getSelectionModel()
				.getSelectedIndex();
		int defensive = defensivePlayDropDown.getSelectionModel()
				.getSelectedIndex();
		int mental = mentalDropDown.getSelectionModel().getSelectedIndex();
		int trainingSum = shooting + stamina + skill + passing + power
				+ offensive + defensive;
		if (trainingSum <= 100) {
			mental = 100 - trainingSum;
		} else {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_TRAINING_SUM_MUST_BE_100");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			return;
		}
		training.setTraining(shooting, stamina, skill, passing, power,
				offensive, defensive, mental, effort);
		if (ch.hockman.model.Options.playing == ch.hockman.model.Options.Playing.RESULT) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_CHANGES_HAVE_NO_EFFECT");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void cancel(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
