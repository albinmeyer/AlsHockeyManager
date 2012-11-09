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
import ch.hockman.model.player.Nation;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.position.Position;
import ch.hockman.model.team.Team;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

/**
 * The mask at end of season showing the national team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class EofSeas implements Initializable {

	@FXML
	private Label nationalTeamLbl;

	@FXML
	private TextArea nationalTeamPlayers;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		PlayerPtrVector ppv = HockmanMain.game.getLeague().getPlayerVector();
		String s;
		Nation nation = HockmanMain.currManagedTeam.getNation();
		s = Util.getModelResourceBundle().getString("L_NATIONAL_TEAM");
		s += nation.getName();
		nationalTeamLbl.setText(s);
		ppv.SortPlayerStrengthWithoutEnergy();
		int nofPlayers = ppv.GetNofPlayers();
		int nofGoalies = 0;
		int nofDefenders = 0;
		int nofLeftWings = 0;
		int nofCenters = 0;
		int nofRightWings = 0;
		s = "";
		for (int i = 0; i < nofPlayers; i++) {
			Player player = ppv.GetPlayer(i);
			if (player.getNation().equals(nation)) {
				boolean take = true;
				if (nofGoalies < 3
						&& player.getPosition().getPosID() == Position.PosID.GOALIE) {
					nofGoalies++;
				} else if (nofDefenders < 10
						&& player.getPosition().getPosID() == Position.PosID.DEFENDER) {
					nofDefenders++;
				} else if (nofLeftWings < 5
						&& player.getPosition().getPosID() == Position.PosID.LEFTWING) {
					nofLeftWings++;
				} else if (nofCenters < 5
						&& player.getPosition().getPosID() == Position.PosID.CENTER) {
					nofCenters++;
				} else if (nofRightWings < 5
						&& player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
					nofRightWings++;
				} else {
					take = false;
				}
				if (take) {
					s += GetNatPlayerLineUpString(player);
					for (int j = 0; j < 20; j++) {
						if (Util.random(2) > 0) {
							player.incNofNatMatches();
						}
					}
				}
			}
		}
		nationalTeamPlayers.setText(s);
	}

	private String GetNatPlayerLineUpString(Player player) {
		String s = player.getLastName();
		s += " ";
		s += player.getFirstName();
		s += " (";
		s += player.getPosition().posName();
		s += ", ";
		s += player.getAge();
		s += " y, ";
		s += player.getNatGames();
		s += " Nat, ";
		Team team = player.getContracts().getCurrContr().getTeam();
		if (team != null) {
			s += team.getTeamName();
		} else {
			s += Util.getModelResourceBundle().getString("L_FREEAGENT");
		}
		s += ")";
		s += "\n";
		return s;
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}
}
