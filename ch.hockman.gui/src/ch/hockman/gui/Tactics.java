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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

/**
 * Mask for letting change the tactics of the managed team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Tactics implements Initializable {

	@FXML
	private RadioButton offensiveRadio;

	@FXML
	private RadioButton normalGameplayRadio;

	@FXML
	private RadioButton defensiveRadio;

	@FXML
	private RadioButton fullRadio;

	@FXML
	private RadioButton normalEffortRadio;

	@FXML
	private RadioButton easyRadio;

	@FXML
	private RadioButton zoneRadio;

	@FXML
	private RadioButton markRadio;

	@FXML
	private RadioButton shootMoreRadio;

	@FXML
	private RadioButton passMoreRadio;

	@FXML
	private RadioButton fixBlockRadio;

	@FXML
	private RadioButton sameBlockRadio;

	@FXML
	private RadioButton strongerBlocksRadio;

	@FXML
	private RadioButton lessTiredRadio;

	@FXML
	private CheckBox pullGoalieCheckBox;

	@FXML
	private ToggleGroup gamePlay;

	@FXML
	private ToggleGroup effort;

	@FXML
	private ToggleGroup defense;

	@FXML
	private ToggleGroup offense;

	@FXML
	private ToggleGroup blockSelection;

	private boolean home;
	private ch.hockman.model.Tactics.GamePlay gamePlayModel;
	private ch.hockman.model.Tactics.Effort effortModel;
	private ch.hockman.model.Tactics.Defense defenseModel;
	private ch.hockman.model.Tactics.Offense offenseModel;
	private ch.hockman.model.Tactics.BlockSelection blockSelectionModel;
	private boolean pullGoalieModel;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		HockmanMain.game.getSchedule().getOpTeam(HockmanMain.game.getRound(),
				HockmanMain.currManagedTeam);
		home = HockmanMain.currManagedTeam.getHome();
		
		offensiveRadio.setText(Util.getModelResourceBundle().getString("L_GP_OFFENSIVE"));
		normalGameplayRadio.setText(Util.getModelResourceBundle().getString("L_GP_NORMAL"));
		defensiveRadio.setText(Util.getModelResourceBundle().getString("L_GP_DEFENSIVE"));
		strongerBlocksRadio.setText(Util.getModelResourceBundle().getString("L_BLOCK_STRONGER"));
		lessTiredRadio.setText(Util.getModelResourceBundle().getString("L_BLOCK_LESSTIRED"));
		sameBlockRadio.setText(Util.getModelResourceBundle().getString("L_BLOCK_SAME"));
		fixBlockRadio.setText(Util.getModelResourceBundle().getString("L_BLOCK_AGAINST"));
		shootMoreRadio.setText(Util.getModelResourceBundle().getString("L_OFF_SHOOT"));
		passMoreRadio.setText(Util.getModelResourceBundle().getString("L_OFF_PASS"));
		fullRadio.setText(Util.getModelResourceBundle().getString("L_EFF_FULL"));
		normalEffortRadio.setText(Util.getModelResourceBundle().getString("L_EFF_NORMAL"));
		easyRadio.setText(Util.getModelResourceBundle().getString("L_EFF_EASY"));
		
		gamePlayModel = HockmanMain.currManagedTeam.getTactics().getGamePlay();
		effortModel = HockmanMain.currManagedTeam.getTactics().getEffort();
		defenseModel = HockmanMain.currManagedTeam.getTactics().getDefense();
		offenseModel = HockmanMain.currManagedTeam.getTactics().getOffense();
		blockSelectionModel = HockmanMain.currManagedTeam.getTactics()
				.getBlockSelection();
		pullGoalieModel = HockmanMain.currManagedTeam.getTactics()
				.getPullGoalie();

		pullGoalieCheckBox.selectedProperty().set(pullGoalieModel);
		pullGoalieCheckBox.setText(Util.getModelResourceBundle().getString("L_TACTIC_PULLGOALIE"));
		
		gamePlay.selectToggle(gamePlay.getToggles().get(
				gamePlayModel.getValue()));
		gamePlay.selectedToggleProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				RadioButton rb = ((RadioButton) gamePlay.getSelectedToggle());
				if (rb.equals(defensiveRadio)) {
					gamePlayModel = ch.hockman.model.Tactics.GamePlay.DEFENSIVE;
				} else if (rb.equals(normalGameplayRadio)) {
					gamePlayModel = ch.hockman.model.Tactics.GamePlay.NORMAL_GP;
				} else if (rb.equals(offensiveRadio)) {
					gamePlayModel = ch.hockman.model.Tactics.GamePlay.OFFENSIVE;
				}
				HockmanMain.currManagedTeam.getTactics().setTactics(
						gamePlayModel, effortModel, defenseModel, offenseModel,
						blockSelectionModel, pullGoalieModel);
			}
		});

		effort.selectToggle(effort.getToggles().get(effortModel.getValue()));
		effort.selectedToggleProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				RadioButton rb = ((RadioButton) effort.getSelectedToggle());
				if (rb.equals(fullRadio)) {
					effortModel = ch.hockman.model.Tactics.Effort.FULL;
				} else if (rb.equals(easyRadio)) {
					effortModel = ch.hockman.model.Tactics.Effort.EASY;
				} else if (rb.equals(normalEffortRadio)) {
					effortModel = ch.hockman.model.Tactics.Effort.NORMAL_EFF;
				}
				HockmanMain.currManagedTeam.getTactics().setTactics(
						gamePlayModel, effortModel, defenseModel, offenseModel,
						blockSelectionModel, pullGoalieModel);
			}
		});

		defense.selectToggle(defense.getToggles().get(defenseModel.getValue()));
		defense.selectedToggleProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				RadioButton rb = ((RadioButton) defense.getSelectedToggle());
				if (rb.equals(zoneRadio)) {
					defenseModel = ch.hockman.model.Tactics.Defense.ZONE;
				} else if (rb.equals(markRadio)) {
					defenseModel = ch.hockman.model.Tactics.Defense.MARK;
				}
				HockmanMain.currManagedTeam.getTactics().setTactics(
						gamePlayModel, effortModel, defenseModel, offenseModel,
						blockSelectionModel, pullGoalieModel);
			}
		});

		offense.selectToggle(offense.getToggles().get(offenseModel.getValue()));
		offense.selectedToggleProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				RadioButton rb = ((RadioButton) offense.getSelectedToggle());
				if (rb.equals(passMoreRadio)) {
					offenseModel = ch.hockman.model.Tactics.Offense.PASS;
				} else if (rb.equals(shootMoreRadio)) {
					offenseModel = ch.hockman.model.Tactics.Offense.SHOOT;
				}
				HockmanMain.currManagedTeam.getTactics().setTactics(
						gamePlayModel, effortModel, defenseModel, offenseModel,
						blockSelectionModel, pullGoalieModel);
			}
		});

		sameBlockRadio.setDisable(!home);
		fixBlockRadio.setDisable(!home);

		if (blockSelectionModel != ch.hockman.model.Tactics.BlockSelection.STRONGER
				&& !home) {
			blockSelection.selectToggle(blockSelection.getToggles().get(
					ch.hockman.model.Tactics.BlockSelection.LESSTIRED
							.getValue()));
		} else {
			blockSelection.selectToggle(blockSelection.getToggles().get(
					blockSelectionModel.getValue()));
		}
		blockSelection.selectedToggleProperty().addListener(
				new ChangeListener() {
					@Override
					public void changed(ObservableValue arg0, Object arg1,
							Object arg2) {
						RadioButton rb = ((RadioButton) blockSelection
								.getSelectedToggle());
						if (rb.equals(sameBlockRadio)) {
							blockSelectionModel = ch.hockman.model.Tactics.BlockSelection.SAME;
						} else if (rb.equals(lessTiredRadio)) {
							blockSelectionModel = ch.hockman.model.Tactics.BlockSelection.LESSTIRED;
						} else if (rb.equals(strongerBlocksRadio)) {
							blockSelectionModel = ch.hockman.model.Tactics.BlockSelection.STRONGER;
						} else if (rb.equals(fixBlockRadio)) {
							blockSelectionModel = ch.hockman.model.Tactics.BlockSelection.AGAINST;
						}
						HockmanMain.currManagedTeam.getTactics().setTactics(
								gamePlayModel, effortModel, defenseModel,
								offenseModel, blockSelectionModel,
								pullGoalieModel);
					}
				});

	}

	@FXML
	private void ok(ActionEvent event) {
		if (ch.hockman.model.Options.playing == ch.hockman.model.Options.Playing.RESULT) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_CHANGES_HAVE_NO_EFFECT");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void pullGoalie(ActionEvent event) {
		pullGoalieModel = pullGoalieCheckBox.selectedProperty().get();
		HockmanMain.currManagedTeam.getTactics().setTactics(gamePlayModel,
				effortModel, defenseModel, offenseModel, blockSelectionModel,
				pullGoalieModel);
	}

}
