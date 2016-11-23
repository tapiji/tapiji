/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.e4.tapiji.glossary.model.filter;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class FilterInfo implements Serializable {
    
    private static final long serialVersionUID = -6117869799503980234L;

    private final List<String> foundInTranslation = new ArrayList<String>();
    private final Map<String, List<FilterRegion>> occurrences = new HashMap<String, List<FilterRegion>>();
    private final Map<String, Double> localeSimilarity = new HashMap<String, Double>();

    public FilterInfo() {
        super();
    }

    public void addSimilarity(final String l, final Double similarity) {
        localeSimilarity.put(l, similarity);
    }

    public Double getSimilarityLevel(final String l) {
        return localeSimilarity.get(l);
    }

    public void addFoundInTranslation(final String loc) {
        foundInTranslation.add(loc);
    }

    public void removeFoundInTranslation(final String loc) {
        foundInTranslation.remove(loc);
    }

    public void clearFoundInTranslation() {
        foundInTranslation.clear();
    }

    public boolean hasFoundInTranslation(final String l) {
        return foundInTranslation.contains(l);
    }

    public List<FilterRegion> getFoundInTranslationRanges(final String locale) {
        final List<FilterRegion> reg = occurrences.get(locale);
        return (reg == null ? new ArrayList<FilterRegion>() : reg);
    }

    public void addFoundInTranslationRange(final String locale, final int start, final int length) {
        List<FilterRegion> regions = occurrences.get(locale);
        if (regions == null) {
            regions = new ArrayList<FilterRegion>();
        }
        regions.add(new FilterRegion(start, length));
        occurrences.put(locale, regions);
    }

    @Override
    public String toString() {
        return "FilterInfo [foundInTranslation=" + foundInTranslation + ", occurrences=" + occurrences + ", localeSimilarity=" + localeSimilarity + "]";
    }
}
