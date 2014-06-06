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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import ch.hockman.model.character.PlayerCharacter;
import ch.hockman.model.common.Util;
import ch.hockman.model.match.News;
import ch.hockman.model.player.Contracts;
import ch.hockman.model.player.Motivation;
import ch.hockman.model.player.Player;
import ch.hockman.model.player.PlayerPtrVector;
import ch.hockman.model.position.Position;
import ch.hockman.model.team.Team;
import ch.hockman.model.team.TeamPtrDivVector;
import ch.hockman.model.team.TeamState;

/**
 * The transfer ai of a team.
 *
 * @author Albin
 *
 */
public class Transfer {

	// bits for Properties
	static final int NO_FOREIGNER_LIMIT = 0;
	static final int FOREIGNERS = 1;
	static final int YOUNGSTERS = 2;
	static final int OLDS = 3;
	static final int GOALIES = 4;
	static final int DEFENDERS = 5;
	static final int LEFTWINGS = 6;
	static final int CENTERS = 7;
	static final int RIGHTWINGS = 8;
	
	public static final int TRANS_IMMEDIATELY_FACTOR = 2;
	
	private class Properties {
		BitSet bitSet;

		Properties() {
			bitSet = new BitSet(RIGHTWINGS + 1);
		}
	}

	static private class Positions {
		int nofForeigners = 0;
		int goalies = 0;
		int defenders = 0;
		int leftwings = 0;
		int rightwings = 0;
		int center = 0;
		int age = 0;
	};

	public enum MakingOffer { // return value of MakeOffer()
		FAILED, // could not make offer
		SUCCEED, // offer in pending negotiation lists of buyer/seller
		ACCEPTED, // offer already accepted (free agents)
		ACCEPTED_AND_DONE // offer accepted and transfer done (immediately)
	}

	private int counter;
	private List<Offer> sellNeg;
	private List<Offer> buyNeg;

	public Transfer() {
		this.counter = 0;
		this.sellNeg = new ArrayList<Offer>();
		this.buyNeg = new ArrayList<Offer>();
	}

	public void save(GameLeagueFile xmlFile) {
		xmlFile.writeSurroundingStartElement("Transfer");
		int no = getNofSellNegOffers();
		xmlFile.write("NofSellNegOffers", no);
		for (int i = 0; i < no; i++) {
			Offer offer = getSellNegOffer(i);
			offer.save(xmlFile);
		}
		xmlFile.writeSurroundingEndElement();
	}

	public void transferAI(int nofTotalRounds, Modus.GameType gt,
			PlayerPtrVector allPlayers, Team team, News news,
			int maxNofForeigners) {
		checkContract(nofTotalRounds, team, allPlayers, news, maxNofForeigners,
				gt == Modus.GameType.TOURNAMENT);
		scanSellNeg(nofTotalRounds, allPlayers, team, news, maxNofForeigners);
		scanBuyNeg(team);
		checkBuying(allPlayers, team, nofTotalRounds,
				gt == Modus.GameType.TRADE, maxNofForeigners, news);
		checkSelling(team);
	}

	/**
	 * Called by CheckBuying() of own team, or by GUI; no money check.
	 * 
	 * @param offer
	 * @param allPlayers
	 * @param news
	 * @return
	 */
	public MakingOffer makeOffer(Offer offer, PlayerPtrVector allPlayers,
			News news) {
		// called by CheckBuying() of own team, or by GUI
		if (offer.getPlayer().getContracts().getNextContr().getTeam() != null
				|| offer.getBuyer().equals(offer.getPlayer().getContracts().getCurrContr()
						.getTeam())) {
			// has already a new contract or same team
			return MakingOffer.FAILED;
		}
		// unsigned
		int nofOffers = getNofBuyNegOffers();
		for (int i = 0; i < nofOffers; i++) {
			Offer alreadyOffer = getBuyNegOffer(i);
			if (offer.getPlayer().equals(alreadyOffer.getPlayer())) {
				// already a pending offer to same player
				return MakingOffer.FAILED;
			}
		}
		if (offer.getSeller() == null) {
			// free agent

			// NYI AI of the free agent

			if (Util.random(3) == 0
					&& buyerNofPlayers(offer, allPlayers) < Team.TEAM_MAXTEAMPLAYER
							+ Team.TEAM_MAXFARMPLAYER) {
				String s = news.getTransfers();
				s += offer.getPlayer().getLastName();
				s += Util.getModelResourceBundle().getString("L_SIGNS_AT");
				s += offer.getBuyer().getTeamName();
				if (offer.isImmediately()) {
					offer.getPlayer().getContracts().setCurrContr(new Contracts.TContract(
							offer.getBuyer()));
					offer.getPlayer().getContracts().setCurrContrYears(offer.getYears());
					offer.getPlayer().getContracts().setCurrContrWage(offer.getWage());
					offer.getPlayer().calcFee(false); // calc new fee after transferring
					offer.getBuyer().addPlayer(offer.getPlayer());
					s += Util.getModelResourceBundle().getString("L_IMMEDIATELY");
					s += "\n";
					news.setTransfers(s);
					return MakingOffer.ACCEPTED_AND_DONE;
				} else {
					offer.getPlayer().getContracts().setNextContr(new Contracts.TContract(
							offer.getBuyer()));
					offer.getPlayer().getContracts().setNextContrYears(offer.getYears());
					offer.getPlayer().getContracts().setNextContrWage(offer.getWage());
					s += Util.getModelResourceBundle().getString("L_FOR_NEXT_SEASON");
					s += "\n";
					news.setTransfers(s);
					return MakingOffer.ACCEPTED;
				}
			} else {
				offer.getPlayer().getContracts().setNoContractThisRound(true);
				return MakingOffer.FAILED;
			}
		} else {
			buyNeg.add(offer);
			offer.getSeller().getTransfer().sellNeg.add(offer);
			String s;
			s = offer.getBuyer().getTeamName();
			s += Util.getModelResourceBundle().getString("L_IS_INTERESTED_IN");
			if (offer.getPlayer().isMultipleName()) {
				s += offer.getPlayer().getFirstName();
				s += " ";
			}
			s += offer.getPlayer().getLastName();
			offer.getSeller().getMessages().add(s);
		}
		return MakingOffer.SUCCEED;
	}

