package org.eclipselabs.e4.tapiji.resource.internal;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.e4.tapiji.resource.ITapijiResourceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;


public class ResourceLoader implements ITapijiResourceProvider {

    private static final Map<String, Image> IMAGES = new HashMap<String, Image>();

    @Override
    public Image loadImage(final String path) {
        Image img = IMAGES.get(path);
        if (null == img) {
            final Bundle bundle = FrameworkUtil.getBundle(ResourceLoader.class);
            final URL url = FileLocator.find(bundle, new Path(path), null);
            final ImageDescriptor imageDescr = ImageDescriptor.createFromURL(url);
            img = imageDescr.createImage();
            IMAGES.put(path, img);
        }
        return img;
    }

    @Override
    public void dispose() {
        for (Image image : IMAGES.values()) {
            image.dispose();
        }
    }
}
