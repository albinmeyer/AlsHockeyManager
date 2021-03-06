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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ch.hockman.model.GameCreator;
import ch.hockman.model.League;
import ch.hockman.model.LeagueCreator;
import ch.hockman.model.common.Util;

/**
 * Starting a new game, a league file must be chosen.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class NewGame implements Initializable {

	@FXML
	private Label newGameLbl;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		newGameLbl.setText(Util.getModelResourceBundle().getString("L_NEWGAME"));
	}

	@FXML
	private void ok(ActionEvent event) throws IOException {
		File file = null;
		if(!needsFileChooserWorkaround()) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File("."));
			fileChooser.getExtensionFilters().add(new ExtensionFilter("XML files (*.xml)", "*.xml"));		
			file = fileChooser.showOpenDialog(null);
		} else {
			// TODO workaround for JavaFX bug: FileChooser not running under 64 bit Windows
			// see http://www.javaworld.com/javaworld/jw-05-2012/120529-jtip-deploying-javafx.html?page=3
		   class FileNameFilter extends FileFilter {
		      public boolean accept(File arg0) {
		         if(arg0.isDirectory()) return true;
		         if(arg0.getName().endsWith("xml")) return true;
		         return false;
		      }
		      public String getDescription() {
		         return "XML files (*.xml)";
		      }
		   }		
		   JFileChooser chooser = new JFileChooser(".");
		   FileNameFilter filter = new FileNameFilter();
		   chooser.setFileFilter(filter);
		   int returnVal = chooser.showOpenDialog(null);
		   if(returnVal == JFileChooser.APPROVE_OPTION) {
			   file=chooser.getSelectedFile();
		   }		
		}		
		if (HockmanMain.game != null) {
			GameCreator.instance().deInit(HockmanMain.game);
			HockmanMain.game = null;
		}
		if(file == null) {
			HockmanMain.msgBoxLbl = "Could not load league";
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			HockmanMain.stageHandler.closeModalStage();
			return;
		}

		LeagueCreator.instance().setCurrFileName(file.getAbsolutePath());
		GameCreator.instance().setCurrFileName("");

		League league = null;
		// Switch between loading league in old format or xml format
		// League league= LeagueCreator.Instance().LoadLgu();
		try {
			league = LeagueCreator.instance().loadLguXML();
		} catch (Throwable e) {
			HockmanMain.msgBoxLbl = "Exception " + e.getClass().getName()
					+ "\n\n" + e.getMessage();
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			HockmanMain.stageHandler.closeModalStage();
			return;
		}
		if (league == null) {
			HockmanMain.msgBoxLbl = "Could not load league";
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
			HockmanMain.stageHandler.closeModalStage();
		} else {
			// deinit old game, create new game and assign league to it
			ch.hockman.model.Options.reset();
			HockmanMain.game = GameCreator.instance().newGame(league);
			HockmanMain.stageHandler.closeModalStage();
		}
	}
	
	private boolean needsFileChooserWorkaround() {
		// as of JavaFX 2.2, the JavaFX FileChooser did not work on all platforms
		// but now on JavaFX 8, it works at least on Mac OS X 10.9.3 and Win8.1!
		return false;
	}
}
