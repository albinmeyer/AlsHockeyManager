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

import ch.hockman.model.common.Util;

/**
 * The health state of a player.
 * If a player is injured, he cannot play.
 * If he is hurt, he can play, but has limited factors.
 *
 * @author Albin
 *
 */
public class Health {
	static final float HEALTH_HURT_FACTOR = ((float) 3 / 4);

	private int injury;
	private int hurt;

	public Health() {
		injury = 0;
		hurt = 0;
	}

	public void setInjury() {
		int inj = Util.random(20) - Util.random(10);
		if (inj < 1) {
			inj = 1 + Util.random(4);
		}
		injury = inj;
	}

	void setInjury(int inj) {
		injury = inj;
	}

	public void setHurt() {
		int h = Util.random(8) - Util.random(4);
		if (h < 1) {
			h = Util.random(4);
		}
		hurt = h;
	}

	public void setHurt(int i) {
		hurt = i;
	}

	void recoveryDay() {
		assert (injury == 0 || hurt == 0);
		if (injury > 0) {
			injury--;
			if (Util.random(5) == 0) {
				injury += Util.random(4);
			}
			if (Util.random(5) == 0) {
				injury -= Util.random(4);
				if (injury < 0) {
					injury = 0;
				}
			}
			if (injury == 0) { // can play now, but is still hurt
				setHurt();
			}
		}
		if (hurt > 0) {
			hurt--;
			if (Util.random(5) == 0) {
				hurt += Util.random(2);
			}
			if (Util.random(5) == 0) {
				hurt -= Util.random(2);
				if (hurt < 0) {
					hurt = 0;
				}
			}
		}
	}

	public int getInjury() {
		return injury;
	}

	public int getHurt() {
		return hurt;
	}
};