	/**
	 * // called by ScanSellNeg() of own team, or by GUI
	 */
	public void rejectOffer(Offer offer) {
		sellNeg.remove(offer);
		offer.getBuyer().getTransfer().buyNeg.remove(offer);
		String s = new String();
		if (offer.getPlayer().isMultipleName()) {
			s += offer.getPlayer().getFirstName();
			s += " ";
		}
		s += offer.getPlayer().getLastName();
		s += " (";
		s += offer.getSeller().getTeamName();
		s += ") ";
		s += Util.getModelResourceBundle().getString("L_REJECTS_YOUR_OFFER");
		offer.getBuyer().getMessages().add(s);
	}

	/**
	 * called by ScanSellNeg() of own team, or by GUI; no money check
	 */
	public void acceptOffer(Offer offer, News news) {
		sellNeg.remove(offer);
		offer.getBuyer().getTransfer().buyNeg.remove(offer);
		if (offer.isImmediately()) {
			offer.getPlayer().setMinFee(false);
			offer.getPlayer().getMotivation().reset(offer.getPlayer());
			if (offer.getTrade() != null) {
				offer.getTrade().setMinFee(false);
				offer.getTrade().getMotivation().reset(offer.getTrade());
				assert (offer.getPlayer() != offer.getTrade());
				assert (offer.getBuyer() != offer.getSeller());
				offer.getSeller().removePlayer(offer.getPlayer());
				offer.getSeller().addPlayer(offer.getTrade());
				offer.getBuyer().addPlayer(offer.getPlayer());
				offer.getBuyer().removePlayer(offer.getTrade());
				offer.getPlayer().updateCareer(offer.getSeller().getTeamName());
				// update career info of imm.transfer
				offer.getTrade().updateCareer(offer.getBuyer().getTeamName());
				// update career info of imm.transfer
				offer.getPlayer().getContracts().setCurrContr(new Contracts.TContract(
						offer.getBuyer()));
				offer.getPlayer().getContracts().setCurrContrYears(1);
				offer.getTrade().getContracts().setCurrContr(new Contracts.TContract(
						offer.getSeller()));
				offer.getTrade().getContracts().setCurrContrYears(1);
				offer.getPlayer().calcFee(false); // calc new fee after transferring
				offer.getTrade().calcFee(false); // calc new fee after transferring
				String s = news.getTransfers();
				if (offer.getPlayer().isMultipleName()) {
					s += offer.getPlayer().getFirstName();
					s += " ";
				}
				s += offer.getPlayer().getLastName();
				s += Util.getModelResourceBundle().getString("L_OF");
				s += offer.getSeller().getTeamName();
				s += Util.getModelResourceBundle().getString("L_TRADES_WITH");
				if (offer.getTrade().isMultipleName()) {
					s += offer.getTrade().getFirstName();
					s += " ";
				}
				s += offer.getTrade().getLastName();
				s += Util.getModelResourceBundle().getString("L_OF");
				s += offer.getBuyer().getTeamName();
				s += Util.getModelResourceBundle().getString("L_IMMEDIATELY");
				s += "\n";
				news.setTransfers(s);
			} else {
				offer.getPlayer().getContracts().setCurrContr(new Contracts.TContract(
						offer.getBuyer()));
				offer.getPlayer().getContracts().setCurrContrYears(offer.getYears());
				offer.getPlayer().getContracts().setCurrContrWage(offer.getWage());
				offer.getPlayer().calcFee(false); // calc new fee after transferring
				offer.getSeller().removePlayer(offer.getPlayer());
				offer.getBuyer().addPlayer(offer.getPlayer());
				offer.getPlayer().updateCareer(offer.getSeller().getTeamName());
				// update career info of imm.transfer
				int fee = offer.getFee();
				offer.getSeller().getFinances().setTransfer(
						fee * TRANS_IMMEDIATELY_FACTOR, 0);
				offer.getBuyer().getFinances().setTransfer(0,
						fee * TRANS_IMMEDIATELY_FACTOR);
				String s = news.getTransfers();
				if (offer.getPlayer().isMultipleName()) {
					s += offer.getPlayer().getFirstName();
					s += " ";
				}
				s += offer.getPlayer().getLastName();
				s += Util.getModelResourceBundle().getString("L_OF");
				s += offer.getSeller().getTeamName();
				s += Util.getModelResourceBundle().getString("L_SIGNS_AT");
				s += offer.getBuyer().getTeamName();
				s += Util.getModelResourceBundle().getString("L_IMMEDIATELY");
				s += "\n";
				news.setTransfers(s);
			}
		} else {
			offer.getPlayer().getContracts().setNextContr(new Contracts.TContract(
					offer.getBuyer()));
			offer.getPlayer().getContracts().setNextContrYears(offer.getYears());
			offer.getPlayer().getContracts().setNextContrWage(offer.getWage());
			int fee = offer.getFee();
			offer.getSeller().getFinances().setTransfer(fee, 0);
			offer.getBuyer().getFinances().setTransfer(0, fee);
			String s = news.getTransfers();
			if (offer.getPlayer().isMultipleName()) {
				s += offer.getPlayer().getFirstName();
				s += " ";
			}
			s += offer.getPlayer().getLastName();
			s += Util.getModelResourceBundle().getString("L_OF");
			s += offer.getSeller().getTeamName();
			s += Util.getModelResourceBundle().getString("L_SIGNS_AT");
			s += offer.getBuyer().getTeamName();
			s += Util.getModelResourceBundle().getString("L_FOR_NEXT_SEASON");
			s += "\n";
			news.setTransfers(s);
		}
		String s = new String();
		if (offer.getPlayer().isMultipleName()) {
			s += offer.getPlayer().getFirstName();
			s += " ";
		}
		s += offer.getPlayer().getLastName();
		s += " (";
		s += offer.getSeller().getTeamName();
		s += ") ";
		s += Util.getModelResourceBundle().getString("L_ACCEPTS_YOUR_OFFER");
		if (offer.isImmediately()) {
			s += Util.getModelResourceBundle().getString("L_IMMEDIATELY");
		} else {
			s += Util.getModelResourceBundle().getString("L_FOR_NEXT_SEASON");
		}
		offer.getBuyer().getMessages().add(s);
		this.rejectAllOffersOfPlayer(offer.getPlayer());
		if (offer.getTrade() != null) {
			offer.getBuyer().getTransfer().rejectAllOffersOfPlayer(offer.getTrade());
		}
	}

