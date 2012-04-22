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

import org.eclipse.babel.editor.api.AnalyzerFactory;
import org.eclipse.babel.tapiji.translator.rbe.model.analyze.ILevenshteinDistanceAnalyzer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.tapiji.translator.model.Term;
import org.eclipselabs.tapiji.translator.model.Translation;

public class FuzzyMatcher extends ExactMatcher {

	protected ILevenshteinDistanceAnalyzer lvda;
	protected float minimumSimilarity = 0.75f;
	
	public FuzzyMatcher(StructuredViewer viewer) {
		super(viewer);
		lvda = AnalyzerFactory.getLevenshteinDistanceAnalyzer();
	}

	public double getMinimumSimilarity () {
		return minimumSimilarity;
	}
	
	public void setMinimumSimilarity (float similarity) {
		this.minimumSimilarity = similarity;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean exactMatch = super.select(viewer, parentElement, element);
		boolean match = exactMatch;
		
		Term term = (Term) element;
		
		FilterInfo filterInfo = (FilterInfo) term.getInfo();
		
		for (Translation translation : term.getAllTranslations()) {
			String value = translation.value;
			String locale = translation.id;
			if (filterInfo.hasFoundInTranslation(locale))
				continue;
			double dist = lvda.analyse(value, getPattern());
			if (dist >= minimumSimilarity) {
				filterInfo.addFoundInTranslation(locale);
				filterInfo.addSimilarity(locale, dist);
				match = true;
				filterInfo.addFoundInTranslationRange(locale, 0, value.length());
			}
		}
		
		term.setInfo(filterInfo);
		return match; 
	}

}
