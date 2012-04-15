package org.eclipse.babel.tapiji.tools.rbmanager.viewer.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.rbmanager.ui.hover.Hover;
import org.eclipse.babel.tapiji.tools.rbmanager.ui.hover.HoverInformant;
import org.eclipse.babel.tapiji.tools.rbmanager.viewer.actions.hoverinformants.I18NProjectInformant;
import org.eclipse.babel.tapiji.tools.rbmanager.viewer.actions.hoverinformants.RBMarkerInformant;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;


public class GeneralActionProvider extends CommonActionProvider {
	private IAction expandAction;
	
	public GeneralActionProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(ICommonActionExtensionSite aSite){
		super.init(aSite);
		//init Expand-Action
		expandAction = new ExpandAction((CommonViewer) aSite.getStructuredViewer());
		
		//activate View-Hover
		List<HoverInformant> informants = new ArrayList<HoverInformant>();
		informants.add(new I18NProjectInformant());
		informants.add(new RBMarkerInformant());
		
		Hover hover = new Hover(Display.getCurrent().getActiveShell(), informants);
		hover.activateHoverHelp(((CommonViewer)aSite.getStructuredViewer()).getTree());
	}

	@Override
	public void fillContextMenu(IMenuManager menu) {
		menu.appendToGroup("expand",expandAction);
	}
}