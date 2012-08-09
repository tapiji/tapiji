package org.eclipselabs.tapiji.translator;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;

public class ApplicationWorkbenchWindowAdvisor extends
		AbstractWorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// TODO [RAP] not yet supported (RAP 1.5)
		//configurer.setShowFastViewBars(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setTitle("TapiJI Translator");
	}

}
