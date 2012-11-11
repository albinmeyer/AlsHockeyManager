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

package ch.hockman.model;

import ch.hockman.model.common.Util;
import ch.hockman.model.player.Nation;
import ch.hockman.model.player.NationPtrVector;
import ch.hockman.model.team.Team;

/**
 * The modus played in this league.
 *
 * @author Albin
 *
 */
public class Modus {

	static class NofRounds {
		int nofMatch;
		int nofMatchInOwnDiv;
		int nofMatchInOwnConf;
		int nofMatchInOtherConf;
	};

	public enum GameType {
		TOURNAMENT(0), TRADE(1), MONEY(2);
		private int i;

		private GameType(int i) {
			this.i = i;
		}

		private int getValue() {
			return this.i;
		}
		
		public String getName() {
			switch(i) {
				case 0:
					return Util.getModelResourceBundle().getString("L_MODUS_TOURNAMENT");
				case 1:
					return Util.getModelResourceBundle().getString("L_MODUS_TRADE");
				case 2:
					return Util.getModelResourceBundle().getString("L_MODUS_MONEY");
				default:
					return "";
			}
		}

		public static GameType getEnumByValue(int value) {
			for (GameType test : GameType.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No GameType available for value " + value);
		}
	}

	public static final int LGU_NOFNOFDIV = 3; // e.g. possible nof divisions are 1,2,4 => NOFNOFDIV==3
	private static int[] possNofDivisions; // for the GUI
	static final int maxNofForeignersExceptUnlimited = League.LGU_MAXNOF_FOREIGN;
	
	private GameType gameType;
	private int nofDivisions;
	private int nofTeams; // per division
	Nation nation;
	int nofMatchInOwnDiv;
	int nofMatchInOwnConf;
	int nofMatchInOtherConf;
	int nofForeigners; // if nofForeigners==maxNofForeignersExceptUnlimited+1 => unlimited !
	private boolean advHome;
	private int nofRounds;
	private boolean twoPoints;
	private int nofPlayoffFinals; // 1 = final, 2 = 1/2, 3 = 1/4, 4 = 1/8
	private int gamesPerFinal;
	boolean acrossDivFinals;
	boolean penalties;

	Modus() {
		setPossNofDivisions(new int[LGU_NOFNOFDIV]);
		getPossNofDivisions()[0] = 1;
		getPossNofDivisions()[1] = 2;
		getPossNofDivisions()[2] = 4;
		setGameType(GameType.MONEY);
		setNofDivisions(1);
		setNofTeams(10);
		nation = null; // no nation instance allocated yet
		nofMatchInOwnDiv = 36;
		nofMatchInOwnConf = 0;
		nofMatchInOtherConf = 0;
		nofForeigners = 3;
		setAdvHome(true);
		setNofRounds(36);
		setTwoPoints(true);
		setNofPlayoffFinals(3);
		setGamesPerFinal(7);
		acrossDivFinals = false;
		penalties = true;
	}

	boolean checkAttributes() {
		// actually I wanted to use exception handling for failing
		// as in Player.CheckAttributes() and Team.CheckAttributes()
		// but the Borland VCL was handling the thrown Modus exception first.
		// Don't know why ! So now just a bool is returned for success/failing
		if (getGameType() != GameType.TOURNAMENT && getGameType() != GameType.TRADE
				&& getGameType() != GameType.MONEY) {
			return false;
		}
		int i;
		for (i = 0; i < LGU_NOFNOFDIV; i++) {
			if (getPossNofDivisions()[i] == getNofDivisions()) {
				break;
			}
		}
		if (i == LGU_NOFNOFDIV) {
			return false;
		}
		if (getNofTeams() < 1 || getNofTeams() > 99) {
			return false;
		}
		// if(nation== NULL) // cannot check that
		NofRounds[] nofRoundsValueArray = new NofRounds[League.LGU_NOFNOFROUNDS];
		for (i = 0; i < League.LGU_NOFNOFROUNDS; i++) {
			nofRoundsValueArray[i] = new NofRounds();
		}
		possibleNofRounds(League.LGU_NOFNOFROUNDS, nofRoundsValueArray);
		for (i = 0; i < League.LGU_NOFNOFROUNDS; i++) {
			if (nofRoundsValueArray[i].nofMatch == getNofRounds()
					&& nofRoundsValueArray[i].nofMatchInOwnDiv == nofMatchInOwnDiv
					&& nofRoundsValueArray[i].nofMatchInOwnConf == nofMatchInOwnConf
					&& nofRoundsValueArray[i].nofMatchInOtherConf == nofMatchInOtherConf) {
				break;
			}
		}
		if (i == League.LGU_NOFNOFROUNDS) {
			return false;
		}

		if (nofForeigners > Team.TEAM_MAXTEAMPLAYER + Team.TEAM_MAXFARMPLAYER) {
			return false;
		}
		// advHome=true // bool automatically correct
		// twoPoints= true; // bool automatically correct
		if (getNofPlayoffFinals() < minPossibleNofFinals()
				|| getNofPlayoffFinals() > maxPossibleNofFinals()) {
			return false;
		}
		if (getGamesPerFinal() > League.LGU_PLAYOFFGAMES) {
			return false;
		}
		// acrossDivFinals= false; // bool automatically correct
		// penalties= true; // bool automatically correct
		return true;
	}

