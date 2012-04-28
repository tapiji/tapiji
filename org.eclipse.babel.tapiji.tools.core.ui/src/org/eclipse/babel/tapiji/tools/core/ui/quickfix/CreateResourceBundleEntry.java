/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Alexej Strelzow - modified CreateResourceBundleEntryDialog instantiation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.quickfix;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.ui.builder.I18nBuilder;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog.DialogConfiguration;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMarkerResolution2;

public class CreateResourceBundleEntry implements IMarkerResolution2 {

	private String key;
	private String bundleId;

	public CreateResourceBundleEntry(String key, String bundleId) {
		this.key = key;
		this.bundleId = bundleId;
	}

	@Override
	public String getDescription() {
		return "Creates a new Resource-Bundle entry for the property-key '"
		        + key + "'";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		return "Create Resource-Bundle entry for '" + key + "'";
	}

	@Override
	public void run(IMarker marker) {
		int startPos = marker.getAttribute(IMarker.CHAR_START, 0);
		int endPos = marker.getAttribute(IMarker.CHAR_END, 0) - startPos;
		IResource resource = marker.getResource();

		ITextFileBufferManager bufferManager = FileBuffers
		        .getTextFileBufferManager();
		IPath path = resource.getRawLocation();
		try {
			bufferManager.connect(path, null);
			ITextFileBuffer textFileBuffer = bufferManager
			        .getTextFileBuffer(path);
			IDocument document = textFileBuffer.getDocument();

			CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
			        Display.getDefault().getActiveShell());

			DialogConfiguration config = dialog.new DialogConfiguration();
			config.setPreselectedKey(key != null ? key : "");
			config.setPreselectedMessage("");
			config.setPreselectedBundle(bundleId);
			config.setPreselectedLocale("");
			config.setProjectName(resource.getProject().getName());

			dialog.setDialogConfiguration(config);

			if (dialog.open() != InputDialog.OK) {
				return;
			}
		} catch (Exception e) {
			Logger.logError(e);
		} finally {
			try {
				resource.getProject().build(
				        IncrementalProjectBuilder.FULL_BUILD,
				        I18nBuilder.BUILDER_ID, null, null);
			} catch (CoreException e) {
				Logger.logError(e);
			}
		}

	}

}
