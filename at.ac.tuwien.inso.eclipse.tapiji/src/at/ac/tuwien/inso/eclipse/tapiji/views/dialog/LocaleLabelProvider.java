package at.ac.tuwien.inso.eclipse.tapiji.views.dialog;

import java.util.Locale;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class LocaleLabelProvider implements ILabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
		
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		
	}

	@Override
	public Image getImage(Object element) {
		// TODO add image output for Locale entries
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element != null && element instanceof Locale)
			return ((Locale)element).getDisplayName();
		
		return null;
	}

}
