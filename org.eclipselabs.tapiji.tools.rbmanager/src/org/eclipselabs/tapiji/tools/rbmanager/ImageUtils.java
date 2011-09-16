package org.eclipselabs.tapiji.tools.rbmanager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipselabs.tapiji.tools.core.util.OverlayIcon;

public class ImageUtils {
	private static final ImageRegistry imageRegistry = new ImageRegistry();
	
	private static final String WARNING_FLAG_IMAGE = "warning_flag.gif";
	private static final String FRAGMENT_FLAG_IMAGE = "fragment_flag.gif";
	public static final String WARNING_IMAGE = "warning.gif";
	public static final String FRAGMENT_PROJECT_IMAGE = "fragmentproject.gif";
	public static final String RESOURCEBUNDLE_IMAGE = "resourcebundle.gif";
	public static final String DEFAULT_LOCALICON = File.separatorChar+"countries"+File.separatorChar+"_f.gif";
	public static final String LOCATION_WITHOUT_ICON = File.separatorChar+"countries"+File.separatorChar+"un.gif";

	
	/**
	 * Return ImageDescriptor
	 * @param name
	 * @return
	 * @throws MalformedURLException 
	 */
	public static ImageDescriptor getImageDescriptor(String name){
		try {
			URL url = getImageURL(name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
		}
		return null;
    }
	
	private static URL getImageURL(String name) throws MalformedURLException {
		String iconPath = "icons" + File.separator;
		URL installURL = RBManagerActivator.getDefault().getBundle().getEntry(File.separator);
		return new URL(installURL, iconPath + name);
	}
	
	/**
	 * @return a Image from the folder 'icons'
	 * @throws URISyntaxException 
	 */
	public static Image getBaseImage(String imageName){
		Image image = imageRegistry.get(imageName);
        if (image == null) {
        	ImageDescriptor descriptor = getImageDescriptor(imageName);
        	
        	if (descriptor.getImageData() != null){
        		image = descriptor.createImage(false);
				imageRegistry.put(imageName, image);
        	}
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
			Image warningImage = getBaseImage(WARNING_FLAG_IMAGE);
			imageWithWarning = new OverlayIcon(baseImage, warningImage, OverlayIcon.BOTTOM_LEFT).createImage();
			imageRegistry.put(imageWithWarningId, imageWithWarning);
		}
		
		return imageWithWarning;
	}
	
	public static Image getImageWithFragment(Image baseImage){
		String imageWithFragmentId = baseImage.toString()+".f";
		Image imageWithFragment =  imageRegistry.get(imageWithFragmentId);
		
		if (imageWithFragment==null){
			Image fragement =  getBaseImage(FRAGMENT_FLAG_IMAGE);
			imageWithFragment = new OverlayIcon(baseImage, fragement, OverlayIcon.BOTTOM_RIGHT).createImage();
			imageRegistry.put(imageWithFragmentId, imageWithFragment);
		}
		
		return imageWithFragment;
	}
	
	/**
	 * @return a Image with the flag of the country or if the country is not specified the standard language of given locale
	 */
	public static Image getLocalIcon(Locale locale) {
		String imageName;
		Image image = null;
		
		if (!locale.getCountry().equals("")){
			imageName = File.separatorChar+"countries"+File.separatorChar+ locale.getCountry().toLowerCase() +".gif";
			image = getBaseImage(imageName);
		}else {
			if (!locale.toString().equals("")){
				imageName = File.separatorChar+"countries"+File.separatorChar+"l_"+locale.getLanguage().toLowerCase() +".gif";
				image = getBaseImage(imageName);
			}else {
				imageName = DEFAULT_LOCALICON;				//Default locale icon
				image = getBaseImage(imageName);
			}
		}
		
		if (image == null) image = getBaseImage(LOCATION_WITHOUT_ICON);
		return image;
	}

}
