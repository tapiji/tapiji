/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.widgets.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Region;

public class FilterInfo {

    private List<String> foundInTranslation = new ArrayList<String>();
    private Map<String, List<Region>> occurrences = new HashMap<String, List<Region>>();
    private Map<String, Double> localeSimilarity = new HashMap<String, Double>();

    public FilterInfo() {

    }

    public void addSimilarity(String l, Double similarity) {
	localeSimilarity.put(l, similarity);
    }

    public Double getSimilarityLevel(String l) {
	return localeSimilarity.get(l);
    }

    public void addFoundInTranslation(String loc) {
	foundInTranslation.add(loc);
    }

    public void removeFoundInTranslation(String loc) {
	foundInTranslation.remove(loc);
    }

    public void clearFoundInTranslation() {
	foundInTranslation.clear();
    }

    public boolean hasFoundInTranslation(String l) {
	return foundInTranslation.contains(l);
    }

    public List<Region> getFoundInTranslationRanges(String locale) {
	List<Region> reg = occurrences.get(locale);
	return (reg == null ? new ArrayList<Region>() : reg);
    }

    public void addFoundInTranslationRange(String locale, int start, int length) {
	List<Region> regions = occurrences.get(locale);
	if (regions == null)
	    regions = new ArrayList<Region>();
	regions.add(new Region(start, length));
	occurrences.put(locale, regions);
    }

}
