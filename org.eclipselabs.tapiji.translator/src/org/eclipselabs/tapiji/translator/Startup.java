package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IStartup;
import org.eclipselabs.tapiji.translator.suggestionlookup.SuggestionProviderRegistry;

public class Startup implements IStartup{

	@Override
	public void earlyStartup() {
		SuggestionProviderRegistry.registerProviders();
		
	}

}
