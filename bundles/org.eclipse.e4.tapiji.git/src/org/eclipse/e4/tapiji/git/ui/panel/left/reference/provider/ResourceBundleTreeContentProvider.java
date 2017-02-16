package org.eclipse.e4.tapiji.git.ui.panel.left.reference.provider;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.jface.viewers.ITreeContentProvider;


public class ResourceBundleTreeContentProvider implements ITreeContentProvider {

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        return ((List<PropertyDirectory>) inputElement).toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        return ((PropertyDirectory) parentElement).getFiles().toArray();
    }

    @Override
    public Object getParent(Object element) {
        return element;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof PropertyDirectory) {
            PropertyDirectory category = (PropertyDirectory) element;
            return category.hasFiles();
        }
        return false;
    }

}
