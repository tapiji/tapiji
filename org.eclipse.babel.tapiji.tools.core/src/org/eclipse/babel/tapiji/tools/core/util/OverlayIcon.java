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
package org.eclipse.babel.tapiji.tools.core.util;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class OverlayIcon extends CompositeImageDescriptor {

	public static final int TOP_LEFT = 0;
	public static final int TOP_RIGHT = 1;
	public static final int BOTTOM_LEFT = 2;
	public static final int BOTTOM_RIGHT = 3;

	private Image img;
	private Image overlay;
	private int location;
	private Point imgSize;

	public OverlayIcon(Image baseImage, Image overlayImage, int location) {
		super();
		this.img = baseImage;
		this.overlay = overlayImage;
		this.location = location;
		this.imgSize = new Point(baseImage.getImageData().width,
		        baseImage.getImageData().height);
	}

	@Override
	protected void drawCompositeImage(int width, int height) {
		drawImage(img.getImageData(), 0, 0);
		ImageData imageData = overlay.getImageData();

		switch (location) {
		case TOP_LEFT:
			drawImage(imageData, 0, 0);
			break;
		case TOP_RIGHT:
			drawImage(imageData, imgSize.x - imageData.width, 0);
			break;
		case BOTTOM_LEFT:
			drawImage(imageData, 0, imgSize.y - imageData.height);
			break;
		case BOTTOM_RIGHT:
			drawImage(imageData, imgSize.x - imageData.width, imgSize.y
			        - imageData.height);
			break;
		}
	}

	@Override
	protected Point getSize() {
		return new Point(img.getImageData().width, img.getImageData().height);
	}

}
