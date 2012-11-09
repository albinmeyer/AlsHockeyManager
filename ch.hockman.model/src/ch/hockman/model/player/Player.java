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

package ch.hockman.model.player;

import java.util.BitSet;

import ch.hockman.model.Game;
import ch.hockman.model.GameLeagueFile;
import ch.hockman.model.League;
import ch.hockman.model.Tactics;
import ch.hockman.model.Training;
import ch.hockman.model.character.Aggressor;
import ch.hockman.model.character.Ambitiousman;
import ch.hockman.model.character.CharacterFactory;
import ch.hockman.model.character.Extrovert;
import ch.hockman.model.character.Funnyman;
import ch.hockman.model.character.Introvert;
import ch.hockman.model.character.Leader;
import ch.hockman.model.character.Moneymaker;
import ch.hockman.model.character.PlayerCharacter;
import ch.hockman.model.character.Teamworker;
import ch.hockman.model.common.HockmanException;
import ch.hockman.model.common.Util;
import ch.hockman.model.position.Position;
import ch.hockman.model.position.PositionFactory;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;

/**
 * This class describes an icehockey player.
 * Usage:
 * 1. default Ctor is called
 * 2. if loaded from lgu/gam file: LoadLgu/LoadGam MUST be called
 * if created by league editor: SetPlayer MUST be called
 * LoadLgu/LoadGam/SetPlayer must assign a nation to the player
 * 3. if loaded from lgu/gam file: UpdateContrTeams MUST be called
 *
 * Invariants:
 * „number“ must not be equal to another player’s number in same team
 * energy, form, ... must be between 0 and 99
 * this is checked by GUI + CheckAttributes()
 * 
 * @author Albin
 *
 */
public class Player {

	public enum MatchPlayerEvent {
		NOTHING, ONICE, GOAL_RECEIVED, GOAL_SHOT, SAVE, ASSIST
	};
	
	static final int PLR_MAXNOF = 4000;
	// if modified, modify TEAM_MAXNOFTEAMINDIV and/or LGU_DIVISION_NUMBERS too !!
	
	static final int PLR_MAXHEIGHT = 250;
	static final int PLR_MAXWEIGHT = 120;
	static final int PLR_MAXWAGE = 100;
	static final int PLR_MAXFEE = 10000;
	static final int PLR_DEFAULT_NUMBER = 50;
	static final int PLR_BIRTHDAY = 5;
	static final int PLR_BIRTHMONTH = 7;
	static final int PLR_BIRTHYEAR = 1970;

	private static final String L_PLAYERFIRSTNAME = "Al";
	private static final String L_PLAYERLASTNAME = "Meyer";
	private static final String L_PLAYERPERSONAL = "-";
	private static final String L_PLAYERCAREER = "-";
	private static final String L_SIGNED_AT = " signed at ";
	private static final String L_OF = " of ";
	private static final String L_BECOMES_FREE_AGENT = " becomes free agent.";
	private static final String L_RETIRES = " retires.";

	static BitSet idBar; // needed for generating ids

	private boolean flag; // used for multiple purposes, e.g. LineUpAI
	private boolean minFee;
	boolean updatedAfterSeason;
	private boolean multipleName;
	private int id; // needed for load/save
	private String picPath;
	private String firstName;
	private String lastName;
	private String personal;
	private String career;
	private Birthday birthday;
	private int age;
	private Position pos;
	private int number;
	private Nation nation;
	private int natGames;
	private int height;
	private int weight;
	private PlayerCharacter character;
	private int energy, form;
	private int shooting, stamina, skill, passing, power, offense, defense;
	private int goals, assists_save; // different meaning for offenders/defenders and goalie
	private int matchesPlayed;
	private int bestPlayerPoints; // needed for calculating best player of a match
	private int penalty, plus_minus;
	private Health health; // mutable
	private int formTendency;
	private Motivation motivation; // mutable
	private Contracts contracts; // mutable
	private int remainingPenSec;

	public Player(int year) {
		this.motivation = new Motivation(this);
		this.contracts = new Contracts();
		this.health = new Health();
		createNewId();
		this.firstName = L_PLAYERFIRSTNAME;
		this.lastName = L_PLAYERLASTNAME;
		this.personal = L_PLAYERPERSONAL;
		this.career = L_PLAYERCAREER;
		this.picPath = "";
		this.age = year; // misusing age temporary to store the current year
		this.pos = PositionFactory.instance(Position.PosID.CENTER);
		this.number = PLR_DEFAULT_NUMBER;
		this.nation = null;
		this.natGames = 0;
		this.height = 190;
		this.weight = 80;
		this.character = CharacterFactory
				.instance(PlayerCharacter.CharID.TEAMWORKER);
		this.shooting = PLR_DEFAULT_NUMBER;
		this.stamina = PLR_DEFAULT_NUMBER;
		this.skill = PLR_DEFAULT_NUMBER;
		this.passing = PLR_DEFAULT_NUMBER;
		this.power = PLR_DEFAULT_NUMBER;
		this.offense = PLR_DEFAULT_NUMBER;
		this.defense = PLR_DEFAULT_NUMBER;
		this.energy = PLR_DEFAULT_NUMBER;
		this.form = PLR_DEFAULT_NUMBER;
		this.goals = 0;
		this.assists_save = 0;
		this.matchesPlayed = 0;
		this.bestPlayerPoints = 0;
		this.penalty = 0;
		this.plus_minus = 0;
		this.formTendency = Util.random(2);
		this.birthday =  new Birthday();
		setFlag(false);
		setMinFee(false);
		updatedAfterSeason = false;
		setMultipleName(false);
		remainingPenSec = 0;
	}

