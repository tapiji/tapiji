package org.eclipselabs.tapiji.translator.rap.extResources;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TextEditorInput implements IEditorInput {

	public static final String ID = "org.eclipselabs.tapiji.translator.rap.TestEditorInput";
	
	private final IFile file;
	
	public TextEditorInput(IFile file) {
		this.file = file;
	}
	
	@Override
	public Object getAdapter(Class adapter) {
		return file;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		return file.getName();
	}

}
