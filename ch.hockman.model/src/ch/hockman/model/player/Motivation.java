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

import ch.hockman.model.GameLeagueFile;
import ch.hockman.model.Statistics;
import ch.hockman.model.character.PlayerCharacter;
import ch.hockman.model.common.Util;
import ch.hockman.model.team.Team;

/**
 * The motivation of a player. Calculating
 * how motivated he is for playing. Depends on many factors.
 *
 * @author Albin
 *
 */
public class Motivation {
	private MotivationValue mv;
	private int refined_mv;
	private String reasonText;

	public enum MotivationValue {
		GREAT, GOOD, OK, UNHAPPY, LOW
	}

	Motivation(final Player player) {
		reset(player);
	}

	void saveGam(GameLeagueFile xmlFile) {
		xmlFile.write("RefinedMotivation", this.refined_mv);
	}

	public void calculate(final Player player, int inc, boolean tournament) {
		// set mv of Player
		reasonText = "";
		refined_mv += inc;
		Team team = player.getContracts().getCurrContr().getTeam();
		assert (team != null);
		// improved motivation according to contract / iceminutes (energy)
		Statistics stats = team.getStatistics();
		int vic = stats.getVic();
		int lost = stats.getLost();
		if (tournament) {
			// no contract checks
			if (vic > lost) {
				refined_mv++;
			} else {
				refined_mv--;
			}
		} else {
			if (player.getContracts().getNextContr().getTeam() == null
					&& player.getContracts().getCurrContrYears() == 1
					&& player.getEnergy() < 50 && player.getAge() < 32) {
				// no contract for next season && low energy (much icetime) =>
				// less motivated
				if (vic < lost) {
					if (Util.random(2) == 0) {
						refined_mv--;
						reasonText += Util.getModelResourceBundle().getString("L_NO_CONTRACT_NEXT");
					}
				} else if (Util.random(4) == 0) {
					refined_mv--;
					reasonText += Util.getModelResourceBundle().getString("L_NO_CONTRACT_NEXT");
				}
			} else if (player.getEnergy() > 90
					&& player.getHealth().getInjury() == 0
					&& player.getHealth().getHurt() == 0) {
				// much energy => few icetime => less motivated
				if (vic < lost) {
					if (Util.random(2) == 0) {
						refined_mv--;
						reasonText += Util.getModelResourceBundle().getString("L_WANT_MORE_ICETIME");
					}
				} else if (Util.random(4) == 0) {
					refined_mv--;
					reasonText += Util.getModelResourceBundle().getString("L_WANT_MORE_ICETIME");
				}
			} else {
				if (vic > lost) {
					if (Util.random(3) == 0) {
						refined_mv++;
					}
				} else if (Util.random(6) == 0) {
					refined_mv++;
				}
			}
		}
		if (team != null) {
			// motivation depends also on character types in whole team
			int nofPlayers = team.getNofPlayers();
			int nofAggressors = 0;
			int nofAmbitiousman = 0;
			int nofFunnyman = 0;
			int nofLeader = 0;
			int nofMoneymaker = 0;
			int nofExtrovert = 0;
			int nofIntrovert = 0;
			int nofTeamworker = 0;
			for (int i = 0; i < nofPlayers; i++) {
				PlayerCharacter.CharID charID = team.getPlayer(i)
						.getCharacter().getCharID();
				if (charID == PlayerCharacter.CharID.AGGRESSOR) {
					nofAggressors++;
				} else if (charID == PlayerCharacter.CharID.AMBITIOUSMAN) {
					nofAmbitiousman++;
				} else if (charID == PlayerCharacter.CharID.FUNNYMAN) {
					nofFunnyman++;
				} else if (charID == PlayerCharacter.CharID.LEADER) {
					nofLeader++;
				} else if (charID == PlayerCharacter.CharID.MONEYMAKER) {
					nofMoneymaker++;
				} else if (charID == PlayerCharacter.CharID.EXTROVERT) {
					nofExtrovert++;
				} else if (charID == PlayerCharacter.CharID.INTROVERT) {
					nofIntrovert++;
				} else if (charID == PlayerCharacter.CharID.TEAMWORKER) {
					nofTeamworker++;
				} else {
					assert (false);
				}
			}
			if (Util.random(5) == 0) {
				refined_mv++;
			}
			if (nofAggressors < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_AGGRESSORS");
			}
			if (nofAmbitiousman < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_AMBITIOUSMAN");
			}
			if (nofFunnyman < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_FUNNYMAN");
			}
			if (nofLeader < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_LEADER");
			}
			if (nofMoneymaker < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_MONEYMAKER");
			}
			if (nofExtrovert < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_EXTROVERT");
			}
			if (nofIntrovert < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_INTROVERT");
			}
			if (nofTeamworker < 2 && Util.random(5) == 0) {
				refined_mv--;
				reasonText += Util.getModelResourceBundle().getString("L_TOO_FEW_TEAMWORKER");
			}
		}
		if (Util.random(100) == 0) {
			// sometimes reset motivation (players are not always same
			// motivated !)
			refined_mv = 50 + Util.random(20) - Util.random(20);
		}
		calculate(player);

	}

	void calculate(final Player player) {
		if (refined_mv > 99) {
			refined_mv = 99;
		}
		if (refined_mv < 1) {
			refined_mv = 1;
		}
		if (refined_mv > 80) {
			mv = MotivationValue.GREAT;
		} else if (refined_mv > 60) {
			mv = MotivationValue.GOOD;
		} else if (refined_mv > 40) {
			mv = MotivationValue.OK;
		} else if (refined_mv > 20) {
			mv = MotivationValue.UNHAPPY;
		} else {
			mv = MotivationValue.LOW;
		}
	}

	public void reset(final Player player) {
		refined_mv = 50 + Util.random(20) - Util.random(20);
		calculate(player);
		reasonText = "";
	}

	public MotivationValue getMotVal() {
		return mv;
	}

	public String getReasonText() {
		String s;
		if (mv == MotivationValue.OK) {
			s = Util.getModelResourceBundle().getString("L_OK");
		} else if (mv == MotivationValue.UNHAPPY) {
			s = Util.getModelResourceBundle().getString("L_UNHAPPY");
		} else if (mv == MotivationValue.LOW) {
			s = Util.getModelResourceBundle().getString("L_LOW");
		} else if (mv == MotivationValue.GOOD) {
			s = Util.getModelResourceBundle().getString("L_GOOD");
		} else {
			assert (mv == MotivationValue.GREAT);
			s = Util.getModelResourceBundle().getString("L_GREAT");
		}
		return s + '\n' + reasonText;
	}

	public void loadGam(GameLeagueFile file, Player player) {
		refined_mv = file.getInt("RefinedMotivation");
		calculate(player);
	}

}