	public void cancelOffer(Offer offer) {
		buyNeg.remove(offer);
		offer.getSeller().getTransfer().sellNeg.remove(offer);
		String s;
		s = offer.getBuyer().getTeamName();
		s += Util.getModelResourceBundle().getString("L_IS_NOT_INTERESTED_ANYMORE_IN");
		if (offer.getPlayer().isMultipleName()) {
			s += offer.getPlayer().getFirstName();
			s += " ";
		}
		s += offer.getPlayer().getLastName();
		offer.getSeller().getMessages().add(s);
	}

	/**
	 * 
	 * @return unsigned!
	 */
	public int getNofSellNegOffers() {
		return sellNeg.size();
	}

	/**
	 * 
	 * @param i
	 *            unsigned
	 * @return
	 */
	public Offer getSellNegOffer(int i) {
		assert (getNofSellNegOffers() > i);
		return sellNeg.get(i);
	}

	/**
	 * 
	 * @return unsigned
	 */
	public int getNofBuyNegOffers() {
		return buyNeg.size();
	}

	/**
	 * 
	 * @param i
	 *            unsigned
	 * @return
	 */
	public Offer getBuyNegOffer(int i) {
		assert (getNofBuyNegOffers() > i);
		return buyNeg.get(i);
	}

	public Player getNextSeasFirst(PlayerPtrVector allPlayers, Team team) {
		counter = 0;
		return getNextSeasNext(allPlayers, team);
	}

	public Player getNextSeasNext(PlayerPtrVector allPlayers, Team team) {
		Player player;
		while (true) {
			player = allPlayers.GetPlayer(counter);
			if (player == null) {
				return null;
			}
			counter++;
			Contracts contracts = player.getContracts();
			if (team.equals(contracts.getCurrContr().getTeam())
					&& contracts.getCurrContrYears() > 1
					&& contracts.getNextContr().getTeam() == null
					|| team.equals(contracts.getNextContr().getTeam())) {
				return player;
			}
		}
	}

