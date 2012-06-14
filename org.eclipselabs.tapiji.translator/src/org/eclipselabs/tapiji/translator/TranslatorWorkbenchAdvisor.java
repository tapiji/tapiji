package org.eclipse.tapiji.rap.translator;

import org.eclipse.ui.application.WorkbenchAdvisor;

public class TranslatorWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "org.eclipse.tapiji.rap.translator.perspective";
	
	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

}
