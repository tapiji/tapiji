package at.ac.tuwien.inso.eclipse.i18n.ui.decorators;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

import at.ac.tuwien.inso.eclipse.i18n.builder.InternationalizationNature;
import at.ac.tuwien.inso.eclipse.i18n.model.IResourceExclusionListener;
import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceExclusionEvent;
import at.ac.tuwien.inso.eclipse.i18n.util.ImageUtils;
import at.ac.tuwien.inso.eclipse.i18n.util.OverlayIcon;

public class ExcludedResource implements ILabelDecorator,
	IResourceExclusionListener {

	private static final String ENTRY_SUFFIX = "[no i18n]";
	private static final Image OVERLAY_IMAGE_ON = 
		ImageUtils.getImage(ImageUtils.IMAGE_EXCLUDED_RESOURCE_ON);
	private static final Image OVERLAY_IMAGE_OFF = 
		ImageUtils.getImage(ImageUtils.IMAGE_EXCLUDED_RESOURCE_OFF);
	private final List<ILabelProviderListener> label_provider_listener = 
		new ArrayList<ILabelProviderListener> ();
	
	public boolean decorate(Object element) {
		boolean needsDecoration = false;
		if (element instanceof IFolder ||
			element instanceof IFile) {
			IResource resource = (IResource) element; 
			if (!InternationalizationNature.hasNature(resource.getProject()))
				return false; 
			try {
				ResourceBundleManager manager = ResourceBundleManager.getManager(resource.getProject());
				if (!manager.isResourceExclusionListenerRegistered(this))
					manager.registerResourceExclusionListener(this);
				if (ResourceBundleManager.isResourceExcluded(resource)) {
					needsDecoration = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
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
		ResourceBundleManager.unregisterResourceExclusionListenerFromAllManagers (this);
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
		LabelProviderChangedEvent labelEvent = new LabelProviderChangedEvent(this, event.getChangedResources().toArray());
		for (ILabelProviderListener l : label_provider_listener)
			l.labelProviderChanged(labelEvent);
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		if (decorate(element)) {
			OverlayIcon overlayIcon = new OverlayIcon(image, OVERLAY_IMAGE_OFF, OverlayIcon.TOP_RIGHT);
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