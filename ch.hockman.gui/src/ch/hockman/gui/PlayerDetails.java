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
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import ch.hockman.model.common.Util;
import ch.hockman.model.player.Contracts;
import ch.hockman.model.player.Motivation;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Mask showing the details of one player.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class PlayerDetails implements Initializable {
	private static final String L_PLAYERPICPATH = "player.jpg";
	
	@FXML
	private TextArea motivationText;

	@FXML
	private TextArea personalText;

	@FXML
	private TextArea careerText;

	@FXML
	private Label injuryLbl;

	@FXML
	private Label hurtLbl;

	@FXML
	private Label firstNameTxtLbl;

	@FXML
	private Label lastNameTxtLbl;

	@FXML
	private Label birthdayTxtLbl;

	@FXML
	private Label positionTxtLbl;

	@FXML
	private Label numberTxtLbl;

	@FXML
	private Label nationalityTxtLbl;

	@FXML
	private Label heightTxtLbl;

	@FXML
	private Label weightTxtLbl;

	@FXML
	private Label characterTxtLbl;

	@FXML
	private Label energyTxtLbl;

	@FXML
	private Label formTxtLbl;

	@FXML
	private Label shootingTxtLbl;

	@FXML
	private Label staminaTxtLbl;

	@FXML
	private Label skillTxtLbl;

	@FXML
	private Label passingTxtLbl;

	@FXML
	private Label powerTxtLbl;

	@FXML
	private Label offenseTxtLbl;

	@FXML
	private Label defenseTxtLbl;

	@FXML
	private Label totalStrengthTxtLbl;

	@FXML
	private ImageView playerPic;

	@FXML
	private Label natGamesLbl;

	@FXML
	private Label firstNameLbl;

	@FXML
	private Label lastNameLbl;

	@FXML
	private Label birthdayLbl;

	@FXML
	private Label positionLbl;

	@FXML
	private Label numberLbl;

	@FXML
	private Label nationalityLbl;

	@FXML
	private Label heightLbl;

	@FXML
	private Label weightLbl;

	@FXML
	private Label characterLbl;

	@FXML
	private Label energyLbl;

	@FXML
	private Label formLbl;

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
	private Label offenseLbl;

	@FXML
	private Label defenseLbl;

	@FXML
	private Label totalStrengthLbl;

	@FXML
	private Label gamesLbl;

	@FXML
	private Label goalsLbl;

	@FXML
	private Label assistsLbl;

	@FXML
	private Label penaltyLbl;

	@FXML
	private Label plusMinusLbl;

	@FXML
	private Label wageLbl;

	@FXML
	private Label contractLbl;

	@FXML
	private Label feeLbl;

	@FXML
	private Label wageTxtLbl;

	@FXML
	private Label contractTxtLbl;

	@FXML
	private Label feeTxtLbl;

	@FXML
	private Label goalsTextLbl;

	@FXML
	private Label assistsTextLbl;

	@FXML
	private Label injuryTxtLbl;
	
	@FXML
	private Label hurtTxtLbl;
	
	@FXML
	private Label roundsLeft1Lbl;
	
	@FXML
	private Label roundsLeft2Lbl;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		firstNameTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_FIRSTNAME"));
		lastNameTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_LASTNAME"));
		birthdayTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_BIRTHDAY"));
		positionTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_POSITION"));
		numberTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_NUMBER"));
		nationalityTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_NATION"));
		heightTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_HEIGHT"));
		weightTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_WEIGHT"));
		characterTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_CHARACTER"));
		energyTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_ENERGY"));
		formTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_FORM"));
		shootingTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_SHOOTING"));
		staminaTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_STAMINA"));
		skillTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_SKILL"));
		passingTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_PASSING"));
		powerTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_POWER"));
		offenseTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_OFFENSE"));
		defenseTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_DEFENSE"));
		totalStrengthTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_TOTAL_STRENGTH"));
		wageTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_WAGE"));
		contractTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_CONTRACT"));
		feeTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_FEE"));
		injuryTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_INJURY"));
		hurtTxtLbl.setText(Util.getModelResourceBundle().getString("L_LBL_HURT"));
		roundsLeft1Lbl.setText(Util.getModelResourceBundle().getString("L_LBL_ROUNDSLEFT"));
		roundsLeft2Lbl.setText(Util.getModelResourceBundle().getString("L_LBL_ROUNDSLEFT"));
		
		Image image;
		try {
			String url = "file:" + HockmanMain.currPlayer.getPicPath();
			image = new Image(url);
			if (image.isError()) {
				url = PlayerDetails.class.getResource(L_PLAYERPICPATH)
						.toString();
				image = new Image(url);
			}
		} catch (IllegalArgumentException ex) {
			String url = PlayerDetails.class.getResource(L_PLAYERPICPATH)
					.toString();
			image = new Image(url);
		}
		playerPic.setImage(image);
		firstNameLbl.setText(HockmanMain.currPlayer.getFirstName());
		lastNameLbl.setText(HockmanMain.currPlayer.getLastName());
		birthdayLbl.setText(HockmanMain.currPlayer.getBirthday().toString());
		positionLbl.setText(HockmanMain.currPlayer.getPosition().posName());
		numberLbl.setText(Integer.toString(HockmanMain.currPlayer.getNumber()));
		nationalityLbl.setText(HockmanMain.currPlayer.getNation().getName());
		heightLbl.setText(Integer.toString(HockmanMain.currPlayer.getHeight()));
		weightLbl.setText(Integer.toString(HockmanMain.currPlayer.getWeight()));
		characterLbl.setText(HockmanMain.currPlayer.getCharacter()
				.getCharacterName());
		natGamesLbl.setText(Integer.toString(HockmanMain.currPlayer
				.getNatGames()));

		energyLbl.setText(Integer.toString(HockmanMain.currPlayer.getEnergy()));
		formLbl.setText(Integer.toString(HockmanMain.currPlayer.getForm()));
		shootingLbl.setText(Integer.toString(HockmanMain.currPlayer
				.getShooting()));
		staminaLbl
				.setText(Integer.toString(HockmanMain.currPlayer.getStamina()));
		skillLbl.setText(Integer.toString(HockmanMain.currPlayer.getSkill()));
		passingLbl
				.setText(Integer.toString(HockmanMain.currPlayer.getPassing()));
		powerLbl.setText(Integer.toString(HockmanMain.currPlayer.getPower()));
		offenseLbl
				.setText(Integer.toString(HockmanMain.currPlayer.getOffense()));
		defenseLbl
				.setText(Integer.toString(HockmanMain.currPlayer.getDefense()));
		totalStrengthLbl.setText(Integer.toString(HockmanMain.currPlayer
				.getTotalStrength()));
		gamesLbl.setText(Integer.toString(HockmanMain.currPlayer
				.getMatchesPlayed()));
		if (HockmanMain.currPlayer.getPosition().getPosID() == ch.hockman.model.position.Position.PosID.GOALIE) {
			goalsTextLbl.setText("GAA");
			assistsTextLbl.setText("Save %");
			// goalie: GAA and save percentage
			int matchesPlayed = HockmanMain.currPlayer.getMatchesPlayed();
			DecimalFormat df = new DecimalFormat(",##0.00");			
			String gaa = df.format((matchesPlayed == 0 ? 0
							: (float) HockmanMain.currPlayer.getGoals()
									/ matchesPlayed));

			int shots = HockmanMain.currPlayer.getAssists()
					+ HockmanMain.currPlayer.getGoals();
			String savePerc = df.format(shots == 0 ? 0
					: (100 - (float) 100 / shots
							* HockmanMain.currPlayer.getGoals()));

			goalsLbl.setText(gaa);
			assistsLbl.setText(savePerc);
		} else {
			goalsTextLbl.setText("Goals");
			assistsTextLbl.setText("Assists");
			goalsLbl.setText(Integer.toString(HockmanMain.currPlayer.getGoals()));
			assistsLbl.setText(Integer.toString(HockmanMain.currPlayer
					.getAssists()));
		}
		penaltyLbl
				.setText(Integer.toString(HockmanMain.currPlayer.getPenalty()));
		plusMinusLbl.setText(Integer.toString(HockmanMain.currPlayer
				.getPlusMinus()));
		wageLbl.setText(Integer.toString(HockmanMain.currPlayer.getWage()));

		Contracts contract = HockmanMain.currPlayer.getContracts();
		String s;
		if (contract.getNextContr().getTeam() == null) {
			s = Integer.toString(contract.getCurrContrYears());
			s += " Y";
		} else if (contract.getCurrContr().getTeam().equals(
				contract.getNextContr().getTeam())) {
			s = Util.getModelResourceBundle().getString("L_PROLONGED");
		} else {
			s = Util.getModelResourceBundle().getString("L_CANCELED");
		}
		contractLbl.setText(s);
		feeLbl.setText(Integer.toString(HockmanMain.currPlayer.getFee()));

		injuryLbl.setText(Integer.toString(HockmanMain.currPlayer.getHealth()
				.getInjury()));
		hurtLbl.setText(Integer.toString(HockmanMain.currPlayer.getHealth()
				.getHurt()));

		Motivation motivation = HockmanMain.currPlayer.getMotivation();
		motivationText.setText(motivation.getReasonText());
		motivationText.setEditable(false);
		personalText.setText(HockmanMain.currPlayer.getPersonal());
		personalText.setEditable(false);
		careerText.setText(HockmanMain.currPlayer.getCareer());
		careerText.setEditable(false);
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void cancel(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
