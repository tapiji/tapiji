package at.ac.tuwien.inso.eclipse.i18n.model;

public interface IResourceDescriptor {

	public void setProjectName (String projName);
	
	public void setRelativePath (String relPath);
	
	public void setAbsolutePath (String absPath);
	
	public void setBundleId (String bundleId);
	
	public String getProjectName ();
	
	public String getRelativePath ();
	
	public String getAbsolutePath ();
	
	public String getBundleId ();
	
}