	/**
	 * @return unsigned
	 */
	public static int nofFuturePlayers(Team team, PlayerPtrVector allPlayers) {
		Player player = team.getTransfer().getNextSeasFirst(allPlayers, team);
		int nofPlayers = 0;
		while (player != null) {
			nofPlayers++;
			player = team.getTransfer().getNextSeasNext(allPlayers, team);
		}
		nofPlayers += Team.TEAM_MAX_NOF_ROOKIES_A_YEAR;
		return nofPlayers;
	}

	/**
	 * number of players of potential buyer at time of getting the desired
	 * player.
	 * 
	 * @return unsigned
	 */
	public static int buyerNofPlayers(Offer offer, PlayerPtrVector allPlayers) {
		int nofNowPlayers = offer.getBuyer().getNofPlayers();
		int nofFuturePlayers = nofFuturePlayers(offer.getBuyer(), allPlayers);
		if (offer.isImmediately()) {
			if (offer.getYears() == 1) {
				return nofNowPlayers;
			} else {
				return Math.max(nofNowPlayers, nofFuturePlayers);
			}
		} else {
			return nofFuturePlayers;
		}
	}

	public void rejectAllOffersOfPlayer(Player player) {
		// if player prolonges or accepts, he has to reject all other offers
		int i = 0;
		while (i < this.getNofSellNegOffers()) {
			Offer offer = this.getSellNegOffer(i);
			if (offer.getPlayer().equals(player)) {
				// reject
				this.rejectOffer(offer);
				i = 0; // defensive programming (who asserts the first part of
						// the list of sell negs remains untouched at removing
						// offer "i" ?)
			} else {
				i++;
			}
		}
		// cancel all offers, where the player is offered as trade
		i = 0;
		while (i < this.getNofBuyNegOffers()) {
			Offer offer = this.getBuyNegOffer(i);
			if (player.equals(offer.getTrade())) {
				// cancel
				this.cancelOffer(offer);
				i = 0; // defensive programming (who asserts the first part of
						// the list of sell negs remains untouched at removing
						// offer "i" ?)
			} else {
				i++;
			}
		}
	}

