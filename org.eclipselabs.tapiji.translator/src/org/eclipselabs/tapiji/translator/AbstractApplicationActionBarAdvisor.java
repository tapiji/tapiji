/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public abstract class AbstractApplicationActionBarAdvisor extends ActionBarAdvisor {

	private IContributionItem showViewItem;
	
	public static final String INSTANCE_CLASS = "org.eclipselabs.tapiji.translator.ApplicationActionBarAdvisor";
	
	public AbstractApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		super.fillCoolBar(coolBar);

		coolBar.add(new GroupMarker("group.file"));
		{
			// File Group
			IToolBarManager fileToolBar = new ToolBarManager(coolBar.getStyle());

			fileToolBar.add(new Separator(IWorkbenchActionConstants.NEW_GROUP));
			fileToolBar
			        .add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));

			fileToolBar.add(new GroupMarker(
			        IWorkbenchActionConstants.SAVE_GROUP));
			fileToolBar.add(getAction(ActionFactory.SAVE.getId()));

			// Add to the cool bar manager
			coolBar.add(new ToolBarContributionItem(fileToolBar,
			        IWorkbenchActionConstants.TOOLBAR_FILE));
		}

		coolBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		coolBar.add(new GroupMarker(IWorkbenchActionConstants.GROUP_EDITOR));
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		super.fillMenuBar(menuBar);

		menuBar.add(fileMenu());
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));		
		menuBar.add(windowMenu());		
		menuBar.add(helpMenu());
	}

	private MenuManager helpMenu() {
		MenuManager helpMenu = new MenuManager("&Help",
		        IWorkbenchActionConstants.M_HELP);

		// TODO [RAP] helpMenu.add(getAction(ActionFactory.ABOUT.getId()));
		helpMenu.add(getAction(ActionFactory.ABOUT.getId()));

		return helpMenu;
	}

	private MenuManager windowMenu() {
		MenuManager windowMenu = new MenuManager("&Window",	IWorkbenchActionConstants.M_WINDOW);
		MenuManager showViewMenuMgr = new MenuManager("Show View", "showView");
		
		showViewMenuMgr.add(showViewItem);
		windowMenu.add(showViewMenuMgr);
		
		return windowMenu;
	}
	
	abstract protected MenuManager fileMenu(); 
	
	@Override
	protected void makeActions(IWorkbenchWindow window) {
		super.makeActions(window);

		registerAsGlobal(ActionFactory.SAVE.create(window));
		registerAsGlobal(ActionFactory.SAVE_AS.create(window));
		registerAsGlobal(ActionFactory.SAVE_ALL.create(window));
		registerAsGlobal(ActionFactory.CLOSE.create(window));
		registerAsGlobal(ActionFactory.CLOSE_ALL.create(window));
		registerAsGlobal(ActionFactory.CLOSE_ALL_SAVED.create(window));
		//TODO [RAP] registerAsGlobal(ActionFactory.ABOUT.create(window));
		registerAsGlobal(ActionFactory.ABOUT.create(window));
		registerAsGlobal(ActionFactory.QUIT.create(window));
		
		showViewItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
	}

	private void registerAsGlobal(IAction action) {
		getActionBarConfigurer().registerGlobalAction(action);
		register(action);
	}

}
