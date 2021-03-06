/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.ui.widget.filter;


import org.eclipse.e4.babel.editor.model.checks.proximity.LevenshteinDistanceAnalyzer;
import org.eclipse.e4.babel.editor.model.checks.proximity.ProximityAnalyzer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.model.filter.FilterInfo;


public class FuzzyMatcher extends ExactMatcher {

    protected ProximityAnalyzer lvda;
    protected float minimumSimilarity = 0.75f;

    public FuzzyMatcher(final StructuredViewer viewer) {
        super(viewer);
        lvda = LevenshteinDistanceAnalyzer.getInstance();
    }

    public double getMinimumSimilarity() {
        return minimumSimilarity;
    }

    public void setMinimumSimilarity(final float similarity) {
        this.minimumSimilarity = similarity;
    }

    @Override
    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
        final boolean exactMatch = super.select(viewer, parentElement, element);
        boolean match = exactMatch;

        final Term term = (Term) element;

        final FilterInfo filterInfo = (FilterInfo) term.getInfo();

        for (final Translation translation : term.getTranslations()) {
            final String value = translation.value;
            final String locale = translation.id;
            if (filterInfo.hasFoundInTranslation(locale)) {
                continue;
            }
            final double dist = lvda.analyse(value, getPattern());
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
