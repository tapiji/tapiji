package org.eclipse.babel.core.configuration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/**
 * 
 * @author Alexej Strelzow
 */
public class ConfigurationManager {

	private static ConfigurationManager INSTANCE = null;
	
	private IConfiguration config = null;
	
	private ConfigurationManager() {
		config = getConfig();
	}
	
	private IConfiguration getConfig() {
		
		IExtensionPoint extp = Platform.getExtensionRegistry().getExtensionPoint(
                "org.eclipse.babel.core" + ".babelConfiguration");
        IConfigurationElement[] elements = extp.getConfigurationElements();
        
        if (elements.length != 0) {
        	try {
				return (IConfiguration) elements[0].createExecutableExtension("class");
        	} catch (CoreException e) {
				e.printStackTrace();
			}
        } 
    	return null;
	}

	public static ConfigurationManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ConfigurationManager();
		}
		return INSTANCE;
	}
	
	public IConfiguration getConfiguration() {
		return this.config;
	}
}
