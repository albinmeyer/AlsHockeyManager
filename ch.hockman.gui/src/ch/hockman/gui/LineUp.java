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

import ch.hockman.model.common.Util;
import ch.hockman.model.player.Player;
import ch.hockman.model.position.Position;
import ch.hockman.model.team.CoachAI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * Mask showing the lineup of the managed team.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class LineUp implements Initializable {

	@FXML
	private ListView<String> playerTable;

	@FXML
	private ListView<String> defense1_55;

	@FXML
	private ListView<String> defense2_55;

	@FXML
	private ListView<String> defense3_55;

	@FXML
	private ListView<String> offense1_55;

	@FXML
	private ListView<String> offense2_55;

	@FXML
	private ListView<String> offense3_55;

	@FXML
	private ListView<String> offense4_55;

	@FXML
	private ListView<String> line1_44;

	@FXML
	private ListView<String> line2_44;

	@FXML
	private ListView<String> line1_33;

	@FXML
	private ListView<String> line2_33;

	@FXML
	private ListView<String> line1_pp5;

	@FXML
	private ListView<String> line2_pp5;

	@FXML
	private ListView<String> line1_pp4;

	@FXML
	private ListView<String> line2_pp4;

	@FXML
	private ListView<String> line1_pk4;

	@FXML
	private ListView<String> line2_pk4;

	@FXML
	private ListView<String> line1_pk3;

	@FXML
	private ListView<String> line2_pk3;

	@FXML
	private ListView<String> goalies;

	@FXML
	private Label helpLbl;
	
	@FXML
	private Label foreignersLbl;
	
	@FXML
	private Label mouseLbl;

	private String dragStringWorkaround;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setupLists();
		setupPlayerTable();
		linesShow();
	}

	private void setupPlayerTable() {
		playerTable.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				/* drag was detected, start a drag-and-drop gesture */
				/* allow any transfer mode */
				Dragboard db = playerTable.startDragAndDrop(TransferMode.ANY);

				/* Put a string on a dragboard */
				ClipboardContent content = new ClipboardContent();
				String player = Integer.toString(playerTable
						.getSelectionModel().getSelectedIndex());
				
				// drag&drop does not work on Ubuntu with Oracle VM 7u9
				//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
				dragStringWorkaround = player;
				
				content.putString(player);
				db.setContent(content);

				event.consume();
			}
		});
		playerTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent me) {
				if (me.getClickCount() < 2) {
					return;
				} // double click
				playerDetails(new ActionEvent());
			}
		});
		helpLbl.setText(Util.getModelResourceBundle().getString("L_LINEUP_HELP"));
		mouseLbl.setText(Util.getModelResourceBundle().getString("L_DRAW_MOUSE"));
		foreignersLbl.setText(Util.getModelResourceBundle().getString("L_MAX_NUMBER_FOREIGNERS")
				+ HockmanMain.game.getLeague().getModus().maxNofForeigners());
		// fill the team list box
		HockmanMain.lineupTeam.sortPlayerPos();
		int nofPlayers = HockmanMain.lineupTeam.getNofTeamPlayers();
		List<String> players = new ArrayList<String>();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
			String s = player.getLastName();
			while (s.length() < 12) {
				s += ' ';
			}
			s += player.getPosition().posName();
			while (s.length() < 15) {
				s += ' ';
			}
			s += player.getNation().getName();
			while (s.length() < 27) {
				s += ' ';
			}
			s += player.getHealth().getInjury();
			players.add(s);
		}
		ObservableList<String> items = FXCollections
				.observableArrayList(players);
		playerTable.setItems(items);
		HockmanMain.setListViewCellFactory(playerTable);
	}

	private void setupLists() {
		setupGoalie();
		setupDefense1_55();
		setupDefense2_55();
		setupDefense3_55();
		setupOffense1_55();
		setupOffense2_55();
		setupOffense3_55();
		setupOffense4_55();
		setupLine1_44();
		setupLine2_44();
		setupLine1_33();
		setupLine2_33();
		setupLine1_pp5();
		setupLine2_pp5();
		setupLine1_pp4();
		setupLine2_pp4();
		setupLine1_pk4();
		setupLine2_pk4();
		setupLine1_pk3();
		setupLine2_pk3();
	}

	private void setupGoalie() {
		goalies.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = goalies.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getG().goal1 = null;
					break;
				case 1:
					lineUp.getG().goal2 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		goalies.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != goalies && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);
					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getG().goal1 == null || lineUp.getG().goal2 == null)
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& lineUp.getE55().d11 != player
								&& lineUp.getE55().d12 != player
								&& lineUp.getE55().d21 != player
								&& lineUp.getE55().d22 != player
								&& lineUp.getE55().d31 != player
								&& lineUp.getE55().d32 != player
								&& lineUp.getE55().lw1 != player
								&& lineUp.getE55().c1 != player
								&& lineUp.getE55().rw1 != player
								&& lineUp.getE55().lw2 != player
								&& lineUp.getE55().c2 != player
								&& lineUp.getE55().rw2 != player
								&& lineUp.getE55().lw3 != player
								&& lineUp.getE55().c3 != player
								&& lineUp.getE55().rw3 != player
								&& lineUp.getE55().lw4 != player
								&& lineUp.getE55().c4 != player
								&& lineUp.getE55().rw4 != player
								&& lineUp.getE44().d11 != player
								&& lineUp.getE44().d12 != player
								&& lineUp.getE44().f11 != player
								&& lineUp.getE44().f12 != player
								&& lineUp.getE44().d21 != player
								&& lineUp.getE44().d22 != player
								&& lineUp.getE44().f21 != player
								&& lineUp.getE44().f22 != player
								&& lineUp.getE33().d11 != player
								&& lineUp.getE33().d12 != player
								&& lineUp.getE33().f1 != player
								&& lineUp.getE33().d21 != player
								&& lineUp.getE33().d22 != player
								&& lineUp.getE33().f2 != player
								&& lineUp.getPp5().d11 != player
								&& lineUp.getPp5().d12 != player
								&& lineUp.getPp5().lw1 != player
								&& lineUp.getPp5().c1 != player
								&& lineUp.getPp5().rw1 != player
								&& lineUp.getPp5().d21 != player
								&& lineUp.getPp5().d22 != player
								&& lineUp.getPp5().lw2 != player
								&& lineUp.getPp5().c2 != player
								&& lineUp.getPp5().rw2 != player
								&& lineUp.getPp4().d11 != player
								&& lineUp.getPp4().d12 != player
								&& lineUp.getPp4().f11 != player
								&& lineUp.getPp4().f12 != player
								&& lineUp.getPp4().d21 != player
								&& lineUp.getPp4().d22 != player
								&& lineUp.getPp4().f21 != player
								&& lineUp.getPp4().f22 != player
								&& lineUp.getPk4().d11 != player
								&& lineUp.getPk4().d12 != player
								&& lineUp.getPk4().f11 != player
								&& lineUp.getPk4().f12 != player
								&& lineUp.getPk4().d21 != player
								&& lineUp.getPk4().d22 != player
								&& lineUp.getPk4().f21 != player
								&& lineUp.getPk4().f22 != player
								&& lineUp.getPk3().d11 != player
								&& lineUp.getPk3().d12 != player
								&& lineUp.getPk3().f1 != player
								&& lineUp.getPk3().d21 != player
								&& lineUp.getPk3().d22 != player
								&& lineUp.getPk3().f2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		goalies.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);
					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.GOALIE) {
						if (lineUp.getG().goal1 == null) {
							lineUp.getG().goal1 = player;
						} else if (lineUp.getG().goal1.getPosition().getPosID() != Position.PosID.GOALIE) {
							Player swap = lineUp.getG().goal1;
							lineUp.getG().goal1 = player;
							assert (lineUp.getG().goal2 == null);
							lineUp.getG().goal2 = swap;
						} else {
							assert (lineUp.getG().goal2 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getG().goal2 = player;
						}
					} else {
						Player swap = lineUp.getG().goal2;
						lineUp.getG().goal2 = player;
						if (swap != null) {
							assert (lineUp.getG().goal1 == null);
							lineUp.getG().goal1 = swap;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupDefense1_55() {
		defense1_55.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = defense1_55.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
					case 0:
						lineUp.getE55().d11 = null;
						break;
					case 1:
						lineUp.getE55().d12 = null;
						break;
					default:
						assert (i == -1);
				}
				linesShow();
			}
		});
		defense1_55.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != defense1_55 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);
					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE55().d11 == null || lineUp.getE55().d12 == null)
								&& lineUp.getE55().d11 != player
								&& lineUp.getE55().d12 != player
								&& lineUp.getE55().lw1 != player
								&& lineUp.getE55().c1 != player
								&& lineUp.getE55().rw1 != player
								&& lineUp.getE55().lw2 != player
								&& lineUp.getE55().c2 != player
								&& lineUp.getE55().rw2 != player
								&& lineUp.getE55().lw3 != player
								&& lineUp.getE55().c3 != player
								&& lineUp.getE55().rw3 != player
								&& lineUp.getE55().lw4 != player
								&& lineUp.getE55().c4 != player
								&& lineUp.getE55().rw4 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		defense1_55.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);
					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (lineUp.getE55().d11 == null) {
						lineUp.getE55().d11 = player;
					} else {
						assert (lineUp.getE55().d12 == null); // DragOver allows drop
															// only if lineUp
															// incomplete
						lineUp.getE55().d12 = player;
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupDefense2_55() {
		defense2_55.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = defense2_55.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE55().d21 = null;
					break;
				case 1:
					lineUp.getE55().d22 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		defense2_55.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != defense2_55 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE55().d21 == null || lineUp.getE55().d22 == null)
								&& lineUp.getE55().d21 != player
								&& lineUp.getE55().d32 != player
								&& lineUp.getE55().lw1 != player
								&& lineUp.getE55().c1 != player
								&& lineUp.getE55().rw1 != player
								&& lineUp.getE55().lw2 != player
								&& lineUp.getE55().c2 != player
								&& lineUp.getE55().rw2 != player
								&& lineUp.getE55().lw3 != player
								&& lineUp.getE55().c3 != player
								&& lineUp.getE55().rw3 != player
								&& lineUp.getE55().lw4 != player
								&& lineUp.getE55().c4 != player
								&& lineUp.getE55().rw4 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		defense2_55.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (lineUp.getE55().d21 == null) {
						lineUp.getE55().d21 = player;
					} else {
						assert (lineUp.getE55().d22 == null); // DragOver allows drop
															// only if lineUp
															// incomplete
						lineUp.getE55().d22 = player;
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupDefense3_55() {
		defense3_55.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = defense3_55.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE55().d31 = null;
					break;
				case 1:
					lineUp.getE55().d32 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		defense3_55.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != defense3_55 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE55().d31 == null || lineUp.getE55().d32 == null)
								&& lineUp.getE55().d31 != player
								&& lineUp.getE55().d32 != player
								&& lineUp.getE55().lw1 != player
								&& lineUp.getE55().c1 != player
								&& lineUp.getE55().rw1 != player
								&& lineUp.getE55().lw2 != player
								&& lineUp.getE55().c2 != player
								&& lineUp.getE55().rw2 != player
								&& lineUp.getE55().lw3 != player
								&& lineUp.getE55().c3 != player
								&& lineUp.getE55().rw3 != player
								&& lineUp.getE55().lw4 != player
								&& lineUp.getE55().c4 != player
								&& lineUp.getE55().rw4 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		defense3_55.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (lineUp.getE55().d31 == null) {
						lineUp.getE55().d31 = player;
					} else {
						assert (lineUp.getE55().d32 == null); // DragOver allows drop
															// only if lineUp
															// incomplete
						lineUp.getE55().d32 = player;
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupOffense1_55() {
		offense1_55.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = offense1_55.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE55().lw1 = null;
					break;
				case 1:
					lineUp.getE55().c1 = null;
					break;
				case 2:
					lineUp.getE55().rw1 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		offense1_55.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != offense1_55 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE55().lw1 == null
								|| lineUp.getE55().c1 == null || lineUp.getE55().rw1 == null)
								&& lineUp.getE55().lw1 != player
								&& lineUp.getE55().c1 != player
								&& lineUp.getE55().rw1 != player
								&& lineUp.getE55().d11 != player
								&& lineUp.getE55().d12 != player
								&& lineUp.getE55().d21 != player
								&& lineUp.getE55().d22 != player
								&& lineUp.getE55().d31 != player
								&& lineUp.getE55().d32 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		offense1_55.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.LEFTWING) {
						Player swap = lineUp.getE55().lw1;
						lineUp.getE55().lw1 = player;
						if (swap != null) {
							if (lineUp.getE55().c1 == null) {
								lineUp.getE55().c1 = swap;
							} else {
								assert (lineUp.getE55().rw1 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw1 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
						Player swap = lineUp.getE55().c1;
						lineUp.getE55().c1 = player;
						if (swap != null) {
							if (lineUp.getE55().lw1 == null) {
								lineUp.getE55().lw1 = swap;
							} else {
								assert (lineUp.getE55().rw1 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw1 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
						Player swap = lineUp.getE55().rw1;
						lineUp.getE55().rw1 = player;
						if (swap != null) {
							if (lineUp.getE55().c1 == null) {
								lineUp.getE55().c1 = swap;
							} else {
								assert (lineUp.getE55().lw1 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().lw1 = swap;
							}
						}
					} else {
						// player has wrong position, so insert just at an empty
						// space
						if (lineUp.getE55().lw1 == null) {
							lineUp.getE55().lw1 = player;
						} else if (lineUp.getE55().c1 == null) {
							lineUp.getE55().c1 = player;
						} else {
							assert (lineUp.getE55().rw1 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE55().rw1 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupOffense2_55() {
		offense2_55.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = offense2_55.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE55().lw2 = null;
					break;
				case 1:
					lineUp.getE55().c2 = null;
					break;
				case 2:
					lineUp.getE55().rw2 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		offense2_55.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != offense2_55 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE55().lw2 == null
								|| lineUp.getE55().c2 == null || lineUp.getE55().rw2 == null)
								&& lineUp.getE55().lw2 != player
								&& lineUp.getE55().c2 != player
								&& lineUp.getE55().rw2 != player
								&& lineUp.getE55().d11 != player
								&& lineUp.getE55().d12 != player
								&& lineUp.getE55().d21 != player
								&& lineUp.getE55().d22 != player
								&& lineUp.getE55().d31 != player
								&& lineUp.getE55().d32 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());

						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		offense2_55.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.LEFTWING) {
						Player swap = lineUp.getE55().lw2;
						lineUp.getE55().lw2 = player;
						if (swap != null) {
							if (lineUp.getE55().c2 == null) {
								lineUp.getE55().c2 = swap;
							} else {
								assert (lineUp.getE55().rw2 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw2 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
						Player swap = lineUp.getE55().c2;
						lineUp.getE55().c2 = player;
						if (swap != null) {
							if (lineUp.getE55().lw2 == null) {
								lineUp.getE55().lw2 = swap;
							} else {
								assert (lineUp.getE55().rw2 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw2 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
						Player swap = lineUp.getE55().rw2;
						lineUp.getE55().rw2 = player;
						if (swap != null) {
							if (lineUp.getE55().c2 == null) {
								lineUp.getE55().c2 = swap;
							} else {
								assert (lineUp.getE55().lw2 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().lw2 = swap;
							}
						}
					} else {
						// player has wrong position, so insert just at an empty
						// space
						if (lineUp.getE55().lw2 == null) {
							lineUp.getE55().lw2 = player;
						} else if (lineUp.getE55().c2 == null) {
							lineUp.getE55().c2 = player;
						} else {
							assert (lineUp.getE55().rw2 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE55().rw2 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupOffense3_55() {
		offense3_55.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = offense3_55.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE55().lw3 = null;
					break;
				case 1:
					lineUp.getE55().c3 = null;
					break;
				case 2:
					lineUp.getE55().rw3 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		offense3_55.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != offense3_55 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE55().lw3 == null
								|| lineUp.getE55().c3 == null || lineUp.getE55().rw3 == null)
								&& lineUp.getE55().lw3 != player
								&& lineUp.getE55().c3 != player
								&& lineUp.getE55().rw3 != player
								&& lineUp.getE55().d11 != player
								&& lineUp.getE55().d12 != player
								&& lineUp.getE55().d21 != player
								&& lineUp.getE55().d22 != player
								&& lineUp.getE55().d31 != player
								&& lineUp.getE55().d32 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		offense3_55.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.LEFTWING) {
						Player swap = lineUp.getE55().lw3;
						lineUp.getE55().lw3 = player;
						if (swap != null) {
							if (lineUp.getE55().c3 == null) {
								lineUp.getE55().c3 = swap;
							} else {
								assert (lineUp.getE55().rw3 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw3 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
						Player swap = lineUp.getE55().c3;
						lineUp.getE55().c3 = player;
						if (swap != null) {
							if (lineUp.getE55().lw3 == null) {
								lineUp.getE55().lw3 = swap;
							} else {
								assert (lineUp.getE55().rw3 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw3 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
						Player swap = lineUp.getE55().rw3;
						lineUp.getE55().rw3 = player;
						if (swap != null) {
							if (lineUp.getE55().c3 == null) {
								lineUp.getE55().c3 = swap;
							} else {
								assert (lineUp.getE55().lw3 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().lw3 = swap;
							}
						}
					} else {
						// player has wrong position, so insert just at an empty
						// space
						if (lineUp.getE55().lw3 == null) {
							lineUp.getE55().lw3 = player;
						} else if (lineUp.getE55().c3 == null) {
							lineUp.getE55().c3 = player;
						} else {
							assert (lineUp.getE55().rw3 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE55().rw3 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupOffense4_55() {
		offense4_55.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = offense4_55.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE55().lw4 = null;
					break;
				case 1:
					lineUp.getE55().c4 = null;
					break;
				case 2:
					lineUp.getE55().rw4 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		offense4_55.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != offense4_55 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE55().lw4 == null
								|| lineUp.getE55().c4 == null || lineUp.getE55().rw4 == null)
								&& lineUp.getE55().lw4 != player
								&& lineUp.getE55().c4 != player
								&& lineUp.getE55().rw4 != player
								&& lineUp.getE55().d11 != player
								&& lineUp.getE55().d12 != player
								&& lineUp.getE55().d21 != player
								&& lineUp.getE55().d22 != player
								&& lineUp.getE55().d31 != player
								&& lineUp.getE55().d32 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		offense4_55.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.LEFTWING) {
						Player swap = lineUp.getE55().lw4;
						lineUp.getE55().lw4 = player;
						if (swap != null) {
							if (lineUp.getE55().c4 == null) {
								lineUp.getE55().c4 = swap;
							} else {
								assert (lineUp.getE55().rw4 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw4 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
						Player swap = lineUp.getE55().c4;
						lineUp.getE55().c4 = player;
						if (swap != null) {
							if (lineUp.getE55().lw4 == null) {
								lineUp.getE55().lw4 = swap;
							} else {
								assert (lineUp.getE55().rw4 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().rw4 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
						Player swap = lineUp.getE55().rw4;
						lineUp.getE55().rw4 = player;
						if (swap != null) {
							if (lineUp.getE55().c4 == null) {
								lineUp.getE55().c4 = swap;
							} else {
								assert (lineUp.getE55().lw4 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE55().lw4 = swap;
							}
						}
					} else {
						// player has wrong position, so insert just at an empty
						// space
						if (lineUp.getE55().lw4 == null) {
							lineUp.getE55().lw4 = player;
						} else if (lineUp.getE55().c4 == null) {
							lineUp.getE55().c4 = player;
						} else {
							assert (lineUp.getE55().rw4 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE55().rw4 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine1_pp4() {
		line1_pp4.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line1_pp4.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPp4().d11 = null;
					break;
				case 1:
					lineUp.getPp4().d12 = null;
					break;
				case 2:
					lineUp.getPp4().f11 = null;
					break;
				case 3:
					lineUp.getPp4().f12 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line1_pp4.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line1_pp4 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPp4().d11 == null
								|| lineUp.getPp4().d12 == null
								|| lineUp.getPp4().f11 == null || lineUp.getPp4().f12 == null)
								&& lineUp.getPp4().d11 != player
								&& lineUp.getPp4().d12 != player
								&& lineUp.getPp4().f11 != player
								&& lineUp.getPp4().f12 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line1_pp4.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getPp4().d11 == null) {
							lineUp.getPp4().d11 = player;
						} else if (lineUp.getPp4().d11.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp4().d11;
							lineUp.getPp4().d11 = player;
							if (lineUp.getPp4().d12 == null) {
								lineUp.getPp4().d12 = swap;
							} else if (lineUp.getPp4().f11 == null) {
								lineUp.getPp4().f11 = swap;
							} else {
								assert (lineUp.getPp4().f12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().f12 = swap;
							}
						} else if (lineUp.getPp4().d12 == null) {
							lineUp.getPp4().d12 = player;
						} else if (lineUp.getPp4().d12.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp4().d12;
							lineUp.getPp4().d12 = player;
							if (lineUp.getPp4().f11 == null) {
								lineUp.getPp4().f11 = swap;
							} else {
								assert (lineUp.getPp4().f12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().f12 = swap;
							}
						} else if (lineUp.getPp4().f11 == null) {
							lineUp.getPp4().f11 = player;
						} else {
							assert (lineUp.getPp4().f12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPp4().f12 = player;
						}
					} else {
						if (lineUp.getPp4().f11 == null) {
							lineUp.getPp4().f11 = player;
						} else if (lineUp.getPp4().f11.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPp4().f11.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPp4().f11;
							lineUp.getPp4().f11 = player;
							if (lineUp.getPp4().f12 == null) {
								lineUp.getPp4().f12 = swap;
							} else if (lineUp.getPp4().d11 == null) {
								lineUp.getPp4().d11 = swap;
							} else {
								assert (lineUp.getPp4().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().d12 = swap;
							}
						} else if (lineUp.getPp4().f12 == null) {
							lineUp.getPp4().f12 = player;
						} else if (lineUp.getPp4().f12.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPp4().f12.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPp4().f12;
							lineUp.getPp4().f12 = player;
							if (lineUp.getPp4().d11 == null) {
								lineUp.getPp4().d11 = swap;
							} else {
								assert (lineUp.getPp4().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().d12 = swap;
							}
						} else if (lineUp.getPp4().d11 == null) {
							lineUp.getPp4().d11 = player;
						} else {
							assert (lineUp.getPp4().d12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPp4().d12 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine2_pp4() {
		line2_pp4.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line2_pp4.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPp4().d21 = null;
					break;
				case 1:
					lineUp.getPp4().d22 = null;
					break;
				case 2:
					lineUp.getPp4().f21 = null;
					break;
				case 3:
					lineUp.getPp4().f22 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line2_pp4.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line2_pp4 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPp4().d21 == null
								|| lineUp.getPp4().d22 == null
								|| lineUp.getPp4().f21 == null || lineUp.getPp4().f22 == null)
								&& lineUp.getPp4().d21 != player
								&& lineUp.getPp4().d22 != player
								&& lineUp.getPp4().f21 != player
								&& lineUp.getPp4().f22 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line2_pp4.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getPp4().d21 == null) {
							lineUp.getPp4().d21 = player;
						} else if (lineUp.getPp4().d21.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp4().d21;
							lineUp.getPp4().d21 = player;
							if (lineUp.getPp4().d22 == null) {
								lineUp.getPp4().d22 = swap;
							} else if (lineUp.getPp4().f21 == null) {
								lineUp.getPp4().f21 = swap;
							} else {
								assert (lineUp.getPp4().f22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().f22 = swap;
							}
						} else if (lineUp.getPp4().d22 == null) {
							lineUp.getPp4().d22 = player;
						} else if (lineUp.getPp4().d22.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp4().d22;
							lineUp.getPp4().d22 = player;
							if (lineUp.getPp4().f21 == null) {
								lineUp.getPp4().f21 = swap;
							} else {
								assert (lineUp.getPp4().f22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().f22 = swap;
							}
						} else if (lineUp.getPp4().f21 == null) {
							lineUp.getPp4().f21 = player;
						} else {
							assert (lineUp.getPp4().f22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPp4().f22 = player;
						}
					} else {
						if (lineUp.getPp4().f21 == null) {
							lineUp.getPp4().f21 = player;
						} else if (lineUp.getPp4().f21.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPp4().f21.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPp4().f21;
							lineUp.getPp4().f21 = player;
							if (lineUp.getPp4().f22 == null) {
								lineUp.getPp4().f22 = swap;
							} else if (lineUp.getPp4().d21 == null) {
								lineUp.getPp4().d21 = swap;
							} else {
								assert (lineUp.getPp4().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().d22 = swap;
							}
						} else if (lineUp.getPp4().f22 == null) {
							lineUp.getPp4().f22 = player;
						} else if (lineUp.getPp4().f22.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPp4().f22.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPp4().f22;
							lineUp.getPp4().f22 = player;
							if (lineUp.getPp4().d21 == null) {
								lineUp.getPp4().d21 = swap;
							} else {
								assert (lineUp.getPp4().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp4().d22 = swap;
							}
						} else if (lineUp.getPp4().d21 == null) {
							lineUp.getPp4().d21 = player;
						} else {
							assert (lineUp.getPp4().d22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPp4().d22 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine1_pp5() {
		line1_pp5.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line1_pp5.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPp5().d11 = null;
					break;
				case 1:
					lineUp.getPp5().d12 = null;
					break;
				case 2:
					lineUp.getPp5().lw1 = null;
					break;
				case 3:
					lineUp.getPp5().c1 = null;
					break;
				case 4:
					lineUp.getPp5().rw1 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line1_pp5.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line1_pp5 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPp5().d11 == null
								|| lineUp.getPp5().d12 == null
								|| lineUp.getPp5().lw1 == null
								|| lineUp.getPp5().c1 == null || lineUp.getPp5().rw1 == null)
								&& lineUp.getPp5().d11 != player
								&& lineUp.getPp5().d12 != player
								&& lineUp.getPp5().lw1 != player
								&& lineUp.getPp5().c1 != player
								&& lineUp.getPp5().rw1 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());

						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line1_pp5.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER
							|| player.getPosition().getPosID() == Position.PosID.GOALIE) {
						if (lineUp.getPp5().d11 == null) {
							lineUp.getPp5().d11 = player;
						} else if (lineUp.getPp5().d11.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp5().d11;
							lineUp.getPp5().d11 = player;
							if (lineUp.getPp5().d12 == null) {
								lineUp.getPp5().d12 = swap;
							} else if (lineUp.getPp5().lw1 == null) {
								lineUp.getPp5().lw1 = swap;
							} else if (lineUp.getPp5().c1 == null) {
								lineUp.getPp5().c1 = swap;
							} else {
								assert (lineUp.getPp5().rw1 == null);
								lineUp.getPp5().rw1 = swap;
							}
						} else if (lineUp.getPp5().d12 == null) {
							lineUp.getPp5().d12 = player;
						} else if (lineUp.getPp5().d12.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp5().d12;
							lineUp.getPp5().d12 = player;
							if (lineUp.getPp5().lw1 == null) {
								lineUp.getPp5().lw1 = swap;
							} else if (lineUp.getPp5().c1 == null) {
								lineUp.getPp5().c1 = swap;
							} else {
								assert (lineUp.getPp5().rw1 == null);
								lineUp.getPp5().rw1 = swap;
							}
						} else if (lineUp.getPp5().lw1 == null) {
							lineUp.getPp5().lw1 = player;
						} else if (lineUp.getPp5().c1 == null) {
							lineUp.getPp5().c1 = player;
						} else {
							assert (lineUp.getPp5().rw1 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPp5().rw1 = player;
						}
					} else if (player.getPosition().getPosID() == Position.PosID.LEFTWING) {
						Player swap = lineUp.getPp5().lw1;
						lineUp.getPp5().lw1 = player;
						if (swap != null) {
							if (lineUp.getPp5().c1 == null) {
								lineUp.getPp5().c1 = swap;
							} else if (lineUp.getPp5().rw1 == null) {
								lineUp.getPp5().rw1 = swap;
							} else if (lineUp.getPp5().d11 == null) {
								lineUp.getPp5().d11 = swap;
							} else {
								assert (lineUp.getPp5().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp5().d12 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
						Player swap = lineUp.getPp5().c1;
						lineUp.getPp5().c1 = player;
						if (swap != null) {
							if (lineUp.getPp5().lw1 == null) {
								lineUp.getPp5().lw1 = swap;
							} else if (lineUp.getPp5().rw1 == null) {
								lineUp.getPp5().rw1 = swap;
							} else if (lineUp.getPp5().d11 == null) {
								lineUp.getPp5().d11 = swap;
							} else {
								assert (lineUp.getPp5().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp5().d12 = swap;
							}
						}
					} else {
						assert (player.getPosition().getPosID() == Position.PosID.RIGHTWING);
						Player swap = lineUp.getPp5().rw1;
						lineUp.getPp5().rw1 = player;
						if (swap != null) {
							if (lineUp.getPp5().c1 == null) {
								lineUp.getPp5().c1 = swap;
							} else if (lineUp.getPp5().lw1 == null) {
								lineUp.getPp5().lw1 = swap;
							} else if (lineUp.getPp5().d11 == null) {
								lineUp.getPp5().d11 = swap;
							} else {
								assert (lineUp.getPp5().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp5().d12 = swap;
							}
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine2_pp5() {
		line2_pp5.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line2_pp5.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPp5().d21 = null;
					break;
				case 1:
					lineUp.getPp5().d22 = null;
					break;
				case 2:
					lineUp.getPp5().lw2 = null;
					break;
				case 3:
					lineUp.getPp5().c2 = null;
					break;
				case 4:
					lineUp.getPp5().rw2 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line2_pp5.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line2_pp5 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPp5().d21 == null
								|| lineUp.getPp5().d22 == null
								|| lineUp.getPp5().lw2 == null
								|| lineUp.getPp5().c2 == null || lineUp.getPp5().rw2 == null)
								&& lineUp.getPp5().d21 != player
								&& lineUp.getPp5().d22 != player
								&& lineUp.getPp5().lw2 != player
								&& lineUp.getPp5().c2 != player
								&& lineUp.getPp5().rw2 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line2_pp5.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER
							|| player.getPosition().getPosID() == Position.PosID.GOALIE) {
						if (lineUp.getPp5().d21 == null) {
							lineUp.getPp5().d21 = player;
						} else if (lineUp.getPp5().d21.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp5().d21;
							lineUp.getPp5().d21 = player;
							if (lineUp.getPp5().d22 == null) {
								lineUp.getPp5().d22 = swap;
							} else if (lineUp.getPp5().lw2 == null) {
								lineUp.getPp5().lw2 = swap;
							} else if (lineUp.getPp5().c2 == null) {
								lineUp.getPp5().c2 = swap;
							} else {
								assert (lineUp.getPp5().rw2 == null);
								lineUp.getPp5().rw2 = swap;
							}
						} else if (lineUp.getPp5().d22 == null) {
							lineUp.getPp5().d22 = player;
						} else if (lineUp.getPp5().d22.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPp5().d22;
							lineUp.getPp5().d22 = player;
							if (lineUp.getPp5().lw2 == null) {
								lineUp.getPp5().lw2 = swap;
							} else if (lineUp.getPp5().c2 == null) {
								lineUp.getPp5().c2 = swap;
							} else {
								assert (lineUp.getPp5().rw2 == null);
								lineUp.getPp5().rw2 = swap;
							}
						} else if (lineUp.getPp5().lw2 == null) {
							lineUp.getPp5().lw2 = player;
						} else if (lineUp.getPp5().c2 == null) {
							lineUp.getPp5().c2 = player;
						} else {
							assert (lineUp.getPp5().rw2 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPp5().rw2 = player;
						}
					} else if (player.getPosition().getPosID() == Position.PosID.LEFTWING) {
						Player swap = lineUp.getPp5().lw2;
						lineUp.getPp5().lw2 = player;
						if (swap != null) {
							if (lineUp.getPp5().c2 == null) {
								lineUp.getPp5().c2 = swap;
							} else if (lineUp.getPp5().rw2 == null) {
								lineUp.getPp5().rw2 = swap;
							} else if (lineUp.getPp5().d21 == null) {
								lineUp.getPp5().d21 = swap;
							} else {
								assert (lineUp.getPp5().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp5().d22 = swap;
							}
						}
					} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
						Player swap = lineUp.getPp5().c2;
						lineUp.getPp5().c2 = player;
						if (swap != null) {
							if (lineUp.getPp5().lw2 == null) {
								lineUp.getPp5().lw2 = swap;
							} else if (lineUp.getPp5().rw2 == null) {
								lineUp.getPp5().rw2 = swap;
							} else if (lineUp.getPp5().d21 == null) {
								lineUp.getPp5().d21 = swap;
							} else {
								assert (lineUp.getPp5().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp5().d22 = swap;
							}
						}
					} else {
						assert (player.getPosition().getPosID() == Position.PosID.RIGHTWING);
						Player swap = lineUp.getPp5().rw2;
						lineUp.getPp5().rw2 = player;
						if (swap != null) {
							if (lineUp.getPp5().c2 == null) {
								lineUp.getPp5().c2 = swap;
							} else if (lineUp.getPp5().lw2 == null) {
								lineUp.getPp5().lw2 = swap;
							} else if (lineUp.getPp5().d21 == null) {
								lineUp.getPp5().d21 = swap;
							} else {
								assert (lineUp.getPp5().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPp5().d22 = swap;
							}
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine1_33() {
		line1_33.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line1_33.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE33().d11 = null;
					break;
				case 1:
					lineUp.getE33().d12 = null;
					break;
				case 2:
					lineUp.getE33().f1 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line1_33.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line1_33 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE33().d11 == null
								|| lineUp.getE33().d12 == null || lineUp.getE33().f1 == null)
								&& lineUp.getE33().d11 != player
								&& lineUp.getE33().d12 != player
								&& lineUp.getE33().f1 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line1_33.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getE33().d11 == null) {
							lineUp.getE33().d11 = player;
						} else if (lineUp.getE33().d11.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE33().d11;
							lineUp.getE33().d11 = player;
							if (lineUp.getE33().d12 == null) {
								lineUp.getE33().d12 = swap;
							} else {
								assert (lineUp.getE33().f1 == null); // DragOver
																// allows drop
																// only if
																// lineUp
																// incomplete
								lineUp.getE33().f1 = swap;
							}
						} else if (lineUp.getE33().d12 == null) {
							lineUp.getE33().d12 = player;
						} else if (lineUp.getE33().d12.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE33().d12;
							lineUp.getE33().d12 = player;
							assert (lineUp.getE33().f1 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getE33().f1 = swap;
						} else {
							assert (lineUp.getE33().f1 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getE33().f1 = player;
						}
					} else {
						if (lineUp.getE33().f1 == null) {
							lineUp.getE33().f1 = player;
						} else if (lineUp.getE33().f1.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getE33().f1.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getE33().f1;
							lineUp.getE33().f1 = player;
							if (lineUp.getE33().d11 == null) {
								lineUp.getE33().d11 = swap;
							} else {
								assert (lineUp.getE33().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE33().d12 = swap;
							}
						} else if (lineUp.getE33().d11 == null) {
							lineUp.getE33().d11 = player;
						} else {
							assert (lineUp.getE33().d12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE33().d12 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine2_33() {
		line2_33.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line2_33.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE33().d21 = null;
					break;
				case 1:
					lineUp.getE33().d22 = null;
					break;
				case 2:
					lineUp.getE33().f2 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line2_33.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line2_33 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE33().d21 == null
								|| lineUp.getE33().d22 == null || lineUp.getE33().f2 == null)
								&& lineUp.getE33().d21 != player
								&& lineUp.getE33().d22 != player
								&& lineUp.getE33().f2 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line2_33.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getE33().d21 == null) {
							lineUp.getE33().d21 = player;
						} else if (lineUp.getE33().d21.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE33().d21;
							lineUp.getE33().d21 = player;
							if (lineUp.getE33().d22 == null) {
								lineUp.getE33().d22 = swap;
							} else {
								assert (lineUp.getE33().f2 == null); // DragOver
																// allows drop
																// only if
																// lineUp
																// incomplete
								lineUp.getE33().f2 = swap;
							}
						} else if (lineUp.getE33().d22 == null) {
							lineUp.getE33().d22 = player;
						} else if (lineUp.getE33().d22.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE33().d22;
							lineUp.getE33().d22 = player;
							assert (lineUp.getE33().f2 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getE33().f2 = swap;
						} else {
							assert (lineUp.getE33().f2 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getE33().f2 = player;
						}
					} else {
						if (lineUp.getE33().f2 == null) {
							lineUp.getE33().f2 = player;
						} else if (lineUp.getE33().f2.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getE33().f2.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getE33().f2;
							lineUp.getE33().f2 = player;
							if (lineUp.getE33().d21 == null) {
								lineUp.getE33().d21 = swap;
							} else {
								assert (lineUp.getE33().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE33().d22 = swap;
							}
						} else if (lineUp.getE33().d21 == null) {
							lineUp.getE33().d21 = player;
						} else {
							assert (lineUp.getE33().d22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE33().d22 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine1_44() {
		line1_44.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line1_44.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE44().d11 = null;
					break;
				case 1:
					lineUp.getE44().d12 = null;
					break;
				case 2:
					lineUp.getE44().f11 = null;
					break;
				case 3:
					lineUp.getE44().f12 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line1_44.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line1_44 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE44().d11 == null
								|| lineUp.getE44().d12 == null
								|| lineUp.getE44().f11 == null || lineUp.getE44().f12 == null)
								&& lineUp.getE44().d11 != player
								&& lineUp.getE44().d12 != player
								&& lineUp.getE44().f11 != player
								&& lineUp.getE44().f12 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line1_44.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getE44().d11 == null) {
							lineUp.getE44().d11 = player;
						} else if (lineUp.getE44().d11.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE44().d11;
							lineUp.getE44().d11 = player;
							if (lineUp.getE44().d12 == null) {
								lineUp.getE44().d12 = swap;
							} else if (lineUp.getE44().f11 == null) {
								lineUp.getE44().f11 = swap;
							} else {
								assert (lineUp.getE44().f12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().f12 = swap;
							}
						} else if (lineUp.getE44().d12 == null) {
							lineUp.getE44().d12 = player;
						} else if (lineUp.getE44().d12.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE44().d12;
							lineUp.getE44().d12 = player;
							if (lineUp.getE44().f11 == null) {
								lineUp.getE44().f11 = swap;
							} else {
								assert (lineUp.getE44().f12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().f12 = swap;
							}
						} else if (lineUp.getE44().f11 == null) {
							lineUp.getE44().f11 = player;
						} else {
							assert (lineUp.getE44().f12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE44().f12 = player;
						}
					} else {
						if (lineUp.getE44().f11 == null) {
							lineUp.getE44().f11 = player;
						} else if (lineUp.getE44().f11.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getE44().f11.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getE44().f11;
							lineUp.getE44().f11 = player;
							if (lineUp.getE44().f12 == null) {
								lineUp.getE44().f12 = swap;
							} else if (lineUp.getE44().d11 == null) {
								lineUp.getE44().d11 = swap;
							} else {
								assert (lineUp.getE44().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().d12 = swap;
							}
						} else if (lineUp.getE44().f12 == null) {
							lineUp.getE44().f12 = player;
						} else if (lineUp.getE44().f12.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getE44().f12.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getE44().f12;
							lineUp.getE44().f12 = player;
							if (lineUp.getE44().d11 == null) {
								lineUp.getE44().d11 = swap;
							} else {
								assert (lineUp.getE44().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().d12 = swap;
							}
						} else if (lineUp.getE44().d11 == null) {
							lineUp.getE44().d11 = player;
						} else {
							assert (lineUp.getE44().d12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE44().d12 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine2_44() {
		line2_44.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line2_44.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getE44().d21 = null;
					break;
				case 1:
					lineUp.getE44().d22 = null;
					break;
				case 2:
					lineUp.getE44().f21 = null;
					break;
				case 3:
					lineUp.getE44().f22 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line2_44.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line2_44 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getE44().d21 == null
								|| lineUp.getE44().d22 == null
								|| lineUp.getE44().f21 == null || lineUp.getE44().f22 == null)
								&& lineUp.getE44().d21 != player
								&& lineUp.getE44().d22 != player
								&& lineUp.getE44().f21 != player
								&& lineUp.getE44().f22 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line2_44.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getE44().d21 == null) {
							lineUp.getE44().d21 = player;
						} else if (lineUp.getE44().d21.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE44().d21;
							lineUp.getE44().d21 = player;
							if (lineUp.getE44().d22 == null) {
								lineUp.getE44().d22 = swap;
							} else if (lineUp.getE44().f21 == null) {
								lineUp.getE44().f21 = swap;
							} else {
								assert (lineUp.getE44().f22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().f22 = swap;
							}
						} else if (lineUp.getE44().d22 == null) {
							lineUp.getE44().d22 = player;
						} else if (lineUp.getE44().d22.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getE44().d22;
							lineUp.getE44().d22 = player;
							if (lineUp.getE44().f21 == null) {
								lineUp.getE44().f21 = swap;
							} else {
								assert (lineUp.getE44().f22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().f22 = swap;
							}
						} else if (lineUp.getE44().f21 == null) {
							lineUp.getE44().f21 = player;
						} else {
							assert (lineUp.getE44().f22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE44().f22 = player;
						}
					} else {
						if (lineUp.getE44().f21 == null) {
							lineUp.getE44().f21 = player;
						} else if (lineUp.getE44().f21.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getE44().f21.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getE44().f21;
							lineUp.getE44().f21 = player;
							if (lineUp.getE44().f22 == null) {
								lineUp.getE44().f22 = swap;
							} else if (lineUp.getE44().d21 == null) {
								lineUp.getE44().d21 = swap;
							} else {
								assert (lineUp.getE44().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().d22 = swap;
							}
						} else if (lineUp.getE44().f22 == null) {
							lineUp.getE44().f22 = player;
						} else if (lineUp.getE44().f22.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getE44().f22.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getE44().f22;
							lineUp.getE44().f22 = player;
							if (lineUp.getE44().d21 == null) {
								lineUp.getE44().d21 = swap;
							} else {
								assert (lineUp.getE44().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getE44().d22 = swap;
							}
						} else if (lineUp.getE44().d21 == null) {
							lineUp.getE44().d21 = player;
						} else {
							assert (lineUp.getE44().d22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getE44().d22 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine1_pk4() {
		line1_pk4.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line1_pk4.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPk4().d11 = null;
					break;
				case 1:
					lineUp.getPk4().d12 = null;
					break;
				case 2:
					lineUp.getPk4().f11 = null;
					break;
				case 3:
					lineUp.getPk4().f12 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line1_pk4.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line1_pk4 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPk4().d11 == null
								|| lineUp.getPk4().d12 == null
								|| lineUp.getPk4().f11 == null || lineUp.getPk4().f12 == null)
								&& lineUp.getPk4().d11 != player
								&& lineUp.getPk4().d12 != player
								&& lineUp.getPk4().f11 != player
								&& lineUp.getPk4().f12 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line1_pk4.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getPk4().d11 == null) {
							lineUp.getPk4().d11 = player;
						} else if (lineUp.getPk4().d11.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk4().d11;
							lineUp.getPk4().d11 = player;
							if (lineUp.getPk4().d12 == null) {
								lineUp.getPk4().d12 = swap;
							} else if (lineUp.getPk4().f11 == null) {
								lineUp.getPk4().f11 = swap;
							} else {
								assert (lineUp.getPk4().f12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().f12 = swap;
							}
						} else if (lineUp.getPk4().d12 == null) {
							lineUp.getPk4().d12 = player;
						} else if (lineUp.getPk4().d12.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk4().d12;
							lineUp.getPk4().d12 = player;
							if (lineUp.getPk4().f11 == null) {
								lineUp.getPk4().f11 = swap;
							} else {
								assert (lineUp.getPk4().f12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().f12 = swap;
							}
						} else if (lineUp.getPk4().f11 == null) {
							lineUp.getPk4().f11 = player;
						} else {
							assert (lineUp.getPk4().f12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPk4().f12 = player;
						}
					} else {
						if (lineUp.getPk4().f11 == null) {
							lineUp.getPk4().f11 = player;
						} else if (lineUp.getPk4().f11.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPk4().f11.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPk4().f11;
							lineUp.getPk4().f11 = player;
							if (lineUp.getPk4().f12 == null) {
								lineUp.getPk4().f12 = swap;
							} else if (lineUp.getPk4().d11 == null) {
								lineUp.getPk4().d11 = swap;
							} else {
								assert (lineUp.getPk4().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().d12 = swap;
							}
						} else if (lineUp.getPk4().f12 == null) {
							lineUp.getPk4().f12 = player;
						} else if (lineUp.getPk4().f12.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPk4().f12.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPk4().f12;
							lineUp.getPk4().f12 = player;
							if (lineUp.getPk4().d11 == null) {
								lineUp.getPk4().d11 = swap;
							} else {
								assert (lineUp.getPk4().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().d12 = swap;
							}
						} else if (lineUp.getPk4().d11 == null) {
							lineUp.getPk4().d11 = player;
						} else {
							assert (lineUp.getPk4().d12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPk4().d12 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine2_pk4() {
		line2_pk4.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line2_pk4.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPk4().d21 = null;
					break;
				case 1:
					lineUp.getPk4().d22 = null;
					break;
				case 2:
					lineUp.getPk4().f21 = null;
					break;
				case 3:
					lineUp.getPk4().f22 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line2_pk4.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line2_pk4 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPk4().d21 == null
								|| lineUp.getPk4().d22 == null
								|| lineUp.getPk4().f21 == null || lineUp.getPk4().f22 == null)
								&& lineUp.getPk4().d21 != player
								&& lineUp.getPk4().d22 != player
								&& lineUp.getPk4().f21 != player
								&& lineUp.getPk4().f22 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line2_pk4.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getPk4().d21 == null) {
							lineUp.getPk4().d21 = player;
						} else if (lineUp.getPk4().d21.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk4().d21;
							lineUp.getPk4().d21 = player;
							if (lineUp.getPk4().d22 == null) {
								lineUp.getPk4().d22 = swap;
							} else if (lineUp.getPk4().f21 == null) {
								lineUp.getPk4().f21 = swap;
							} else {
								assert (lineUp.getPk4().f22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().f22 = swap;
							}
						} else if (lineUp.getPk4().d22 == null) {
							lineUp.getPk4().d22 = player;
						} else if (lineUp.getPk4().d22.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk4().d22;
							lineUp.getPk4().d22 = player;
							if (lineUp.getPk4().f21 == null) {
								lineUp.getPk4().f21 = swap;
							} else {
								assert (lineUp.getPk4().f22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().f22 = swap;
							}
						} else if (lineUp.getPk4().f21 == null) {
							lineUp.getPk4().f21 = player;
						} else {
							assert (lineUp.getPk4().f22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPk4().f22 = player;
						}
					} else {
						if (lineUp.getPk4().f21 == null) {
							lineUp.getPk4().f21 = player;
						} else if (lineUp.getPk4().f21.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPk4().f21.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPk4().f21;
							lineUp.getPk4().f21 = player;
							if (lineUp.getPk4().f22 == null) {
								lineUp.getPk4().f22 = swap;
							} else if (lineUp.getPk4().d21 == null) {
								lineUp.getPk4().d21 = swap;
							} else {
								assert (lineUp.getPk4().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().d22 = swap;
							}
						} else if (lineUp.getPk4().f22 == null) {
							lineUp.getPk4().f22 = player;
						} else if (lineUp.getPk4().f22.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPk4().f22.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPk4().f22;
							lineUp.getPk4().f22 = player;
							if (lineUp.getPk4().d21 == null) {
								lineUp.getPk4().d21 = swap;
							} else {
								assert (lineUp.getPk4().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk4().d22 = swap;
							}
						} else if (lineUp.getPk4().d21 == null) {
							lineUp.getPk4().d21 = player;
						} else {
							assert (lineUp.getPk4().d22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPk4().d22 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine1_pk3() {
		line1_pk3.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line1_pk3.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPk3().d11 = null;
					break;
				case 1:
					lineUp.getPk3().d12 = null;
					break;
				case 2:
					lineUp.getPk3().f1 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line1_pk3.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line1_pk3 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPk3().d11 == null
								|| lineUp.getPk3().d12 == null || lineUp.getPk3().f1 == null)
								&& lineUp.getPk3().d11 != player
								&& lineUp.getPk3().d12 != player
								&& lineUp.getPk3().f1 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line1_pk3.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getPk3().d11 == null) {
							lineUp.getPk3().d11 = player;
						} else if (lineUp.getPk3().d11.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk3().d11;
							lineUp.getPk3().d11 = player;
							if (lineUp.getPk3().d12 == null) {
								lineUp.getPk3().d12 = swap;
							} else {
								assert (lineUp.getPk3().f1 == null); // DragOver
																// allows drop
																// only if
																// lineUp
																// incomplete
								lineUp.getPk3().f1 = swap;
							}
						} else if (lineUp.getPk3().d12 == null) {
							lineUp.getPk3().d12 = player;
						} else if (lineUp.getPk3().d12.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk3().d12;
							lineUp.getPk3().d12 = player;
							assert (lineUp.getPk3().f1 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getPk3().f1 = swap;
						} else {
							assert (lineUp.getPk3().f1 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getPk3().f1 = player;
						}
					} else {
						if (lineUp.getPk3().f1 == null) {
							lineUp.getPk3().f1 = player;
						} else if (lineUp.getPk3().f1.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPk3().f1.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPk3().f1;
							lineUp.getPk3().f1 = player;
							if (lineUp.getPk3().d11 == null) {
								lineUp.getPk3().d11 = swap;
							} else {
								assert (lineUp.getPk3().d12 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk3().d12 = swap;
							}
						} else if (lineUp.getPk3().d11 == null) {
							lineUp.getPk3().d11 = player;
						} else {
							assert (lineUp.getPk3().d12 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPk3().d12 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void setupLine2_pk3() {
		line2_pk3.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				int i = line2_pk3.getSelectionModel().getSelectedIndex();
				ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
						.getLineUp();
				switch (i) {
				case 0:
					lineUp.getPk3().d21 = null;
					break;
				case 1:
					lineUp.getPk3().d22 = null;
					break;
				case 2:
					lineUp.getPk3().f2 = null;
					break;
				default:
					assert (i == -1);
				}
				linesShow();
			}
		});
		line2_pk3.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data is dragged over the target */
				/*
				 * accept it only if it is not dragged from the same node and if
				 * it has a string data
				 */
				Dragboard db = event.getDragboard();
				if (event.getGestureSource() != line2_pk3 && db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					if (i >= 0) {
						Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
						boolean accept = (lineUp.getPk3().d21 == null
								|| lineUp.getPk3().d22 == null || lineUp.getPk3().f2 == null)
								&& lineUp.getPk3().d21 != player
								&& lineUp.getPk3().d22 != player
								&& lineUp.getPk3().f2 != player
								&& lineUp.getG().goal1 != player
								&& lineUp.getG().goal2 != player
								&& HockmanMain.lineupTeam.checkForeigners(
										player, HockmanMain.game.getLeague()
												.getModus().maxNofForeigners());
						if (accept) {
							/*
							 * allow for both copying and moving, whatever user
							 * chooses
							 */
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					}
				}
				event.consume();
			}
		});
		line2_pk3.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* data dropped */
				/* if there is a string data on dragboard, read it and use it */
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
							.getLineUp();
					String playerStr = db.getString();
					
					// drag&drop does not work on Ubuntu with Oracle VM 7u9
					//TODO remove this workaround as soon as the bug is fixed in JVM for Linux
					playerStr = dragStringWorkaround;

					int i = Integer.parseInt(playerStr);

					Player player = HockmanMain.lineupTeam.getTeamPlayer(i);
					if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
						if (lineUp.getPk3().d21 == null) {
							lineUp.getPk3().d21 = player;
						} else if (lineUp.getPk3().d21.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk3().d21;
							lineUp.getPk3().d21 = player;
							if (lineUp.getPk3().d22 == null) {
								lineUp.getPk3().d22 = swap;
							} else {
								assert (lineUp.getPk3().f2 == null); // DragOver
																// allows drop
																// only if
																// lineUp
																// incomplete
								lineUp.getPk3().f2 = swap;
							}
						} else if (lineUp.getPk3().d22 == null) {
							lineUp.getPk3().d22 = player;
						} else if (lineUp.getPk3().d22.getPosition().getPosID() != Position.PosID.DEFENDER) {
							Player swap = lineUp.getPk3().d22;
							lineUp.getPk3().d22 = player;
							assert (lineUp.getPk3().f2 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getPk3().f2 = swap;
						} else {
							assert (lineUp.getPk3().f2 == null); // DragOver allows
															// drop only if
															// lineUp incomplete
							lineUp.getPk3().f2 = player;
						}
					} else {
						if (lineUp.getPk3().f2 == null) {
							lineUp.getPk3().f2 = player;
						} else if (lineUp.getPk3().f2.getPosition().getPosID() == Position.PosID.DEFENDER
								|| lineUp.getPk3().f2.getPosition().getPosID() == Position.PosID.GOALIE) {
							Player swap = lineUp.getPk3().f2;
							lineUp.getPk3().f2 = player;
							if (lineUp.getPk3().d21 == null) {
								lineUp.getPk3().d21 = swap;
							} else {
								assert (lineUp.getPk3().d22 == null); // DragOver
																	// allows
																	// drop
																	// only if
																	// lineUp
																	// incomplete
								lineUp.getPk3().d22 = swap;
							}
						} else if (lineUp.getPk3().d21 == null) {
							lineUp.getPk3().d21 = player;
						} else {
							assert (lineUp.getPk3().d22 == null); // DragOver allows
																// drop only if
																// lineUp
																// incomplete
							lineUp.getPk3().d22 = player;
						}
					}

					success = true;
				}
				/*
				 * let the source know whether the string was successfully
				 * transferred and used
				 */
				event.setDropCompleted(success);

				event.consume();
				linesShow();
			}
		});
	}

	private void linesShow() {

		// TODO remove copy/paste code with OpLineUp.java

		// fill the lineup tabs
		ch.hockman.model.team.LineUp lineUp = HockmanMain.lineupTeam
				.getLineUp();

		List<String> goalieNames = new ArrayList<String>();
		goalieNames.add(HockmanMain.GetPlayerLineUpString(lineUp.getG().goal1,
				Position.PosID.GOALIE));
		goalieNames.add(HockmanMain.GetPlayerLineUpString(lineUp.getG().goal2,
				Position.PosID.GOALIE));
		ObservableList<String> items = FXCollections
				.observableArrayList(goalieNames);
		goalies.setItems(items);
		HockmanMain.setListViewCellFactory(goalies);

		List<String> e55defense1Names = new ArrayList<String>();
		e55defense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d11,
				Position.PosID.DEFENDER));
		e55defense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d12,
				Position.PosID.DEFENDER));
		items = FXCollections.observableArrayList(e55defense1Names);
		this.defense1_55.setItems(items);
		HockmanMain.setListViewCellFactory(defense1_55);

		List<String> e55defense2Names = new ArrayList<String>();
		e55defense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d21,
				Position.PosID.DEFENDER));
		e55defense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d22,
				Position.PosID.DEFENDER));
		items = FXCollections.observableArrayList(e55defense2Names);
		this.defense2_55.setItems(items);
		HockmanMain.setListViewCellFactory(defense2_55);

		List<String> e55defense3Names = new ArrayList<String>();
		e55defense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d31,
				Position.PosID.DEFENDER));
		e55defense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().d32,
				Position.PosID.DEFENDER));
		items = FXCollections.observableArrayList(e55defense3Names);
		this.defense3_55.setItems(items);
		HockmanMain.setListViewCellFactory(defense3_55);

		List<String> e55offense1Names = new ArrayList<String>();
		e55offense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw1,
				Position.PosID.LEFTWING));
		e55offense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c1,
				Position.PosID.CENTER));
		e55offense1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw1,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense1Names);
		this.offense1_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense1_55);

		List<String> e55offense2Names = new ArrayList<String>();
		e55offense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw2,
				Position.PosID.LEFTWING));
		e55offense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c2,
				Position.PosID.CENTER));
		e55offense2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw2,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense2Names);
		this.offense2_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense2_55);

		List<String> e55offense3Names = new ArrayList<String>();
		e55offense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw3,
				Position.PosID.LEFTWING));
		e55offense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c3,
				Position.PosID.CENTER));
		e55offense3Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw3,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense3Names);
		this.offense3_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense3_55);

		List<String> e55offense4Names = new ArrayList<String>();
		e55offense4Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().lw4,
				Position.PosID.LEFTWING));
		e55offense4Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().c4,
				Position.PosID.CENTER));
		e55offense4Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE55().rw4,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(e55offense4Names);
		this.offense4_55.setItems(items);
		HockmanMain.setListViewCellFactory(offense4_55);

		List<String> e44line1Names = new ArrayList<String>();
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d11));
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d12));
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f11));
		e44line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f12));
		items = FXCollections.observableArrayList(e44line1Names);
		this.line1_44.setItems(items);
		HockmanMain.setListViewCellFactory(line1_44);

		List<String> e44line2Names = new ArrayList<String>();
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d21));
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().d22));
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f21));
		e44line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE44().f22));
		items = FXCollections.observableArrayList(e44line2Names);
		this.line2_44.setItems(items);
		HockmanMain.setListViewCellFactory(line2_44);

		List<String> e33line1Names = new ArrayList<String>();
		e33line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d11));
		e33line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d12));
		e33line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().f1));
		items = FXCollections.observableArrayList(e33line1Names);
		this.line1_33.setItems(items);
		HockmanMain.setListViewCellFactory(line1_33);

		List<String> e33line2Names = new ArrayList<String>();
		e33line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d21));
		e33line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().d22));
		e33line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getE33().f2));
		items = FXCollections.observableArrayList(e33line2Names);
		this.line2_33.setItems(items);
		HockmanMain.setListViewCellFactory(line2_33);

		List<String> pp5line1Names = new ArrayList<String>();
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d11,
				Position.PosID.DEFENDER));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d12,
				Position.PosID.DEFENDER));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().lw1,
				Position.PosID.LEFTWING));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().c1,
				Position.PosID.CENTER));
		pp5line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().rw1,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(pp5line1Names);
		this.line1_pp5.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pp5);

		List<String> pp5line2Names = new ArrayList<String>();
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d21,
				Position.PosID.DEFENDER));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().d22,
				Position.PosID.DEFENDER));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().lw2,
				Position.PosID.LEFTWING));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().c2,
				Position.PosID.CENTER));
		pp5line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp5().rw2,
				Position.PosID.RIGHTWING));
		items = FXCollections.observableArrayList(pp5line2Names);
		this.line2_pp5.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pp5);

		List<String> pp4line1Names = new ArrayList<String>();
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d11));
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d12));
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f11));
		pp4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f12));
		items = FXCollections.observableArrayList(pp4line1Names);
		this.line1_pp4.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pp4);

		List<String> pp4line2Names = new ArrayList<String>();
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d21));
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().d22));
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f21));
		pp4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPp4().f22));
		items = FXCollections.observableArrayList(pp4line2Names);
		this.line2_pp4.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pp4);

		List<String> pk4line1Names = new ArrayList<String>();
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d11));
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d12));
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f11));
		pk4line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f12));
		items = FXCollections.observableArrayList(pk4line1Names);
		this.line1_pk4.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pk4);

		List<String> pk4line2Names = new ArrayList<String>();
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d21));
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().d22));
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f21));
		pk4line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk4().f22));
		items = FXCollections.observableArrayList(pk4line2Names);
		this.line2_pk4.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pk4);

		List<String> pk3line1Names = new ArrayList<String>();
		pk3line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d11));
		pk3line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d12));
		pk3line1Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().f1));
		items = FXCollections.observableArrayList(pk3line1Names);
		this.line1_pk3.setItems(items);
		HockmanMain.setListViewCellFactory(line1_pk3);

		List<String> pk3line2Names = new ArrayList<String>();
		pk3line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d21));
		pk3line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().d22));
		pk3line2Names.add(HockmanMain.GetPlayerLineUpString(lineUp.getPk3().f2));
		items = FXCollections.observableArrayList(pk3line2Names);
		this.line2_pk3.setItems(items);
		HockmanMain.setListViewCellFactory(line2_pk3);
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
	private void defaultLineup(ActionEvent event) {
		CoachAI.Analysis dummy = new CoachAI.Analysis();
		HockmanMain.lineupTeam.getLineUp().lineUpAI(HockmanMain.lineupTeam,
				dummy,
				HockmanMain.game.getLeague().getModus().maxNofForeigners());
		setupLists();
		setupPlayerTable();
		linesShow();
	}

	@FXML
	private void playerDetails(ActionEvent event) {
		initPlayer();
		HockmanMain.stageHandler.showModalStageAndWait("PlayerDetails.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_PLAYERDETAILS"));
	}

	private void initPlayer() {
		int index = playerTable.getSelectionModel().getSelectedIndex();
		if (index < 0) {
			index = 0;
		}
		HockmanMain.currPlayer = HockmanMain.lineupTeam.getTeamPlayer(index);
	}

}
