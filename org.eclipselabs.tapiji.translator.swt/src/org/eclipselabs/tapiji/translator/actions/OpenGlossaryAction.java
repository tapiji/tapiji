/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.actions;

import java.io.File;

import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.provider.SuggestionProviderUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.tapiji.translator.suggestionprovider.glossary.GlossarySuggestionProvider;
import org.eclipselabs.tapiji.translator.utils.FileUtils;

public class OpenGlossaryAction implements IWorkbenchWindowActionDelegate {

	/** The workbench window */
	private IWorkbenchWindow window;

	@Override
	public void run(IAction action) {
		String[] fileNames = FileUtils.queryFileName(window.getShell(),
		        "Open Glossary", SWT.OPEN, new String[] { "*.xml" });
		String fileName = fileNames[0];
		
		if (!FileUtils.isGlossary(fileName)) {
			MessageDialog.openError(window.getShell(), "Cannot open Glossary",
			        "The choosen file does not represent a Glossary!");
			return;
		}

		if (fileName != null) {
			IWorkbenchPage page = window.getActivePage();
			if (fileName != null) {
				GlossaryManager.loadGlossary(new File(fileName));
			}
		}
		
		for(ISuggestionProvider provider : SuggestionProviderUtils.getSuggetionProviders()){
			if(provider instanceof GlossarySuggestionProvider){
				((GlossarySuggestionProvider)provider).setGlossaryFile(fileName);
				SuggestionProviderUtils.fireSuggestionProviderUpdated(provider);
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void dispose() {
		this.window = null;
	}

	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

}
