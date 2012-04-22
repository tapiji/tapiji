/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Locale;

import org.eclipse.babel.tapiji.tools.core.util.OverlayIcon;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class ImageUtils {
	private static final ImageRegistry imageRegistry = new ImageRegistry();
	
	private static final String WARNING_FLAG_IMAGE = "warning_flag.gif";
	private static final String FRAGMENT_FLAG_IMAGE = "fragment_flag.gif";
	public static final String WARNING_IMAGE = "warning.gif";
	public static final String FRAGMENT_PROJECT_IMAGE = "fragmentproject.gif";
	public static final String RESOURCEBUNDLE_IMAGE = "resourcebundle.gif";
	public static final String EXPAND = "expand.gif";
	public static final String DEFAULT_LOCALICON = File.separatorChar+"countries"+File.separatorChar+"_f.gif";
	public static final String LOCATION_WITHOUT_ICON = File.separatorChar+"countries"+File.separatorChar+"un.gif";
	
	/**
	 * @return a Image from the folder 'icons'
	 * @throws URISyntaxException 
	 */
	public static Image getBaseImage(String imageName){
		Image image = imageRegistry.get(imageName);
        if (image == null) {
        	ImageDescriptor descriptor = RBManagerActivator.getImageDescriptor(imageName);
        	
        	if (descriptor.getImageData() != null){
        		image = descriptor.createImage(false);
				imageRegistry.put(imageName, image);
        	}
        }
        
        return image;
	}
	
	/**
	 * @param baseImage
	 * @return baseImage with a overlay warning-image
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
	
	/**
	 * 
	 * @param baseImage
	 * @return baseImage with a overlay fragment-image
	 */
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
	 * @return a Image with a flag of the given country
	 */
	public static Image getLocalIcon(Locale locale) {
		String imageName;
		Image image = null;
		
		if (locale != null && !locale.getCountry().equals("")){
			imageName = File.separatorChar+"countries"+File.separatorChar+ locale.getCountry().toLowerCase() +".gif";
			image = getBaseImage(imageName);
		}else {
			if (locale != null){
				imageName = File.separatorChar+"countries"+File.separatorChar+"l_"+locale.getLanguage().toLowerCase()+".gif";
				image = getBaseImage(imageName);
			}else {
				imageName = DEFAULT_LOCALICON.toLowerCase();				//Default locale icon
				image = getBaseImage(imageName);
			}
		}
		
		if (image == null) image = getBaseImage(LOCATION_WITHOUT_ICON.toLowerCase());
		return image;
	}

}
