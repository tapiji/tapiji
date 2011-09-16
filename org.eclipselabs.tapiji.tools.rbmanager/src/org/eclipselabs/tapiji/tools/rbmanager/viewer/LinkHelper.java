package org.eclipselabs.tapiji.tools.rbmanager.viewer;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.navigator.ILinkHelper;

/*
 * Support 'link with editor'
 */
public class LinkHelper implements ILinkHelper {

	public static IStructuredSelection viewer;
	
	@Override
	public IStructuredSelection findSelection(IEditorInput anInput) {		
		IFile file = ResourceUtil.getFile(anInput);
		 if (file != null) {
			 return new StructuredSelection(file);
			 }
		return StructuredSelection.EMPTY;
	}

	@Override
	public void activateEditor(IWorkbenchPage aPage,IStructuredSelection aSelection) {
		if (aSelection.getFirstElement() instanceof IFile)
			try {
				IDE.openEditor(aPage, (IFile) aSelection.getFirstElement());
			} catch (PartInitException e) {/**/}
	}

}
