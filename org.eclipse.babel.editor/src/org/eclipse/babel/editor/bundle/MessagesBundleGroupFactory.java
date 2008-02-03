/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.editor.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

import org.eclipse.babel.core.message.MessagesBundleGroup;
import org.eclipse.babel.editor.preferences.MsgEditorPreferences;
import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorSite;


/**
 * @author Pascal Essiembre
 * @author Hugues Malphettes
 */
//TODO make /*default*/ that only the registry would use
public final class MessagesBundleGroupFactory {

    /**
     * 
     */
    private MessagesBundleGroupFactory() {
        super();
    }

    /**
     * Creates a new bundle group, based on the given site and file.  Currently,
     * only default bundle groups and Eclipse NL within a plugin are supported.
     * @param site
     * @param file
     * @return
     */
    public static MessagesBundleGroup createBundleGroup(IEditorSite site, IFile file) {
//  	MessagesBundleGroup defaultGroup = new MessagesBundleGroup(
//  	new DefaultBundleGroupStrategy(site, file));
    	//return defaultGroup;
    	/*
    	 * Check if NL is supported.
    	 */
    	//TODO implement NL support
    	if (!MsgEditorPreferences.getInstance().isNLSupportEnabled()) {
    		return createDefaultBundleGroup(site, file);
    	}

    	//check we are inside an eclipse plugin project where NL is supported at runtime:
    	try {
    		IProject proj = file.getProject();
    		if (proj == null || proj.getNature(UIUtils.PDE_NATURE) == null) { //$NON-NLS-1$
    			return createDefaultBundleGroup(site, file);
    		}
    	} catch (CoreException e) {
    		return createDefaultBundleGroup(site, file);
    	}

    	IFolder nl = getNLFolder(file);
    	//look if we are inside a fragment plugin:
    	String hostId = getHostPluginId(file);
    	if (hostId != null) {
    		//we are indeed inside a fragment.
    		//use the appropriate strategy.
    		return new MessagesBundleGroup(
    				new NLFragmentBundleGroupStrategy(site, file, hostId, nl));
    	}

    	/*
    	 * Check if there is an NL directory
    	 * something like: nl/en/US/messages.properties
    	 * which is for eclipse runtime equivalent to: messages_en_US.properties
    	 */
    	return new MessagesBundleGroup(new NLPluginBundleGroupStrategy(
    			site, file, nl));

    }

    private static MessagesBundleGroup createDefaultBundleGroup(IEditorSite site, IFile file) {
    	return new MessagesBundleGroup(
                new DefaultBundleGroupStrategy(site, file));
    }
    
//reading plugin manifests related utility methods. TO BE MOVED TO CORE ?
    
    private static String FRAG_HOST = "Fragment-Host:"; //$NON-NLS-1$
    /**
     * @param file
     * @return The id of the host-plugin if the edited file is inside a
     * pde-project that is a fragment. null otherwise.
     */
    private static String getHostPluginId(IFile file) {
    	return getPDEManifestAttribute(file, FRAG_HOST);
    }
    /**
     * Fetches the IProject in which openedFile is located.
     * If the project is a PDE project, looks for the MANIFEST.MF file
     * Parses the file and returns the value corresponding to the key
     * The value is stripped of its eventual properties (version constraints and others).
     */
    static String getPDEManifestAttribute(IResource openedFile, String key) {
    	IProject proj = openedFile.getProject();
    	if (proj == null && proj.isAccessible()) {
    		return null;
    	}
    	try {
			if (proj.getNature(UIUtils.PDE_NATURE) == null) { //$NON-NLS-1$
				return null;
			}
		} catch (CoreException e) {
			return null;
		}
		IResource mf = proj.findMember(new Path("META-INF/MANIFEST.MF")); //$NON-NLS-1$
		if (mf == null || mf.getType() != IResource.FILE) {
			return null;
		}
		//now look for the FragmentHost.
		//don't use the java.util.Manifest API to parse the manifest as sometimes,
		//eclipse tolerates faulty manifests where lines are more than 70 characters long.
		InputStream in = null;
		try {
			 in = ((IFile)mf).getContents();
			//supposedly in utf-8. should not really matter for us
			 Reader r = new InputStreamReader(in, "UTF-8");
			 LineNumberReader lnr = new LineNumberReader(r);
			 String line = lnr.readLine();
			 while (line != null) {
				if (line.startsWith(key)) {
					String value = line.substring(key.length());
					int index = value.indexOf(';');
					if (index != -1) {
						//remove the versions constraints and other properties.
						value = value.substring(0, index);
					}
					return value.trim();
				}
				line = lnr.readLine();
			 }
			 lnr.close();
			 r.close();
		} catch (IOException ioe) {
			//TODO: something!
			ioe.printStackTrace();
		} catch (CoreException ce) {
			//TODO: something!
			ce.printStackTrace();
		} finally {
			if (in != null) try { in.close(); } catch (IOException e) {}
		}
		return null;
    }
    
    /**
     * @see http://dev.eclipse.org/mhonarc/lists/babel-dev/msg00111.
     * 
     * @param openedFile
     * @return The nl folder that is a direct child of the project and an ancestor
     * of the opened file or null if no such thing.
     */
    protected static IFolder getNLFolder(IFile openedFile) {
    	IContainer cont = openedFile.getParent();
    	while (cont != null) {
    		if (cont.getParent() != null
    				&& cont.getParent().getType() == IResource.PROJECT) {
    			if (cont.getName().equals("nl")) {
    				return (IFolder)cont;
    			}
    			return null;
    		}
    		cont = cont.getParent();
    	}
    	return null;
    }   
        
}
 