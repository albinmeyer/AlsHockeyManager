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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * Mask at the end of the season showing all transfers and new rookies
 * and retirements.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class AllTransfers implements Initializable {

	@FXML
	private TextArea transfersText;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		transfersText.setText(HockmanMain.game.getNews().getTransfers());
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}
}
