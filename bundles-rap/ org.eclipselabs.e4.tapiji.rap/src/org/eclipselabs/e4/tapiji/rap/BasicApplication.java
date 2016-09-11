package org.eclipselabs.e4.tapiji.rap;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.rap.e4.E4ApplicationConfig;
import org.eclipse.rap.e4.E4EntryPointFactory;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;


public class BasicApplication implements ApplicationConfiguration {

    @Override
    public void configure(Application application) {
    	Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Hello e4 RAP");
        application.addEntryPoint("/rap", new E4EntryPointFactory(E4ApplicationConfig.create("platform:/plugin/org.eclipselabs.e4.tapiji.translator/Application.e4xmi")), properties);
        application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
    }

}
