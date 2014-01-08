/*******************************************************************************
 * Copyright (c) 2013 Samir Soyer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Samir Soyer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.suggestionlookup.SuggestionProviderLoader;

/**
 * Class that contains the method to call after startup
 * 
 * @author Samir Soyer
 * 
 */
public class Startup implements IStartup {

	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		SuggestionProviderLoader.registerProviders();

	}

}
