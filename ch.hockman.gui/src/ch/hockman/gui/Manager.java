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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import ch.hockman.model.GameCreator;
import ch.hockman.model.Modus;
import ch.hockman.model.Modus.GameType;
import ch.hockman.model.Schedule.Result;
import ch.hockman.model.TeamMessages;
import ch.hockman.model.common.Util;
import ch.hockman.model.team.CoachAI;
import ch.hockman.model.team.Team;

/**
 * The main manager mask.
 * This is a controller-class referenced by a fxml-file with the UI-Layout.
 *
 * @author Albin
 *
 */
public class Manager implements Initializable {
	static final String L_TEAMPICPATH = "team.png";

	private File currGameFile;

	@FXML
	private Menu fileMenu;
	
	@FXML
	private MenuItem newMenu;

	@FXML
	private MenuItem loadMenu;

	@FXML
	private MenuItem saveMenu;

	@FXML
	private MenuItem saveAsMenu;

	@FXML
	private MenuItem quitMenu;
	
	@FXML
	private Button scheduleBtn;

	@FXML
	private Button rosterBtn;

	@FXML
	private Button optionsBtn;

	@FXML
	private Button tablesBtn;

	@FXML
	private Button transfersBtn;

	@FXML
	private Button statsBtn;

	@FXML
	private Button lineupBtn;

	@FXML
	private Button financesBtn;

	@FXML
	private Button trainingBtn;

	@FXML
	private Button newsBtn;

	@FXML
	private Button tacticsBtn;

	@FXML
	private Button playRoundBtn;

	@FXML
	private Label leagueLabel;

	@FXML
	private Label teamLabel;

	@FXML
	private Label yearLabel;

	@FXML
	private Label nextRoundLabel;

	@FXML
	private Label newsLbl;
	
	@FXML
	private TextArea messageArea;

	@FXML
	private ImageView homePic;

