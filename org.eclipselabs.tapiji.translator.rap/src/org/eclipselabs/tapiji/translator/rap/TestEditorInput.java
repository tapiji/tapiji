package org.eclipselabs.tapiji.translator.rap;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TestEditorInput implements IEditorInput {

	public static final String ID = "org.eclipselabs.tapiji.translator.rap.TestEditorInput";
	
	private final File file;
	
	public TestEditorInput(File file) {
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
		return file.getAbsolutePath();
	}

}
