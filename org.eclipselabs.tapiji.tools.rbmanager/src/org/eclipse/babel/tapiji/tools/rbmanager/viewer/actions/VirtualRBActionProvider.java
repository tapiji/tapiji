package org.eclipse.babel.tapiji.tools.rbmanager.viewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/*
 * Will be only active for VirtualResourceBundeles
 */
public class VirtualRBActionProvider extends CommonActionProvider {
	private IAction openAction;
	
	public VirtualRBActionProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ICommonActionExtensionSite aSite){
		super.init(aSite);
		openAction = new OpenVRBAction(aSite.getViewSite().getSelectionProvider());
	}
	
	@Override
	public void fillActionBars(IActionBars actionBars){
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
	}
}