	public static void initIdBitSet() {
		idBar = new BitSet(PLR_MAXNOF); // needed for generating ids
	}
	
	public void saveGam(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("PlayerGam");
		// league stuff
		saveLgu(xmlFile);
		// game stuff
		xmlFile.write("Energy", this.energy);
		xmlFile.write("Form", this.form);
		xmlFile.write("Goals", this.goals);
		xmlFile.write("AssistsSave", this.assists_save);
		xmlFile.write("MatchesPlayed", this.matchesPlayed);
		xmlFile.write("Penalty", this.penalty);
		xmlFile.write("PlusMinus", this.plus_minus);
		xmlFile.write("Injury", this.health.getInjury());
		xmlFile.write("Hurt", this.health.getHurt());
		xmlFile.write("FormTendency", this.formTendency);
		Team team = this.contracts.getNextContr().getTeam();
		if (team != null) {
			xmlFile.write("NextContrTeamId", team.getId());
		} else {
			xmlFile.write("NextContrTeamId", -1);
		}
		xmlFile.write("NextContrYears", this.contracts.getNextContrYears());
		xmlFile.write("CurrContrWage", this.contracts.getCurrContrWage());
		xmlFile.write("NextContrWage", this.contracts.getNextContrWage());
		xmlFile.write("NoContrThisRound", this.contracts.isNoContractThisRound());
		motivation.saveGam(xmlFile);
		xmlFile.writeSurroundingEndElement(); // PlayerGam
	}

	public void saveRookieLgu(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("RookiePlayer");
		xmlFile.write("Id", id);
		xmlFile.write("FirstName", firstName);
		xmlFile.write("LastName", lastName);
		xmlFile.writeSurroundingEndElement(); // RookiePlayer		
	}
	
	public void saveLgu(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Player");
		xmlFile.write("Id", id);
		xmlFile.write("FirstName", firstName);
		xmlFile.write("LastName", lastName);
		xmlFile.write("Personal", this.personal);
		xmlFile.write("Career", this.career);
		xmlFile.write("PicPath", this.picPath);
		xmlFile.write("BirthdayDay", birthday.getDay());
		xmlFile.write("BirthdayMonth", birthday.getMonth());
		xmlFile.write("BirthdayYear", birthday.getYear());
		xmlFile.write("PosId", this.pos.getPosID().getValue(), "0=G, 1=D, 2=LW, 3=C, 4=RW");
		xmlFile.write("Number", this.number);
		xmlFile.write("NationId", this.nation.getId(), "Id referencing to a Nation element");
		xmlFile.write("NatGames", this.natGames);
		xmlFile.write("Height", this.height);
		xmlFile.write("Weight", this.weight);
		xmlFile.write("CharacterId", this.character.getCharID().getValue(), "0=Aggr, 1=Ambit, 2=Fun, 3=Lead, 4=Money, 5=Extr, 6=Intr, 7=Teamw");
		xmlFile.write("Shooting", this.shooting);
		xmlFile.write("Stamina", this.stamina);
		xmlFile.write("Skill", this.skill);
		xmlFile.write("Passing", this.passing);
		xmlFile.write("Power", this.power);
		xmlFile.write("Offense", this.offense);
		xmlFile.write("Defense", this.defense);
		if (this.contracts.getCurrContr().getTeam() != null) {
			xmlFile.write("CurrContrTeamId", this.contracts.getCurrContr().getTeam()
					.getId());
		} else {
			xmlFile.write("CurrContrTeamId", -1);
		}
		xmlFile.write("CurrContrYears", this.contracts.getCurrContrYears());
		xmlFile.writeSurroundingEndElement(); // Player
	}

