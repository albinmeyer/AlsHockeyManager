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

package ch.hockman.model.team;

import java.io.File;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import ch.hockman.model.Finances;
import ch.hockman.model.Game;
import ch.hockman.model.GameLeagueFile;
import ch.hockman.model.League;
import ch.hockman.model.Modus;
import ch.hockman.model.Statistics;
import ch.hockman.model.Tactics;
import ch.hockman.model.TeamMessages;
import ch.hockman.model.Training;
import ch.hockman.model.Transfer;
import ch.hockman.model.common.HockmanException;
import ch.hockman.model.common.Util;
import ch.hockman.model.player.Nation;
import ch.hockman.model.player.NationPtrVector;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.position.Center;
import ch.hockman.model.position.Defender;
import ch.hockman.model.position.Goalie;
import ch.hockman.model.position.LeftWing;
import ch.hockman.model.position.Position;
import ch.hockman.model.position.RightWing;

/**
 * An icehockey team of a league.
 *
 * Invariants:
 * max TEAM_MAXTEAMPLAYER players in teamPlayerVec
 * max TEAM_MAXFARMPLAYER players in farmPlayerVec
 * max TEAM_MAXROOKIEPLAYER players in rookiePlayerVec
 *
 * @author Albin
 *
 */
public class Team {

	static final int TEAM_NOFFARMPLAYERS = 8;
	static final int TEAM_CAPACITY = 8000;
	static final int TEAM_MAXCAPACITY = 30000;
	static final int TEAM_MAXNOFTEAMINDIV = 20; // if modified, modify
	// PLR_MAXNOF too !
	static final int TEAM_MAXNOFTEAMS = 100; // if modified, modify
	// NAT_MAXNOFNAT,
	// LGU_DIVISION_NUMBERS too !
	public static final int TEAM_MAXTEAMPLAYER = 22;
	public static final int TEAM_MAXFARMPLAYER = 8;
	public static final int TEAM_MAXROOKIEPLAYER = 10;
	public static final int TEAM_MUSTBEINITPLAYER = 25;
	public static final int TEAM_MAX_NOF_ROOKIES_A_YEAR = 3;
	private static final String L_TEAMPICPATH = "..\\pics\\team.bmp";
	private static final String L_TEAMNAME = "ZSC Lions";
	private static final String L_FARMNAME = "GCK Lions";
	private static final String L_STADIUMNAME = "Hallenstadion";

	static BitSet idBar = new BitSet(TEAM_MAXNOFTEAMS);; // managing used IDs

	private int id; // needed for load/save
	private boolean home; // where is the next match (needed for coaching this team)
	private PlayerPtrVector teamPlayerVec; // TEAM_MAXTEAMPLAYER
	private PlayerPtrVector farmPlayerVec; // TEAM_MAXFARMPLAYER
	private PlayerPtrVector rookiePlayerVec; // TEAM_MAXROOKIEPLAYER
	private int goalieIter, defenderIter, leftWingIter, centerIter,
			rightWingIter, nonPlayingIter;
	private int maxNofForeigners; // for internal use only: calculate


	private String picPath;
	String teamName;
	String farmName;
	String stadiumName;
	private int capacity, division;
	private Nation nation;
	private Sponsoring sponsoring;
	private Finances finances;
	private Transfer transfer;
	private Training training;
	private Tactics tactics;
	private LineUp lineUp;
	private Statistics statistics;
	private TeamState teamState; // coached league, non-coached league,
	// non-coached non-league
	private TeamMessages messages;
	
	public enum Sponsoring {
		GOOD(0), OK(1), POOR(2);
		private int i;

		private Sponsoring(int i) {
			this.i = i;
		}

		private int getValue() {
			return this.i;
		}
		
		public String getName() {
			switch(i) {
				case 0:
					return Util.getModelResourceBundle().getString("L_SPONSOR_GOOD");
				case 1:
					return Util.getModelResourceBundle().getString("L_SPONSOR_OK");
				case 2:
					return Util.getModelResourceBundle().getString("L_SPONSOR_POOR");
				default:
					return "";
			}			
		}

		public static Sponsoring getEnumByValue(int value) {
			for (Sponsoring test : Sponsoring.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException(
					"No Sponsoring available for value " + value);
		}
	};

	public Team(int division) {
		// called for LeagueCreator (league editor/loading team from lgu file)
		this.teamPlayerVec = new PlayerPtrVector();
		this.farmPlayerVec = new PlayerPtrVector();
		this.rookiePlayerVec = new PlayerPtrVector();
		this.lineUp = new LineUp();
		this.statistics = new Statistics();
		this.messages = new TeamMessages();
		this.training = new Training();
		this.tactics = new Tactics();
		this.finances = new Finances();
		this.transfer = new Transfer();
		createNewId(); // create a new id
		// default inits
		home = false;
		picPath = L_TEAMPICPATH;
		teamName = L_TEAMNAME;
		farmName = L_FARMNAME;
		stadiumName = L_STADIUMNAME;
		capacity = TEAM_CAPACITY;
		this.division = division;
		nation = null;
		sponsoring = Sponsoring.OK;
		// lineup, tactics, training, finances, statistics already init by their
		// default ctors
		teamState = null;
		goalieIter = 0;
		defenderIter = 0;
		leftWingIter = 0;
		centerIter = 0;
		rightWingIter = 0;
		nonPlayingIter = 0;
		maxNofForeigners = 0;
	}

