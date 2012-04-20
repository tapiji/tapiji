/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.quickfix;

import java.util.Locale;

import org.eclipse.babel.tapiji.tools.core.ui.dialogs.ResourceBundleEntrySelectionDialog;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMarkerResolution2;


public class ReplaceResourceBundleReference implements IMarkerResolution2 {

	private String key;
	private String bundleId;
	
	public ReplaceResourceBundleReference(String key, String bundleId) {
		this.key = key;
		this.bundleId = bundleId;
	}

	@Override
	public String getDescription() {
		return "Replaces the non-existing Resource-Bundle key '"
				+ key
				+ "' with a reference to an already existing localized string literal.";
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getLabel() {
		return "Select alternative Resource-Bundle entry";
	}

	@Override
	public void run(IMarker marker) {
		int startPos = marker.getAttribute(IMarker.CHAR_START, 0);
		int endPos = marker.getAttribute(IMarker.CHAR_END, 0) - startPos;
		IResource resource = marker.getResource();
		
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); 
		IPath path = resource.getRawLocation(); 
		try {
			bufferManager.connect(path, LocationKind.NORMALIZE, null); 
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.NORMALIZE);
			IDocument document = textFileBuffer.getDocument(); 
		
			ResourceBundleEntrySelectionDialog dialog = new ResourceBundleEntrySelectionDialog(
					Display.getDefault().getActiveShell());
			
			dialog.setProjectName(resource.getProject().getName());
			dialog.setBundleName(bundleId);
			
			if (dialog.open() != InputDialog.OK)
				return;
			
			String key = dialog.getSelectedResource();
			Locale locale = dialog.getSelectedLocale();
			
			document.replace(startPos, endPos, "\"" + key + "\"");
			
			textFileBuffer.commit(null, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bufferManager.disconnect(path, LocationKind.NORMALIZE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			} 
		}
	}

}
