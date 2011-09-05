package com.gknsintermetals.eclipse.resourcebundle.manager.viewer.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/*
 * Will be only active for VirtualResourceBundeles
 */
public class ActionRBProvider extends CommonActionProvider {
	private IAction openAction;
	private SelectionListener showProblemsListener;
	
	public ActionRBProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(ICommonActionExtensionSite aSite){
		super.init(aSite);
		openAction = new OpenVRBAction(aSite.getViewSite().getSelectionProvider());
		
//		showProblemsListener = new ShowProblemInfoListener();
//		((CommonViewer)aSite.getStructuredViewer()).getTree().addSelectionListener(showProblemsListener);
	}
	
	@Override
	public void fillActionBars(IActionBars actionBars){
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
	}
	
//	@Override
//	public void fillContextMenu(IMenuManager menu){
//		menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openAction);
//	}
}