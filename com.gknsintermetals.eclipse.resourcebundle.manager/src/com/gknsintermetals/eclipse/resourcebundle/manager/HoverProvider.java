package com.gknsintermetals.eclipse.resourcebundle.manager;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import com.gknsintermetals.eclipse.resourcebundle.manager.viewer.actions.Hover;
import com.gknsintermetals.eclipse.resourcebundle.manager.viewer.actions.RBMarkerInformant;

public class HoverProvider extends CommonActionProvider {

	public HoverProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(ICommonActionExtensionSite aSite){
		super.init(aSite);
		Hover hover = new Hover(Display.getCurrent().getActiveShell(), new RBMarkerInformant());
		hover.activateHoverHelp(((CommonViewer)aSite.getStructuredViewer()).getTree());
	}

}