	private void scanSellNeg(int nofTotalRounds, PlayerPtrVector allPlayers,
			Team team, News news, int maxNofForeigners) {
		do {
			int nofOffers = getNofSellNegOffers();
			if (nofOffers > 0) {
				if (Util.random(3) == 0) {
					Properties pImm = tooFewImm(team, maxNofForeigners);
					Properties pNext = tooFewNextYear(team, maxNofForeigners,
							allPlayers);

					// NYI better KI for
					// reject an offer

					// choose which offer to remove
					int ran;
					Offer offer;
					int loopCounter = 0;
					do {
						ran = Util.random(nofOffers);
						offer = getSellNegOffer(ran);
						Player player = offer.getPlayer();
						Properties p;
						if (offer.isImmediately()) {
							p = pImm;
						} else {
							p = pNext;
						}
						if (p.bitSet.isEmpty()
								|| p.bitSet.get(NO_FOREIGNER_LIMIT)
								&& p.bitSet.cardinality() == 1) {
							loopCounter = 50;
						} else if (p.bitSet.get(FOREIGNERS)
								&& player.getNation() != team.getNation()) {
							loopCounter = 50;
						} else if (p.bitSet.get(YOUNGSTERS)
								&& player.getAge() < 28) {
							loopCounter = 50;
						} else if (p.bitSet.get(OLDS) && player.getAge() > 27) {
							loopCounter = 50;
						} else if (p.bitSet.get(GOALIES)
								&& player.getPosition().getPosID() == Position.PosID.GOALIE) {
							loopCounter = 50;
						} else if (p.bitSet.get(DEFENDERS)
								&& player.getPosition().getPosID() == Position.PosID.DEFENDER) {
							loopCounter = 50;
						} else if (p.bitSet.get(LEFTWINGS)
								&& player.getPosition().getPosID() == Position.PosID.LEFTWING) {
							loopCounter = 50;
						} else if (p.bitSet.get(CENTERS)
								&& player.getPosition().getPosID() == Position.PosID.CENTER) {
							loopCounter = 50;
						} else if (p.bitSet.get(RIGHTWINGS)
								&& player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
							loopCounter = 50;
						} else {
							loopCounter++;
						}
					} while (Util.random(3) > 0 && loopCounter < 50);
					if (Util.random(nofTotalRounds / 20 + 2) == 0
							|| offer.isImmediately() && Util.random(2) > 0) {
						rejectOffer(offer);
					}
				} else {
					Properties pImm = tooMuchImm(team, maxNofForeigners);
					Properties pNext = tooMuchNextYear(team, maxNofForeigners,
							allPlayers);

					// NYI better KI for
					// accept an offer

					// choose which offer to remove
					int ran;
					Offer offer;
					int loopCounter = 0;
					do {
						ran = Util.random(nofOffers);
						offer = getSellNegOffer(ran);
						Player player = offer.getPlayer();
						Properties p;
						if (offer.isImmediately()) {
							p = pImm;
						} else {
							p = pNext;
						}
						if (p.bitSet.isEmpty()
								|| p.bitSet.get(NO_FOREIGNER_LIMIT)
								&& p.bitSet.cardinality() == 1) {
							loopCounter = 50;
						} else if (p.bitSet.get(FOREIGNERS)
								&& player.getNation() != team.getNation()) {
							loopCounter = 50;
						} else if (p.bitSet.get(YOUNGSTERS)
								&& player.getAge() < 28) {
							loopCounter = 50;
						} else if (p.bitSet.get(OLDS) && player.getAge() > 27) {
							loopCounter = 50;
						} else if (p.bitSet.get(GOALIES)
								&& player.getPosition().getPosID() == Position.PosID.GOALIE) {
							loopCounter = 50;
						} else if (p.bitSet.get(DEFENDERS)
								&& player.getPosition().getPosID() == Position.PosID.DEFENDER) {
							loopCounter = 50;
						} else if (p.bitSet.get(LEFTWINGS)
								&& player.getPosition().getPosID() == Position.PosID.LEFTWING) {
							loopCounter = 50;
						} else if (p.bitSet.get(CENTERS)
								&& player.getPosition().getPosID() == Position.PosID.CENTER) {
							loopCounter = 50;
						} else if (p.bitSet.get(RIGHTWINGS)
								&& player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
							loopCounter = 50;
						} else {
							loopCounter++;
						}
					} while (Util.random(3) > 0 && loopCounter < 50);
					int fee = offer.getPlayer().getFee();
					int currTotal = offer.getBuyer().getFinances().calcCurrTotal();
					int moneymaker = 3;
					if (offer.getPlayer().getCharacter().getCharID() == PlayerCharacter.CharID.MONEYMAKER) {
						// moneymakers want more wage
						moneymaker--;
					}
					Motivation.MotivationValue mv = offer.getPlayer()
							.getMotivation().getMotVal();
					if (offer.getWage() >= offer.getPlayer().getInitWage()
							- Util.random(moneymaker) + offer.getYears() - 1
							&& offer.getWage() >= 0
							// happy players are less likely to change team
							&& (mv == Motivation.MotivationValue.LOW
									|| mv == Motivation.MotivationValue.UNHAPPY
									|| mv == Motivation.MotivationValue.OK
									|| mv == Motivation.MotivationValue.GOOD
									&& Util.random(2) == 0 || mv == Motivation.MotivationValue.GREAT
									&& Util.random(3) == 0)
							&& (!offer.isImmediately() && fee <= currTotal || offer.isImmediately()
									&& fee * TRANS_IMMEDIATELY_FACTOR <= currTotal)
							// don't accept any trade player !
							&& (offer.getTrade() == null || offer.getTrade()
									.getTotalStrengthWithoutEnergyWithoutHealth()
									- offer.getPlayer()
											.getTotalStrengthWithoutEnergyWithoutHealth() > -Util
										.random(10)
									&& (offer.getTrade().getPosition() == offer.getPlayer()
											.getPosition() || Util.random(2) > 0))) {
						// if the wage is acceptable and the buyer can pay the fee
						// TODO transfer should be improved ...
						int nofPlayers = buyerNofPlayers(offer, allPlayers);
						if (nofPlayers < Team.TEAM_MAXTEAMPLAYER
								+ Team.TEAM_MAXFARMPLAYER) {
							// prof level => less accepts !
							ran = 2;
							if (Options.level == Options.Level.PROF
									&& offer.getBuyer().getTeamState()
											.getTeamStateID() == TeamState.TeamStateID.COACHED) {
								ran = 3;
							}
							if (Util.random(nofTotalRounds / 20 + ran) == 0
									|| offer.isImmediately()
									&& Util.random(ran) > 0) {
								acceptOffer(offer, news);
							}
						}
					}
				}
			}
		} while (Util.random(2) == 0);
	}

	private void scanBuyNeg(Team team) {
		while (Util.random(3) == 0) {
			// remove an offer
			int nofOffers = getNofBuyNegOffers();
			// choose which offer to remove
			if (nofOffers > 0) {
				int ran = Util.random(nofOffers);
				Offer offer = getBuyNegOffer(ran);
				cancelOffer(offer);
			}
		}
	}

