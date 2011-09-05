package com.gknsintermetals.eclipse.resourcebundle.manager.viewer.actions;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;

import com.gknsintermetals.eclipse.resourcebundle.manager.model.VirtualResourceBundle;

public class RBMarkerInformant implements HoverInformant {

	private int MAX_PROBLEMS = 20;
	private boolean show = true;
	
	@Override
	public String getText(Object data) {
		show = true;
		IMarker[] ms = null;
		
		if (data instanceof IResource){
			IResource res = (IResource) data;
			try {
				ms = res.findMarkers(EditorUtils.RB_MARKER_ID, false,
						IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		if (data instanceof VirtualResourceBundle){
			VirtualResourceBundle vRB = (VirtualResourceBundle) data;
			
			ResourceBundleManager rbmanager = vRB.getResourceBundleManager();
			IMarker[] file_ms;
			for (IResource r : rbmanager.getResourceBundles(vRB.getResourceBundleId())){
				try {
					file_ms = r.findMarkers(EditorUtils.RB_MARKER_ID, false, IResource.DEPTH_INFINITE);
					if (ms != null) {
						IMarker[] old_ms = ms;
						ms = new IMarker[old_ms.length + file_ms.length];

						System.arraycopy(old_ms, 0, ms, 0, old_ms.length);
						System.arraycopy(file_ms, 0, ms, old_ms.length,
								file_ms.length);
					}else{
						ms = file_ms;
					}
//					if (ms == null || ms.length == 0) ms = file_ms;
//					else if (file_ms !=null && file_ms.length != 0)
//						System.arraycopy(file_ms, 0, ms, 0, file_ms.length);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		StringBuilder sb = new StringBuilder("Problems: \n");
		int count=0;
		
		if (ms != null && ms.length!=0)
			for (IMarker m : ms) {
				try {
					sb.append(m.getAttribute(IMarker.MESSAGE));
					sb.append("\n");
					count++;
					if (count == MAX_PROBLEMS){
						sb.append(" ... and ");
						sb.append(ms.length-count);
						sb.append(" other problems");
						break;
					}
				} catch (CoreException e) {
				}
				;
			}
		else show = false;
		
		return sb.toString();
	}

	@Override
	public String getTitel(Object data) {
		
		if (data instanceof IResource){
			return ((IResource)data).getFullPath().toString();
		}
		if (data instanceof VirtualResourceBundle){
			return ((VirtualResourceBundle)data).getResourceBundleId();
		}
		
		return "";
	}

	@Override
	public boolean show(){
		return show;
	}
}
