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
package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.babel.tapiji.tools.core.ui.dialogs.ResourceBundleEntrySelectionDialog;
import org.eclipse.babel.tapiji.tools.java.ui.util.ASTutilsUI;
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

public class InsertResourceBundleReferenceProposal implements
	IJavaCompletionProposal {

    private int offset = 0;
    private int length = 0;
    private IResource resource;
    private String reference;
    private String projectName;

    public InsertResourceBundleReferenceProposal(int offset, int length,
	    String projectName, IResource resource,
	    Collection<String> availableBundles) {
	this.offset = offset;
	this.length = length;
	this.resource = resource;
	this.projectName = projectName;
    }

    @Override
    public void apply(IDocument document) {
	ResourceBundleEntrySelectionDialog dialog = new ResourceBundleEntrySelectionDialog(
		Display.getDefault().getActiveShell());
	dialog.setProjectName(projectName);

	if (dialog.open() != InputDialog.OK) {
	    return;
	}

	String resourceBundleId = dialog.getSelectedResourceBundle();
	String key = dialog.getSelectedResource();
	Locale locale = dialog.getSelectedLocale();

	reference = ASTutilsUI.insertExistingBundleRef(document, resource,
		offset, length, resourceBundleId, key, locale);
    }

    @Override
    public String getAdditionalProposalInfo() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IContextInformation getContextInformation() {
	return null;
    }

    @Override
    public String getDisplayString() {
	return "Insert reference to a localized string literal";
    }

    @Override
    public Image getImage() {
	return PlatformUI.getWorkbench().getSharedImages()
		.getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage();
    }

    @Override
    public Point getSelection(IDocument document) {
	// TODO Auto-generated method stub
	int referenceLength = reference == null ? 0 : reference.length();
	return new Point(offset + referenceLength, 0);
    }

    @Override
    public int getRelevance() {
	// TODO Auto-generated method stub
	if (this.length == 0) {
	    return 97;
	} else {
	    return 1097;
	}
    }

}
