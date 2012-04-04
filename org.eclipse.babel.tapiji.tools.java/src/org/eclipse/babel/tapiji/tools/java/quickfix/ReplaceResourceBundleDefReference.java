package org.eclipse.babel.tapiji.tools.java.quickfix;

import org.eclipse.babel.tapiji.tools.core.ui.dialogs.ResourceBundleSelectionDialog;
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


public class ReplaceResourceBundleDefReference implements IMarkerResolution2 {

	private String key;
	private int start;
	private int end;
	
	public ReplaceResourceBundleDefReference(String key, int start, int end) {
		this.key = key;
		this.start = start;
		this.end = end;
	}

	@Override
	public String getDescription() {
		return "Replaces the non-existing Resource-Bundle reference '"
				+ key
				+ "' with a reference to an already existing Resource-Bundle.";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel() {
		return "Select an alternative Resource-Bundle";
	}

	@Override
	public void run(IMarker marker) {
		int startPos = start;
		int endPos = end-start;
		IResource resource = marker.getResource();
		
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); 
		IPath path = resource.getRawLocation(); 
		try {
			bufferManager.connect(path, LocationKind.NORMALIZE, null); 
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.NORMALIZE);
			IDocument document = textFileBuffer.getDocument(); 
		
			ResourceBundleSelectionDialog dialog = new ResourceBundleSelectionDialog(Display.getDefault().getActiveShell(),
					resource.getProject());
			
			if (dialog.open() != InputDialog.OK)
				return;
			
			key = dialog.getSelectedBundleId();
			int iSep = key.lastIndexOf("/");
			key = iSep != -1 ? key.substring(iSep+1) : key;
			
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
