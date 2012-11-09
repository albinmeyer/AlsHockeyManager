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

import ch.hockman.model.Modus;
import ch.hockman.model.Schedule.Result;
import ch.hockman.model.common.Util;
import ch.hockman.model.team.Team;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

/**
 * Mask showing the schedule of this season.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Schedule implements Initializable {
	private ch.hockman.model.Schedule schedule;
	private Modus modus;
	private int currRound, round;

	@FXML
	private Label nextRoundLabel;

	@FXML
	private ListView scheduleList;

	/**
	 * Getting the model values needed for this scene from the global instance
	 * of Game.
	 */
	public Schedule() {
		this.schedule = HockmanMain.game.getSchedule();
		this.modus = HockmanMain.game.getLeague().getModus();
		this.currRound = HockmanMain.game.getRound();
		this.round = currRound;
		assert (schedule != null && modus != null);
	}

	private void formShow() {
		List<String> matchList = new ArrayList<String>();
		Team homeTeam, awayTeam;
		int homeScore, awayScore;
		int nofMatches;
		if (round <= modus.getNofRounds()) {
			// regular season
			nextRoundLabel.setText("Regular Round " + round);
			nofMatches = ((modus.getNofTeams() % 2 == 0) ? modus.getNofTeams()
					: (modus.getNofTeams() + 1)) * modus.getNofDivisions() / 2;
		} else {
			// playoff
			String s = "Playoff ";
			s += 1 + (round - modus.getNofRounds() - 1) / modus.getGamesPerFinal();
			s += ", Game ";
			s += 1 + (round - modus.getNofRounds() - 1) % modus.getGamesPerFinal();
			nextRoundLabel.setText(s);
			nofMatches = (int) Math.pow(2, modus.getNofPlayoffFinals() - 1);
		}
		for (int match = 0; match < nofMatches; match++) {
			Result res = schedule.getResult(round, match);
			homeTeam = res.homeTeam;
			awayTeam = res.awayTeam;
			homeScore = res.homeScore;
			awayScore = res.awayScore;
			String string;
			if (homeTeam == null) {
				string = Util.getModelResourceBundle().getString("L_NOGAME");
			} else {
				string = homeTeam.getTeamName();
			}
			string += " - ";
			if (awayTeam == null) {
				string += Util.getModelResourceBundle().getString("L_NOGAME");
			} else {
				string += awayTeam.getTeamName();
			}
			if (homeTeam != null && awayTeam != null) {
				while (string.length() < 35) {
					string += ' ';
				}
				string += Integer.toString(homeScore);
				string += " : ";
				string += Integer.toString(awayScore);
				if(res.overTime) {
					string += " OT/SO";
				}
			}
			matchList.add(string);
		}

		ObservableList<String> items = FXCollections
				.observableArrayList(matchList);
		scheduleList.setItems(items);
		HockmanMain.setListViewCellFactory(scheduleList);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		formShow();
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void previousRound(ActionEvent event) {
		round--;
		if (round < 1) {
			round = modus.getNofRounds() + modus.getNofPlayoffFinals()
					* modus.getGamesPerFinal();
		}

		formShow();
	}

	@FXML
	private void thisRound(ActionEvent event) {
		round = currRound;

		formShow();
	}

	@FXML
	private void nextRound(ActionEvent event) {
		round++;
		if (round > modus.getNofRounds() + modus.getNofPlayoffFinals()
				* modus.getGamesPerFinal()) {
			round = 1;
		}

		formShow();
	}

}
