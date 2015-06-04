package org.eclipselabs.e4.tapiji.resource.internal;


import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.e4.tapiji.resource.ITapijiResourceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;


public class ResourceLoader implements ITapijiResourceProvider {

    @Override
    public Image loadImage(String path) {
        Bundle bundle = FrameworkUtil.getBundle(ResourceLoader.class);
        URL url = FileLocator.find(bundle, new Path(path), null);
        ImageDescriptor imageDescr = ImageDescriptor.createFromURL(url);
        return imageDescr.createImage();
    }
}
