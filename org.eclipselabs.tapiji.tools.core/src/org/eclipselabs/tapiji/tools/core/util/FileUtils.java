package org.eclipselabs.tapiji.tools.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipselabs.tapiji.tools.core.Activator;
import org.eclipselabs.tapiji.tools.core.Logger;


public class FileUtils {

	public static String readFile (IResource resource) {
		return readFileAsString(resource.getRawLocation().toFile());
	}
	
    protected static String readFileAsString(File filePath) {
    	String content = "";
    	
    	if (!filePath.exists()) return content;
    	try {
    		BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
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
		return Activator.getDefault().getStateLocation().append("internationalization.xml").toFile();
	}

	/**
	 * Don't use that -> causes {@link ResourceException} -> because File out of sync
	 * @param file
	 * @param editorContent
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	public synchronized void saveTextFile(IFile file, String editorContent) 
		   throws CoreException, OperationCanceledException {
		try {
			file.setContents(new ByteArrayInputStream(editorContent.getBytes()), 
					false, true, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
