package org.eclipselabs.tapiji.translator.rap.views.dialog;

import java.util.List;
import java.util.Locale;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class LocaleContentProvider implements IStructuredContentProvider {
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List) {
			List<Locale> locales = (List<Locale>) inputElement;
			return locales.toArray(new Locale[locales.size()]);
		}
		return null;
	}

}
