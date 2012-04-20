/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.quickfix;

import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog.DialogConfiguration;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
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


public class ExportToResourceBundleResolution implements IMarkerResolution2 {

	public ExportToResourceBundleResolution () {}
	
	@Override
	public String getDescription() {
		return "Export constant string literal to a resource bundle.";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		return "Export to Resource-Bundle";
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
			
			CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
					Display.getDefault().getActiveShell());
			
			DialogConfiguration config = dialog.new DialogConfiguration();
			config.setPreselectedKey("");
			config.setPreselectedMessage("");
			config.setPreselectedBundle((startPos+1 < document.getLength() && endPos > 1) ? document.get(startPos+1, endPos-2) : "");
			config.setPreselectedLocale("");
			config.setProjectName(resource.getProject().getName());
			
			dialog.setDialogConfiguration(config);
			
			if (dialog.open() != InputDialog.OK)
				return;
			
			ASTutils.insertNewBundleRef(document, 
					resource, 
					startPos, 
					endPos, 
					dialog.getSelectedResourceBundle(), 
					dialog.getSelectedKey());
	
			textFileBuffer.commit(null, false);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bufferManager.disconnect(path, null);
			} catch (CoreException e) {
				e.printStackTrace();
			} 
		}
		

	}

}
