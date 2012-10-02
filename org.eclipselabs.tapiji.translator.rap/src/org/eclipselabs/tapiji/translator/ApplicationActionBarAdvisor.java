package org.eclipselabs.tapiji.translator;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends
		AbstractApplicationActionBarAdvisor {

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected MenuManager fileMenu() {
		MenuManager menu = new MenuManager("&File", "file_mnu");
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));

		menu.add(getAction(ActionFactory.CLOSE.getId()));
		menu.add(getAction(ActionFactory.CLOSE_ALL.getId()));

		menu.add(new GroupMarker(IWorkbenchActionConstants.CLOSE_EXT));
		menu.add(new Separator());
		menu.add(getAction(ActionFactory.SAVE.getId()));
		menu.add(getAction(ActionFactory.SAVE_ALL.getId()));		
		return menu;
	}
}
