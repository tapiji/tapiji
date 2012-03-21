package org.eclipse.babel.core.configuration;

import org.eclipse.babel.core.message.resource.ser.IPropertiesDeserializerConfig;
import org.eclipse.babel.core.message.resource.ser.IPropertiesSerializerConfig;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

/**
 * 
 * @author Alexej Strelzow
 */
public class ConfigurationManager {

	private static ConfigurationManager INSTANCE;
	
	private IConfiguration config;
	
	private IPropertiesSerializerConfig serializerConfig;
	
	private IPropertiesDeserializerConfig deserializerConfig;
	
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

	public IPropertiesSerializerConfig getSerializerConfig() {
		return serializerConfig;
	}

	public void setSerializerConfig(IPropertiesSerializerConfig serializerConfig) {
		this.serializerConfig = serializerConfig;
	}

	public IPropertiesDeserializerConfig getDeserializerConfig() {
		return deserializerConfig;
	}

	public void setDeserializerConfig(
			IPropertiesDeserializerConfig deserializerConfig) {
		this.deserializerConfig = deserializerConfig;
	}
	
}
