/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexej Strelzow - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.rap.babel.editor.api;

import org.eclipse.babel.core.message.checks.proximity.LevenshteinDistanceAnalyzer;

/**
 * Provides the {@link ILevenshteinDistanceAnalyzer}
 * <br><br>
 * 
 * @author Alexej Strelzow
 */
public class AnalyzerFactory {

	/**
	 * @return An instance of the {@link LevenshteinDistanceAnalyzer}
	 */
	public static ILevenshteinDistanceAnalyzer getLevenshteinDistanceAnalyzer () {
		return (ILevenshteinDistanceAnalyzer) LevenshteinDistanceAnalyzer.getInstance();
	}
}