	@FXML
	private ImageView awayPic;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		newsLbl.setText(Util.getModelResourceBundle().getString("L_LBL_NEWS"));
		fileMenu.setText(Util.getModelResourceBundle().getString("L_FILEMENU"));
		newMenu.setText(Util.getModelResourceBundle().getString("L_NEWMENU"));
		loadMenu.setText(Util.getModelResourceBundle().getString("L_LOADMENU"));
		saveMenu.setText(Util.getModelResourceBundle().getString("L_SAVEMENU"));
		saveAsMenu.setText(Util.getModelResourceBundle().getString("L_SAVEASMENU"));
		quitMenu.setText(Util.getModelResourceBundle().getString("L_QUITMENU"));
		scheduleBtn.setText(Util.getModelResourceBundle().getString("L_BTN_SCHEDULE"));
		rosterBtn.setText(Util.getModelResourceBundle().getString("L_BTN_ROSTER"));
		optionsBtn.setText(Util.getModelResourceBundle().getString("L_BTN_OPTIONS"));
		tablesBtn.setText(Util.getModelResourceBundle().getString("L_BTN_TABLES"));
		transfersBtn.setText(Util.getModelResourceBundle().getString("L_BTN_TRANSFERS"));
		statsBtn.setText(Util.getModelResourceBundle().getString("L_BTN_STATS"));
		lineupBtn.setText(Util.getModelResourceBundle().getString("L_BTN_LINEUP"));
		financesBtn.setText(Util.getModelResourceBundle().getString("L_BTN_FINANCES"));
		trainingBtn.setText(Util.getModelResourceBundle().getString("L_BTN_TRAINING"));
		newsBtn.setText(Util.getModelResourceBundle().getString("L_BTN_NEWS"));
		tacticsBtn.setText(Util.getModelResourceBundle().getString("L_BTN_TACTICS"));
		playRoundBtn.setText(Util.getModelResourceBundle().getString("L_BTN_PLAYROUND"));
		updateNextRound();
		messageArea.setEditable(false);
	}

	private void updateNextRound() {
		if (HockmanMain.game != null && HockmanMain.currManagedTeam != null) {
			// if a game is initialized. (different solution than in c++)

			final Team opteam = HockmanMain.game.getSchedule().getOpTeam(
					HockmanMain.game.getRound(), HockmanMain.currManagedTeam);
			Boolean home = HockmanMain.currManagedTeam.getHome();
			String s = Util.getModelResourceBundle().getString("L_NEXT_ROUND");
			int round = HockmanMain.game.getRound();
			Modus modus = HockmanMain.game.getLeague().getModus();
			int nofRounds = modus.getNofRounds();
			if (round > nofRounds) {
				s += "(Playoff ";
				s += 1 + (round - nofRounds - 1) / modus.getGamesPerFinal();
				s += ", Game ";
				s += 1 + (round - nofRounds - 1) % modus.getGamesPerFinal();
				s += ") ";
			} else if (!HockmanMain.eofSeason) {
				s += "(Regular ";
				s += round;
				s += ".) ";
			} else {
				s += Util.getModelResourceBundle().getString("L_END_OF_SEASON");
			}
			if (!HockmanMain.eofSeason && opteam != null) {
				Image image = null;
				if (home) {
					s += Util.getModelResourceBundle().getString("L_HOME_AGAINST");
					try {
						String url = "file:"
								+ HockmanMain.currManagedTeam.getPicPath();
						image = new Image(url);
						if (image.isError()) {
							url = Manager.class.getResource(L_TEAMPICPATH)
									.toString();
							image = new Image(url);
						}
					} catch (IllegalArgumentException ex) {
						String url = Manager.class.getResource(L_TEAMPICPATH)
								.toString();
						image = new Image(url);
					}
					homePic.setDisable(false);
					homePic.setImage(image);
					homePic.setOnMouseClicked(new EventHandler<MouseEvent>() {
						public void handle(MouseEvent me) {
							HockmanMain.lineupTeam = HockmanMain.currManagedTeam;
							HockmanMain.stageHandler
									.showModalStageAndWait("OpLineup.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_OPLINEUP"));
						}
					});

					try {
						String url = "file:" + opteam.getPicPath();
						image = new Image(url);
						if (image.isError()) {
							url = Manager.class.getResource(L_TEAMPICPATH)
									.toString();
							image = new Image(url);
						}
					} catch (IllegalArgumentException ex) {
						String url = Manager.class.getResource(L_TEAMPICPATH)
								.toString();
						image = new Image(url);
					}
					awayPic.setDisable(false);
					awayPic.setImage(image);
					awayPic.setOnMouseClicked(new EventHandler<MouseEvent>() {
						public void handle(MouseEvent me) {
							HockmanMain.lineupTeam = opteam;
							HockmanMain.stageHandler
									.showModalStageAndWait("OpLineup.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_OPLINEUP"));
						}
					});
				} else {
					s += Util.getModelResourceBundle().getString("L_AWAY_AGAINST");
					try {
						String url = "file:"
								+ HockmanMain.currManagedTeam.getPicPath();
						image = new Image(url);
						if (image.isError()) {
							url = Manager.class.getResource(L_TEAMPICPATH)
									.toString();
							image = new Image(url);
						}
					} catch (IllegalArgumentException ex) {
						String url = Manager.class.getResource(L_TEAMPICPATH)
								.toString();
						image = new Image(url);
					}
					awayPic.setDisable(false);
					awayPic.setImage(image);
					awayPic.setOnMouseClicked(new EventHandler<MouseEvent>() {
						public void handle(MouseEvent me) {
							HockmanMain.lineupTeam = HockmanMain.currManagedTeam;
							HockmanMain.stageHandler
									.showModalStageAndWait("OpLineup.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_OPLINEUP"));
						}
					});
					try {
						String url = "file:" + opteam.getPicPath();
						image = new Image(url);
						if (image.isError()) {
							url = Manager.class.getResource(L_TEAMPICPATH)
									.toString();
							image = new Image(url);
						}
					} catch (IllegalArgumentException ex) {
						String url = Manager.class.getResource(L_TEAMPICPATH)
								.toString();
						image = new Image(url);
					}
					homePic.setDisable(false);
					homePic.setImage(image);
					homePic.setOnMouseClicked(new EventHandler<MouseEvent>() {
						public void handle(MouseEvent me) {
							HockmanMain.lineupTeam = opteam;
							HockmanMain.stageHandler
									.showModalStageAndWait("OpLineup.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_OPLINEUP"));
						}
					});
				}
				s += opteam.getTeamName();
			} else {
				s += Util.getModelResourceBundle().getString("L_NOGAME");
				String url = Manager.class.getResource(L_TEAMPICPATH)
						.toString();
				Image image = new Image(url);
				awayPic.setImage(image);
				image = new Image(url);
				homePic.setImage(image);
			}
			nextRoundLabel.setText(s);

			TeamMessages messages = HockmanMain.currManagedTeam.getMessages();
			messages.setBoV();
			this.messageArea.clear();
			while (!messages.eoV()) {
				this.messageArea.appendText(messages.getCurrSetNext() + "\n");
			}

			// update league name and year, actually an update is necessary only
			// each season ...
			// NYI
			this.leagueLabel.setText(HockmanMain.game.getLeague()
					.getLeagueName());
			this.teamLabel.setText(HockmanMain.currManagedTeam.getTeamName());
			this.yearLabel
					.setText(Integer.toString(HockmanMain.game.getYear()));
			enableButtons();
		} else {
			// no active game
			disableButtons();
			this.messageArea
					.setText(Util.getModelResourceBundle().getString("L_CHOOSE"));
			leagueLabel.setText("");
			teamLabel.setText("");
			yearLabel.setText("");
			nextRoundLabel.setText("");
			homePic.setImage(null);
			homePic.setDisable(true);
			awayPic.setImage(null);
			awayPic.setDisable(true);
		}
	}

	private void enableButtons() {
		scheduleBtn.setDisable(false);
		rosterBtn.setDisable(false);
		optionsBtn.setDisable(false);
		tablesBtn.setDisable(false);
		statsBtn.setDisable(false);
		lineupBtn.setDisable(false);
		trainingBtn.setDisable(false);
		newsBtn.setDisable(false);
		tacticsBtn.setDisable(false);
		playRoundBtn.setDisable(false);
		if(HockmanMain.game.getLeague().getModus().getGameType() != GameType.TOURNAMENT) {
			// transfers and finances only for non-tournament games
			transfersBtn.setDisable(false);		
			financesBtn.setDisable(false);
		}
	}

	private void disableButtons() {
		scheduleBtn.setDisable(true);
		rosterBtn.setDisable(true);
		optionsBtn.setDisable(true);
		tablesBtn.setDisable(true);
		transfersBtn.setDisable(true);
		statsBtn.setDisable(true);
		lineupBtn.setDisable(true);
		financesBtn.setDisable(true);
		trainingBtn.setDisable(true);
		newsBtn.setDisable(true);
		tacticsBtn.setDisable(true);
		playRoundBtn.setDisable(true);
	}

	@FXML
	private void newGame(ActionEvent event) throws IOException {
		if(HockmanMain.modified) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_WITHOUT_SAVING");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
		HockmanMain.stageHandler.showModalStageAndWait("NewGame.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_NEWGAME"));
		HockmanMain.stageHandler.showModalStageAndWait("ChooseTeam.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_CHOOSETEAM"));
		HockmanMain.modified = false;		
		updateNextRound();
	}

	@FXML
	private void showStats(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("Stats.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_STATS"));
	}

	@FXML
	private void playRound(ActionEvent event) {
		try {
			if (!HockmanMain.eofSeason) {
				HockmanMain.modified = true;				
				HockmanMain.game.beforeMatch();
				ch.hockman.model.Schedule schedule = HockmanMain.game
						.getSchedule();
				// don't show match, if managed team is not playing
				Team opTeam = null;
				if (ch.hockman.model.Options.playing == ch.hockman.model.Options.Playing.COACHING) {
					Modus modus = HockmanMain.game.getLeague().getModus();
					int nofMatches = modus.nofMatches(HockmanMain.playoffs);
					Team homeTeam, awayTeam;
					for (int match = 0; match < nofMatches; match++) {
						Result res = schedule.getResult(
								HockmanMain.game.getRound(), match);
						homeTeam = res.homeTeam;
						awayTeam = res.awayTeam;
						if (HockmanMain.currManagedTeam.equals(homeTeam)) {
							opTeam = awayTeam;
						}
						if (HockmanMain.currManagedTeam.equals(awayTeam)) {
							opTeam = homeTeam;
						}
					}
				}
				if (opTeam != null) {
					// coaching game
					// boolean home;
					schedule.getOpTeam(HockmanMain.game.getRound(),
							HockmanMain.currManagedTeam);

					HockmanMain.stageHandler
							.showModalStageAndWait("Match.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MATCH"));
					// in match form, game.NextMatch() is called

				} else {
					// result only
					// automatic lineup for managed team at "result only"
					// the other teams already did a lineup at Daily.DoDaily()
					// the managed team only did, if "result only" was already
					// enabled,
					// and if immediate transfer was done with AI team buyer,
					// lineup for managed team is not done yet.
					Modus modus = HockmanMain.game.getLeague().getModus();
					CoachAI.Analysis dummy = new CoachAI.Analysis();
					HockmanMain.currManagedTeam.getLineUp().lineUpAI(
							HockmanMain.currManagedTeam, dummy,
							modus.maxNofForeigners());
					assert (opTeam == null || ch.hockman.model.Options.playing == ch.hockman.model.Options.Playing.RESULT);
					HockmanMain.game.doResult();
				}
				HockmanMain.game.afterMatch();

				if (ch.hockman.model.Options.showReport) {
					// show report
					HockmanMain.stageHandler.showModalStageAndWait("News.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_NEWS"));
				}

				if (HockmanMain.game.getRound() > HockmanMain.game.getLeague()
						.getModus().getNofRounds()) {
					HockmanMain.playoffs = true;
				} else {
					HockmanMain.playoffs = false; // needed, if loading another
													// game or new game
				}
				if (HockmanMain.playoffs) {
					Modus modus = HockmanMain.game.getLeague().getModus();
					if (HockmanMain.game.getRound() > modus.getNofRounds()
							+ modus.getNofPlayoffFinals() * modus.getGamesPerFinal()) {
						HockmanMain.game.afterLastRound();
						HockmanMain.eofSeason = true;
						// TODO disable saving at end of season
					} else {
						schedule.createNextPlayOffFinals(HockmanMain.game
								.getLeague().getTeamDivVector(),
								HockmanMain.game.getRound() - modus.getNofRounds());
					}
				}
				updateNextRound();
			} else if (HockmanMain.game.getLeague().getModus().getGameType() == Modus.GameType.TOURNAMENT) {
				// tournament modus: quit at end of season!
				Team team = HockmanMain.game.getChampion();
				HockmanMain.stageHandler
						.showModalStageAndWait("Champions.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_CHAMPIONS"));

				HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_GAME_OVER");
				HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));

				HockmanMain.eofSeason = false;
				HockmanMain.modified = false;

				System.exit(0);

			} else {
				HockmanMain.stageHandler
						.showModalStageAndWait("Champions.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_CHAMPIONS"));
				HockmanMain.stageHandler.showModalStageAndWait("EofSeas.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_EOFSEAS"));
				HockmanMain.game.eoSeason();
				HockmanMain.stageHandler
						.showModalStageAndWait("AllTransfers.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_ALLTRANSFERS"));
				HockmanMain.eofSeason = false;
				HockmanMain.playoffs = false;
				// TODO enabling saving after showing all transfers at end of season
				HockmanMain.game.newSeason();
				updateNextRound();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			HockmanMain.msgBoxLbl = "An exception occured:\n" + t.getMessage();
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
	}

	@FXML
	private void showLineUp(ActionEvent event) {
		HockmanMain.lineupTeam = HockmanMain.currManagedTeam;
		HockmanMain.stageHandler.showModalStageAndWait("LineUp.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_LINEUP"));
	}

	@FXML
	private void showFinances(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("Finances.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_FINANCES"));
	}

	@FXML
	private void showTraining(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("Training.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_TRAINING"));
	}

	@FXML
	private void showNews(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("News.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_NEWS"));
	}

	@FXML
	private void showTactics(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("Tactics.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_TACTICS"));
	}

	@FXML
	private void showTables(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("Tables.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_TABLES"));
	}

	@FXML
	private void showTransfers(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("Transfers.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_TRANSFERS"));
	}

	@FXML
	private void loadGame(ActionEvent event) {
		if(HockmanMain.modified) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_WITHOUT_SAVING");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
		
//TODO enable JavaFX version of FileChooser, as soon as the Java FX bug is fixed
//		FileChooser fileChooser = new FileChooser();
//		fileChooser.setInitialDirectory(new File("."));
//		fileChooser.getExtensionFilters().add(new ExtensionFilter("Game XML files (*.gam)", "*.gam"));		
//		File file = fileChooser.showOpenDialog(null);
		
// workaround for JavaFX bug: FileChooser not running under 64 bit Windows
// see http://www.javaworld.com/javaworld/jw-05-2012/120529-jtip-deploying-javafx.html?page=3
	   class FileNameFilter extends FileFilter {
	      public boolean accept(File arg0) {
	         if(arg0.isDirectory()) return true;
	         if(arg0.getName().endsWith("gam")) return true;
	         return false;
	      }
	      public String getDescription() {
	         return "GAM files (*.gam)";
	      }
	   }		
	   JFileChooser chooser = new JFileChooser(".");
	   FileNameFilter filter = new FileNameFilter();
	   chooser.setFileFilter(filter);
	   int returnVal = chooser.showOpenDialog(null);
	   currGameFile = null;
	   if(returnVal == JFileChooser.APPROVE_OPTION) {
		   currGameFile=chooser.getSelectedFile();
	   }				
// end workaround		
		
		if(currGameFile == null) {
			return;
		}
		GameCreator.instance().setCurrFileName(currGameFile.getAbsolutePath());
		try {
			HockmanMain.game = GameCreator.instance().loadGam();
			HockmanMain.currManagedTeam = HockmanMain.game.getLeague()
					.getTeamDivVector().getFirstCoachedTeam();
			HockmanMain.modified = false;			
			updateNextRound();			
		} catch (Throwable t) {
			t.printStackTrace();
			HockmanMain.msgBoxLbl = "An exception occured:\n" + t.getMessage();
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
	}

	@FXML
	private void saveGame(ActionEvent event) {
		if (HockmanMain.game != null) {		
			if(currGameFile == null) {
//TODO enable JavaFX version of FileChooser, as soon as the Java FX bug is fixed				
//				FileChooser fileChooser = new FileChooser();
//				fileChooser.setInitialDirectory(new File("."));
//				fileChooser.getExtensionFilters().add(new ExtensionFilter("Game XML files (*.gam)", "*.gam"));
//				currGameFile = fileChooser.showSaveDialog(null);
				
		// workaround for JavaFX bug: FileChooser not running under 64 bit Windows
		// see http://www.javaworld.com/javaworld/jw-05-2012/120529-jtip-deploying-javafx.html?page=3
			   class FileNameFilter extends FileFilter {
			      public boolean accept(File arg0) {
			         if(arg0.isDirectory()) return true;
			         if(arg0.getName().endsWith("gam")) return true;
			         return false;
			      }
			      public String getDescription() {
			         return "GAM files (*.gam)";
			      }
			   }		
			   JFileChooser chooser = new JFileChooser(".");
			   FileNameFilter filter = new FileNameFilter();
			   chooser.setFileFilter(filter);
			   int returnVal = chooser.showSaveDialog(null);
			   currGameFile = null;
			   if(returnVal == JFileChooser.APPROVE_OPTION) {
				   currGameFile=chooser.getSelectedFile();
			   }		
		// end workaround
			    if(currGameFile == null) {
				   return;
			    }
				
				if(!currGameFile.getName().contains(".")) {
					// workaround JavaFX bug about not adding file extensions					
					currGameFile = new File(currGameFile.getAbsolutePath() + ".gam");
				}
			}
			GameCreator.instance().setCurrFileName(currGameFile.getAbsolutePath());
			GameCreator.instance().saveGam(HockmanMain.game);
			HockmanMain.modified = false;			
		}
	}

	@FXML
	private void saveAsGame(ActionEvent event) {
		if (HockmanMain.game != null) {
//TODO enable JavaFX version of FileChooser, as soon as the Java FX bug is fixed			
//			FileChooser fileChooser = new FileChooser();
//			fileChooser.setInitialDirectory(new File("."));
//			fileChooser.getExtensionFilters().add(new ExtensionFilter("Game XML files (*.gam)", "*.gam"));			
//			currGameFile = fileChooser.showSaveDialog(null);
			
	// workaround for JavaFX bug: FileChooser not running under 64 bit Windows
	// see http://www.javaworld.com/javaworld/jw-05-2012/120529-jtip-deploying-javafx.html?page=3
		   class FileNameFilter extends FileFilter {
		      public boolean accept(File arg0) {
		         if(arg0.isDirectory()) return true;
		         if(arg0.getName().endsWith("gam")) return true;
		         return false;
		      }
		      public String getDescription() {
		         return "GAM files (*.gam)";
		      }
		   }		
		   JFileChooser chooser = new JFileChooser(".");
		   FileNameFilter filter = new FileNameFilter();
		   chooser.setFileFilter(filter);
		   int returnVal = chooser.showSaveDialog(null);
		   currGameFile = null;
		   if(returnVal == JFileChooser.APPROVE_OPTION) {
			   currGameFile=chooser.getSelectedFile();
		   }		
	// end workaround
		   
		    if(currGameFile == null) {
			   return;
		    }
			if(!currGameFile.getName().contains(".")) {
				// workaround JavaFX bug about not adding file extensions
				currGameFile = new File(currGameFile.getAbsolutePath() + ".gam");
			}
			GameCreator.instance().setCurrFileName(currGameFile.getAbsolutePath());
			GameCreator.instance().saveGam(HockmanMain.game);
			HockmanMain.modified = false;			
		}
	}

	@FXML
	private void about(ActionEvent event) {
		HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_ABOUT_TEXT");
		HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
	}
	
	@FXML
	private void quitGame(ActionEvent event) {
		if (HockmanMain.modified) {
			HockmanMain.msgBoxLbl = Util.getModelResourceBundle().getString("L_REALLY_QUIT");
			HockmanMain.stageHandler.showModalStageAndWait("MessageBox.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_MESSAGEBOX"));
		}
		System.exit(0);
	}

	@FXML
	private void showRoster(ActionEvent event) {
		HockmanMain.stageHandler.showModalStageAndWait("Roster.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_ROSTER"));
	}

	@FXML
	private void showSchedule(ActionEvent event) throws IOException {
		HockmanMain.stageHandler.showModalStageAndWait("Schedule.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_SCHEDULE"));
	}

	@FXML
	private void showOptions(ActionEvent event) throws IOException {
		HockmanMain.stageHandler.showModalStageAndWait("Options.fxml", Util.getModelResourceBundle().getString("L_MASKTITLE_OPTIONS"));
	}

}
