package org.eclipselabs.tapiji.translator.rap.babel.core.factory;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipselabs.tapiji.translator.rap.babel.core.configuration.ConfigurationManager;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.MessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.strategy.PropertiesFileGroupStrategy;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;

public class MessagesBundleGroupFactory {
	
    public static IMessagesBundleGroup createBundleGroup(IResource resource) {
    	
            File ioFile = new File(resource.getRawLocation().toFile().getPath());
            
            return new MessagesBundleGroup(new PropertiesFileGroupStrategy(ioFile, 
            		ConfigurationManager.getInstance().getSerializerConfig(), 
            		ConfigurationManager.getInstance().getDeserializerConfig()));
    }
}
