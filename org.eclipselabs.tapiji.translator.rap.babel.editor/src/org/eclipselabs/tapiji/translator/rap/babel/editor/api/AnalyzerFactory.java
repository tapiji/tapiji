/*
 * Copyright (C) 2011, 
 * 
 * This file is part of Essiembre ResourceBundle Editor.
 * 
 * Essiembre ResourceBundle Editor is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * Essiembre ResourceBundle Editor is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Essiembre ResourceBundle Editor; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
package org.eclipselabs.tapiji.translator.rap.babel.editor.api;

import org.eclipse.babel.core.message.checks.proximity.LevenshteinDistanceAnalyzer;
import org.eclipselabs.tapiji.translator.rbe.model.analyze.ILevenshteinDistanceAnalyzer;


public class AnalyzerFactory {

	public static ILevenshteinDistanceAnalyzer getLevenshteinDistanceAnalyzer () {
		return (ILevenshteinDistanceAnalyzer) LevenshteinDistanceAnalyzer.getInstance();
	}
}