	void checkAttributes() throws HockmanException {
		boolean throwIt = false; // could be several wrong GUI inputs
		// => correct them all => throw at very end !
		if (id < 0 || id > PLR_MAXNOF) {
			throwIt = true;
		}
		if (age > 99) {
			age = 20; // correct the wrong GUI input
			throwIt = true;
		}
		if (pos == null) {
			throwIt = true;
		}
		if (number < 1 || number > 99) {
			number = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		if (nation == null) {
			throwIt = true;
		}
		if (natGames > 1000) { // this cannot be !!
			natGames = 0; // correct the wrong GUI input
			throwIt = true;
		}
		if (height < 1 || height > PLR_MAXHEIGHT) {
			height = 190; // correct the wrong GUI input
			throwIt = true;
		}
		if (weight < 1 || weight > PLR_MAXWEIGHT) {
			weight = 80; // correct the wrong GUI input
			throwIt = true;
		}
		if (character == null) {
			throwIt = true;
		}
		if (shooting < 0 || shooting > 99) {
			shooting = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		if (stamina < 0 || stamina > 99) {
			stamina = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		if (skill < 0 || skill > 99) {
			skill = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		if (passing < 0 || passing > 99) {
			passing = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		if (power < 0 || power > 99) {
			power = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		if (offense < 0 || offense > 99) {
			offense = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		if (defense < 0 || defense > 99) {
			defense = PLR_DEFAULT_NUMBER; // correct the wrong GUI input
			throwIt = true;
		}
		// if(contracts.currContr.t) // cannot check this
		if (contracts.getCurrContrYears() < 0 || contracts.getCurrContrYears() > 99) {
			contracts.setCurrContrYears(1); // correct the wrong GUI input
			throwIt = true;
		}
		if (energy < 0 || energy > 99) {
			throwIt = true;
		}
		if (form < 0 || form > 99) {
			throwIt = true;
		}
		if (goals < 0) {
			throwIt = true;
		}
		if (assists_save < 0) {
			throwIt = true;
		}
		if (penalty < 0) {
			throwIt = true;
		}
		if (health.getInjury() < 0 || health.getInjury() > 99) {
			throwIt = true;
		}
		if (health.getHurt() < 0 || health.getHurt() > 99) {
			throwIt = true;
		}
		if (formTendency < 0 || formTendency > 1) {
			throwIt = true;
		}
		if (birthday.getDay() > 31 || birthday.getMonth() > 12 || birthday.getYear() > 3000) {
			// check value of birthday
			birthday.setDay(PLR_BIRTHDAY); // correct the wrong GUI input
			birthday.setMonth(PLR_BIRTHMONTH); // correct the wrong GUI input
			birthday.setYear(PLR_BIRTHYEAR); // correct the wrong GUI input
			throwIt = true;
		}
		if (contracts.getNextContr().getId() > 0 && contracts.getNextContrYears() == 0) {
			// if contract for next season, years must be > 0
			// actually should check id for >= 0, but nextContr.t could be NULL
			throwIt = true;
		}
		if (contracts.getNextContrYears() < 0 || contracts.getNextContrYears() > 99) {
			throwIt = true;
		}
		if (contracts.getCurrContrWage() < 0
				|| contracts.getCurrContrWage() > PLR_MAXWAGE) {
			throwIt = true;
		}
		if (contracts.getNextContrWage() < 0
				|| contracts.getNextContrWage() > PLR_MAXWAGE) {
			throwIt = true;
		}
		if (contracts.getFee() < 0 || contracts.getFee() > PLR_MAXFEE) {
			throwIt = true;
		}
		if (throwIt) {
			throw new HockmanException(
					"Invalid player attribute values provided for "
							+ this.getLastName());
		}
	}

	void setNation(Nation nation) {
		if (this.nation != null) {
			this.nation.decreaseReferences();
		}
		assert (nation != null); // must set a nation
		this.nation = nation;
		nation.increaseReferences();
	}

	public void setNumber(int i) {
		this.number = i;
	}

	public void updateContrTeams(ch.hockman.model.team.TeamPtrDivVector tpdv) {
		contracts.setCurrContr(new Contracts.TContract(
				tpdv.getIdTeam(contracts.getCurrContr().getId())));
		contracts.setNextContr(new Contracts.TContract(
				tpdv.getIdTeam(contracts.getNextContr().getId())));
	}

	public void updateContrTeams(Team team) {
		contracts.setCurrContr(new Contracts.TContract(team));
		contracts.setNextContr(new Contracts.TContract(null));
	}

	void setName(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	void checkStrength() {
		if (this.energy > 99) {
			this.energy = 99;
		} else if (this.energy < 0) {
			this.energy = 0;
		}
		if (this.shooting > 99) {
			this.shooting = 99;
		} else if (this.shooting < 0) {
			this.shooting = 0;
		}
		if (this.stamina > 99) {
			this.stamina = 99;
		} else if (this.stamina < 0) {
			this.stamina = 0;
		}
		if (this.skill > 99) {
			this.skill = 99;
		} else if (this.skill < 0) {
			this.skill = 0;
		}
		if (this.passing > 99) {
			this.passing = 99;
		} else if (this.passing < 0) {
			this.passing = 0;
		}
		if (this.power > 99) {
			this.power = 99;
		} else if (this.power < 0) {
			this.power = 0;
		}
		if (this.offense > 99) {
			this.offense = 99;
		} else if (this.offense < 0) {
			this.offense = 0;
		}
		if (this.defense > 99) {
			this.defense = 99;
		} else if (this.defense < 0) {
			this.defense = 0;
		}
	}

	public void updateMatchSecond(MatchPlayerEvent mpe, int plusMinus) {
		// update energy, goals, assists, penalty, injury, hurt
		if (mpe == Player.MatchPlayerEvent.GOAL_SHOT) {
			bestPlayerPoints += 10;
			goals++;
		} else if (mpe == Player.MatchPlayerEvent.GOAL_RECEIVED) {
			bestPlayerPoints -= 1;
			goals++;
		} else if (mpe == Player.MatchPlayerEvent.SAVE) {
			bestPlayerPoints++;
			assists_save++;
		} else if (mpe == Player.MatchPlayerEvent.ASSIST) {
			bestPlayerPoints += 5;
			assists_save++;
		} else if (mpe == Player.MatchPlayerEvent.ONICE) {
			Team t = this.getContracts().getCurrContr().getTeam();
			Tactics.Effort effort = t.getTactics().getEffort();
			int intensity;
			if (effort == Tactics.Effort.FULL) {
				intensity = 3;
			} else if (effort == Tactics.Effort.NORMAL_EFF) {
				intensity = 2;
			} else {
				assert (effort == Tactics.Effort.EASY);
				intensity = 1;
			}
			if (Util.random((intensity << 6) + 2 + this.stamina) / 3 == 0) {
				// (intensity*60+10+average of stamina)/3==average 60 (seconds)
				// where intensity has average 2
				// => average one minute on ice => decrease energy
				if (this.getPosition().getPosID() == Position.PosID.GOALIE) {
					// Goalies are 3 times as much on ice as normal players
					if (Util.random(3) == 0) {
						energy--;
					}
				} else {
					energy--;
				}
				if (energy < 0)
					energy = 0;
			}
		} else {
			assert (mpe == Player.MatchPlayerEvent.NOTHING);
		}
		plus_minus += plusMinus;
		bestPlayerPoints += plusMinus * 2;

	}

	public void updateDay(boolean tournament) {
		// update motivation, strengths, injuries, form, wage, fee
		getContracts().setNoContractThisRound(false);
		calcFee();
		getHealth().recoveryDay();
		if (Util.random(10) != 0) {
			formTendency = Util.random(2);
		}
		if (formTendency == 0) {
			if (form > 0) {
				form--;
			}
		} else if (form < 99) {
			form++;
		}
		remainingPenSec = 0;
		bestPlayerPoints = 0;

		Team team = this.getContracts().getCurrContr().getTeam();
		if (team != null) {
			int tshooting = team.getTraining().getShooting();
			int tstamina = team.getTraining().getStamina();
			int tskill = team.getTraining().getSkill();
			int tpassing = team.getTraining().getPassing();
			int tpower = team.getTraining().getPower();
			int toffensive = team.getTraining().getOffensive();
			int tdefensive = team.getTraining().getDefensive();
			int tmental = team.getTraining().getMental();
			Training.Effort teffort = team.getTraining().getEffort();

			if (teffort == Training.Effort.FULL) {
				this.shooting += tshooting - Training.TRAIN_DEFAULT;
				this.stamina += tstamina - Training.TRAIN_DEFAULT;
				this.skill += tskill - Training.TRAIN_DEFAULT;
				this.passing += tpassing - Training.TRAIN_DEFAULT;
				this.power += tpower - Training.TRAIN_DEFAULT;
				this.offense += toffensive - Training.TRAIN_DEFAULT;
				this.defense += tdefensive - Training.TRAIN_DEFAULT;
			} else if (teffort == Training.Effort.NORMAL) {
				this.shooting += tshooting - Training.TRAIN_DEFAULT
						- Util.random(2);
				this.stamina += tstamina - Training.TRAIN_DEFAULT
						- Util.random(2);
				this.skill += tskill - Training.TRAIN_DEFAULT - Util.random(2);
				this.passing += tpassing - Training.TRAIN_DEFAULT
						- Util.random(2);
				this.power += tpower - Training.TRAIN_DEFAULT - Util.random(2);
				this.offense += toffensive - Training.TRAIN_DEFAULT
						- Util.random(2);
				this.defense += tdefensive - Training.TRAIN_DEFAULT
						- Util.random(2);
			} else {
				assert (teffort == Training.Effort.EASY);
				this.shooting += tshooting - Training.TRAIN_DEFAULT - 1;
				this.stamina += tstamina - Training.TRAIN_DEFAULT - 1;
				this.skill += tskill - Training.TRAIN_DEFAULT - 1;
				this.passing += tpassing - Training.TRAIN_DEFAULT - 1;
				this.power += tpower - Training.TRAIN_DEFAULT - 1;
				this.offense += toffensive - Training.TRAIN_DEFAULT - 1;
				this.defense += tdefensive - Training.TRAIN_DEFAULT - 1;
			}

			this.getMotivation().calculate(this,
					tmental - Training.TRAIN_DEFAULT - Util.random(2),
					tournament);

			if (teffort == Training.Effort.FULL) {
				this.energy += 10;
			} else if (teffort == Training.Effort.NORMAL) {
				this.energy += 20;
			} else {
				assert (teffort == Training.Effort.EASY);
				this.energy += 30;
			}
			if (Util.random(55 - this.age) == 0) {
				// old players don't recover that much
				this.energy -= 10;
			}
			this.checkStrength();
		}
	}

	public void updateCareer(String s) {
		career += "\n";
		career += s; // history updating
	}

	public void decreaseMatchEnergy() {
		if (getHealth().getInjury() == 0) {
			if (getHealth().getHurt() == 0) {
				energy -= 16 + Util.random(5);
			} else {
				energy -= 6 + Util.random(5);
			}
		}
		if (energy < 0) {
			energy = 1;
		}
	}

	public void initSeason(int year) {
		// init points and age
		updatedAfterSeason = false;
		goals = 0;
		assists_save = 0;
		matchesPlayed = 0;
		penalty = 0;
		plus_minus = 0;
		age = year - birthday.getYear();
		energy = PLR_DEFAULT_NUMBER - Util.random(PLR_DEFAULT_NUMBER / 2);
		form = PLR_DEFAULT_NUMBER - Util.random(PLR_DEFAULT_NUMBER / 2)
				+ Util.random(PLR_DEFAULT_NUMBER / 2);
		setMinFee(false);
		motivation.reset(this);
	}

	public boolean updateAfterSeason(Game game) {
		// do transfers
		// update abilities and history
		if (!updatedAfterSeason) {
			Contracts contracts = this.getContracts();
			Team oldTeam = contracts.getCurrContr().getTeam();
			if (contracts.getNextContr().getTeam() != null) {
				// go to new team (could be same team)
				Team newTeam = contracts.getNextContr().getTeam();
				if (!newTeam.equals(oldTeam)) {
					if (oldTeam != null) {
						updateCareer(oldTeam.getTeamName());
						// remove offers to this player						
						oldTeam.getTransfer().rejectAllOffersOfPlayer(this);
						oldTeam.removePlayer(this);
						if (oldTeam.getNofTeamPlayers() < Team.TEAM_MAXTEAMPLAYER
								&& oldTeam.getNofFarmPlayers() > 0) {
							// avoid temporary overfilled farm as much as
							// possible
							Player temp = oldTeam.getFarmPlayer(0);
							oldTeam.removeFarmPlayer(temp);
							oldTeam.addTeamPlayer(temp);
						}
					}
					newTeam.addPlayer(this);
					String s = game.getNews().getTransfers(); // show all transfers
					// at end of season
					s += this.getLastName();
					if (oldTeam != null) {
						s += Util.getModelResourceBundle().getString("L_OF");
						s += oldTeam.getTeamName();
					}
					s += Util.getModelResourceBundle().getString("L_SIGNED_AT");
					s += newTeam.getTeamName();
					s += '\n';
					game.getNews().setTransfers(s);
				}
				contracts.setCurrContr(new Contracts.TContract(newTeam));
				assert (contracts.getNextContrYears() > 0);
				contracts.setCurrContrYears(contracts.getNextContrYears());
				contracts.setCurrContrWage(contracts.getNextContrWage());
				
				contracts.setNextContr(new Contracts.IdContract(-1));
				// TODO correct for no next contract? or better like this:
				//contracts.setNextContr(new Contracts.TContract(null));
				
				contracts.setNextContrYears(0);
				contracts.setNextContrWage(0);
				// finances are already payed at contract sign
			} else if (contracts.getCurrContrYears() <= 1) {
				// no contract for next season
				if (oldTeam != null) {
					// was in a team this season
					assert (contracts.getCurrContrYears() == 1);
					updateCareer(oldTeam.getTeamName());
					
					contracts.setCurrContr(new Contracts.IdContract(-1));
					// TODO correct for no curr contract? or better like this:
					//contracts.setCurrContr(new Contracts.TContract(null));
					
					
					contracts.setCurrContrYears(0);
					contracts.setCurrContrWage(0);
					oldTeam.getTransfer().rejectAllOffersOfPlayer(this); // remove offers to this player
					oldTeam.removePlayer(this);
					if (oldTeam.getNofTeamPlayers() < Team.TEAM_MAXTEAMPLAYER
							&& oldTeam.getNofFarmPlayers() > 0) {
						// avoid temporary overfilled farm as much as possible
						Player temp = oldTeam.getFarmPlayer(0);
						oldTeam.removeFarmPlayer(temp);
						oldTeam.addTeamPlayer(temp);
					}
					// go to free agent list
					this.calcFee(false); // non minimum fee (otherwise non-0
					// fee!)
					String s = game.getNews().getTransfers(); // show all transfers
					// at end of season
					s += this.getLastName();
					s += Util.getModelResourceBundle().getString("L_OF");
					s += oldTeam.getTeamName();
					s += Util.getModelResourceBundle().getString("L_BECOMES_FREE_AGENT");
					s += "\n";
					game.getNews().setTransfers(s);
				}
				if (age > 25 && Util.random(5) == 0 || age > 30
						&& Util.random(4) != 0) {

					// NYI
					// better calc about retiring players

					League league = game.getLeague();
					// get team with smallest nof rookie players
					Team team = null;
					int minRookies = Team.TEAM_MAXROOKIEPLAYER;
					int nofDiv = league.getModus().getNofDivisions();
					TeamPtrDivVector tpdv = league.getTeamDivVector();
					for (int div = 1; div <= nofDiv + 1; div++) {
						int nofTeams = tpdv.getNofTeams(div);
						for (int i = 0; i < nofTeams; i++) {
							Team scanTeam = tpdv.getTeam(div, i);
							int nofRookies = scanTeam.getNofRookiePlayers();
							if (nofRookies < minRookies
									&& scanTeam.getNation() == this.getNation()) {
								minRookies = nofRookies;
								team = scanTeam;
							}
						}
					}
					if (team == null) {
						// no team with nation of player is available
						// => insert anywhere
						for (int div = 1; div <= nofDiv + 1; div++) {
							int nofTeams = tpdv.getNofTeams(div);
							for (int i = 0; i < nofTeams; i++) {
								Team scanTeam = tpdv.getTeam(div, i);
								int nofRookies = scanTeam.getNofRookiePlayers();
								if (nofRookies < minRookies) {
									minRookies = nofRookies;
									team = scanTeam;
								}
							}
						}
					}
					if (team != null) {
						// retire
						String s = game.getNews().getTransfers(); // show all
						// transfers at end
						// of season
						s += this.getLastName();
						s += Util.getModelResourceBundle().getString("L_RETIRES");
						s += "\n";
						game.getNews().setTransfers(s);
						picPath = "";
						lastName = lastName + " Jr";
						career = "-";
						personal = "-";
						team.addRookiePlayer(this);
						league.getPlayerVector().RemovePlayer(this);
					}
				}
			} else {
				assert (contracts.getCurrContrYears() > 1);
				contracts.setCurrContrYears(contracts.getCurrContrYears() - 1);
			}
			updatedAfterSeason = true;
			this.getHealth().setInjury(0); // heal injuries/hurtings after
			// season
			this.getHealth().setHurt(0); // heal injuries/hurtings after season

			// summer training
			if (this.age < 25 || Util.random(this.age) == 0) {
				// young players get more
				int progress = Util.random(3) + 1; // 1..3
				this.shooting += progress * Util.random(3);
				this.stamina += progress * Util.random(3);
				this.skill += progress * Util.random(3);
				this.passing += progress * Util.random(3);
				this.power += progress * Util.random(3);
				this.offense += progress * Util.random(3);
				this.defense += progress * Util.random(3);
			} else if (this.age > 30 || Util.random(55 - this.age) == 0) {
				// assumed average age is 27.5
				// old players loose
				int regress = Util.random(3) + 1; // 1..3
				this.shooting -= regress * Util.random(3);
				this.stamina -= regress * Util.random(3);
				this.skill -= regress * Util.random(3);
				this.passing -= regress * Util.random(3);
				this.power -= regress * Util.random(3);
				this.offense -= regress * Util.random(3);
				this.defense -= regress * Util.random(3);
			} else {
				// other players vary
				if (Util.random(2) > 0) {
					this.shooting += Util.random(3);
				} else {
					this.shooting -= Util.random(3);
				}
				if (Util.random(2) > 0) {
					this.stamina += Util.random(3);
				} else {
					this.stamina -= Util.random(3);
				}
				if (Util.random(2) > 0) {
					this.skill += Util.random(3);
				} else {
					this.skill -= Util.random(3);
				}
				if (Util.random(2) > 0) {
					this.passing += Util.random(3);
				} else {
					this.passing -= Util.random(3);
				}
				if (Util.random(2) > 0) {
					this.power += Util.random(3);
				} else {
					this.power -= Util.random(3);
				}
				if (Util.random(2) > 0) {
					this.offense += Util.random(3);
				} else {
					this.offense -= Util.random(3);
				}
				if (Util.random(2) > 0) {
					this.defense += Util.random(3);
				} else {
					this.defense -= Util.random(3);
				}
			}
			this.checkStrength();
			return true;
		} else {
			return false;
		}
	}

	public void incNofMatches() {
		matchesPlayed++;
	}

	public void incNofNatMatches() {
		natGames++;
	}

	int getBestPlayerPoints() {
		return bestPlayerPoints;
	}

	public void setPenMin(int min) {
		bestPlayerPoints -= min;
		remainingPenSec = 60 * min;
		// don't increment penalty here, because players can go to pen box for
		// other players (goalie)
	}

	public void incPenalty(int min) {
		penalty += min;
	}

	public int getPenSec() {
		return remainingPenSec;
	}

	public void decPenSec() {
		assert (remainingPenSec > 0);
		remainingPenSec--;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getPersonal() {
		return this.personal;
	}

	public String getCareer() {
		return this.career;
	}

	public String getPicPath() {
		return this.picPath;
	}

	public Birthday getBirthday() {
		return this.birthday;
	}

	public int getAge() {
		return this.age;
	}

	public Position getPosition() {
		return this.pos;
	}

	public int getNumber() {
		return this.number;
	}

	public Nation getNation() {
		return this.nation;
	}

	public int getNatGames() {
		return this.natGames;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWeight() {
		return this.weight;
	}

	public PlayerCharacter getCharacter() {
		return this.character;
	}

	public int getEnergy() {
		return this.energy;
	}

	public int getForm() {
		return this.form;
	}

	public int getShooting() {
		return this.shooting;
	}

	public int getStamina() {
		return this.stamina;
	}

	public int getSkill() {
		return this.skill;
	}

	public int getPassing() {
		return this.passing;
	}

	public int getPower() {
		return this.power;
	}

	public int getOffense() {
		return this.offense;
	}

	public int getDefense() {
		return this.defense;
	}

	public int getTotalStrength() {
		// WITHOUT motivation, WITH energy
		int strength = getTotalStrengthWithoutHealth();
		if (getHealth().getInjury() > 0) {
			// injured
			strength = 0;
		}
		if (getHealth().getHurt() > 0) {
			// hurt
			strength = (int) ((float) strength * Health.HEALTH_HURT_FACTOR);
		}
		return strength;
	}

	public int getTotalStrengthWithMot() {
		// strength WITH motivation, WITH energy
		int strength = getTotalStrength();
		Motivation.MotivationValue mv = getMotivation().getMotVal();
		if (mv == Motivation.MotivationValue.GREAT) {
			strength += 2;
		} else if (mv == Motivation.MotivationValue.GOOD) {
			strength++;
		} else if (mv == Motivation.MotivationValue.OK) {
			// do nothing
		} else if (mv == Motivation.MotivationValue.UNHAPPY) {
			strength--;
		} else {
			assert (mv == Motivation.MotivationValue.LOW);
			strength -= 2;
		}
		return strength;
	}

	int getTotalStrengthWithoutEnergy() {
		// strength WITHOUT energy, WITHOUT motivation
		int strength = getTotalStrengthWithoutEnergyWithoutHealth();
		if (getHealth().getInjury() > 0) {
			// injured
			strength = 0;
		}
		if (getHealth().getHurt() > 0) {
			// hurt
			strength = (int) ((float) strength * Health.HEALTH_HURT_FACTOR);
		}
		return strength;
	}

	public int getTotalStrengthWithoutEnergyWithoutHealth() {
		int strength = (form + shooting + stamina + skill + passing + power
				+ offense + defense) / 8;
		return strength;
	}

	public int getTotalStrengthWithoutHealth() {
		int strength = (energy + form + shooting + stamina + skill + passing
				+ power + offense + defense) / 9;
		return strength;
	}

	public int getGoals() {
		return this.goals;
	}

	public int getAssists() {
		return this.assists_save;
	}

	public int getMatchesPlayed() {
		return this.matchesPlayed;
	}

	public int getPenalty() {
		return this.penalty;
	}

	public int getPlusMinus() {
		return this.plus_minus;
	}

	public Health getHealth() {
		return this.health;
	}

	public int getWage() {
		return contracts.getCurrContrWage();
	}

	public void calcFee() {
		int baseFee = 10 * (getTotalStrengthWithoutEnergyWithoutHealth() - getAge());
		if (!isMinFee()) {
			contracts.setFee(contracts.getCurrContrYears() * baseFee);
		} else {
			contracts.setFee(baseFee);
		}
		if (contracts.getFee() < 0) {
			contracts.setFee(0);
		}
	}

	public void calcFee(boolean min) {
		this.setMinFee(min);
		calcFee();
	}

	public int getFee() {
		return this.contracts.getFee();
	}

	int getFormTendency() {
		return this.formTendency;
	}

	public Motivation getMotivation() {
		return this.motivation;
	}

	public Contracts getContracts() {
		return this.contracts;
	}

	public int getId() {
		return this.id;
	}

	public int getInitWage() {
		// for init game and contract negotiations wage base
		float exp = (float) getTotalStrengthWithoutEnergyWithoutHealth() / 10;
		// exp= 0..9.9
		int wage = (int) (Math.pow(2, exp) / (double) 15) + 1;
		return wage;
	}

	public void calcYouthAttributes(Team team, Position pos, int year,
			int nofRounds, int saved) {
		// calc attributes of rookie player according to spent youth
		// money
		if(this.nation != null) {
			this.nation.decreaseReferences();
		}
		this.nation = team.getNation();
		this.nation.increaseReferences();
		this.getContracts().setCurrContr(new Contracts.TContract(team));
		this.getContracts().setCurrContrYears(2 + Util.random(3));
		this.getContracts().setCurrContrWage(getInitWage()); 
		this.natGames = 0;
		this.age = 21 - Util.random(4);
		this.birthday.setYear(year - this.age);
		this.birthday.setMonth(Util.random(12) + 1);
		this.birthday.setDay(Util.random(28) + 1);
		this.pos = pos;
		this.height = 170 + Util.random(32);
		this.weight = 70 + Util.random(32);

		int ran = Util.random(8);
		switch (ran) {
		case 0:
			this.character = Aggressor.instance();
			break;
		case 1:
			this.character = Ambitiousman.instance();
			break;
		case 2:
			this.character = Funnyman.instance();
			break;
		case 3:
			this.character = Leader.instance();
			break;
		case 4:
			this.character = Moneymaker.instance();
			break;
		case 5:
			this.character = Extrovert.instance();
			break;
		case 6:
			this.character = Introvert.instance();
			break;
		case 7:
			this.character = Teamworker.instance();
			break;
		default:
			assert (false);
		}

		int base = saved / nofRounds;
		if (base > 1) {
			base = (int) (43.6 * Math.log10(base)); // logarithmic calc +
			// empirical factor
		}
		base = base - (base - 50) / 2; // weaker rookies
		if (Util.random(2) == 0) { // some rookies are weaker than others
			this.shooting = base + Util.random(20) - Util.random(10);
			this.stamina = base + Util.random(20) - Util.random(10);
			this.skill = base + Util.random(20) - Util.random(10);
			this.passing = base + Util.random(20) - Util.random(10);
			this.power = base + Util.random(20) - Util.random(10);
			this.offense = base + Util.random(20) - Util.random(10);
			this.defense = base + Util.random(20) - Util.random(10);
		} else {
			this.shooting = base + Util.random(10) - Util.random(20);
			this.stamina = base + Util.random(10) - Util.random(20);
			this.skill = base + Util.random(10) - Util.random(20);
			this.passing = base + Util.random(10) - Util.random(20);
			this.power = base + Util.random(10) - Util.random(20);
			this.offense = base + Util.random(10) - Util.random(20);
			this.defense = base + Util.random(10) - Util.random(20);
		}
		if (this.shooting > 99) {
			this.shooting = 99;
		} else if (this.shooting < 0) {
			this.shooting = 0;
		}
		if (this.stamina > 99) {
			this.stamina = 99;
		} else if (this.stamina < 0) {
			this.stamina = 0;
		}
		if (this.skill > 99) {
			this.skill = 99;
		} else if (this.skill < 0) {
			this.skill = 0;
		}
		if (this.passing > 99) {
			this.passing = 99;
		} else if (this.passing < 0) {
			this.passing = 0;
		}
		if (this.power > 99) {
			this.power = 99;
		} else if (this.power < 0) {
			this.power = 0;
		}
		if (this.offense > 99) {
			this.offense = 99;
		} else if (this.offense < 0) {
			this.offense = 0;
		}
		if (this.defense > 99) {
			this.defense = 99;
		} else if (this.defense < 0) {
			this.defense = 0;
		}
		this.calcFee();
	}

	private void setId(int i) throws HockmanException {
		assert (idBar.get(id)); // old id is used
		freeId();
		id = i;
		if (idBar.get(id)) {
			throw new HockmanException("Player id " + id + "already used.");
		}
		idBar.set(id);
	}

	private void createNewId() {
		id = 0;
		while (id < PLR_MAXNOF) {
			if (!idBar.get(id)) {
				idBar.set(id);
				break;
			}
			id++;
		}
		assert (id < PLR_MAXNOF); // must have breaked in "while" above
	}

	private void freeId() {
		idBar.clear(id);
	}

	public void loadGam(GameLeagueFile file, NationPtrVector npv) throws HockmanException {
		file.parseSurroundingStartElement("PlayerGam");
		// league stuff
		loadLgu(file, npv);
		// game stuff
		this.energy = file.getInt("Energy");
		this.form = file.getInt("Form");
		this.goals = file.getInt("Goals");
		this.assists_save = file.getInt("AssistsSave");
		this.matchesPlayed = file.getInt("MatchesPlayed");
		this.penalty = file.getInt("Penalty");
		this.plus_minus = file.getInt("PlusMinus");
		this.health.setInjury(file.getInt("Injury"));
		this.health.setHurt(file.getInt("Hurt"));
		this.formTendency = file.getInt("FormTendency");
		this.contracts.setNextContr(new Contracts.IdContract(
				file.getInt("NextContrTeamId")));
		this.contracts.setNextContrYears(file.getInt("NextContrYears"));
		this.contracts.setCurrContrWage(file.getInt("CurrContrWage"));
		this.contracts.setNextContrWage(file.getInt("NextContrWage"));
		this.contracts.setNoContractThisRound(file.getInt("NoContrThisRound") > 0);
		motivation.loadGam(file, this);
		checkAttributes();
		file.parseSurroundingEndElement();
	}
	
	public void loadRookieLgu(GameLeagueFile file, NationPtrVector npv) throws HockmanException {
		file.parseSurroundingStartElement("RookiePlayer");
		setId(file.getInt("Id"));
		String firstName = file.getString("FirstName");
		String lastName = file.getString("LastName");
		this.setName(firstName, lastName);		
		file.parseSurroundingEndElement();		
	}
	
	public void loadLgu(GameLeagueFile file, NationPtrVector npv) throws HockmanException {
		file.parseSurroundingStartElement("Player");
		setId(file.getInt("Id"));
		String firstName = file.getString("FirstName");
		String lastName = file.getString("LastName");
		this.setName(firstName, lastName);
		this.personal = file.getString("Personal");
		this.career = file.getString("Career");
		this.picPath = file.getString("PicPath");
		Birthday birthday = new Birthday();
		birthday.setDay(file.getInt("BirthdayDay"));
		birthday.setMonth(file.getInt("BirthdayMonth"));
		birthday.setYear(file.getInt("BirthdayYear"));
		this.birthday = birthday;
		this.age = this.age - birthday.getYear(); // age was misused for storing current year
		this.pos = PositionFactory.instance(Position.PosID.getEnumByValue(file
				.getInt("PosId")));
		this.number = file.getInt("Number");
		Nation nation = npv.getIdNation(file.getInt("NationId"));
		if (nation != null) {
			this.setNation(nation);
		} else {
			this.nation = null; // in error case, checked later in
			// CheckAttributes()
		}
		this.natGames = file.getInt("NatGames");
		this.height = file.getInt("Height");
		this.weight = file.getInt("Weight");
		this.character = CharacterFactory.instance(PlayerCharacter.CharID
				.getEnumByValue(file.getInt("CharacterId")));
		this.shooting = file.getInt("Shooting");
		this.stamina = file.getInt("Stamina");
		this.skill = file.getInt("Skill");
		this.passing = file.getInt("Passing");
		this.power = file.getInt("Power");
		this.offense = file.getInt("Offense");
		this.defense = file.getInt("Defense");
		this.contracts.setCurrContr(new Contracts.IdContract(
				file.getInt("CurrContrTeamId")));
		this.contracts.setCurrContrYears(file.getInt("CurrContrYears"));
		this.contracts.setCurrContrWage(getInitWage()); // calc wage at init game
		checkAttributes();
		file.parseSurroundingEndElement();
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isMinFee() {
		return minFee;
	}

	public void setMinFee(boolean minFee) {
		this.minFee = minFee;
	}

	public boolean isMultipleName() {
		return multipleName;
	}

	public void setMultipleName(boolean multipleName) {
		this.multipleName = multipleName;
	}
}
