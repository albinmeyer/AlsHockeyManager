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

/**
 * A checked exception caused by an Hockeymanager application
 * specific problem. Such an exception is allowed to happen sometimes
 * during runtime, e.g. when parsing a league xml file in a wrong format.
 *
 * @author Albin
 *
 */
public class HockmanException extends Exception {
	
	public HockmanException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 934990842565486550L;

}
