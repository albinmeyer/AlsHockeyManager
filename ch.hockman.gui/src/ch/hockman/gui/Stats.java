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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.position.Position;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamState;

/**
 * Mask showing the stats of the players of this season.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Stats implements Initializable {

	@FXML
	private Button goalsBtn;

	@FXML
	private Button assistsBtn;

	@FXML
	private Button pointsBtn;

	@FXML
	private Button nameBtn;

	@FXML
	private Button firstNameBtn;

	@FXML
	private Button positionBtn;

	@FXML
	private Button ageBtn;

	@FXML
	private Button teamBtn;

	@FXML
	private Button plusMinusBtn;

	@FXML
	private Button pimBtn;

	@FXML
	private ToggleGroup positionGrp;

	@FXML
	private ListView<String> statsList;

	@FXML
	private RadioButton goalieRadio;

	@FXML
	private RadioButton defForRadio;

	private PlayerPtrVector statppv;

	private boolean showGoalies;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		PlayerPtrVector ppv = HockmanMain.game.getLeague().getPlayerVector();
		this.statppv = new PlayerPtrVector();
		int nofPlayers = ppv.GetNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = ppv.GetPlayer(i);
			Team team = player.getContracts().getCurrContr().getTeam();
			if (team != null
					&& team.getTeamState().getTeamStateID() != TeamState.TeamStateID.NONLEAGUE) {
				this.statppv.InsertPlayer(player);
			}
		}
		this.showGoalies = false;
		positionGrp.selectToggle(positionGrp.getToggles().get(1));
		positionGrp.selectedToggleProperty().addListener(new ChangeListener<Object>() {
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2) {
				RadioButton rb = ((RadioButton) positionGrp.getSelectedToggle());
				if (rb.equals(goalieRadio)) {
					showGoalies = true;
				} else if (rb.equals(defForRadio)) {
					showGoalies = false;
				}
				statppv.SortPlayerPoints(); // default sorting
				showStatsList();
			}
		});
		this.statsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if (me.getClickCount() < 2) {
					return;
				}
				playerDetails(new ActionEvent());
			}
		});
		this.statppv.SortPlayerPoints(); // default sorting
		showStatsList();
	}

	private void showStatsList() {
		if (this.showGoalies) {
			// Goalie
			goalsBtn.setDisable(true);
			assistsBtn.setText("Save %");
			pointsBtn.setDisable(true);
		} else {
			// Def/Off
			goalsBtn.setDisable(false);
			assistsBtn.setText("Assists");
			pointsBtn.setDisable(false);
		}
		List<String> tableItems = new ArrayList<String>();
		int nofPlayers = statppv.GetNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = statppv.GetPlayer(i);
			if (this.showGoalies
					&& player.getPosition().getPosID() == Position.PosID.GOALIE
					|| !this.showGoalies
					&& player.getPosition().getPosID() != Position.PosID.GOALIE) {
				String s = player.getLastName();
				while (s.length() < 14) {
					s += ' ';
				}
				s += player.getFirstName();
				while (s.length() < 25) {
					s += ' ';
				}
				s += player.getPosition().posName();
				while (s.length() < 30) {
					s += ' ';
				}
				s += player.getAge();
				while (s.length() < 35) {
					s += ' ';
				}
				Team team = player.getContracts().getCurrContr().getTeam();
				if (team != null) {
					s += team.getTeamName();
				} else {
					s += Util.getModelResourceBundle().getString("L_FREEAGENT");
				}
				while (s.length() < 49) {
					s += ' ';
				}
				if (player.getPosition().getPosID() != Position.PosID.GOALIE) {
					s += Integer.toString(player.getGoals());
					while (s.length() < 54) {
						s += ' ';
					}
					s += Integer.toString(player.getAssists());
					while (s.length() < 59) {
						s += ' ';
					}
					s += Integer.toString(player.getGoals()
							+ player.getAssists());
				} else {
					int shots = player.getAssists() + player.getGoals();
					while (s.length() < 52) {
						s += ' ';
					}
					DecimalFormat df = new DecimalFormat(",##0.00");
					s += df.format(shots == 0 ? 0 : (100 - (float) 100 / shots
							* player.getGoals()));
				}
				while (s.length() < 64) {
					s += ' ';
				}
				s += Integer.toString(player.getPlusMinus());
				while (s.length() < 69) {
					s += ' ';
				}
				s += Integer.toString(player.getPenalty());
				while (s.length() < 74) {
					s += ' ';
				}

				tableItems.add(s);
			}
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(tableItems);
		statsList.setItems(items);
		HockmanMain.setListViewCellFactory(statsList);

	}

	@FXML
	private void nameBtn(ActionEvent event) {
		statppv.SortPlayerLastName();
		showStatsList();
	}

	@FXML
	private void firstNameBtn(ActionEvent event) {
		statppv.SortPlayerFirstName();
		showStatsList();
	}

	@FXML
	private void positionBtn(ActionEvent event) {
		statppv.SortPlayerPos();
		showStatsList();
	}

	@FXML
	private void ageBtn(ActionEvent event) {
		statppv.SortPlayerAge();
		showStatsList();
	}

	@FXML
	private void teamBtn(ActionEvent event) {
		statppv.SortPlayerOwner();
		showStatsList();
	}

	@FXML
	private void goalsBtn(ActionEvent event) {
		statppv.SortPlayerGoals();
		showStatsList();
	}

	@FXML
	private void assistsBtn(ActionEvent event) {
		statppv.SortPlayerAssists();
		showStatsList();
	}

	@FXML
	private void pointsBtn(ActionEvent event) {
		statppv.SortPlayerPoints();
		showStatsList();
	}

	@FXML
	private void plusMinusBtn(ActionEvent event) {
		statppv.SortPlayerPlusMinus();
		showStatsList();
	}

	@FXML
	private void pimBtn(ActionEvent event) {
		statppv.SortPlayerPenalty();
		showStatsList();
	}

	@FXML
	private void playerDetails(ActionEvent event) {
		int counter = 0;
		int i = 0;
		int index = this.statsList.getSelectionModel().getSelectedIndex();
		if (index < 0) {
			index = 0;
		}
		Player player = null;
		while (counter <= index) {
			player = statppv.GetPlayer(i);
			i++;
			if (this.showGoalies
					&& player.getPosition().getPosID() == Position.PosID.GOALIE
					|| !this.showGoalies
					&& player.getPosition().getPosID() != Position.PosID.GOALIE) {
				counter++;
			}
		}
		HockmanMain.currPlayer = player;
		HockmanMain.stageHandler.showModalStageAndWait("PlayerDetails.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_PLAYERDETAILS"));
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

}