	public void saveGam(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("TeamGam");
		saveLgu(xmlFile);
		this.training.save(xmlFile);
		this.tactics.save(xmlFile);
		this.finances.save(xmlFile);
		this.lineUp.save(xmlFile);
		this.messages.save(xmlFile);
		xmlFile.writeSurroundingEndElement();
	}

	public void saveLgu(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Team");
		xmlFile.write("Id", id);
		int nofPlayers = getNofTeamPlayers();
		xmlFile.write("NofTeamPlayers", nofPlayers,  "Now follows a list of ids of element Player");
		teamPlayerVec.SortPlayerId();
		for (int i = 0; i < nofPlayers; i++) {
			xmlFile.write("TeamPlayerId", teamPlayerVec.GetPlayer(i).getId());
		}
		nofPlayers = getNofFarmPlayers();
		xmlFile.write("NofFarmPlayers", nofPlayers, "Now follows a list of ids of element Player");
		farmPlayerVec.SortPlayerId();
		for (int i = 0; i < nofPlayers; i++) {
			xmlFile.write("FarmPlayerId", farmPlayerVec.GetPlayer(i).getId());
		}
		nofPlayers = getNofRookiePlayers();
		xmlFile.write("NofRookiePlayers", nofPlayers, "Now follows a list of names for rookies");
		for (int i = 0; i < nofPlayers; i++) {
			Player player = rookiePlayerVec.GetPlayer(i);
			player.saveRookieLgu(xmlFile);
		}

		xmlFile.write("PicPath", this.picPath);
		xmlFile.write("TeamName", this.teamName);
		xmlFile.write("FarmName", this.farmName);
		xmlFile.write("StadiumName", this.stadiumName);
		xmlFile.write("Capacity", this.capacity);
		xmlFile.write("DivisionId", this.division, "Depending on kind of league, the division in which the team plays");
		xmlFile.write("NationId", nation.getId(), "Referencing Element Nation");
		xmlFile.write("Sponsoring", this.sponsoring.getValue(), "0=Good, 1=Normal, 2=Poor");
		// don't save lineup, tactics, training, finances, statistics
		xmlFile.write("TeamStateId", this.teamState.getTeamStateID().getValue(), "0=nonleague, 1=coached, 2=ai");
		xmlFile.writeSurroundingEndElement();
	}

	public void saveGamUpdate(GameLeagueFile xmlFile) {
		this.transfer.save(xmlFile);
		this.statistics.save(xmlFile);
	}

	void checkAttributes() throws HockmanException {
		if (id < 0 || id > TEAM_MAXNOFTEAMS) {
			throw new HockmanException("wrong team id");
		}
		if (capacity > TEAM_MAXCAPACITY) {
			throw new HockmanException("wrong team capacity");
		}
		if (division < 1
				|| division > Modus.getPossNofDivisions()[Modus.LGU_NOFNOFDIV - 1] + 1) {
			throw new HockmanException("wrong team division");
		}
		if (nation == null) {
			throw new HockmanException("wrong team nation");
		}
		if (sponsoring != Sponsoring.OK && sponsoring != Sponsoring.GOOD
				&& sponsoring != Sponsoring.POOR) {
			throw new HockmanException("wrong team sponsoring");
		}
		if (teamState == null) {
			throw new HockmanException("wrong team state");
		}
		if (teamPlayerVec.GetNofPlayers() > TEAM_MAXTEAMPLAYER) {
			throw new HockmanException("wrong nof team players in team");
		}
		if (farmPlayerVec.GetNofPlayers() > TEAM_MAXFARMPLAYER) {
			throw new HockmanException("wrong nof farm players in team");
		}
		if (rookiePlayerVec.GetNofPlayers() > TEAM_MAXROOKIEPLAYER) {
			throw new HockmanException("wrong nof rookie players in team");
		}
	}

	public void setTeamState(TeamState teamState) {
		this.teamState = teamState;
	}

	void setNation(Nation nation) {
		if (this.nation != null) {
			this.nation.decreaseReferences();
		}
		assert (nation != null); // must set a nation
		this.nation = nation;
		nation.increaseReferences();
	}

	public void initSeason() {
		getStatistics().reset();
		getMessages().reset();
		getMessages().add(Util.getModelResourceBundle().getString("L_NEW_SEASON"));
	}

