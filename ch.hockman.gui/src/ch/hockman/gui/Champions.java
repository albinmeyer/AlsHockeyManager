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
import ch.hockman.model.team.Team;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The Mask showing who has won the championship.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Champions implements Initializable {
	@FXML
	private ImageView championImg;

	@FXML
	private ImageView teamImg;

	@FXML
	private Label userTeamHasWonLbl;

	@FXML
	private Label championsTeamLbl;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Team champ = HockmanMain.game.getChampion();
		championsTeamLbl.setText(champ.getTeamName());
		if (champ.equals(HockmanMain.currManagedTeam)) {
			userTeamHasWonLbl.setText(Util.getModelResourceBundle().getString("L_CONGRATS"));
		} else {
			userTeamHasWonLbl.setText("");
		}

		Image image;
		try {
			String url = "file:" + champ.getPicPath();
			image = new Image(url);
			if (image.isError()) {
				url = Manager.class.getResource(Manager.L_TEAMPICPATH)
						.toString();
				image = new Image(url);
			}
		} catch (IllegalArgumentException ex) {
			String url = Manager.class.getResource(Manager.L_TEAMPICPATH)
					.toString();
			image = new Image(url);
		}
		teamImg.setImage(image);
		String url = Champions.class.getResource("champion.jpg").toString();
		image = new Image(url);
		championImg.setImage(image);
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
