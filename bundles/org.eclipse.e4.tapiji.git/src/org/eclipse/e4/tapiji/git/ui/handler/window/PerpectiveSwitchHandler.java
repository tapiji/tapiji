package org.eclipse.e4.tapiji.git.ui.handler.window;

import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public class PerpectiveSwitchHandler {

	private static final String PARAMETER_PERSPECTIVE_ID = "org.eclipse.e4.tapiji.git.commandparameter.perspective";

	@Execute
	public void switchPerspective(MPerspective activePerspective, MApplication application, EPartService partService, EModelService modelService,  @Named(PARAMETER_PERSPECTIVE_ID) String perspectiveId) {
		List<MPerspective> perspectives = modelService.findElements(application, perspectiveId, MPerspective.class, null);
		 if (!perspectives.isEmpty()) {
             partService.switchPerspective(perspectives.get(0));
		 }
	}
}
