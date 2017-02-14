package org.eclipse.e4.tapiji.git.ui.property.provider;


import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.model.property.PropertyFile;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class PropertyTreeLabelProvider extends LabelProvider {

    private final Image folderImage;
    private final Image propertyImage;

    public PropertyTreeLabelProvider(Image folderImage, Image propertyImage) {
        this.folderImage = folderImage;
        this.propertyImage = propertyImage;
    }

    @Override
    public String getText(Object element) {
        if (element == null) {
            return "";
        } else {
            if (element instanceof PropertyDirectory) {
                return ((PropertyDirectory) element).getName();
            } else if (element instanceof PropertyFile) {
                return ((PropertyFile) element).getName();
            } else {
                return "" + element;
            }
        }
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof PropertyDirectory) {
            return folderImage;
        } else {
            return propertyImage;
        }
    }
}
