package org.eclipselabs.tapiji.translator.rap;

import org.eclipse.ui.application.WorkbenchAdvisor;

public class TranslatorWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.eclipselabs.tapiji.translator.rap.perspective";
	
	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

}
