package org.eclipse.babel.tapiji.tools.core.ui.quickfix;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.builder.StringLiteralAuditor;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
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
	
	public CreateResourceBundleEntry (String key, ResourceBundleManager manager, String bundleId) {
		this.key = key;
		this.bundleId = bundleId;
	}
	
	@Override
	public String getDescription() {
		return "Creates a new Resource-Bundle entry for the property-key '" + key + "'";
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
		
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); 
		IPath path = resource.getRawLocation(); 
		try {
			bufferManager.connect(path, null); 
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path);
			IDocument document = textFileBuffer.getDocument(); 
			
			CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
					Display.getDefault().getActiveShell(),
					ResourceBundleManager.getManager(resource.getProject()),
					key != null ? key : "",
					"",
					bundleId,
					"");
			if (dialog.open() != InputDialog.OK)
				return;
		} catch (Exception e) {
			Logger.logError(e);
		} finally {
			try {
				(new StringLiteralAuditor()).buildResource(resource, null);
				bufferManager.disconnect(path, null);
			} catch (CoreException e) {
				Logger.logError(e);
			} 
		}
		
	}

}
