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
package org.eclipse.babel.tapiji.tools.core.ui.widgets.filter;

import java.util.Locale;

import org.eclipse.babel.editor.api.AnalyzerFactory;
import org.eclipse.babel.editor.api.ILevenshteinDistanceAnalyzer;
import org.eclipse.babel.editor.api.IValuedKeyTreeNode;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

public class FuzzyMatcher extends ExactMatcher {

	protected ILevenshteinDistanceAnalyzer lvda;
	protected float minimumSimilarity = 0.75f;

	public FuzzyMatcher(StructuredViewer viewer) {
		super(viewer);
		lvda = AnalyzerFactory.getLevenshteinDistanceAnalyzer();
		;
	}

	public double getMinimumSimilarity() {
		return minimumSimilarity;
	}

	public void setMinimumSimilarity(float similarity) {
		this.minimumSimilarity = similarity;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean exactMatch = super.select(viewer, parentElement, element);
		boolean match = exactMatch;

		IValuedKeyTreeNode vkti = (IValuedKeyTreeNode) element;
		FilterInfo filterInfo = (FilterInfo) vkti.getInfo();

		for (Locale l : vkti.getLocales()) {
			String value = vkti.getValue(l);
			if (filterInfo.hasFoundInLocale(l))
				continue;
			double dist = lvda.analyse(value, getPattern());
			if (dist >= minimumSimilarity) {
				filterInfo.addFoundInLocale(l);
				filterInfo.addSimilarity(l, dist);
				match = true;
				filterInfo.addFoundInLocaleRange(l, 0, value.length());
			}
		}

		vkti.setInfo(filterInfo);
		return match;
	}

}