	public void eofSeason(Game game) {
		League league = game.getLeague();
		// new rookies
		int no = Util.random(TEAM_MAX_NOF_ROOKIES_A_YEAR + 1);
		int nofRookies = getNofRookiePlayers();
		if (no > nofRookies) {
			no = nofRookies;
		}
		int empty = TEAM_MAXTEAMPLAYER + TEAM_MAXFARMPLAYER - getNofPlayers();
		assert (empty >= 0);
		if (no > empty) {
			no = empty;
		}
		int youthExpenses = this.getFinances().getAndResetSavedYouth();
		PlayerPtrVector allPlayers = league.getPlayerVector();
		for (int i = 0; i < no; i++) {
			Position pos = null;
			int nofGoalies = 0;
			int nofDefenders = 0;
			int nofLeftWings = 0;
			int nofCenters = 0;
			int nofRightWings = 0;
			Player player = getTransfer().getNextSeasFirst(allPlayers, this);
			while (player != null) {
				Position position = player.getPosition();
				player = getTransfer().getNextSeasNext(allPlayers, this);
				if (position.getPosID() == Position.PosID.GOALIE) {
					nofGoalies++;
				} else if (position.getPosID() == Position.PosID.DEFENDER) {
					nofDefenders++;
				} else if (position.getPosID() == Position.PosID.RIGHTWING) {
					nofRightWings++;
				} else if (position.getPosID() == Position.PosID.CENTER) {
					nofCenters++;
				} else if (position.getPosID() == Position.PosID.LEFTWING) {
					nofLeftWings++;
				} else {
					assert (false);
				}
			}
			if (nofGoalies < 3) {
				pos = Goalie.instance();
			} else if (nofCenters < 4) {
				pos = Center.instance();
			} else if (nofRightWings < 4 && nofLeftWings < 4) {
				if (Util.random(2) == 1) {
					pos = RightWing.instance();
				} else {
					pos = LeftWing.instance();
				}
			} else if (nofRightWings < 4) {
				pos = RightWing.instance();
			} else if (nofLeftWings < 4) {
				pos = LeftWing.instance();
			} else if (nofDefenders < 8) {
				pos = Defender.instance();
			} else {
				int ran = Util.random(5);
				switch (ran) {
				case 0:
					pos = Goalie.instance();
					break;
				case 1:
					pos = Defender.instance();
					break;
				case 2:
					pos = RightWing.instance();
					break;
				case 3:
					pos = Center.instance();
					break;
				case 4:
					pos = LeftWing.instance();
					break;
				default:
					assert (false);
				}
			}
			assert (player == null);
			player = getRookiePlayer(i);
			removeRookiePlayer(player);
			// init player attributes according youth expenses
			player.calcYouthAttributes(
					this,
					pos,
					game.getYear(),
					league.getModus().getNofRounds()
							+ league.getModus().getNofPlayoffFinals()
							* league.getModus().getGamesPerFinal(), youthExpenses);
			int no1 = Util.random(98) + 1; // 1..98 (99 is for Gretzky only!)
			if (no1 > 50) {
				// smaller numbers are more likely
				no1 = Util.random(98) + 1; // 1..98 (99 is for Gretzky only!)
			}
			// only goalies have number 1, only defenders can have 2,3,4,5
			while (no1 == 1
					&& player.getPosition().getPosID() != Position.PosID.GOALIE
					|| no1 > 1
					&& no1 < 6
					&& player.getPosition().getPosID() != Position.PosID.DEFENDER) {
				no1 = Util.random(98) + 1; // 1..98 (99 is for Gretzky only!)
				if (no1 > 50) {
					// smaller numbers are more likely
					no1 = Util.random(98) + 1; // 1..98 (99 is for Gretzky
					// only!)
				}
			}
			player.setNumber(no1);
			addPlayer(player);
			league.getPlayerVector().InsertPlayer(player);
			// show new rookies in window at end of season
			String s = game.getNews().getTransfers();
			s += player.getLastName();
			s += Util.getModelResourceBundle().getString("L_NEW_ROOKIE");
			s += this.getTeamName();
			s += "\n";
			game.getNews().setTransfers(s);
		}
		if (Util.random(10) == 0) {
			// eventually change sponsor quality
			sponsoring = Sponsoring.getEnumByValue(Util.random(3));
		}
	}

	public String getTeamName() {
		return teamName;
	}

	public String getPicPath() {
		return picPath;
	}

	public String getFarmName() {
		return farmName;
	}

	public String getStadiumName() {
		return stadiumName;
	}

	public int getCapacity() {
		// needed for saving
		return capacity;
	}

	public int getDivision() {
		// needed for saving
		return division;
	}

	public Nation getNation() {
		// needed for saving
		return nation;
	}

	public Sponsoring getSponsoring() {
		// needed for saving
		return sponsoring;
	}

	public Finances getFinances() {
		return finances;
	}

	public Tactics getTactics() {
		return tactics;
	}

	public Training getTraining() {
		return training;
	}

	public LineUp getLineUp() {
		return lineUp;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public TeamMessages getMessages() {
		return messages;
	}

	public Transfer getTransfer() {
		return transfer;
	}

	public TeamState getTeamState() {
		return teamState;
	}

	public int getStrengthWithMot() {
		// go through all players and add all their abilities
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getTotalStrengthWithMot();
		}
		return strength / TEAM_MAXTEAMPLAYER;
	}

	public int getShootingAv() {
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getShooting();
		}
		return strength / nofPlayers;
	}