	private void checkBuying(PlayerPtrVector allPlayers, Team team,
			int nofRounds, boolean tradeEnabled, int maxNofForeigners, News news) {
		int iter;
		if (nofRounds < 40) {
			iter = 3;
		} else if (nofRounds < 80) {
			iter = 2;
		} else if (nofRounds < 120) {
			iter = 1;
		} else {
			iter = Util.random(2);
		}
		for (int i = 0; i < iter; i++) {
			int ran = 2;
			if (team.getNofPlayers() < Team.TEAM_MAXTEAMPLAYER
					&& nofFuturePlayers(team, allPlayers) < Team.TEAM_MAXTEAMPLAYER) {
				// team with too less players
				ran = 1;
			}
			if (Util.random(ran) == 0) {
				boolean imm = true;
				Player player;
				int nofPlayers = allPlayers.GetNofPlayers();
				Properties p = tooFewImm(team, maxNofForeigners);
				if (p.bitSet.isEmpty() || p.bitSet.get(NO_FOREIGNER_LIMIT)
						&& p.bitSet.cardinality() == 1
						|| (!tradeEnabled || Util.random(2) == 0)
						&& Util.random(10) > 0 && !p.bitSet.get(FOREIGNERS)) {
					imm = false;
					p = tooFewNextYear(team, maxNofForeigners, allPlayers);
				}
				int loopCounter = 0;
				boolean foreigner;
				boolean again = true;
				do {
					ran = Util.random(nofPlayers);
					player = allPlayers.GetPlayer(ran);
					foreigner = (player.getNation() != team.getNation());
					if (p.bitSet.isEmpty() || p.bitSet.get(NO_FOREIGNER_LIMIT)
							&& p.bitSet.cardinality() == 1) {
						again = false;
					} else if (p.bitSet.get(FOREIGNERS) && foreigner) {
						again = false;
					} else if (p.bitSet.get(NO_FOREIGNER_LIMIT)
							|| !p.bitSet.get(FOREIGNERS) && !foreigner) {
						// own nation player
						if (p.bitSet.get(YOUNGSTERS) && player.getAge() < 28) {
							again = false;
						} else if (p.bitSet.get(OLDS) && player.getAge() > 27) {
							again = false;
						} else if (p.bitSet.get(GOALIES)
								&& player.getPosition().getPosID() == Position.PosID.GOALIE) {
							again = false;
						} else if (p.bitSet.get(DEFENDERS)
								&& player.getPosition().getPosID() == Position.PosID.DEFENDER) {
							again = false;
						} else if (p.bitSet.get(LEFTWINGS)
								&& player.getPosition().getPosID() == Position.PosID.LEFTWING) {
							again = false;
						} else if (p.bitSet.get(CENTERS)
								&& player.getPosition().getPosID() == Position.PosID.CENTER) {
							again = false;
						} else if (p.bitSet.get(RIGHTWINGS)
								&& player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
							again = false;
						}
					}
					loopCounter++;
					int strengthDiff = (foreigner && !p.bitSet
							.get(NO_FOREIGNER_LIMIT)) ? 0 : -Util.random(24);
					again = Util.random(3) > 0
							&& again
							|| player.getAge() > 39 // 40 years old
							// players not wanted
							|| player
									.getTotalStrengthWithoutEnergyWithoutHealth()
									- team.getStrengthWithMot() < strengthDiff
							|| !p.bitSet.get(OLDS)
							&& player.getAge() > 30
							&& Util.random(5) != 0 // don't buy too
							// much old players
							|| !p.bitSet.get(NO_FOREIGNER_LIMIT)
							&& !p.bitSet.get(FOREIGNERS) && foreigner
							&& Util.random(10) != 0 || p.bitSet.get(FOREIGNERS)
							&& !foreigner && (Util.random(5) != 0 || imm);
					if (loopCounter > 100) {
						again = false;
					}
				} while (again);
				assert (player.getFee() >= 0);
				int diff = Team.TEAM_MAX_NOF_ROOKIES_A_YEAR
						+ Team.TEAM_MAXTEAMPLAYER + Team.TEAM_MAXFARMPLAYER
						- nofFuturePlayers(team, allPlayers);
				assert (diff >= 0);
				if (tradeEnabled
						&& imm
						|| player.getFee() <= team.getFinances()
								.calcCurrTotal() - (nofRounds / 2) * diff
						|| player.getFee() == 0 // can get free agents
				// with negative cash !
				) {
					// enough money available or trade
					Offer offer = new Offer();
					offer.setPlayer(player);
					int years = Util.random(5) + 1;
					if (years == 5 || player.getAge() > 30
							&& Util.random(3) != 0) {
						// 1 year contracts are most likely
						years = 1;
					}
					offer.setYears(years);
					offer.setWage(player.getInitWage() + offer.getYears() - 1);
					offer.setFee(player.getFee());
					offer.setImmediately(imm);
					if (!tradeEnabled
							&& offer.isImmediately()
							&& offer.getFee() * TRANS_IMMEDIATELY_FACTOR > team
									.getFinances().calcCurrTotal()) {
						// not enough money for immediate buying
						offer.setImmediately(false);
					}
					if (tradeEnabled && offer.isImmediately()) {

						// TODO improve AI for trading

						int counter = 0;
						do {
							offer.setTrade(team.getPlayer(Util.random(team
									.getNofPlayers())));
							if (offer.getTrade().getPosition() != player
									.getPosition()) {
								offer.setTrade(team.getPlayer(Util.random(team
										.getNofPlayers())));
							}
							counter++;
						} while (counter < 50
								&& (offer.getTrade().getContracts().getNextContr()
										.getTeam() != null || p.bitSet
										.get(GOALIES)
										&& offer.getTrade().getPosition().getPosID() == Position.PosID.GOALIE
								// too few goalies
								));
						if (counter == 50) {
							offer.setImmediately(false);
						}

					}
					offer.setSeller(player.getContracts().getCurrContr().getTeam());
					offer.setBuyer(team);
					makeOffer(offer, allPlayers, news);
				}
			}
		}
	}

