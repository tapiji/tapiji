/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.widgets.filter;

import java.util.Locale;

import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ExactMatcher extends ViewerFilter {

	protected final StructuredViewer viewer;
	protected String pattern = "";
	protected StringMatcher matcher;
	
	public ExactMatcher (StructuredViewer viewer) {
		this.viewer = viewer;
	}
	
	public String getPattern () {
		return pattern;
	}
	
	public void setPattern (String p) {
		boolean filtering = matcher != null;
		if (p != null && p.trim().length() > 0) {
			pattern = p;
			matcher = new StringMatcher ("*" + pattern + "*", true, false);
			if (!filtering)
				viewer.addFilter(this);
			else
				viewer.refresh();
		} else {
			pattern = "";
			matcher = null;
			if (filtering) {
				viewer.removeFilter(this);
			}
		}
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		IValuedKeyTreeNode vEle = (IValuedKeyTreeNode) element;
		FilterInfo filterInfo = new FilterInfo();
		boolean selected = matcher.match(vEle.getMessageKey());
		
		if (selected) {
			int start = -1;
			while ((start = vEle.getMessageKey().toLowerCase().indexOf(pattern.toLowerCase(), start+1)) >= 0) {
				filterInfo.addKeyOccurrence(start, pattern.length());
			}
			filterInfo.setFoundInKey(selected);
			filterInfo.setFoundInKey(true);
		} else
			filterInfo.setFoundInKey(false);
		
		// Iterate translations
		for (Locale l : vEle.getLocales()) {
			String value = vEle.getValue(l);
			if (matcher.match(value)) {
				filterInfo.addFoundInLocale(l);
				filterInfo.addSimilarity(l, 1d);
				int start = -1;
				while ((start = value.toLowerCase().indexOf(pattern.toLowerCase(), start+1)) >= 0) {
					filterInfo.addFoundInLocaleRange(l, start, pattern.length());
				}
				selected = true;
			}
		}
		
		vEle.setInfo(filterInfo);
		return selected;
	}


}
