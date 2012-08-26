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
package ui.autocompletion;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog.DialogConfiguration;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class NewResourceBundleEntryProposal implements IJavaCompletionProposal {

    private int startPos;
    private int endPos;
    private String value;
    private ResourceBundleManager manager;
    private IResource resource;
    private String bundleName;
    private String reference;
    private boolean isKey;

    public NewResourceBundleEntryProposal(IResource resource, String str,
	    int startPos, int endPos, ResourceBundleManager manager,
	    String bundleName, boolean isKey) {
	this.startPos = startPos;
	this.endPos = endPos;
	this.manager = manager;
	this.value = str;
	this.resource = resource;
	this.bundleName = bundleName;
	this.isKey = isKey;
    }

    @Override
    public void apply(IDocument document) {

	CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
		Display.getDefault().getActiveShell());

	DialogConfiguration config = dialog.new DialogConfiguration();
	config.setPreselectedKey(isKey ? value : "");
	config.setPreselectedMessage(!isKey ? value : "");
	config.setPreselectedBundle(bundleName == null ? "" : bundleName);
	config.setPreselectedLocale("");
	config.setProjectName(resource.getProject().getName());

	dialog.setDialogConfiguration(config);

	if (dialog.open() != InputDialog.OK) {
	    return;
	}

	String resourceBundleId = dialog.getSelectedResourceBundle();
	String key = dialog.getSelectedKey();

	try {
	    document.replace(startPos, endPos, key);
	    reference = key + "\"";
	    ResourceBundleManager.rebuildProject(resource);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    public String getAdditionalProposalInfo() {
	// TODO Auto-generated method stub
	return "Creates a new string literal within one of the"
		+ " project's resource bundles. This action results "
		+ "in a reference to the localized string literal!";
    }

    @Override
    public IContextInformation getContextInformation() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getDisplayString() {
	String displayStr = "";

	displayStr = "Create a new localized string literal";

	if (this.isKey) {
	    if (value != null && value.length() > 0)
		displayStr += " with the key '" + value + "'";
	} else {
	    if (value != null && value.length() > 0)
		displayStr += " for '" + value + "'";
	}
	return displayStr;
    }

    @Override
    public Image getImage() {
	return PlatformUI.getWorkbench().getSharedImages()
		.getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage();
    }

    @Override
    public Point getSelection(IDocument document) {
	// TODO Auto-generated method stub
	return new Point(startPos + reference.length() - 1, 0);
    }

    @Override
    public int getRelevance() {
	// TODO Auto-generated method stub
	if (this.value.trim().length() == 0)
	    return 1096;
	else
	    return 1096;
    }

}
