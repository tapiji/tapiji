package org.eclipselabs.tapiji.tools.rbmanager.viewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;

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