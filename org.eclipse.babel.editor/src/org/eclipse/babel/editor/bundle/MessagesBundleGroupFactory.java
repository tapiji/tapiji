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

import org.eclipse.babel.core.message.MessagesBundleGroup;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorSite;


/**
 * @author Pascal Essiembre
 *
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
        MessagesBundleGroup defaultGroup = new MessagesBundleGroup(
                new DefaultBundleGroupStrategy(site, file));
        return defaultGroup;
        /*
         * Check if NL is supported.
         */
        //TODO implement NL support
//       if (!RBEPreferences.getSupportNL()) {
//           return defaultGroup;
//       }
//
//       /*
//        * Check if there is an NL directory
//        */
//       IContainer container = file.getParent();
//       IResource nlDir = null;
//       while (container != null 
//               && (nlDir == null || !(nlDir instanceof Folder))) {
//           nlDir = container.findMember("nl"); //$NON-NLS-1$
//           container = container.getParent();
//       }
//       if (nlDir == null || !(nlDir instanceof Folder)) {
//           return defaultGroup;
//       }
//
//       /*
//        * Ensures NL directory is part of file path, or that file dir
//        * is parent of NL directory.
//        */
//       IPath filePath = file.getFullPath();
//       IPath nlDirPath = nlDir.getFullPath();
//       if (!nlDirPath.isPrefixOf(filePath)
//               && !filePath.removeLastSegments(1).isPrefixOf(nlDirPath)) {
//           return defaultGroup;
//       }
//       
//       /*
//        * Ensure that there are no other files which could make a standard
//        * resource bundle.
//        */
//       if (defaultGroup.getBundleCount() > 1) {
//           return defaultGroup;
//       }
//       return new MessagesBundleGroup(new NLPluginBundleGroupStrategy());
    }
        
}
