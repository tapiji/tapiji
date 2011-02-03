package at.ac.tuwien.inso.eclipse.i18n.ui.filters;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PropertiesFileFilter extends ViewerFilter {

	private boolean debugEnabled = true;
	
	public PropertiesFileFilter() {

	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (debugEnabled)
			return true;
		
		if (element.getClass().getSimpleName().equals("CompilationUnit"))
			return false;
		
		if (!(element instanceof IFile))
			return true;
		
		IFile file = (IFile) element;
		
		return file.getFileExtension().equalsIgnoreCase("properties");
	}

}