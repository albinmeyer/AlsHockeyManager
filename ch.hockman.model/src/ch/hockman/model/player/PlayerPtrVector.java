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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.hockman.model.position.Position;

/**
 * A container of players.
 *
 * @author Albin
 *
 */
public class PlayerPtrVector {
	
	private List<Player> playerVector;

	public PlayerPtrVector() {
		playerVector = new ArrayList<Player>();
	}

	public void InsertPlayer(Player player) {
		if (player != null) {
			playerVector.add(player);
		}
	}

	public void RemovePlayer(Player player) {
		// remove, but not delete
		playerVector.remove(player);
	}

	public void RemoveAllPlayers() {
		while (GetNofPlayers() > 0) {
			RemovePlayer(GetPlayer(0));
		}
	}

	public int GetNofPlayers() {
		return playerVector.size();
	}

	public Player GetPlayer(int i) {
		if (GetNofPlayers() > i) {
			return playerVector.get(i);
		} else {
			return null;
		}
	}

	public Player GetIdPlayer(int id) {
		int i = 0;
		final int nofPla = GetNofPlayers();

		while (i < nofPla) {
			// as an alternative we could also iterate with an iterator !!
			if (playerVector.get(i).getId() == id) {
				return playerVector.get(i);
			}
			i++;
		}
		return null;
	}

	public void SortPlayerFirstName() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p1.getFirstName().compareTo(p2.getFirstName()));
	}

	public void SortPlayerLastName() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p1.getLastName().compareTo(p2.getLastName()));
	}

	public void SortPlayerPos() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p1.getPosition().getPosID()
						.compareTo(p2.getPosition().getPosID()));
	}

	public void SortPlayerNumber() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p1.getNumber() - p2.getNumber());
	}

	public void SortPlayerNation() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p1.getNation().getName()
						.compareTo(p2.getNation().getName()));
	}

	public void SortPlayerAge() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p1.getAge() - p2.getAge());
	}

	public void SortPlayerOwner() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				String teamName1 = p1.getContracts().getCurrContr().getTeamName();
				String teamName2 = p2.getContracts().getCurrContr().getTeamName();
				return teamName1.compareTo(teamName2);
			}
		);
	}

	public void SortPlayerId() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				int playerId1 = p1.getId();
				int playerId2 = p2.getId();
				return playerId1 - playerId2;
			}
		);
	}
	
	public void SortPlayerContract() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				return p1.getContracts().getCurrContrYears()
						- p2.getContracts().getCurrContrYears();
			}
		);
	}

	public void SortPlayerNextSeason() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				String teamName1 = p1.getContracts().getNextContr().getTeamName();
				String teamName2 = p2.getContracts().getNextContr().getTeamName();
				return teamName1.compareTo(teamName2);
			}
		);
	}

	public void SortPlayerFee() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				return p1.getFee() - p2.getFee();
			}
		);
	}

	public void SortPlayerWage() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				return p1.getWage() - p2.getWage();
			}
		);
	}

	public void SortPlayerInjury() {
		Collections.sort(playerVector, (Player p1, Player p2) -> 
				p1.getHealth().getInjury() - p2.getHealth().getInjury());
	}

	public void SortPlayerGoals() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				if (p1.getPosition().getPosID() == Position.PosID.GOALIE) {
					if (p2.getPosition().getPosID() == Position.PosID.GOALIE) {
						int shots = p1.getAssists() + p1.getGoals();
						float perc1 = (shots == 0 ? 0 : (100 - (float) 100
								/ shots * p1.getGoals()));
						shots = p2.getAssists() + p2.getGoals();
						float perc2 = (shots == 0 ? 0 : (100 - (float) 100
								/ shots * p2.getGoals()));
						if (perc1 > perc2) {
							return -1;
						} else if (perc2 > perc1) {
							return 1;
						}
						return 0;
					} else {
						return -1;
					}
				} else if (p2.getPosition().getPosID() == Position.PosID.GOALIE) {
					return 1;
				} else {
					return p2.getGoals() - p1.getGoals();
				}
			}
		);
	}

	public void SortPlayerAssists() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				if (p1.getPosition().getPosID() == Position.PosID.GOALIE) {
					if (p2.getPosition().getPosID() == Position.PosID.GOALIE) {
						int shots = p1.getAssists() + p1.getGoals();
						float perc1 = (shots == 0 ? 0 : (100 - (float) 100
								/ shots * p1.getGoals()));
						shots = p2.getAssists() + p2.getGoals();
						float perc2 = (shots == 0 ? 0 : (100 - (float) 100
								/ shots * p2.getGoals()));
						if (perc1 > perc2) {
							return -1;
						} else if (perc2 > perc1) {
							return 1;
						}
						return 0;
					} else {
						return -1;
					}
				} else if (p2.getPosition().getPosID() == Position.PosID.GOALIE) {
					return 1;
				} else {
					return p2.getAssists() - p1.getAssists();
				}
			}
		);
	}

	public void SortPlayerPoints() {
		Collections.sort(playerVector, (Player p1, Player p2) -> {
				if (p1.getPosition().getPosID() == Position.PosID.GOALIE) {
					if (p2.getPosition().getPosID() == Position.PosID.GOALIE) {
						int shots = p1.getAssists() + p1.getGoals();
						float perc1 = (shots == 0 ? 0 : (100 - (float) 100
								/ shots * p1.getGoals()));
						shots = p2.getAssists() + p2.getGoals();
						float perc2 = (shots == 0 ? 0 : (100 - (float) 100
								/ shots * p2.getGoals()));
						if (perc1 > perc2) {
							return -1;
						} else if (perc2 > perc1) {
							return 1;
						}
						return 0;
					} else {
						return -1;
					}
				} else if (p2.getPosition().getPosID() == Position.PosID.GOALIE) {
					return 1;
				} else {
					return p2.getAssists() + p2.getGoals() - p1.getAssists()
							- p1.getGoals();
				}
			}
		);
	}

	public void SortPlayerPlusMinus() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p2.getPlusMinus() - p1.getPlusMinus());
	}

	public void SortPlayerPenalty() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p2.getPenalty() - p1.getPenalty());
	}

	public void SortPlayerStrength() {
		Collections.sort(playerVector, (Player p1, Player p2) -> p2.getTotalStrength() - p1.getTotalStrength());
	}

	public void SortPlayerStrengthWithoutEnergy() {
		// for national team
		Collections.sort(playerVector, (Player p1, Player p2) -> p2.getTotalStrengthWithoutEnergy()
						- p1.getTotalStrengthWithoutEnergy());
	}

	public void SortPlayerWeakness() {
		// for lineup KI
		Collections.sort(playerVector, (Player p1, Player p2) -> p1.getTotalStrength() - p2.getTotalStrength());
	}

	public void SortPlayerBest() {
		// for match best player
		Collections.sort(playerVector, (Player p1, Player p2) -> p2.getBestPlayerPoints() - p1.getBestPlayerPoints());
	}
}