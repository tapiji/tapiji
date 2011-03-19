package quickfix;

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
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;

import util.ASTutils;

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
		return "Export to resource bundle";
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
					"",
					(startPos+1 < document.getLength() && endPos > 1) ? document.get(startPos+1, endPos-2) : "",
					"",
					"");
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
