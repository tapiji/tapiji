/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer, Matthias Lettmayer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Matthias Lettmayer - improved readFileAsString() to use Apache Commons IO (fixed issue 74)
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;

import org.eclipse.babel.tapiji.tools.core.Activator;
import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;

public class FileUtils {

    public static String readFile(IResource resource) {
        return readFileAsString(resource.getRawLocation().toFile());
    }

    protected static String readFileAsString(File filePath) {
        String content = "";

        if (!filePath.exists())
            return content;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(
                    filePath));
            String line = "";

            while ((line = fileReader.readLine()) != null) {
                content += line + "\n";
            }

            // close filereader
            fileReader.close();
        } catch (Exception e) {
            // TODO log error output
            Logger.logError(e);
        }

        return content;
    }

    public static File getRBManagerStateFile() {
        return Activator.getDefault().getStateLocation()
                .append("internationalization.xml").toFile();
    }

    /**
     * Don't use that -> causes {@link ResourceException} -> because File out of
     * sync
     * 
     * @param file
     * @param editorContent
     * @throws CoreException
     * @throws OperationCanceledException
     */
    public synchronized void saveTextFile(IFile file, String editorContent)
            throws CoreException, OperationCanceledException {
        try {
            file.setContents(
                    new ByteArrayInputStream(editorContent.getBytes()), false,
                    true, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