	private void checkSelling(Team team) {
		while (Util.random(5) == 0) {
			int ran = Util.random(team.getNofPlayers());
			Player player = team.getPlayer(ran);
			if (Util.random(2) > 0) {
				player.calcFee(true);
			} else {
				player.calcFee(false);
			}
		}
	}

	private void checkContract(int nofTotalRounds, Team team,
			PlayerPtrVector allPlayers, News news, int maxNofForeigners,
			boolean tournament) {
		int nofPlayers = team.getNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			int ran;
			if (team.getFinances().calcCurrTotal() < 0) {
				// no cash => more likely to prolonge, because cannot buy !
				ran = (nofTotalRounds * 2) / 3
						- (nofTotalRounds * nofTotalRounds) / 1000;
			} else {
				ran = (nofTotalRounds * 2) / 3
						- (nofTotalRounds * nofTotalRounds) / 1100;
			}

			if (Util.random(ran) == 0) {
				Properties p = tooFewNextYear(team, maxNofForeigners,
						allPlayers);

				// NYI check desired positions (only nation checking done in the
				// condition below)

				Player player = team.getPlayer(i);
				Contracts contracts = player.getContracts();
				Motivation.MotivationValue mv = player.getMotivation()
						.getMotVal();
				if (contracts.getCurrContrYears() == 1
						&& contracts.getNextContr().getTeam() == null
						// unhappy players are less likely to stay
						&& (mv == Motivation.MotivationValue.LOW
								&& Util.random(2) == 0
								|| mv == Motivation.MotivationValue.UNHAPPY
								&& Util.random(3) > 0
								|| mv == Motivation.MotivationValue.OK
								|| mv == Motivation.MotivationValue.GOOD || mv == Motivation.MotivationValue.GREAT)
						&& player.getAge() < 40 // 40 years old players don't
						// prolonge
						&& nofFuturePlayers(team, allPlayers) < Team.TEAM_MAXTEAMPLAYER
								+ Team.TEAM_MAXFARMPLAYER
						&& (p.bitSet.get(NO_FOREIGNER_LIMIT)
								|| p.bitSet.get(FOREIGNERS)
								|| player.getNation().equals(team.getNation())
								|| p.bitSet.get(OLDS) // don't prolonge too much for old players
								|| player.getAge() < 30 // don't prolonge too much for old players
						|| Util.random(10) == 0) // check nation
				) {
					// prolonge
					assert (team == contracts.getCurrContr().getTeam());
					contracts.setNextContr(new Contracts.TContract(team));
					int years = Util.random(5) + 1;
					if (years == 5 || player.getAge() > 30
							&& Util.random(3) != 0) {
						// 1 year contracts are most likely
						years = 1;
					}
					contracts.setNextContrYears(years);
					contracts.setNextContrWage(player.getInitWage() + years - 1);
					if (player.getCharacter().getCharID() == PlayerCharacter.CharID.MONEYMAKER) {
						// moneymakers want more wage
						contracts.setNextContrWage(contracts.getNextContrWage()
								+ Util.random(2));
					}
					player.getMotivation().calculate(player, Util.random(40),
							tournament); // incrase motivation after prolonge contract
					String s = news.getTransfers();
					if (player.isMultipleName()) {
						s += player.getFirstName();
						s += " ";
					}
					s += player.getLastName();
					s += Util.getModelResourceBundle().getString("L_STAYS_AT");
					s += team.getTeamName();
					s += "\n";
					news.setTransfers(s);
					this.rejectAllOffersOfPlayer(player);
				}
			}
		}
	}

	private void thisYear(Team team, Positions posi) {
		int nofPlayers = team.getNofPlayers();
		for (int i = 0; i < nofPlayers; i++) {
			Player player = team.getPlayer(i);
			if (player.getHealth().getInjury() < 3) {
				// long injured player are not considered
				if (player.getNation() != team.getNation()) {
					posi.nofForeigners++;
				}
				if (player.getPosition().getPosID() == Position.PosID.GOALIE) {
					posi.goalies++;
				} else if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
					posi.defenders++;
				} else if (player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
					posi.rightwings++;
				} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
					posi.center++;
				} else {
					assert (player.getPosition().getPosID() == Position.PosID.LEFTWING);
					posi.leftwings++;
				}
			}
			posi.age += player.getAge();
		}
		if (nofPlayers != 0) {
			posi.age = posi.age / nofPlayers;
		}

	}

	private Properties tooMuchImm(Team team, int maxNofForeigners) {
		Positions posi = new Positions();
		thisYear(team, posi);
		return tooMuchProp(posi, maxNofForeigners);
	}

	private Properties tooFewImm(Team team, int maxNofForeigners) {
		Positions posi = new Positions();
		thisYear(team, posi);
		return tooFewProp(posi, maxNofForeigners);
	}

	private void nextYear(Team team, PlayerPtrVector allPlayers, Positions posi) {
		int nofPlayers = 0;
		Player player = getNextSeasFirst(allPlayers, team);
		while (player != null) {
			nofPlayers++;
			if (player.getNation() != team.getNation()) {
				posi.nofForeigners++;
			}
			if (player.getPosition().getPosID() == Position.PosID.GOALIE) {
				posi.goalies++;
			} else if (player.getPosition().getPosID() == Position.PosID.DEFENDER) {
				posi.defenders++;
			} else if (player.getPosition().getPosID() == Position.PosID.RIGHTWING) {
				posi.rightwings++;
			} else if (player.getPosition().getPosID() == Position.PosID.CENTER) {
				posi.center++;
			} else {
				assert (player.getPosition().getPosID() == Position.PosID.LEFTWING);
				posi.leftwings++;
			}
			posi.age += player.getAge();
			player = getNextSeasNext(allPlayers, team);
		}
		if (nofPlayers != 0) {
			posi.age = posi.age / nofPlayers;
		}
	}

	private Properties tooMuchNextYear(Team team, int maxNofForeigners,
			PlayerPtrVector allPlayers) {
		Positions posi = new Positions();
		nextYear(team, allPlayers, posi);
		return tooMuchProp(posi, maxNofForeigners);
	}

	private Properties tooFewNextYear(Team team, int maxNofForeigners,
			PlayerPtrVector allPlayers) {
		Positions posi = new Positions();
		nextYear(team, allPlayers, posi);
		return tooFewProp(posi, maxNofForeigners);
	}

	private Properties tooFewProp(Positions posi, int maxNofForeigners) {
		Properties p = new Properties();
		if (posi.age > 27) {
			p.bitSet.set(YOUNGSTERS);
		} else {
			p.bitSet.set(OLDS);
		}
		if (posi.goalies < 3) {
			p.bitSet.set(GOALIES);
		}
		if (posi.defenders < 8) {
			p.bitSet.set(DEFENDERS);
		}
		if (posi.leftwings < 4) {
			p.bitSet.set(LEFTWINGS);
		}
		if (posi.center < 4) {
			p.bitSet.set(CENTERS);
		}
		if (posi.rightwings < 4) {
			p.bitSet.set(RIGHTWINGS);
		}
		if (maxNofForeigners == Team.TEAM_MAXTEAMPLAYER
				+ Team.TEAM_MAXFARMPLAYER) {
			p.bitSet.set(NO_FOREIGNER_LIMIT);
		} else if (posi.nofForeigners < maxNofForeigners) {
			p.bitSet.set(FOREIGNERS);
		}
		return p;
	}

	private Properties tooMuchProp(Positions posi, int maxNofForeigners) {
		Properties p = new Properties();
		if (posi.age > 27) {
			p.bitSet.set(OLDS);
		} else {
			p.bitSet.set(YOUNGSTERS);
		}
		if (posi.goalies > 3) {
			p.bitSet.set(GOALIES);
		}
		if (posi.defenders > 8) {
			p.bitSet.set(DEFENDERS);
		}
		if (posi.leftwings > 4) {
			p.bitSet.set(LEFTWINGS);
		}
		if (posi.center > 4) {
			p.bitSet.set(CENTERS);
		}
		if (posi.rightwings > 4) {
			p.bitSet.set(RIGHTWINGS);
		}
		if (maxNofForeigners == Team.TEAM_MAXTEAMPLAYER
				+ Team.TEAM_MAXFARMPLAYER) {
			p.bitSet.set(NO_FOREIGNER_LIMIT);
		} else if (posi.nofForeigners > maxNofForeigners) {
			p.bitSet.set(FOREIGNERS);
		}
		return p;
	}

	public void load(GameLeagueFile file, TeamPtrDivVector tpdv,
			PlayerPtrVector ppv) {
		file.parseSurroundingStartElement("Transfer");
		int no = file.getInt("NofSellNegOffers");
		for (int i = 0; i < no; i++) {
			Offer offer = new Offer();
			offer.load(file, tpdv, ppv);
			sellNeg.add(offer);
			offer.getBuyer().getTransfer().buyNeg.add(offer);
		}
		file.parseSurroundingEndElement();
	}
};
