package org.eclipselabs.tapiji.tools.rbmanager.viewer.filters;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;
import org.eclipselabs.tapiji.tools.core.util.RBFileUtils;
import org.eclipselabs.tapiji.tools.rbmanager.model.VirtualResourceBundle;



public class ProblematicResourceBundleFilter extends ViewerFilter {
	
	/**
	 * Shows only IContainer and VirtualResourcebundles with all his
	 * properties-files, which have RB_Marker.
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IFile){
			return true;
		} 
		if (element instanceof VirtualResourceBundle) {
			for (IResource f : ((VirtualResourceBundle)element).getFiles() ){
				if (RBFileUtils.hasResourceBundleMarker(f)) return true;
			}
		}
		if (element instanceof IContainer) {
			try {				
				if (((IContainer) element).findMarkers(EditorUtils.RB_MARKER_ID, true, IResource.DEPTH_INFINITE).length > 0) return true;
				
				
			} catch (CoreException e) {	}
		}
		return false;
	}

}
