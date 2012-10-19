package org.eclipselabs.tapiji.translator.views.widgets.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public class StorageTreeContentProvider implements ITreeContentProvider {
	
	List<String> displayedRBs = new ArrayList<String>();
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((List) inputElement).toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ResourceBundle) {
			ResourceBundle rb = (ResourceBundle) parentElement;
			return rb.getPropertiesFiles().toArray();
		} else {
			return null;
		}
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PropertiesFile) {
			PropertiesFile file = (PropertiesFile) element;
			file.getResourceBundle();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ResourceBundle){
			return true;
		}
		return false;
	}

}
