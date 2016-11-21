package org.eclipse.e4.tapiji.rap;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.e4.E4ApplicationConfig;
import org.eclipse.rap.e4.E4EntryPointFactory;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.ExceptionHandler;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.client.WebClient;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;


public class BasicApplication implements ApplicationConfiguration {

    public void configure(Application application) {
    	
    	application.addStyleSheet("tapiji.business", "theme/business/business.css");
		Map<String, String> properties = new HashMap<String, String>();

		
		properties.put(WebClient.PAGE_TITLE, "e4 RAP App");
		properties.put(WebClient.THEME_ID, "tapiji.business");
        application.addEntryPoint("/rap", new E4EntryPointFactory(E4ApplicationConfig.create("org.eclipse.e4.tapiji.app/Application.e4xmi")), properties);
        application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
        
        application.setExceptionHandler(new ExceptionHandler() {
			
			@Override
			public void handleException(Throwable throwable) {
				System.out.println("" + throwable.getMessage());
				
			}
		});
    }

}
