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
import ch.hockman.model.player.Player;
import ch.hockman.model.team.CoachAI;
import ch.hockman.model.team.Team;

/**
 * The training of a team, including AI.
 * sum of training units must be 100 (checked by GUI).
 *
 * @author Albin
 *
 */
public class Training {

	public enum Effort {
		FULL(0), NORMAL(1), EASY(2);
		private int i;

		private Effort(int i) {
			this.i = i;
		}

		public int getValue() {
			return this.i;
		}

		public static Effort getEnumByValue(int value) {
			for (Effort test : Effort.values()) {
				if (test.getValue() == value) {
					return test;
				}
			}
			throw new IllegalArgumentException("No Effort available for value "
					+ value);
		}
	}

	public static final int TRAIN_DEFAULT = 12;
	
	private Effort effort;
	private int shooting;
	int stamina;
	int skill;
	int passing;
	private int power;
	int offensive;
	int defensive;
	int mental;

	public Training() {
		init();
		assert (shooting + stamina + skill + passing + power + offensive
				+ defensive + mental == 100);
	}

	void init() {
		setEffort(Effort.getEnumByValue(Util.random(3))); // FULL, NORMAL, EASY
		shooting = 12;
		stamina = 12;
		skill = 12;
		passing = 12;
		power = 12;
		offensive = 12;
		defensive = 12;
		mental = 12;
		int ran = Util.random(8);
		switch (ran) {
		case 0:
			shooting++;
			stamina++;
			skill++;
			passing++;
			break;
		case 1:
			stamina++;
			skill++;
			passing++;
			power++;
			break;
		case 2:
			skill++;
			passing++;
			power++;
			offensive++;
			break;
		case 3:
			passing++;
			power++;
			offensive++;
			defensive++;
			break;
		case 4:
			power++;
			offensive++;
			defensive++;
			mental++;
			break;
		case 5:
			offensive++;
			defensive++;
			mental++;
			shooting++;
			break;
		case 6:
			defensive++;
			mental++;
			shooting++;
			stamina++;
			break;
		case 7:
			mental++;
			shooting++;
			stamina++;
			skill++;
			break;
		default:
			assert (false);
		}
	}

	public void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Training");
		xmlFile.write("Shooting", shooting);
		xmlFile.write("Stamina", stamina);
		xmlFile.write("Skill", skill);
		xmlFile.write("Passing", passing);
		xmlFile.write("Power", power);
		xmlFile.write("Offensive", offensive);
		xmlFile.write("Defensive", defensive);
		xmlFile.write("Mental", mental);
		xmlFile.writeSurroundingEndElement();
	}

	public void trainingAI(Team thisTeam, CoachAI.Analysis analysis) {
		// opTeam may be NULL (if odd nof teams, or out of playoff)
		init();
		assert (shooting + stamina + skill + passing + power + offensive
				+ defensive + mental == 100);
		// NYI
		// improve this function according to lack of abilities of team players
	}

	public void doTraining(Team team) {
		// with injuries
		int nofPlayers = team.getNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = team.getPlayer(i);
			int ranRange;
			if (getEffort() == Effort.EASY) {
				ranRange = 230;
			} else if (getEffort() == Effort.NORMAL) {
				ranRange = 200;
			} else {
				assert (getEffort() == Effort.FULL);
				ranRange = 170;
			}
			if (player.getHealth().getInjury() == 0
					&& Util.random(ranRange) == 0) {
				if (Util.random(2) > 0) {
					// injury
					String s = Util.getModelResourceBundle().getString("L_INJURY_TRAINING");
					player.getHealth().setInjury();
					player.getHealth().setHurt(0);
					s += player.getLastName();
					team.getMessages().add(s);
				} else {
					// condition necessary for lineup injured players (!)
					// hurt
					player.getHealth().setHurt();
				}
			}
		}
	}

	public void setTraining(int shooting, int stamina, int skill, int passing,
			int power, int offensive, int defensive, int mental, Effort effort) {
		this.setEffort(effort);
		this.shooting = shooting;
		this.stamina = stamina;
		this.skill = skill;
		this.passing = passing;
		this.power = power;
		this.offensive = offensive;
		this.defensive = defensive;
		this.mental = mental;
	}

	public int getShooting() {
		return shooting;
	}

	public int getStamina() {
		return stamina;
	}

	public int getSkill() {
		return skill;
	}

	public int getPassing() {
		return passing;
	}

	public int getPower() {
		return power;
	}

	public int getOffensive() {
		return offensive;
	}

	public int getDefensive() {
		return defensive;
	}

	public int getMental() {
		return mental;
	}

	public Effort getEffort() {
		return effort;
	}

	public void load(GameLeagueFile file) {
		file.parseSurroundingStartElement("Training");
		shooting = file.getInt("Shooting");
		stamina = file.getInt("Stamina");
		skill = file.getInt("Skill");
		passing = file.getInt("Passing");
		power = file.getInt("Power");
		offensive = file.getInt("Offensive");
		defensive = file.getInt("Defensive");
		mental = file.getInt("Mental");
		file.parseSurroundingEndElement();
	}

	public void setEffort(Effort effort) {
		this.effort = effort;
	}
}
