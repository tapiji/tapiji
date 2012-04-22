/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
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
