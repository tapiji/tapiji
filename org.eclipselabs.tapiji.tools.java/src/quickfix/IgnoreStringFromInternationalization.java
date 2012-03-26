package quickfix;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;

import util.ASTutils;

public class IgnoreStringFromInternationalization implements IMarkerResolution2 {

	@Override
	public String getLabel() {
		return "Ignore String";
	}

	@Override
	public void run(IMarker marker) {
		IResource resource = marker.getResource();
		ResourceBundleManager manager = ResourceBundleManager.getManager(resource.getProject());
		
		IJavaElement je = JavaCore.create(resource, JavaCore.create(resource.getProject()));
		// get the type of the currently loaded resource
		ITypeRoot typeRoot = ((ICompilationUnit) je);
		// get a reference to the shared AST of the loaded CompilationUnit
		CompilationUnit cu = SharedASTProvider.getAST(typeRoot,
		// do not wait for AST creation
				SharedASTProvider.WAIT_YES, null);
				
		/*ICompilationUnit icu = (ICompilationUnit) cu.getJavaElement();
		
		IDocument doc = null;*/
		
		ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager(); 
		IPath path = resource.getRawLocation(); 
		
		
		try {
			bufferManager.connect(path, LocationKind.NORMALIZE, null); 
			ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.NORMALIZE);
			IDocument document = textFileBuffer.getDocument(); 
			
			int position = marker.getAttribute(IMarker.CHAR_START, 0);
			
			ASTutils.createReplaceNonInternationalisationComment(cu, document, position);
			textFileBuffer.commit(null, false);
			
		} catch (JavaModelException e) {
			Logger.logError(e);
		} catch (CoreException e) {
			Logger.logError(e);
		}
		
		
	}

	@Override
	public String getDescription() {
		return "Ignore String from Internationalization";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

}
