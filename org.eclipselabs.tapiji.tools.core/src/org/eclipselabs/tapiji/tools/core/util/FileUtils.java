package org.eclipselabs.tapiji.tools.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
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

	public static void saveTextFile(IFile file, String editorContent) 
		   throws CoreException, OperationCanceledException {
		try {
			// TODO hand over progress monitor
			file.setContents(new ByteArrayInputStream(editorContent.getBytes()), 
					false, true, null);
		} catch (Exception e) {
			
		}
	}
	
}