	void possibleNofRounds(int nofNumbers, NofRounds[] valueArray) {
		// returns nofNumbers times values for possible number of rounds
		if (getNofDivisions() == 1) {
			valueArray[0].nofMatch = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams(); // play once against each team
			valueArray[0].nofMatchInOwnDiv = valueArray[0].nofMatch;
			valueArray[0].nofMatchInOwnConf = 0;
			valueArray[0].nofMatchInOtherConf = 0;
			for (int i = 1; i < nofNumbers; i++) {
				valueArray[i].nofMatch = 2 * i
						* ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams());
				valueArray[i].nofMatchInOwnDiv = valueArray[i].nofMatch;
				valueArray[i].nofMatchInOwnConf = 0;
				valueArray[i].nofMatchInOtherConf = 0;
			}
		} else if (getNofDivisions() == 2) {
			int m = 1;
			int n = 0;
			assert (nofNumbers > 1);
			valueArray[0].nofMatch = ((getNofTeams() % 2 == 0) ? getNofTeams()
					: (getNofTeams() + 1))
					+ ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams());
					// play once against each team
			valueArray[0].nofMatchInOwnDiv = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams();
			valueArray[0].nofMatchInOwnConf = (getNofTeams() % 2 == 0) ? getNofTeams()
					: (getNofTeams() + 1);
			valueArray[0].nofMatchInOtherConf = 0;
			valueArray[1].nofMatch = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams();
			valueArray[1].nofMatchInOwnDiv = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams();
			valueArray[1].nofMatchInOwnConf = 0;
			valueArray[1].nofMatchInOtherConf = 0;
			for (int i = 2; i < nofNumbers; i++) {
				valueArray[i].nofMatch = 2 * m
						* ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams()) + 2
						* n * ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1));
				valueArray[i].nofMatchInOwnDiv = 2 * m
						* ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams());
				valueArray[i].nofMatchInOwnConf = 2 * n
						* ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1));
				valueArray[i].nofMatchInOtherConf = 0;
				if (m <= n) {
					m++;
				} else {
					n++;
				}
			}
		} else {
			int m = 1;
			int n = 0;
			int o = 0;
			assert (getNofDivisions() == 4 && nofNumbers > 2);
			valueArray[0].nofMatch = 3
					* ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1))
					+ ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams());
					// play once against each team
			valueArray[0].nofMatchInOwnDiv = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams();
			valueArray[0].nofMatchInOwnConf = (getNofTeams() % 2 == 0) ? getNofTeams()
					: (getNofTeams() + 1);
			valueArray[0].nofMatchInOtherConf = 2 * ((getNofTeams() % 2 == 0) ? getNofTeams()
					: (getNofTeams() + 1));
			valueArray[1].nofMatch = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams();
			valueArray[1].nofMatchInOwnDiv = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams();
			valueArray[1].nofMatchInOwnConf = 0;
			valueArray[1].nofMatchInOtherConf = 0;
			valueArray[2].nofMatch = ((getNofTeams() % 2 == 0) ? getNofTeams()
					: (getNofTeams() + 1))
					+ ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams());
			valueArray[2].nofMatchInOwnDiv = (getNofTeams() % 2 == 0) ? (getNofTeams() - 1)
					: getNofTeams();
			valueArray[2].nofMatchInOwnConf = (getNofTeams() % 2 == 0) ? getNofTeams()
					: (getNofTeams() + 1);
			valueArray[2].nofMatchInOtherConf = 0;
			for (int i = 3; i < nofNumbers; i++) {
				valueArray[i].nofMatch = 2 * m
						* ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams()) + 2
						* n * ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1))
						+ 4 * o
						* ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1));
				valueArray[i].nofMatchInOwnDiv = 2 * m
						* ((getNofTeams() % 2 == 0) ? (getNofTeams() - 1) : getNofTeams());
				valueArray[i].nofMatchInOwnConf = 2 * n
						* ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1));
				valueArray[i].nofMatchInOtherConf = 4 * o
						* ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1));
				if (m <= n) {
					m++;
				} else if (n <= o) {
					n++;
				} else {
					o++;
				}
			}
		}

	}

	int maxPossibleNofFinals() {
		int retval = 0;
		int team = 2;
		while (team <= getNofTeams()) {
			retval++;
			team *= 2; // a good compiler optimizes this to a leftshift
		}
		if (getNofDivisions() == 2) {
			retval++;
		} else if (getNofDivisions() == 4) {
			retval += 2;
		}
		return retval;
	}

	int minPossibleNofFinals() {
		// if 4 divisions => need at least 2 finals. Otherwise 1.
		return 1 + ((getNofDivisions() == 4) ? 1 : 0);
	}

	public int maxNofForeigners() {
		int n;
		if (nofForeigners > maxNofForeignersExceptUnlimited
				|| nation == null // international league with no foreigner limits
				) {
			n = Team.TEAM_MAXTEAMPLAYER;
		} else {
			n = nofForeigners;
		}
		return n;
	}

	public int nofMatches(boolean playoff) {
		// Number of matches a round (playoff or regular)
		int nofMatches;
		if (!playoff) {
			// Regular Season
			nofMatches = ((getNofTeams() % 2 == 0) ? getNofTeams() : (getNofTeams() + 1))
					* getNofDivisions() / 2;
		} else {
			// Playoff
			nofMatches = (int) Math.pow(2, getNofPlayoffFinals() - 1);
		}
		return nofMatches;
	}

	void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Modus");
		xmlFile.write("GameType", getGameType().getValue(), "0=Tournament, 1=Trade, 2=Money");
		xmlFile.write("NofDivisions", getNofDivisions(), "Number of divisions containing teams in league");
		xmlFile.write("NofTeams", getNofTeams());
		if (nation == null) {
			xmlFile.write("NationId", -1);
		} else {
			xmlFile.write("NationId", nation.getId());
		}
		xmlFile.write("NofMatchInOwnDiv", nofMatchInOwnDiv, "Number of matches in own division of team");
		xmlFile.write("NofMatchInOwnConf", nofMatchInOwnConf, "Number of matches in own conference (if 4 divisions)");
		xmlFile.write("NofMatchInOtherConf", nofMatchInOtherConf);
		xmlFile.write("NofForeigners", nofForeigners, "Allowed number of players of other nations than the team nation");
		xmlFile.write("AdvHome", isAdvHome(), "Home team is playing better");
		xmlFile.write("NofRounds", getNofRounds());
		xmlFile.write("TwoPoints", isTwoPoints(), "0=three points per victory (2 points in overtime), 1=two points per victory");
		xmlFile.write("NofPlayoffFinals", getNofPlayoffFinals(), "How many playoff series");
		xmlFile.write("GamesPerFinal", getGamesPerFinal(), "How many games in one series");
		xmlFile.write("AccrossDivFinals", acrossDivFinals, "Playoff pairing is across divisions");
		xmlFile.write("Penalties", penalties, "0=sudden death, 1=Playoff go to shoot out after 80min tied");
		xmlFile.writeSurroundingEndElement();
	}

	public boolean load(GameLeagueFile file, NationPtrVector npv) {
		file.parseSurroundingStartElement("Modus");
		setGameType(GameType.getEnumByValue(file.getInt("GameType")));
		setNofDivisions(file.getInt("NofDivisions"));
		setNofTeams(file.getInt("NofTeams"));
		int nationId = file.getInt("NationId");
		if (nationId >= 0) {
			nation = npv.getIdNation(nationId);
		} else {
			nation = null;
		}
		nofMatchInOwnDiv = file.getInt("NofMatchInOwnDiv");
		nofMatchInOwnConf = file.getInt("NofMatchInOwnConf");
		nofMatchInOtherConf = file.getInt("NofMatchInOtherConf");
		nofForeigners = file.getInt("NofForeigners");
		setAdvHome(file.getBool("AdvHome"));
		setNofRounds(file.getInt("NofRounds"));
		setTwoPoints(file.getBool("TwoPoints"));
		setNofPlayoffFinals(file.getInt("NofPlayoffFinals"));
		setGamesPerFinal(file.getInt("GamesPerFinal"));
		acrossDivFinals = file.getBool("AccrossDivFinals");
		penalties = file.getBool("Penalties");
		file.parseSurroundingEndElement();
		return checkAttributes();
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public static int[] getPossNofDivisions() {
		return possNofDivisions;
	}

	public static void setPossNofDivisions(int[] possNofDivisions) {
		Modus.possNofDivisions = possNofDivisions;
	}

	public int getNofDivisions() {
		return nofDivisions;
	}

	public void setNofDivisions(int nofDivisions) {
		this.nofDivisions = nofDivisions;
	}

	public int getNofTeams() {
		return nofTeams;
	}

	public void setNofTeams(int nofTeams) {
		this.nofTeams = nofTeams;
	}

	public boolean isAdvHome() {
		return advHome;
	}

	public void setAdvHome(boolean advHome) {
		this.advHome = advHome;
	}

	public int getNofRounds() {
		return nofRounds;
	}

	public void setNofRounds(int nofRounds) {
		this.nofRounds = nofRounds;
	}

	public boolean isTwoPoints() {
		return twoPoints;
	}

	public void setTwoPoints(boolean twoPoints) {
		this.twoPoints = twoPoints;
	}

	public int getNofPlayoffFinals() {
		return nofPlayoffFinals;
	}

	public void setNofPlayoffFinals(int nofPlayoffFinals) {
		this.nofPlayoffFinals = nofPlayoffFinals;
	}

	public int getGamesPerFinal() {
		return gamesPerFinal;
	}

	public void setGamesPerFinal(int gamesPerFinal) {
		this.gamesPerFinal = gamesPerFinal;
	}
}