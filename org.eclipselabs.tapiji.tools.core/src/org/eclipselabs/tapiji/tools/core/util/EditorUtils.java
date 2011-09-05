package org.eclipselabs.tapiji.tools.core.util;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipselabs.tapiji.tools.core.Activator;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.extensions.ILocation;
import org.eclipselabs.tapiji.tools.core.extensions.IMarkerConstants;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;


public class EditorUtils {
	
	/** Marker constants **/
	public static final String MARKER_ID = Activator.PLUGIN_ID
												+ ".StringLiteralAuditMarker";
	public static final String RB_MARKER_ID = Activator.PLUGIN_ID + ".ResourceBundleAuditMarker";
	
	/** Error messages **/
	public static final String MESSAGE_NON_LOCALIZED_LITERAL = "Non-localized string literal ''{0}'' has been found";
	public static final String MESSAGE_BROKEN_RESOURCE_REFERENCE = "Cannot find the key ''{0}'' within the resource-bundle ''{1}''";
	public static final String MESSAGE_BROKEN_RESOURCE_BUNDLE_REFERENCE = "The resource bundle with id ''{0}'' cannot be found";
	
	public static final String MESSAGE_UNSPECIFIED_KEYS  = "Missing or unspecified key ''{0}'' has been found";
	public static final String MESSAGE_SAME_VALUE  = "''{0}'' and ''{1}'' have the same translation for the key ''{2}''";
	public static final String MESSAGE_MISSING_LANGUAGE = " ResourceBundle ''{0}'' lacks a translation for ''{1}''";
	
	/** Editor ids **/
	public static final String RESOURCE_BUNDLE_EDITOR = "com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor";
	
	public static String getFormattedMessage (String pattern, Object[] arguments) {
		String formattedMessage = "";
		
		MessageFormat formatter = new MessageFormat(pattern);
		formattedMessage = formatter.format(arguments);
		
		return formattedMessage;
	}
	
	public static void openEditor (IWorkbenchPage page, IFile file, String editor) {
		// open the default-editor for this file type
		try {
			// TODO open resourcebundleeditor
			IDE.openEditor(page, file, editor);
		} catch (Exception e) {
			Logger.logError(e);
		}
	}
	
	public static void reportToMarker(String string, ILocation problem, int cause, String key, ILocation data, String context) {
		try {
			IMarker marker = problem.getFile().createMarker(MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, string);
			marker.setAttribute(IMarker.CHAR_START, problem.getStartPos());
			marker.setAttribute(IMarker.CHAR_END, problem.getEndPos());
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute("cause", cause);
			marker.setAttribute("key", key);
			marker.setAttribute("context", context);
			if (data != null) {
				marker.setAttribute("bundleName", data.getLiteral());
				marker.setAttribute("bundleStart", data.getStartPos());
				marker.setAttribute("bundleEnd", data.getEndPos());
			}
			
			// TODO: init attributes
			marker.setAttribute("stringLiteral", string);
		} catch (CoreException e) {
			Logger.logError(e);
			return;
		}
		
		Logger.logInfo(string);
	}

	public static void reportToRBMarker(String string, ILocation problem, int cause, String key, String problemPartnerFile, ILocation data, String context) {
		try {
			if (!problem.getFile().exists()) return;
			IMarker marker = problem.getFile().createMarker(RB_MARKER_ID);
			marker.setAttribute(IMarker.MESSAGE, string);
			marker.setAttribute(IMarker.LINE_NUMBER, problem.getStartPos());		//TODO better-dirty implementation
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			marker.setAttribute("cause", cause);
			marker.setAttribute("key", key);
			marker.setAttribute("context", context);
			if (data != null) {
				marker.setAttribute("language", data.getLiteral());
				marker.setAttribute("bundleLine", data.getStartPos());
			}
			marker.setAttribute("stringLiteral", string);
			marker.setAttribute("problemPartner", problemPartnerFile);
		} catch (CoreException e) {
			Logger.logError(e);
			return;
		}
		
		Logger.logInfo(string);
	}
	
	public static boolean deleteAuditMarkersForResource(IResource resource) {
		try {
			if (resource != null && resource.exists()) {				
				resource.deleteMarkers(MARKER_ID, false, IResource.DEPTH_INFINITE);
				deleteAllAuditRBMarkersFromRB(resource);
			}
		} catch (CoreException e) {
			Logger.logError(e);
			return false;
		}
		return true;
	}
	
	/*
	 * Delete all RB_MARKER from the hole resourcebundle
	 */
	private static boolean deleteAllAuditRBMarkersFromRB(IResource resource) throws CoreException{
//		if (resource.findMarkers(RB_MARKER_ID, false, IResource.DEPTH_INFINITE).length > 0)
			if (RBFileUtils.checkIsResourceBundleFile(resource)){
				String rbId = RBFileUtils.getCorrespondingResourceBundleId((IFile)resource);
				if (rbId==null) return true;								//file in no resourcebundle
				
				ResourceBundleManager rbmanager = ResourceBundleManager.getManager(resource.getProject());
				for(IResource r : rbmanager.getResourceBundles(rbId))
					r.deleteMarkers(RB_MARKER_ID, false, IResource.DEPTH_INFINITE);
			}	
		return true;
	}
}
