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
package org.eclipse.babel.tapiji.tools.core.ui.views.messagesview;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.ui.IMemento;

public class MessagesViewState {

    private static final String TAG_VISIBLE_LOCALES = "visible_locales";
    private static final String TAG_LOCALE = "locale";
    private static final String TAG_LOCALE_LANGUAGE = "language";
    private static final String TAG_LOCALE_COUNTRY = "country";
    private static final String TAG_LOCALE_VARIANT = "variant";
    private static final String TAG_FUZZY_MATCHING = "fuzzy_matching";
    private static final String TAG_MATCHING_PRECISION = "matching_precision";
    private static final String TAG_SELECTED_PROJECT = "selected_project";
    private static final String TAG_SELECTED_BUNDLE = "selected_bundle";
    private static final String TAG_ENABLED = "enabled";
    private static final String TAG_VALUE = "value";
    private static final String TAG_SEARCH_STRING = "search_string";
    private static final String TAG_EDITABLE = "editable";

    private List<Locale> visibleLocales;
    private SortInfo sortings;
    private boolean fuzzyMatchingEnabled;
    private float matchingPrecision = .75f;
    private String selectedProjectName;
    private String selectedBundleId;
    private String searchString;
    private boolean editable;

    public void saveState(IMemento memento) {
        try {
            if (memento == null)
                return;

            if (visibleLocales != null) {
                IMemento memVL = memento.createChild(TAG_VISIBLE_LOCALES);
                for (Locale loc : visibleLocales) {
                    IMemento memLoc = memVL.createChild(TAG_LOCALE);
                    memLoc.putString(TAG_LOCALE_LANGUAGE, loc.getLanguage());
                    memLoc.putString(TAG_LOCALE_COUNTRY, loc.getCountry());
                    memLoc.putString(TAG_LOCALE_VARIANT, loc.getVariant());
                }
            }

            if (sortings != null) {
                sortings.saveState(memento);
            }

            IMemento memFuzzyMatching = memento.createChild(TAG_FUZZY_MATCHING);
            memFuzzyMatching.putBoolean(TAG_ENABLED, fuzzyMatchingEnabled);

            IMemento memMatchingPrec = memento
                    .createChild(TAG_MATCHING_PRECISION);
            memMatchingPrec.putFloat(TAG_VALUE, matchingPrecision);

            selectedProjectName = selectedProjectName != null ? selectedProjectName
                    : "";
            selectedBundleId = selectedBundleId != null ? selectedBundleId : "";

            IMemento memSP = memento.createChild(TAG_SELECTED_PROJECT);
            memSP.putString(TAG_VALUE, selectedProjectName);

            IMemento memSB = memento.createChild(TAG_SELECTED_BUNDLE);
            memSB.putString(TAG_VALUE, selectedBundleId);

            IMemento memSStr = memento.createChild(TAG_SEARCH_STRING);
            memSStr.putString(TAG_VALUE, searchString);

            IMemento memEditable = memento.createChild(TAG_EDITABLE);
            memEditable.putBoolean(TAG_ENABLED, editable);
        } catch (Exception e) {

        }
    }

    public void init(IMemento memento) {
        if (memento == null)
            return;

        if (memento.getChild(TAG_VISIBLE_LOCALES) != null) {
            if (visibleLocales == null)
                visibleLocales = new ArrayList<Locale>();
            IMemento[] mLocales = memento.getChild(TAG_VISIBLE_LOCALES)
                    .getChildren(TAG_LOCALE);
            for (IMemento mLocale : mLocales) {
                if (mLocale.getString(TAG_LOCALE_LANGUAGE) == null
                        && mLocale.getString(TAG_LOCALE_COUNTRY) == null
                        && mLocale.getString(TAG_LOCALE_VARIANT) == null) {
                    continue;
                }
                Locale newLocale = new Locale(
                        mLocale.getString(TAG_LOCALE_LANGUAGE),
                        mLocale.getString(TAG_LOCALE_COUNTRY),
                        mLocale.getString(TAG_LOCALE_VARIANT));
                if (!this.visibleLocales.contains(newLocale)) {
                    visibleLocales.add(newLocale);
                }
            }
        }

        if (sortings == null)
            sortings = new SortInfo();
        sortings.init(memento);

        IMemento mFuzzyMatching = memento.getChild(TAG_FUZZY_MATCHING);
        if (mFuzzyMatching != null)
            fuzzyMatchingEnabled = mFuzzyMatching.getBoolean(TAG_ENABLED);

        IMemento mMP = memento.getChild(TAG_MATCHING_PRECISION);
        if (mMP != null)
            matchingPrecision = mMP.getFloat(TAG_VALUE);

        IMemento mSelProj = memento.getChild(TAG_SELECTED_PROJECT);
        if (mSelProj != null)
            selectedProjectName = mSelProj.getString(TAG_VALUE);

        IMemento mSelBundle = memento.getChild(TAG_SELECTED_BUNDLE);
        if (mSelBundle != null)
            selectedBundleId = mSelBundle.getString(TAG_VALUE);

        IMemento mSStr = memento.getChild(TAG_SEARCH_STRING);
        if (mSStr != null)
            searchString = mSStr.getString(TAG_VALUE);

        IMemento mEditable = memento.getChild(TAG_EDITABLE);
        if (mEditable != null)
            editable = mEditable.getBoolean(TAG_ENABLED);
    }

    public MessagesViewState(List<Locale> visibleLocales, SortInfo sortings,
            boolean fuzzyMatchingEnabled, String selectedBundleId) {
        super();
        this.visibleLocales = visibleLocales;
        this.sortings = sortings;
        this.fuzzyMatchingEnabled = fuzzyMatchingEnabled;
        this.selectedBundleId = selectedBundleId;
    }

    public List<Locale> getVisibleLocales() {
        return visibleLocales;
    }

    public void setVisibleLocales(List<Locale> visibleLocales) {
        this.visibleLocales = visibleLocales;
    }

    public SortInfo getSortings() {
        return sortings;
    }

    public void setSortings(SortInfo sortings) {
        this.sortings = sortings;
    }

    public boolean isFuzzyMatchingEnabled() {
        return fuzzyMatchingEnabled;
    }

    public void setFuzzyMatchingEnabled(boolean fuzzyMatchingEnabled) {
        this.fuzzyMatchingEnabled = fuzzyMatchingEnabled;
    }

    public void setSelectedBundleId(String selectedBundleId) {
        this.selectedBundleId = selectedBundleId;
    }

    public void setSelectedProjectName(String selectedProjectName) {
        this.selectedProjectName = selectedProjectName;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getSelectedBundleId() {
        return selectedBundleId;
    }

    public String getSelectedProjectName() {
        return selectedProjectName;
    }

    public String getSearchString() {
        return searchString;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public float getMatchingPrecision() {
        return matchingPrecision;
    }

    public void setMatchingPrecision(float value) {
        this.matchingPrecision = value;
    }

}
