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
package org.eclipselabs.tapiji.translator.views.widgets.filter;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipselabs.tapiji.translator.model.Term;
import org.eclipselabs.tapiji.translator.model.Translation;

public class ExactMatcher extends ViewerFilter {

	protected final StructuredViewer viewer;
	protected String pattern = "";
	protected StringMatcher matcher;

	public ExactMatcher(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String p) {
		boolean filtering = matcher != null;
		if (p != null && p.trim().length() > 0) {
			pattern = p;
			matcher = new StringMatcher("*" + pattern + "*", true, false);
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
		Term term = (Term) element;
		FilterInfo filterInfo = new FilterInfo();
		boolean selected = false;

		// Iterate translations
		for (Translation translation : term.getAllTranslations()) {
			String value = translation.value;
			String locale = translation.id;
			if (matcher.match(value)) {
				filterInfo.addFoundInTranslation(locale);
				filterInfo.addSimilarity(locale, 1d);
				int start = -1;
				while ((start = value.toLowerCase().indexOf(
				        pattern.toLowerCase(), start + 1)) >= 0) {
					filterInfo.addFoundInTranslationRange(locale, start,
					        pattern.length());
				}
				selected = true;
			}
		}

		term.setInfo(filterInfo);
		return selected;
	}

}
