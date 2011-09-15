package org.eclipselabs.tapiji.tools.core.builder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipselabs.tapiji.tools.core.model.preferences.TapiJIPreferences;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;


public class BuilderPropertyChangeListener implements IPropertyChangeListener {

	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getNewValue().equals(true) && isTapiPropertyp(event))
			addProperty();
		
		if (event.getNewValue().equals(false)){			
			if (event.getProperty().equals(TapiJIPreferences.AUDIT_RESOURCE)){
					removeProperty(EditorUtils.MARKER_ID, -1);
			}
			if (event.getProperty().equals(TapiJIPreferences.AUDIT_RB)){
				removeProperty(EditorUtils.RB_MARKER_ID, -1);
			}
			if (event.getProperty().equals(TapiJIPreferences.AUDIT_UNSPEZIFIED_KEY)){
				removeProperty(EditorUtils.RB_MARKER_ID, IMarkerConstants.CAUSE_UNSPEZIFIED_KEY);
			}
			if (event.getProperty().equals(TapiJIPreferences.AUDIT_SAME_VALUE)){
				removeProperty(EditorUtils.RB_MARKER_ID, IMarkerConstants.CAUSE_SAME_VALUE);
			}
			if (event.getProperty().equals(TapiJIPreferences.AUDIT_MISSING_LANGUAGE)){
				removeProperty(EditorUtils.RB_MARKER_ID, IMarkerConstants.CAUSE_MISSING_LANGUAGE);
			}
		}
		
	}
	
	private boolean isTapiPropertyp(PropertyChangeEvent event) {
		if (event.getProperty().equals(TapiJIPreferences.AUDIT_RESOURCE) || 
				event.getProperty().equals(TapiJIPreferences.AUDIT_RB) ||
				event.getProperty().equals(TapiJIPreferences.AUDIT_UNSPEZIFIED_KEY) ||
				event.getProperty().equals(TapiJIPreferences.AUDIT_SAME_VALUE) ||
				event.getProperty().equals(TapiJIPreferences.AUDIT_MISSING_LANGUAGE))
			return true;
		else return false;
	}

	/*
	 * cause == -1 ignores the attribute 'case'
	 */
	private void removeProperty(String markertpye, int cause){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			IMarker[] marker = workspace.getRoot().findMarkers(markertpye, true, IResource.DEPTH_INFINITE);
			
			for (IMarker m : marker){
				if (m.exists()){
					if (m.getAttribute("cause", -1) == cause)
						m.delete();
					if (cause == -1)
						m.getResource().deleteMarkers(markertpye, true, IResource.DEPTH_INFINITE);
				}
			}
		} catch (CoreException e) {
		}
	}
	
	private void addProperty(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		
		try {
			for (IResource res : workspace.getRoot().members()){
				final IProject p = (IProject) res;
				new Job ("Audit source files") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							p.build ( StringLiteralAuditor.FULL_BUILD,
									StringLiteralAuditor.BUILDER_ID,
									null,
									monitor);
						} catch (CoreException e) {
							Logger.logError(e);
						}
						return Status.OK_STATUS;
					}
					
				}.schedule();
			}
		} catch (CoreException e1) {
		}
	}
	
}
