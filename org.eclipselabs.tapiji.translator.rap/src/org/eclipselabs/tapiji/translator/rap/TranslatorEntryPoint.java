package org.eclipselabs.tapiji.translator.rap;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

public class TranslatorEntryPoint implements IEntryPoint {

	@Override
	public int createUI() {
		Display display = PlatformUI.createDisplay();
		WorkbenchAdvisor advisor = new TranslatorWorkbenchAdvisor();
		return PlatformUI.createAndRunWorkbench(display, advisor);		
	}

}
