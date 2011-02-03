package at.ac.tuwien.inso.eclipse.tapiji.utils;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileUtils {

    /** Token to replace in a regular expression with a bundle name. */
    private static final String TOKEN_BUNDLE_NAME = "BUNDLENAME"; 
    /** Token to replace in a regular expression with a file extension. */
    private static final String TOKEN_FILE_EXTENSION = 
            "FILEEXTENSION"; 
    /** Regex to match a properties file. */
    private static final String PROPERTIES_FILE_REGEX = 
            "^(" + TOKEN_BUNDLE_NAME + ")"  
          + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})" 
          + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\." 
          + TOKEN_FILE_EXTENSION + ")$"; 
	
	/** The singleton instance of Workspace */
	private static IWorkspace workspace;
	
	/** Wrapper project for external file resources */
	private static IProject project;
	
	public static boolean isResourceBundle (String fileName) {
		return fileName.toLowerCase().endsWith(".properties");
	}
	
	public static boolean isGlossary (String fileName) {
		return fileName.toLowerCase().endsWith(".xml");
	}

	public static IWorkspace getWorkspace () {
		if (workspace == null) {
			workspace = ResourcesPlugin.getWorkspace();
		}
		
		return workspace;
	}
	
	public static IProject getProject () throws CoreException {
		if (project == null) {
			project = getWorkspace().getRoot().getProject("ExternalResourceBundles");
		}
		
		if (!project.exists())
			project.create(null);
		if (!project.isOpen())
			project.open(null);
		
		return project;
	}
	
	public static void prePareEditorInputs () {
		IWorkspace workspace = getWorkspace();
	}
	
	public static IFile getResourceBundleRef (String location) throws CoreException {
		IPath path = new Path (location);
		
		/** Create all files of the Resource-Bundle within the project space and link them to the original file */
		String regex = getPropertiesFileRegEx(path);
		String projPathName = toProjectRelativePathName(path);
		IFile file = getProject().getFile(projPathName);
		file.createLink(path, IResource.REPLACE, null);
		
		File parentDir = new File (path.toFile().getParent());
		String[] files = parentDir.list();

        for (String fn : files) {
            File fo = new File (parentDir, fn);
            if (!fo.isFile())
            	continue;
            
            IPath newFilePath = new Path(fo.getAbsolutePath());
            if (fo.getName().matches(regex) && !path.toFile().getName().equals(newFilePath.toFile().getName())) {
                IFile newFile = project.getFile(toProjectRelativePathName(newFilePath));
                newFile.createLink(newFilePath, IResource.REPLACE, null);
            }
        }
		
		return file;
	}

	protected static String toProjectRelativePathName (IPath path) {
		String projectRelativeName = "";
		
		projectRelativeName = path.toString().replaceAll(":", "");
		projectRelativeName = projectRelativeName.replaceAll("/", ".");
		
		return projectRelativeName;
	}
	
	protected static String getPropertiesFileRegEx(IPath file) {
	    String bundleName = getBundleName(file);
	    return PROPERTIES_FILE_REGEX.replaceFirst(
	            TOKEN_BUNDLE_NAME, bundleName).replaceFirst(
	                    TOKEN_FILE_EXTENSION, file.getFileExtension());
	}
	
	public static String getBundleName(IPath file) {
        String name = file.toFile().getName();
        String regex = "^(.*?)" //$NON-NLS-1$
                + "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})" 
                + "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\." 
                + file.getFileExtension() + ")$";
        return name.replaceFirst(regex, "$1"); 
    }
	
	public static String queryFileName(Shell shell, String title, int dialogOptions, String[] endings) {
		FileDialog dialog= new FileDialog(shell, dialogOptions);
		dialog.setText( title ); 
		dialog.setFilterExtensions(endings);
		String path= dialog.open();
		
		
		if (path != null && path.length() > 0)
			return path;
		return null;
	}
	
}
