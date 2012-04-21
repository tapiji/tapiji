/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog.DialogConfiguration;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
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
	private boolean bundleContext;
	private String projectName;
	private IResource resource;
	private String bundleName;
	private String reference;

	public NewResourceBundleEntryProposal(IResource resource, int startPos,
	        int endPos, String value, boolean isStringLiteral,
	        boolean bundleContext, String projectName, String bundleName) {

		this.startPos = startPos;
		this.endPos = endPos;
		this.value = value;
		this.bundleContext = bundleContext;
		this.projectName = projectName;
		this.resource = resource;
		this.bundleName = bundleName;
	}

	@Override
	public void apply(IDocument document) {

		CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
		        Display.getDefault().getActiveShell());

		DialogConfiguration config = dialog.new DialogConfiguration();
		config.setPreselectedKey(bundleContext ? value : "");
		config.setPreselectedMessage(value);
		config.setPreselectedBundle(bundleName == null ? "" : bundleName);
		config.setPreselectedLocale("");
		config.setProjectName(projectName);

		dialog.setDialogConfiguration(config);

		if (dialog.open() != InputDialog.OK)
			return;

		String resourceBundleId = dialog.getSelectedResourceBundle();
		String key = dialog.getSelectedKey();

		try {
			if (!bundleContext)
				reference = ASTutils.insertNewBundleRef(document, resource,
				        startPos, endPos - startPos, resourceBundleId, key);
			else {
				document.replace(startPos, endPos - startPos, key);
				reference = key + "\"";
			}
			ResourceBundleManager.refreshResource(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAdditionalProposalInfo() {
		if (value != null && value.length() > 0) {
			return "Exports the focused string literal into a Java Resource-Bundle. This action results "
			        + "in a Resource-Bundle reference!";
		} else
			return "";
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public String getDisplayString() {
		String displayStr = "";
		if (bundleContext)
			displayStr = "Create a new resource-bundle-entry";
		else
			displayStr = "Create a new localized string literal";

		if (value != null && value.length() > 0)
			displayStr += " for '" + value + "'";

		return displayStr;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages()
		        .getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage();
	}

	@Override
	public Point getSelection(IDocument document) {
		int refLength = reference == null ? 0 : reference.length() - 1;
		return new Point(startPos + refLength, 0);
	}

	@Override
	public int getRelevance() {
		if (this.value.trim().length() == 0)
			return 96;
		else
			return 1096;
	}

}
