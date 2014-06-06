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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import ch.hockman.model.Schedule.Result;
import ch.hockman.model.common.Util;
import ch.hockman.model.match.MatchTime;
import ch.hockman.model.match.PlayingMatch;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.team.Team;

/**
 * The mask shown in a managed match (game).
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Match implements Initializable {

	@FXML
	private Label timeLbl;

	@FXML
	private Label shootsLbl;

	@FXML
	private Label spectatorsLbl;

	@FXML
	private Label eventLbl;

	@FXML
	private Button continueBtn;

	@FXML
	private ImageView iceFieldImg;

	@FXML
	private ListView totomatList;

	@FXML
	private TextArea scorerList;

	@FXML
	private ListView homeOnIceList;

	@FXML
	private ListView awayOnIceList;

	@FXML
	private ListView homePenList;

	@FXML
	private ListView awayPenList;

	@FXML
	private ListView homeInjList;

	@FXML
	private ListView awayInjList;

	@FXML
	private Label oponentsLbl;

	@FXML
	private Label stadionLbl;

	@FXML
	private Label resultLbl;

	@FXML
	private Button myLineUpBtn;

	@FXML
	private Button tacticsBtn;

	@FXML
	private Button timeoutBtn;

	@FXML
	private Button opponentBtn;

	@FXML
	private Button okBtn;

	private int interval;
	private int nofMatches;
	private MatchTime mt;
	private boolean done;
	private boolean managedMatchDone;
	private Timeline timeline;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String url = PlayerDetails.class.getResource("rink.jpg").toString();
		Image image = new Image(url);
		iceFieldImg.setImage(image);

		interval = 101 - 10 * ch.hockman.model.Options.speed;
		mt = new MatchTime();
		mt.reset();
		nofMatches = HockmanMain.game.getSchedule().beginRound(
				HockmanMain.game.getRound(), HockmanMain.game.getNews(),
				HockmanMain.game.getLeague().getModus());

		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.millis(interval), new EventHandler() {
					// KeyFrame event handler
					public void handle(Event event) {
						try {
							second();
						} catch (Throwable t) {
							// in case of any error, stop the timeline!
							timeline.stop();
						}
					}
				}));
		timeline.playFromStart();
		continueBtn.setDisable(true);
		this.eventLbl.setText("");
		scorerList.setEditable(false);
	}

	private void second() {
		ch.hockman.model.Schedule schedule = HockmanMain.game.getSchedule();
		if (!managedMatchDone) {
			String s = Integer.toString(mt.getMin());
			s += ":";
			int sec = mt.getMinSec();
			if (sec < 10) {
				s += "0";
			}
			s += Integer.toString(sec);
			timeLbl.setText(s);
		}
		if (done) {
			// distribute points/playoffWins done in Round.EndRound()
			schedule.endRound(HockmanMain.game.getRound(), HockmanMain.game
					.getNews(), nofMatches, HockmanMain.game.getLeague()
					.getModus().isTwoPoints());
			okBtn.setDisable(false);
			this.myLineUpBtn.setDisable(true);
			this.opponentBtn.setDisable(true);
			this.tacticsBtn.setDisable(true);
			this.timeoutBtn.setDisable(true);
			done = false;
			managedMatchDone = false;
			timeline.stop();
		} else {
			okBtn.setDisable(true);
			this.myLineUpBtn.setDisable(false);
			this.opponentBtn.setDisable(false);
			this.tacticsBtn.setDisable(false);
			this.timeoutBtn.setDisable(false);
			managedMatchDone = true;
			done = schedule.doRoundSecond(HockmanMain.game.getRound(),
					HockmanMain.game.getNews(), nofMatches, mt);

			// now show the results and who is on the ice
			ch.hockman.model.team.Team homeTeam;
			ch.hockman.model.team.Team awayTeam;
			int homeScore;
			int awayScore;
			boolean goalChange = (mt.getTotalSec() == 0); // if necessary to
															// update Totomat
															// GUI
			for (int match = 0; match < nofMatches; match++) {
				Result res = schedule.getResult(HockmanMain.game.getRound(),
						match);
				PlayingMatch playingMatch = res.pm;
				homeTeam = res.homeTeam;
				awayTeam = res.awayTeam;
				homeScore = res.homeScore;
				awayScore = res.awayScore;
				if (playingMatch != null
						&& (playingMatch.getEvent().bitSet
								.get(PlayingMatch.GOAL_HOME) || playingMatch
								.getEvent().bitSet.get(PlayingMatch.GOAL_AWAY))) {
					goalChange = true;
				}
			}
			List<String> totomatItems = new ArrayList<String>();
			for (int match = 0; match < nofMatches; match++) {
				Result res = schedule.getResult(HockmanMain.game.getRound(),
						match);
				PlayingMatch playingMatch = res.pm;
				homeTeam = res.homeTeam;
				awayTeam = res.awayTeam;
				homeScore = res.homeScore;
				awayScore = res.awayScore;
				String teamsString, scoreString = "";
				if (homeTeam == null) {
					teamsString = Util.getModelResourceBundle().getString("L_NOGAME");
				} else {
					teamsString = homeTeam.getTeamName();
				}
				teamsString += " - ";
				if (awayTeam == null) {
					teamsString += Util.getModelResourceBundle().getString("L_NOGAME");
				} else {
					teamsString += awayTeam.getTeamName();
				}
				if (homeTeam != null && awayTeam != null) {
					teamsString += "  ";
					scoreString += Integer.toString(homeScore);
					scoreString += " : ";
					scoreString += Integer.toString(awayScore);
				}
				if (HockmanMain.currManagedTeam.equals(homeTeam)
						|| HockmanMain.currManagedTeam.equals(awayTeam)) {
					// managed match: display stuff in GUI
					if (goalChange) {
						if (mt.getTotalSec() == 0) { // should be done ONCE at
														// beginning of match
														// ...
							if (playingMatch != null) {
								this.stadionLbl.setText(homeTeam
										.getStadiumName());
								String s = Integer
										.toString(HockmanMain.game.getNews().getReports()[match].getNofSpectators());
								s += Util.getModelResourceBundle().getString("L_SPECTATORS");
								this.spectatorsLbl.setText(s);
							}
							this.oponentsLbl.setText(teamsString);
						} // should be done ONCE at beginning of match ...
						this.resultLbl.setText(scoreString);
					}
					if (homeTeam != null && awayTeam != null) {
						assert (playingMatch != null);
						managedMatchDone = playingMatch.getEvent().bitSet
								.get(PlayingMatch.FINISH);
						String shotsString = Integer
								.toString(playingMatch.getHomeShots());
						shotsString += ":";
						shotsString += Integer.toString(playingMatch.getAwayShots());
						this.shootsLbl.setText(shotsString);
						// the events must be calculated in the model (get it
						// from DoRoundSecond() ?!)
						if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.INTERMISSION)) {
							if (ch.hockman.model.Options.showIntermissionDialog) {
								eventLbl.setText(eventLbl.getText() + "\n"
										+ Util.getModelResourceBundle().getString("L_INTERMISSION"));
								continueBtn.setDisable(false);
								timeline.pause();
							}
						}
						if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.MINOR_PENALTY_HOME)
								&& playingMatch.getEvent().bitSet
										.get(PlayingMatch.MINOR_PENALTY_AWAY)) {
							if (ch.hockman.model.Options.showPenaltyDialog) {
								// TODO show both penalties once
								String s;
								s = Util.getModelResourceBundle().getString("L_PENALTY");
								s += homeTeam.getTeamName();
								s += Util.getModelResourceBundle().getString("L_PENALTY_ROUGHING");
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();

								s = Util.getModelResourceBundle().getString("L_PENALTY");
								s += awayTeam.getTeamName();
								s += Util.getModelResourceBundle().getString("L_PENALTY_ROUGHING");
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						} else if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.MINOR_PENALTY_HOME)) {
							if (ch.hockman.model.Options.showPenaltyDialog) {
								int ran = Util.random(5);
								String s = "";
								switch (ran) {
								case 0:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += homeTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_ELLBOWING");
									break;
								case 1:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += homeTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_HOLDING");
									break;
								case 2:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += homeTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_CROSSCHECKING");
									break;
								case 3:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += homeTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_TRIPPING");
									break;
								case 4:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += homeTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_HIGHSTICKING");
									break;
								default:
									assert (false);
								}
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						} else if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.MINOR_PENALTY_AWAY)) {
							if (ch.hockman.model.Options.showPenaltyDialog) {
								int ran = Util.random(5);
								String s = "";
								switch (ran) {
								case 0:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += awayTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_ELLBOWING");
									break;
								case 1:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += awayTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_HOLDING");
									break;
								case 2:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += awayTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_CROSSCHECKING");
									break;
								case 3:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += awayTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_TRIPPING");
									break;
								case 4:
									s = Util.getModelResourceBundle().getString("L_PENALTY");
									s += awayTeam.getTeamName();
									s += Util.getModelResourceBundle().getString("L_PENALTY_HIGHSTICKING");
									break;
								default:
									assert (false);
								}
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						}
						if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.MAJOR_PENALTY_HOME)
								&& playingMatch.getEvent().bitSet
										.get(PlayingMatch.MAJOR_PENALTY_AWAY)) {
							if (ch.hockman.model.Options.showPenaltyDialog) {
								// TODO show both penalties once
								String s;
								s = Util.getModelResourceBundle().getString("L_PENALTY");
								s += homeTeam.getTeamName();
								s += Util.getModelResourceBundle().getString("L_PENALTY_FIGHTING");
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();

								s = Util.getModelResourceBundle().getString("L_PENALTY");
								s += awayTeam.getTeamName();
								s += Util.getModelResourceBundle().getString("L_PENALTY_FIGHTING");
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						} else if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.MAJOR_PENALTY_HOME)) {
							if (ch.hockman.model.Options.showPenaltyDialog) {
								String s;
								s = Util.getModelResourceBundle().getString("L_PENALTY");
								s += homeTeam.getTeamName();
								if (Util.random(2) == 0) {
									s += Util.getModelResourceBundle().getString("L_PENALTY_MISCONDUCT");
								} else {
									s += Util.getModelResourceBundle().getString("L_PENALTY_FIGHTING");
								}
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						} else if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.MAJOR_PENALTY_AWAY)) {
							if (ch.hockman.model.Options.showPenaltyDialog) {
								String s;
								s = Util.getModelResourceBundle().getString("L_PENALTY");
								s += awayTeam.getTeamName();
								if (Util.random(2) == 0) {
									s += Util.getModelResourceBundle().getString("L_PENALTY_MISCONDUCT");
								} else {
									s += Util.getModelResourceBundle().getString("L_PENALTY_FIGHTING");
								}
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						}
						if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.GOAL_HOME)) {
							if (ch.hockman.model.Options.showScoringDialog) {
								String s;
								s = Util.getModelResourceBundle().getString("L_GOAL");
								s += homeTeam.getTeamName();
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						}
						if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.GOAL_AWAY)) {
							if (ch.hockman.model.Options.showScoringDialog) {
								String s;
								s = Util.getModelResourceBundle().getString("L_GOAL");
								s += awayTeam.getTeamName();
								eventLbl.setText(eventLbl.getText() + "\n" + s);
								continueBtn.setDisable(false);
								timeline.pause();
							}
						}
						if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.INJURY)) {
							if (ch.hockman.model.Options.showInjuryDialog) {
								eventLbl.setText(eventLbl.getText() + "\n"
										+ Util.getModelResourceBundle().getString("L_INJURY"));
								continueBtn.setDisable(false);
								timeline.pause();
							}
						}
						if (playingMatch.getEvent().bitSet
								.get(PlayingMatch.INTERRUPT)) {
							if (ch.hockman.model.Options.showInterruptDialog) {
								eventLbl.setText(eventLbl.getText() + "\n"
										+ Util.getModelResourceBundle().getString("L_INTERRUPT"));
								continueBtn.setDisable(false);
								timeline.pause();
							}
						}
						if (!managedMatchDone
								&& !playingMatch.getEvent().bitSet.isEmpty()
								|| (mt.getTotalSec() == 0)) {
							// who is on the ice
							PlayerPtrVector ppvh = playingMatch.getOnIce().homePlayer;
							int nofPlayers = ppvh.GetNofPlayers();
							List<String> homeIceItems = new ArrayList<String>();
							for (int i = 0; i < nofPlayers; i++) {
								Player player = ppvh.GetPlayer(i);
								String s;
								s = Integer.toString(player.getNumber());
								while (s.length() < 3) {
									s += ' ';
								}
								s += player.getLastName();
								homeIceItems.add(s);
							}
							ObservableList<String> items = FXCollections
									.observableArrayList(homeIceItems);
							this.homeOnIceList.setItems(items);
							HockmanMain
									.setListViewCellFactory(this.homeOnIceList);

							PlayerPtrVector ppva = playingMatch.getOnIce().awayPlayer;
							nofPlayers = ppva.GetNofPlayers();
							List<String> awayIceItems = new ArrayList<String>();
							for (int i = 0; i < nofPlayers; i++) {
								Player player = ppva.GetPlayer(i);
								String s;
								s = Integer.toString(player.getNumber());
								while (s.length() < 3) {
									s += ' ';
								}
								s += player.getLastName();
								awayIceItems.add(s);
							}
							items = FXCollections
									.observableArrayList(awayIceItems);
							this.awayOnIceList.setItems(items);
							HockmanMain
									.setListViewCellFactory(this.awayOnIceList);

							// penalty box
							PlayerPtrVector ppvhp = playingMatch.getPenBox().homePlayer;
							nofPlayers = ppvhp.GetNofPlayers();
							List<String> homePenItems = new ArrayList<String>();
							for (int i = 0; i < nofPlayers; i++) {
								Player player = ppvhp.GetPlayer(i);
								String s;
								s = Integer.toString(player.getNumber());
								while (s.length() < 3) {
									s += ' ';
								}
								s += player.getLastName();
								homePenItems.add(s);
							}
							items = FXCollections
									.observableArrayList(homePenItems);
							this.homePenList.setItems(items);
							HockmanMain
									.setListViewCellFactory(this.homePenList);

							PlayerPtrVector ppvap = playingMatch.getPenBox().awayPlayer;
							nofPlayers = ppvap.GetNofPlayers();
							List<String> awayPenItems = new ArrayList<String>();
							for (int i = 0; i < nofPlayers; i++) {
								Player player = ppvap.GetPlayer(i);
								String s;
								s = Integer.toString(player.getNumber());
								while (s.length() < 3) {
									s += ' ';
								}
								s += player.getLastName();
								awayPenItems.add(s);
							}
							items = FXCollections
									.observableArrayList(awayPenItems);
							this.awayPenList.setItems(items);
							HockmanMain
									.setListViewCellFactory(this.awayPenList);

							// injuries
							PlayerPtrVector ppvhi = playingMatch.getInjuries().homePlayer;
							nofPlayers = ppvhi.GetNofPlayers();
							List<String> homeInjItems = new ArrayList<String>();
							for (int i = 0; i < nofPlayers; i++) {
								Player player = ppvhi.GetPlayer(i);
								String s;
								s = Integer.toString(player.getNumber());
								while (s.length() < 3) {
									s += ' ';
								}
								s += player.getLastName();
								homeInjItems.add(s);
							}
							items = FXCollections
									.observableArrayList(homeInjItems);
							this.homeInjList.setItems(items);
							HockmanMain
									.setListViewCellFactory(this.homeInjList);

							PlayerPtrVector ppvai = playingMatch.getInjuries().awayPlayer;
							nofPlayers = ppvai.GetNofPlayers();
							List<String> awayInjItems = new ArrayList<String>();
							for (int i = 0; i < nofPlayers; i++) {
								Player player = ppvai.GetPlayer(i);
								String s;
								s = Integer.toString(player.getNumber());
								while (s.length() < 3) {
									s += ' ';
								}
								s += player.getLastName();
								awayInjItems.add(s);
							}
							items = FXCollections
									.observableArrayList(awayInjItems);
							this.awayInjList.setItems(items);
							HockmanMain
									.setListViewCellFactory(this.awayInjList);

							// scorer
							if (goalChange) {
								this.scorerList.setText(HockmanMain.game
										.getNews().getReports()[match].getGoals());
							}
						}
					}
				} else {
					// totomat match
					if (goalChange && homeTeam != null && awayTeam != null) {
						totomatItems.add(teamsString + scoreString);
					}
				}
				if (goalChange) {
					ObservableList<String> items = FXCollections
							.observableArrayList(totomatItems);
					this.totomatList.setItems(items);
					HockmanMain.setListViewCellFactory(this.totomatList);
				}
			} // for
			if (!done) {
				mt.tick();
			}
		}
	}

	@FXML
	private void myLineUpAction(ActionEvent event) {
		timeline.pause();
		HockmanMain.lineupTeam = HockmanMain.currManagedTeam;
		HockmanMain.stageHandler.showModalStageAndWait("LineUp.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_LINEUP"));
		if(continueBtn.isDisabled()) {
			timeline.play();
		}
	}

	@FXML
	private void tacticsAction(ActionEvent event) {
		timeline.pause();
		HockmanMain.stageHandler.showModalStageAndWait("Tactics.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_TACTICS"));
		if(continueBtn.isDisabled()) {
			timeline.play();
		}
	}

	@FXML
	private void timeoutAction(ActionEvent event) {
		timeline.pause();
		HockmanMain.msgBoxLbl = "Timeout";
		// TODO timeout let the players refresh a bit
		HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		if(continueBtn.isDisabled()) {
			timeline.play();
		}
	}

	@FXML
	private void opponentLineUpAction(ActionEvent event) {
		timeline.pause();		
		Team opTeam = null;
		Team homeTeam, awayTeam;
		ch.hockman.model.Schedule schedule = HockmanMain.game.getSchedule();
		for (int match = 0; match < nofMatches; match++) {
			Result res = schedule.getResult(HockmanMain.game.getRound(), match);
			homeTeam = res.homeTeam;
			awayTeam = res.awayTeam;
			if (HockmanMain.currManagedTeam.equals(homeTeam)) {
				opTeam = awayTeam;
			}
			if (HockmanMain.currManagedTeam.equals(awayTeam)) {
				opTeam = homeTeam;
			}
		}
		HockmanMain.lineupTeam = opTeam;
		HockmanMain.stageHandler.showModalStageAndWait("OpLineup.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_OPLINEUP"));
		if(continueBtn.isDisabled()) {
			timeline.play();
		}
	}

	@FXML
	private void ok(ActionEvent event) {
		HockmanMain.stageHandler.closeModalStage();
	}

	@FXML
	private void continueAction(ActionEvent event) {
		continueBtn.setDisable(true);
		this.eventLbl.setText("");
		if(timeline.getStatus() != Animation.Status.STOPPED) {
			timeline.play();
		}
	}
}
