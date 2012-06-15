package org.eclipselabs.tapiji.translator.rap;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class TranslatorWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.eclipselabs.tapiji.translator.rap.perspective";
	
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
	        IWorkbenchWindowConfigurer configurer) {
		return new TranslatorWorkbenchWindowAdvisor(configurer);
	}
	
	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}

}
