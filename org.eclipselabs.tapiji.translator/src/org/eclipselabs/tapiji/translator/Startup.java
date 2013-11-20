package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.suggestionlookup.SuggestionProviderLoader;

public class Startup implements IStartup{

	@Override
	public void earlyStartup() {
		SuggestionProviderLoader.registerProviders();
		
	}

}
