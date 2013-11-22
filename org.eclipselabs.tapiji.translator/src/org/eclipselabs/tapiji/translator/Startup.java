package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.suggestionlookup.SuggestionProviderLoader;

/**
 * Class that contains the method to call 
 * after startup
 * @author Samir Soyer
 *
 */
public class Startup implements IStartup{

	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		SuggestionProviderLoader.registerProviders();
		
	}

}
