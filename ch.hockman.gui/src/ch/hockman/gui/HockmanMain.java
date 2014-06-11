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

import javafx.application.Application;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import ch.hockman.model.Game;
import ch.hockman.model.common.Util;
import ch.hockman.model.match.Report;
import ch.hockman.model.player.Player;
import ch.hockman.model.position.Position;
import ch.hockman.model.team.Team;

/**
 * The main gui javafx application class.
 *
 * @author Albin
 *
 */
public class HockmanMain extends Application {
	static public StageHandler stageHandler; // I found no other way for
												// switching between multiple
												// scenes
	
	// Ugly hack, but I found no other way for providing the model to the gui
	// imho, it's a weakness of the JavaFX 2.2 framework.
	// as a solution, I should probably use the afterburner.fx DI-Framework?
	// see also
	// http://stackoverflow.com/questions/20389567/how-to-initialize-javafx-controllers-with-the-same-model-object
	static public Game game;
	static public Team currManagedTeam;
	static public boolean modified; // asking for saving at quit/new/load only
									// when modified
	static public boolean eofSeason;
	static public boolean playoffs;
	static public Team lineupTeam; // for lineup and opteam dialog
	static public Report currReport; // for report dialog
	static public Player currPlayer; // for Playerdet dialog
	static public String msgBoxLbl; // for MessageBox dialog

	// some gui tools
	static public String GetPlayerLineUpString(Player player,
			Position.PosID posId) {
		// a player with given position
		String s = "";
		if (player != null) {
			s += player.getNumber();
			while (s.length() < 3) {
				s += ' ';
			}
			s += player.getLastName();
			if (player.getPosition().getPosID() != posId) {
				s += Util.getModelResourceBundle().getString("L_WRONG_POS");
			}
			if (player.getHealth().getInjury() > 0) {
				s += Util.getModelResourceBundle().getString("L_INJURED");
			}
			if (player.getHealth().getHurt() > 0) {
				s += Util.getModelResourceBundle().getString("L_HURT");
			}
		}
		return s;
	}

	static public String GetPlayerLineUpString(Player player) {
		// any player except goalie
		String s = "";
		if (player != null) {
			s += player.getNumber();
			while (s.length() < 3) {
				s += ' ';
			}
			s += player.getLastName();
			if (player.getPosition().getPosID() == Position.PosID.GOALIE) {
				s += Util.getModelResourceBundle().getString("L_WRONG_POS");
			}
			if (player.getHealth().getInjury() > 0) {
				s += Util.getModelResourceBundle().getString("L_INJURED");
			}
			if (player.getHealth().getHurt() > 0) {
				s += Util.getModelResourceBundle().getString("L_HURT");
			}
		}
		return s;
	}

	public static void setListViewCellFactory(ListView lv) {
		lv.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> list) {
				ListCell lc = new TextFieldListCell();
				lc.setFont(Font.font(java.awt.Font.MONOSPACED, 16));
				lc.setPrefHeight(25);
				return lc;
			}
		});
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Al's Hockey Manager");
		stageHandler = new StageHandler(primaryStage, "Title.fxml");
		game = null;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