	public int getStaminaAv() {
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getStamina();
		}
		return strength / nofPlayers;
	}

	public int getSkillAv() {
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getSkill();
		}
		return strength / nofPlayers;
	}

	public int getPassingAv() {
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getPassing();
		}
		return strength / nofPlayers;
	}

	public int getPowerAv() {
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getPower();
		}
		return strength / nofPlayers;
	}

	public int getOffensiveAv() {
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getOffense();
		}
		return strength / nofPlayers;
	}

	public int getDefensiveAv() {
		int strength = 0;
		int nofPlayers = this.getNofTeamPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = this.getTeamPlayer(i);
			strength += player.getDefense();
		}
		return strength / nofPlayers;
	}

	int getNofForeigners() {
		int nofPlayers = getNofPlayers();
		int nofForeigners = 0;
		for (int i = 0; i < nofPlayers; i++) {
			if (getPlayer(i).getNation() != nation) {
				nofForeigners++;
			}
		}
		return nofForeigners;
	}

	void checkPlayerNumber(Player player) {
		boolean done = false;
		int nofTeamPlayers = getNofTeamPlayers();
		int nofFarmPlayers = getNofFarmPlayers();
		while (!done) {
			int it = 0;
			while (it < nofTeamPlayers) {
				Player checkPlayer = getTeamPlayer(it);
				if (checkPlayer.equals(player)) {
					throw new IllegalStateException("Player "
							+ player.getLastName()
							+ " already inserted into team "
							+ this.getTeamName());
				}
				if (checkPlayer.getNumber() == player.getNumber()) {
					int no = Util.random(98) + 1; // 1..98 (99 is for Gretzky
					// only!)
					if (no > 50) {
						// smaller numbers are more likely
						no = Util.random(98) + 1; // 1..98 (99 is for Gretzky
						// only!)
					}
					// only goalies have number 1, only defenders can have 2,3,4,5
					while (no == 1
							&& player.getPosition().getPosID() != Position.PosID.GOALIE
							|| no > 1
							&& no < 6
							&& player.getPosition().getPosID() != Position.PosID.DEFENDER) {
						no = Util.random(98) + 1; // 1..98 (99 is for Gretzky
						// only!)
						if (no > 50) {
							// smaller numbers are more likely
							no = Util.random(98) + 1; // 1..98 (99 is for
							// Gretzky only!)
						}
					}
					player.setNumber(no);
					break;
				}
				it++;
			}
			int ifa = 0;
			while (ifa < nofFarmPlayers) {
				Player checkPlayer = getFarmPlayer(ifa);
				if (checkPlayer.getNumber() == player.getNumber()) {
					int no = Util.random(98) + 1; // 1..98 (99 is for Gretzky
					// only!)
					if (no > 50) {
						// smaller numbers are more likely
						no = Util.random(98) + 1; // 1..98 (99 is for Gretzky
						// only!)
					}
					player.setNumber(no);
					break;
				}
				ifa++;
			}
			if (it >= nofTeamPlayers && ifa >= nofFarmPlayers) {
				done = true;
			}
		}
	}

	void checkDoubleName() {
		int nofPlayers = getNofTeamPlayers() + getNofFarmPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = getPlayer(i);
			player.setMultipleName(false);
			for (int j = 0; j < nofPlayers; j++) {
				Player comparePlayer = getPlayer(j);
				if (comparePlayer.getLastName().equals(player.getLastName())
						&& comparePlayer != player) {
					player.setMultipleName(true);
				}
			}
		}
	}

	public void addPlayer(Player player) {
		// add where there is space
		if (getNofTeamPlayers() < TEAM_MAXTEAMPLAYER) {
			addTeamPlayer(player);
		} else if (getNofFarmPlayers() < TEAM_MAXFARMPLAYER) {
			addFarmPlayer(player);
		} else {
			// full team, but at transfering at end of season
			// (Player.UpdateAfterSeason())
			// teams can temporay be larger than allowed (swapping players)
			// however, an assertion for having the correct number of players
			// is done in Team.GetNofPlayers()
			addTempPlayer(player);
		}
	}

	public int getNofPlayers() {
		// team and farm players
		int nofPlayers = getNofTeamPlayers() + getNofFarmPlayers();
		assert (nofPlayers <= TEAM_MAXTEAMPLAYER + TEAM_MAXFARMPLAYER);
		return nofPlayers;
	}

	public void removePlayer(Player player) {
		// not delete, just remove
		player.setMultipleName(false);
		int nofPlayer = getNofTeamPlayers();
		for (int i = 0; i < nofPlayer; i++) {
			if (getTeamPlayer(i).equals(player)) {
				removeTeamPlayer(player);
				checkDoubleName();
				return;
			}
		}
		nofPlayer = getNofFarmPlayers();
		for (int i = 0; i < nofPlayer; i++) {
			if (getFarmPlayer(i).equals(player)) {
				removeFarmPlayer(player);
				checkDoubleName();
				return;
			}
		}
		assert (false); // player must have been removed
	}

	public Player getPlayer(int i) {
		if (i < getNofTeamPlayers()) {
			return teamPlayerVec.GetPlayer(i);
		} else {
			assert (i < getNofTeamPlayers() + getNofFarmPlayers());
			return farmPlayerVec.GetPlayer(i - getNofTeamPlayers());
		}
	}

	public void addTeamPlayer(Player player) {
		if (player == null) {
			throw new IllegalArgumentException(
					"no player found to insert into team " + this.getTeamName());
		}
		checkPlayerNumber(player);
		teamPlayerVec.InsertPlayer(player);
		assert (getNofTeamPlayers() <= TEAM_MAXTEAMPLAYER);
		checkDoubleName();
	}

	public int getNofTeamPlayers() {
		return teamPlayerVec.GetNofPlayers();
	}

	public void removeTeamPlayer(Player player) {
		// not delete, just remove
		lineUp.removePlayer(player);
		teamPlayerVec.RemovePlayer(player);
	}

	public Player getTeamPlayer(int i) {
		// gets team player with index
		assert (i < getNofTeamPlayers());
		return teamPlayerVec.GetPlayer(i);
	}

	public void addFarmPlayer(Player player) {
		if (player == null) {
			throw new IllegalArgumentException(
					"no player found to insert into farm " + this.getFarmName());
		}
		checkPlayerNumber(player);
		farmPlayerVec.InsertPlayer(player);
		assert (getNofFarmPlayers() <= TEAM_MAXFARMPLAYER);
		checkDoubleName();
	}

	void addTempPlayer(Player player) {
		// temp overflow of farm for transfer swapping
		farmPlayerVec.InsertPlayer(player);
	}

	public int getNofFarmPlayers() {
		return (farmPlayerVec.GetNofPlayers());
	}

	public void removeFarmPlayer(Player player) {
		// not delete, just remove
		farmPlayerVec.RemovePlayer(player);
	}

	public Player getFarmPlayer(int i) {
		// gets farm player with index
		assert (i < getNofFarmPlayers());
		return farmPlayerVec.GetPlayer(i);
	}

	public int getNofRookiePlayers() {
		return (rookiePlayerVec.GetNofPlayers());
	}

	public void removeRookiePlayer(Player player) {
		// not delete, just remove
		rookiePlayerVec.RemovePlayer(player);
	}

	public Player getRookiePlayer(int i) {
		assert (i < getNofRookiePlayers());
		return rookiePlayerVec.GetPlayer(i);
	}

	public void addRookiePlayer(Player player) {
		rookiePlayerVec.InsertPlayer(player);
	}

	public int getId() {
		return id;
	}

	public void updateNonLeaguePlayers() {
		// farm and transfer team players change engergy too !
		// update farm players
		int nofPlayers = getNofFarmPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			getFarmPlayer(i).decreaseMatchEnergy();
		}
		if (this.getTeamState().getTeamStateID() == TeamState.TeamStateID.NONLEAGUE) {
			// update first team players too
			nofPlayers = getNofTeamPlayers();
			for (int i = 0; i < nofPlayers; i++) {
				getTeamPlayer(i).decreaseMatchEnergy();
			}
		}
	}

	public Player bestMatchPlayer() {
		// sort for best players of match
		teamPlayerVec.SortPlayerBest();
		Player player = null;
		int i = 0;
		int nofPlayers = this.getNofTeamPlayers();
		while (i < nofPlayers) {
			player = this.getTeamPlayer(i);
			if (player.getHealth().getInjury() == 0
					&& player != this.getLineUp().getG().goal2) {
				return player;
			} else {
				i++;
			}
		}
		// all players injured !!??
		return player;

	}

	// following sorting methods are only for the team players (first team), NOT
	// for farm !
	void sortPlayerStrength() {
		teamPlayerVec.SortPlayerStrength();
	}

	void sortPlayerWeakness() {
		teamPlayerVec.SortPlayerWeakness();
	}

	void sortPlayerName() {
		teamPlayerVec.SortPlayerLastName();
	}

	public void sortPlayerPos() {
		teamPlayerVec.SortPlayerPos();
	}

	void sortPlayerAge() {
		teamPlayerVec.SortPlayerAge();
	}

	void sortPlayerNation() {
		teamPlayerVec.SortPlayerNation();
	}

	void sortPlayerInjury() {
		teamPlayerVec.SortPlayerInjury();
	}

	void resetGetPositionPlayer(int maxNofForeigners) {
		this.maxNofForeigners = maxNofForeigners;
		int nofPlayers = getNofTeamPlayers();
		for (int i = 0; nofPlayers > i; i++) {
			Player player = getTeamPlayer(i);
			player.setFlag(false);
		}
		// goalies get a true flag (cannot be inserted in other pos) !
		LineUp lineUp = this.getLineUp();
		if (lineUp.getG().goal1 != null)
			lineUp.getG().goal1.setFlag(true);
		if (lineUp.getG().goal2 != null)
			lineUp.getG().goal2.setFlag(true);
	}

	Player getFirstGoalie() {
		goalieIter = 0;
		return getNextGoalie();
	}

	Player getNextGoalie() {
		Player player = null;
		if (getNofTeamPlayers() > goalieIter) {
			player = getTeamPlayer(goalieIter);
			goalieIter++;
		}
		while (getNofTeamPlayers() > goalieIter
				&& (player.getPosition().getPosID() != Position.PosID.GOALIE
						|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners))) {
			player = getTeamPlayer(goalieIter);
			goalieIter++;
		}
		if (player == null
				|| player.getPosition().getPosID() != Position.PosID.GOALIE
				|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners)) {
			return null;
		} else {
			player.setFlag(true);
			return player;
		}
	}

	Player getFirstDefender() {
		defenderIter = 0;
		return getNextDefender();
	}

	Player getNextDefender() {
		Player player = null;
		if (getNofTeamPlayers() > defenderIter) {
			player = getTeamPlayer(defenderIter);
			defenderIter++;
		}
		while (getNofTeamPlayers() > defenderIter
				&& (player.getPosition().getPosID() != Position.PosID.DEFENDER
						|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners))) {
			player = getTeamPlayer(defenderIter);
			defenderIter++;
		}
		if (player == null
				|| player.getPosition().getPosID() != Position.PosID.DEFENDER
				|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners)) {
			return null;
		} else {
			player.setFlag(true);
			return player;
		}
	}

	Player getFirstLeftWing() {
		leftWingIter = 0;
		return getNextLeftWing();
	}

	Player getNextLeftWing() {
		Player player = null;
		if (getNofTeamPlayers() > leftWingIter) {
			player = getTeamPlayer(leftWingIter);
			leftWingIter++;
		}
		while (getNofTeamPlayers() > leftWingIter
				&& (player.getPosition().getPosID() != Position.PosID.LEFTWING
						|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners))) {
			player = getTeamPlayer(leftWingIter);
			leftWingIter++;
		}
		if (player == null
				|| player.getPosition().getPosID() != Position.PosID.LEFTWING
				|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners)) {
			return null;
		} else {
			player.setFlag(true);
			return player;
		}
	}

	Player getFirstCenter() {
		centerIter = 0;
		return getNextCenter();
	}

	Player getNextCenter() {
		Player player = null;
		if (getNofTeamPlayers() > centerIter) {
			player = getTeamPlayer(centerIter);
			centerIter++;
		}
		while (getNofTeamPlayers() > centerIter
				&& (player.getPosition().getPosID() != Position.PosID.CENTER
						|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners))) {
			player = getTeamPlayer(centerIter);
			centerIter++;
		}
		if (player == null
				|| player.getPosition().getPosID() != Position.PosID.CENTER
				|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners)) {
			return null;
		} else {
			player.setFlag(true);
			return player;
		}
	}

	Player getFirstRightWing() {
		rightWingIter = 0;
		return getNextRightWing();
	}

	Player getNextRightWing() {
		Player player = null;
		if (getNofTeamPlayers() > rightWingIter) {
			player = getTeamPlayer(rightWingIter);
			rightWingIter++;
		}
		while (getNofTeamPlayers() > rightWingIter
				&& (player.getPosition().getPosID() != Position.PosID.RIGHTWING
						|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners))) {
			player = getTeamPlayer(rightWingIter);
			rightWingIter++;
		}
		if (player == null
				|| player.getPosition().getPosID() != Position.PosID.RIGHTWING
				|| player.isFlag() || player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners)) {
			return null;
		} else {
			player.setFlag(true);
			return player;
		}
	}

	void initNonPlaying() {
		// for filling lineUp with nonmatching pos
		nonPlayingIter = 0;
	}

	Player getNextNonPlaying() {
		return getNextNonPlaying(false);
	}

	Player getNextNonPlaying(boolean all) {
		Player player = null;
		if (getNofTeamPlayers() > nonPlayingIter) {
			player = getTeamPlayer(nonPlayingIter);
			nonPlayingIter++;
		}
		while (getNofTeamPlayers() > nonPlayingIter
				&& (player.isFlag()
						// prefer forwards, don't use goalies on field
						|| !all
						&& player.getPosition().getPosID() == Position.PosID.DEFENDER
						|| player.getPosition().getPosID() == Position.PosID.GOALIE
						|| player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners))) {
			// don't take already playing as substitutes for not available pos
			player = getTeamPlayer(nonPlayingIter);
			nonPlayingIter++;
		}
		if (player == null
				|| player.isFlag()
				// prefer forwards, don't use goalies on field
				|| !all
				&& player.getPosition().getPosID() == Position.PosID.DEFENDER
				|| player.getPosition().getPosID() == Position.PosID.GOALIE
				|| player.getHealth().getInjury() > 0 // don't lineup injured players
				|| !checkForeigners(player, maxNofForeigners)) {
			if (!all) {
				// prefer forwards
				initNonPlaying();
				return getNextNonPlaying(true);
			} else {
				return null;
			}
		} else {
			player.setFlag(true);
			return player;
		}
	}

	public boolean checkForeigners(Player player, int maxNofForeigners) {
		// check, if nof foreigners allowed at inserting player into lineup
		if (this.isForeignPlayer(player)) {
			// maxNofForeigners
			Set<Player> foreigners = new HashSet<Player>(); // set of foreign
			// players (no
			// duplicates)
			foreigners.add(player);
			LineUp lineUp = this.getLineUp();
			if (this.isForeignPlayer(lineUp.getG().goal1))
				foreigners.add(lineUp.getG().goal1);
			if (this.isForeignPlayer(lineUp.getG().goal2))
				foreigners.add(lineUp.getG().goal2);
			if (this.isForeignPlayer(lineUp.getE55().d11))
				foreigners.add(lineUp.getE55().d11);
			if (this.isForeignPlayer(lineUp.getE55().d12))
				foreigners.add(lineUp.getE55().d12);
			if (this.isForeignPlayer(lineUp.getE55().d21))
				foreigners.add(lineUp.getE55().d21);
			if (this.isForeignPlayer(lineUp.getE55().d22))
				foreigners.add(lineUp.getE55().d22);
			if (this.isForeignPlayer(lineUp.getE55().d31))
				foreigners.add(lineUp.getE55().d31);
			if (this.isForeignPlayer(lineUp.getE55().d32))
				foreigners.add(lineUp.getE55().d32);
			if (this.isForeignPlayer(lineUp.getE55().lw1))
				foreigners.add(lineUp.getE55().lw1);
			if (this.isForeignPlayer(lineUp.getE55().c1))
				foreigners.add(lineUp.getE55().c1);
			if (this.isForeignPlayer(lineUp.getE55().rw1))
				foreigners.add(lineUp.getE55().rw1);
			if (this.isForeignPlayer(lineUp.getE55().lw2))
				foreigners.add(lineUp.getE55().lw2);
			if (this.isForeignPlayer(lineUp.getE55().c2))
				foreigners.add(lineUp.getE55().c2);
			if (this.isForeignPlayer(lineUp.getE55().rw2))
				foreigners.add(lineUp.getE55().rw2);
			if (this.isForeignPlayer(lineUp.getE55().lw3))
				foreigners.add(lineUp.getE55().lw3);
			if (this.isForeignPlayer(lineUp.getE55().c3))
				foreigners.add(lineUp.getE55().c3);
			if (this.isForeignPlayer(lineUp.getE55().rw3))
				foreigners.add(lineUp.getE55().rw3);
			if (this.isForeignPlayer(lineUp.getE55().lw4))
				foreigners.add(lineUp.getE55().lw4);
			if (this.isForeignPlayer(lineUp.getE55().c4))
				foreigners.add(lineUp.getE55().c4);
			if (this.isForeignPlayer(lineUp.getE55().rw4))
				foreigners.add(lineUp.getE55().rw4);
			if (this.isForeignPlayer(lineUp.getE44().d11))
				foreigners.add(lineUp.getE44().d11);
			if (this.isForeignPlayer(lineUp.getE44().d12))
				foreigners.add(lineUp.getE44().d12);
			if (this.isForeignPlayer(lineUp.getE44().f11))
				foreigners.add(lineUp.getE44().f11);
			if (this.isForeignPlayer(lineUp.getE44().f12))
				foreigners.add(lineUp.getE44().f12);
			if (this.isForeignPlayer(lineUp.getE44().d21))
				foreigners.add(lineUp.getE44().d21);
			if (this.isForeignPlayer(lineUp.getE44().d22))
				foreigners.add(lineUp.getE44().d22);
			if (this.isForeignPlayer(lineUp.getE44().f21))
				foreigners.add(lineUp.getE44().f21);
			if (this.isForeignPlayer(lineUp.getE44().f22))
				foreigners.add(lineUp.getE44().f22);
			if (this.isForeignPlayer(lineUp.getE33().d11))
				foreigners.add(lineUp.getE33().d11);
			if (this.isForeignPlayer(lineUp.getE33().d12))
				foreigners.add(lineUp.getE33().d12);
			if (this.isForeignPlayer(lineUp.getE33().f1))
				foreigners.add(lineUp.getE33().f1);
			if (this.isForeignPlayer(lineUp.getE33().d21))
				foreigners.add(lineUp.getE33().d21);
			if (this.isForeignPlayer(lineUp.getE33().d22))
				foreigners.add(lineUp.getE33().d22);
			if (this.isForeignPlayer(lineUp.getE33().f2))
				foreigners.add(lineUp.getE33().f2);
			if (this.isForeignPlayer(lineUp.getPp5().d11))
				foreigners.add(lineUp.getPp5().d11);
			if (this.isForeignPlayer(lineUp.getPp5().d12))
				foreigners.add(lineUp.getPp5().d12);
			if (this.isForeignPlayer(lineUp.getPp5().lw1))
				foreigners.add(lineUp.getPp5().lw1);
			if (this.isForeignPlayer(lineUp.getPp5().c1))
				foreigners.add(lineUp.getPp5().c1);
			if (this.isForeignPlayer(lineUp.getPp5().rw1))
				foreigners.add(lineUp.getPp5().rw1);
			if (this.isForeignPlayer(lineUp.getPp5().d21))
				foreigners.add(lineUp.getPp5().d21);
			if (this.isForeignPlayer(lineUp.getPp5().d22))
				foreigners.add(lineUp.getPp5().d22);
			if (this.isForeignPlayer(lineUp.getPp5().lw2))
				foreigners.add(lineUp.getPp5().lw2);
			if (this.isForeignPlayer(lineUp.getPp5().c2))
				foreigners.add(lineUp.getPp5().c2);
			if (this.isForeignPlayer(lineUp.getPp5().rw2))
				foreigners.add(lineUp.getPp5().rw2);
			if (this.isForeignPlayer(lineUp.getPp4().d11))
				foreigners.add(lineUp.getPp4().d11);
			if (this.isForeignPlayer(lineUp.getPp4().d12))
				foreigners.add(lineUp.getPp4().d12);
			if (this.isForeignPlayer(lineUp.getPp4().f11))
				foreigners.add(lineUp.getPp4().f11);
			if (this.isForeignPlayer(lineUp.getPp4().f12))
				foreigners.add(lineUp.getPp4().f12);
			if (this.isForeignPlayer(lineUp.getPp4().d21))
				foreigners.add(lineUp.getPp4().d21);
			if (this.isForeignPlayer(lineUp.getPp4().d22))
				foreigners.add(lineUp.getPp4().d22);
			if (this.isForeignPlayer(lineUp.getPp4().f21))
				foreigners.add(lineUp.getPp4().f21);
			if (this.isForeignPlayer(lineUp.getPp4().f22))
				foreigners.add(lineUp.getPp4().f22);
			if (this.isForeignPlayer(lineUp.getPk4().d11))
				foreigners.add(lineUp.getPk4().d11);
			if (this.isForeignPlayer(lineUp.getPk4().d12))
				foreigners.add(lineUp.getPk4().d12);
			if (this.isForeignPlayer(lineUp.getPk4().f11))
				foreigners.add(lineUp.getPk4().f11);
			if (this.isForeignPlayer(lineUp.getPk4().f12))
				foreigners.add(lineUp.getPk4().f12);
			if (this.isForeignPlayer(lineUp.getPk4().d21))
				foreigners.add(lineUp.getPk4().d21);
			if (this.isForeignPlayer(lineUp.getPk4().d22))
				foreigners.add(lineUp.getPk4().d22);
			if (this.isForeignPlayer(lineUp.getPk4().f21))
				foreigners.add(lineUp.getPk4().f21);
			if (this.isForeignPlayer(lineUp.getPk4().f22))
				foreigners.add(lineUp.getPk4().f22);
			if (this.isForeignPlayer(lineUp.getPk3().d11))
				foreigners.add(lineUp.getPk3().d11);
			if (this.isForeignPlayer(lineUp.getPk3().d12))
				foreigners.add(lineUp.getPk3().d12);
			if (this.isForeignPlayer(lineUp.getPk3().f1))
				foreigners.add(lineUp.getPk3().f1);
			if (this.isForeignPlayer(lineUp.getPk3().d21))
				foreigners.add(lineUp.getPk3().d21);
			if (this.isForeignPlayer(lineUp.getPk3().d22))
				foreigners.add(lineUp.getPk3().d22);
			if (this.isForeignPlayer(lineUp.getPk3().f2))
				foreigners.add(lineUp.getPk3().f2);
			return (foreigners.size() <= maxNofForeigners);
		} else {
			return true;
		}
	}

	public boolean getHome() {
		// where is the next match?
		return home;
	}

	public void setHome(boolean home) {
		this.home = home;
	}

	private boolean isForeignPlayer(Player player) {
		return (player != null && player.getNation() != this.getNation());
	}

	private void setId(int i) {
		assert (idBar.get(id)); // old id is used
		idBar.clear(id);
		id = i;
		assert (!idBar.get(id)); // id should not already be used
		idBar.set(id);
	}

	private void createNewId() {
		id = 0;
		while (id < TEAM_MAXNOFTEAMS) {
			if (!idBar.get(id)) {
				idBar.set(id);
				break;
			}
			id++;
		}
		assert (id < TEAM_MAXNOFTEAMS); // must have breaked in "while" above
	}

	public void loadLgu(GameLeagueFile file, NationPtrVector npv,
			PlayerPtrVector ppv) throws HockmanException {
		file.parseSurroundingStartElement("Team");
		setId(file.getInt("Id"));
		int nofPlayers = file.getInt("NofTeamPlayers");
		for (int i = 0; i < nofPlayers; i++) {
			int id = file.getInt("TeamPlayerId");
			Player player = ppv.GetIdPlayer(id);
			if (this.id != player.getContracts().getCurrContr().getId()) {
				throw new IllegalStateException(
						"Player "
								+ player.getFirstName()
								+ " "
								+ player.getLastName()
								+ "\nhas different current contract as team TeamPlayerId");
			}
			addTeamPlayer(ppv.GetIdPlayer(id));
		}
		nofPlayers = file.getInt("NofFarmPlayers");
		for (int i = 0; i < nofPlayers; i++) {
			int id = file.getInt("FarmPlayerId");
			Player player = ppv.GetIdPlayer(id);
			if (this.id != player.getContracts().getCurrContr().getId()) {
				throw new IllegalStateException(
						"Player "
								+ player.getFirstName()
								+ " "
								+ player.getLastName()
								+ "\nhas different current contract as team TeamPlayerId");
			}
			addFarmPlayer(player);
		}
		nofPlayers = file.getInt("NofRookiePlayers");
		for (int i = 0; i < nofPlayers; i++) {
			Player player = new Player(2012);
			player.loadRookieLgu(file, npv);
			player.updateContrTeams(this);
			addRookiePlayer(player);
		}
		this.picPath = file.getString("PicPath").replace('\\', File.separatorChar).replace('/', File.separatorChar);		
		this.teamName = file.getString("TeamName");
		this.farmName = file.getString("FarmName");
		this.stadiumName = file.getString("StadiumName");
		this.capacity = file.getInt("Capacity");
		this.division = file.getInt("DivisionId");
		this.setNation(npv.getIdNation(file.getInt("NationId")));
		this.sponsoring = Sponsoring.getEnumByValue(file.getInt("Sponsoring"));
		// lineup, tactics, training, finances, statistics already initialized
		this.teamState = TeamStateFactory.instance(TeamState.TeamStateID
				.getEnumByValue(file.getInt("TeamStateId")));
		checkAttributes();
		file.parseSurroundingEndElement();
	}

	public void loadGam(GameLeagueFile file, NationPtrVector npv,
			PlayerPtrVector ppv) throws HockmanException {
		file.parseSurroundingStartElement("TeamGam");
		loadLgu(file, npv, ppv);
		this.home = false;
		this.training.load(file);
		this.tactics.load(file);
		this.finances.load(file);
		this.lineUp.load(file, ppv); // init by default ctor
		this.messages.load(file);
		checkAttributes();
		file.parseSurroundingEndElement();
	}

	public void loadGamUpdate(GameLeagueFile file, TeamPtrDivVector tpdv,
			PlayerPtrVector ppv) {
		// load statistics/transfers of teams
		this.transfer.load(file, tpdv, ppv);
		this.statistics.load(file, tpdv);
	}

	/**
	 * Calculate whether this team has not yet been knocked out in the playoffs
	 * this season. Note: this also could return true, if this team has not
	 * qualified for playoffs!
	 * @return true if this team has not yet been knocked out
	 */
	public boolean notYetKnockedOutInPlayoff() {
		return getStatistics().getPlayoffBeatenBy() == null || getStatistics().isInPlayoff();
	}
}
