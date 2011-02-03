package at.ac.tuwien.inso.eclipse.i18n.extensions;

import org.eclipse.core.resources.IFile;

public interface ILocation {
	public IFile getFile();
	
	public void setFile(IFile file);
	
	public int getStartPos();
	
	public void setStartPos(int startPos);
	
	public int getEndPos();
	
	public void setEndPos(int endPos);
	
	public String getLiteral();
	
	public Object getData ();
	
	public void setData (Object data);
}
