package org.eclipselabs.tapiji.translator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

public class ApplicationWorkbenchWindowAdvisor extends AbstractWorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void preWindowOpen()  {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowFastViewBars(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setTitle("TapiJI Translator");
		/** Init workspace and container project */
		try {
			FileUtils.getProject(FileUtils.EXTERNAL_RB_PROJECT_NAME);
		} catch (CoreException e) {
		}
	}

}
