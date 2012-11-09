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

package ch.hockman.model.common;

import java.util.Random;
import java.util.ResourceBundle;

/**
 * Utility class for the whole game.
 *
 * @author Albin
 *
 */
public class Util {
	static {
		reset();
	}
	private static Random randomInst;
	private static ResourceBundle bundle;
	
	// prevent instantiation
	private Util() {
	}

	static public int random(int i) {
		return randomInst.nextInt(i);
	}
	
	static private void reset() {
		randomInst = new Random();
		bundle = null;
	}
	
	static public ResourceBundle getModelResourceBundle() {
		if(bundle == null) {
			bundle = ResourceBundle.getBundle("ch.hockman.model.common.resources");
		}
		return bundle;
	}
}
