package org.eclipse.babel.editor.util;

import org.eclipse.swt.graphics.Image;

public class UIUtilsRAP {
	/**
     * Creates an image with current display.
     * @param imageName image name
     * @return image
     */
    public static Image getImage(String imageName) {
    	return UIUtils.getImageDescriptor(imageName).createImage();
    }
}
