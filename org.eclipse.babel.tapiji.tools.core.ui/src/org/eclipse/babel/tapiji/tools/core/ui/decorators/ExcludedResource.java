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
package org.eclipse.babel.tapiji.tools.core.ui.decorators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.model.IResourceExclusionListener;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceExclusionEvent;
import org.eclipse.babel.tapiji.tools.core.ui.Activator;
import org.eclipse.babel.tapiji.tools.core.ui.builder.InternationalizationNature;
import org.eclipse.babel.tapiji.tools.core.ui.utils.ImageUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

public class ExcludedResource implements ILabelDecorator,
        IResourceExclusionListener {

	private static final String ENTRY_SUFFIX = "[no i18n]";
	private static final Image OVERLAY_IMAGE_ON = ImageUtils
	        .getImage(ImageUtils.IMAGE_EXCLUDED_RESOURCE_ON);
	private static final Image OVERLAY_IMAGE_OFF = ImageUtils
	        .getImage(ImageUtils.IMAGE_EXCLUDED_RESOURCE_OFF);
	private final List<ILabelProviderListener> label_provider_listener = new ArrayList<ILabelProviderListener>();

	public boolean decorate(Object element) {
		boolean needsDecoration = false;
		if (element instanceof IFolder || element instanceof IFile) {
			IResource resource = (IResource) element;
			if (!InternationalizationNature.hasNature(resource.getProject()))
				return false;
			try {
				ResourceBundleManager manager = ResourceBundleManager
				        .getManager(resource.getProject());
				if (!manager.isResourceExclusionListenerRegistered(this))
					manager.registerResourceExclusionListener(this);
				if (ResourceBundleManager.isResourceExcluded(resource)) {
					needsDecoration = true;
				}
			} catch (Exception e) {
				Logger.logError(e);
			}
		}
		return needsDecoration;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		label_provider_listener.add(listener);
	}

	@Override
	public void dispose() {
		ResourceBundleManager
		        .unregisterResourceExclusionListenerFromAllManagers(this);
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		label_provider_listener.remove(listener);
	}

	@Override
	public void exclusionChanged(ResourceExclusionEvent event) {
		LabelProviderChangedEvent labelEvent = new LabelProviderChangedEvent(
		        this, event.getChangedResources().toArray());
		for (ILabelProviderListener l : label_provider_listener)
			l.labelProviderChanged(labelEvent);
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		if (decorate(element)) {
			DecorationOverlayIcon overlayIcon = new DecorationOverlayIcon(
			        image,
			        Activator
			                .getImageDescriptor(ImageUtils.IMAGE_EXCLUDED_RESOURCE_OFF),
			        IDecoration.TOP_RIGHT);
			return overlayIcon.createImage();
		} else {
			return image;
		}
	}

	@Override
	public String decorateText(String text, Object element) {
		if (decorate(element)) {
			return text + " " + ENTRY_SUFFIX;
		} else
			return text;
	}

}
