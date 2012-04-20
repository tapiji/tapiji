/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
