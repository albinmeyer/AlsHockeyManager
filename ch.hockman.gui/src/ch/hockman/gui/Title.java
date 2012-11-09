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

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import ch.hockman.model.common.Util;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Title of the whole game, let's choose the language.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Title implements Initializable {

	@FXML
	private ImageView titleImg;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String url = Title.class.getResource("title.jpg").toString();
		Image image = new Image(url);
		titleImg.setImage(image);
	}

	@FXML
	private void startDeutsch(ActionEvent event) throws IOException {
		Locale.setDefault(Locale.GERMAN);
		HockmanMain.stageHandler.showModalStageAndWait("Manager.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MANAGER"));
	}

	@FXML
	private void startEnglish(ActionEvent event) throws IOException {
		Locale.setDefault(Locale.ENGLISH);
		HockmanMain.stageHandler.showModalStageAndWait("Manager.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MANAGER"));
	}
}
