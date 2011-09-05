package com.gknsintermetals.eclipse.resourcebundle.manager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.tools.core.util.OverlayIcon;

public class ImageUtils {
	private static final ImageRegistry imageRegistry = new ImageRegistry();
	
	private static final String WARNING_IMAGE = "warning.gif";
	private static final String FRAGMENT_IMAGE = "fragment.gif";
	public static final String RESOURCEBUNDLE_IMAGE = "resourcebundle.gif";
	public static final String DEFAULT_LOCALICON = File.separatorChar+"countries"+File.separatorChar+"_f.gif";
	
	/**
	 * Return ImageDescriptor
	 * @param name
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String name) {
        String iconPath = "icons"+File.separator;
        try {
            URL installURL = RBManagerActivator.getDefault().getBundle().getEntry(File.separator);
            URL url = new URL(installURL, iconPath + name);
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }
	
	/**
	 * @return a Image from the folder 'icons'
	 */
	public static Image getBaseImage(String imageName){
		Image image = imageRegistry.get(imageName);
        if (image == null) {
        	image = getImageDescriptor(imageName).createImage();
            imageRegistry.put(imageName, image);
        }
        
        return image;
	}
	
	/**
	 * 
	 * @param baseImage
	 * @return baseImage with a warning-image
	 */
	public static Image getImageWithWarning(Image baseImage){
		String imageWithWarningId = baseImage.toString()+".w";
		Image imageWithWarning =  imageRegistry.get(imageWithWarningId);
		
		if (imageWithWarning==null){
			imageWithWarning = new OverlayIcon(baseImage, getBaseImage(WARNING_IMAGE), OverlayIcon.BOTTOM_RIGHT).createImage();
			imageRegistry.put(imageWithWarningId, imageWithWarning);
		}
		
		return imageWithWarning;
	}
	
	public static Image getImageWithFragment(Image baseImage){
		String imageWithFragmentId = baseImage.toString()+".f";
		Image imageWithFragment =  imageRegistry.get(imageWithFragmentId);
		
		if (imageWithFragment==null){
			Image fragement =  getBaseImage(FRAGMENT_IMAGE);
			imageWithFragment = new OverlayIcon(baseImage, fragement, OverlayIcon.TOP_LEFT).createImage();
			imageRegistry.put(imageWithFragmentId, imageWithFragment);
		}
		
		return imageWithFragment;
	}
	
	/**
	 * @return a Image with the flag of the country or if the country is not specified the standard language of given locale
	 */
	public static Image getLocalIcon(Locale locale) {
		String imageName;
		
		if (!locale.getCountry().equals("")){
			imageName = File.separatorChar+"countries"+File.separatorChar+ locale.getCountry() +".gif";
		}else {
			if (!locale.toString().equals(""))
				imageName = File.separatorChar+"countries"+File.separatorChar+ locale.getLanguage() +".gif";
			else imageName = DEFAULT_LOCALICON;				//Default locale icon
		}
		
		return getBaseImage(imageName);
	}

}
