package org.eclipselabs.tapiji.translator.rap.views.widgets.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.ui.IMemento;
import org.eclipselabs.tapiji.translator.rap.views.widgets.sorter.SortInfo;


public class GlossaryViewState {

	private static final String TAG_GLOSSARY_FILE =		"glossary_file";
	private static final String TAG_FUZZY_MATCHING = 	"fuzzy_matching";
	private static final String TAG_SELECTIVE_VIEW = 	"selective_content";
	private static final String TAG_DISPLAYED_LOCALES = "displayed_locales";
	private static final String TAG_LOCALE = 			"locale";
	private static final String TAG_REFERENCE_LANG = 	"reference_language";
	private static final String TAG_MATCHING_PRECISION= "matching_precision";
	private static final String TAG_ENABLED = 			"enabled";
	private static final String TAG_VALUE = 			"value";
	private static final String TAG_SEARCH_STRING = 	"search_string";
	private static final String TAG_EDITABLE = 			"editable";
	
	private SortInfo		sortings;
	private boolean 		fuzzyMatchingEnabled;
	private boolean			selectiveViewEnabled;
	private float			matchingPrecision = .75f;
	private String			searchString;
	private boolean			editable;
	private String 			glossaryFile;
	private String			referenceLanguage;
	private List<String>    displayLanguages;
	
	public void saveState (IMemento memento) {
		try {
			if (memento == null)
				return;
			
			if (sortings != null) {
				sortings.saveState(memento);
			}
			
			IMemento memFuzzyMatching = memento.createChild(TAG_FUZZY_MATCHING);
			memFuzzyMatching.putBoolean(TAG_ENABLED, fuzzyMatchingEnabled);
			
			IMemento memSelectiveView = memento.createChild(TAG_SELECTIVE_VIEW);
			memSelectiveView.putBoolean(TAG_ENABLED, selectiveViewEnabled);
			
			IMemento memMatchingPrec = memento.createChild(TAG_MATCHING_PRECISION);
			memMatchingPrec.putFloat(TAG_VALUE, matchingPrecision);
			
			IMemento memSStr = memento.createChild(TAG_SEARCH_STRING);
			memSStr.putString(TAG_VALUE, searchString);
			
			IMemento memEditable = memento.createChild(TAG_EDITABLE);
			memEditable.putBoolean(TAG_ENABLED, editable);
			
			IMemento memRefLang = memento.createChild(TAG_REFERENCE_LANG);
			memRefLang.putString(TAG_VALUE, referenceLanguage);
			
			IMemento memGlossaryFile = memento.createChild(TAG_GLOSSARY_FILE);
			memGlossaryFile.putString(TAG_VALUE, glossaryFile);
			
			IMemento memDispLoc = memento.createChild(TAG_DISPLAYED_LOCALES);
			if (displayLanguages != null) {
				for (String lang : displayLanguages) {
					IMemento memLoc = memDispLoc.createChild(TAG_LOCALE);
					memLoc.putString(TAG_VALUE, lang);
				}
			}
		} catch (Exception e) {
			
		}
	}
	
	public void init (IMemento memento) {
		try {
			if (memento == null)
				return;
			
			if (sortings == null)
				sortings = new SortInfo();
			sortings.init(memento);
			
			IMemento mFuzzyMatching = memento.getChild(TAG_FUZZY_MATCHING);
			if (mFuzzyMatching != null)
				fuzzyMatchingEnabled = mFuzzyMatching.getBoolean(TAG_ENABLED);
			
			IMemento mSelectiveView = memento.getChild(TAG_SELECTIVE_VIEW);
			if (mSelectiveView != null)
				selectiveViewEnabled = mSelectiveView.getBoolean(TAG_ENABLED);
			
			IMemento mMP = memento.getChild(TAG_MATCHING_PRECISION);
			if (mMP != null)
				matchingPrecision = mMP.getFloat(TAG_VALUE);
			
			IMemento mSStr = memento.getChild(TAG_SEARCH_STRING);
			if (mSStr != null)
				searchString = mSStr.getString(TAG_VALUE);
			
			IMemento mEditable = memento.getChild(TAG_EDITABLE);
			if (mEditable != null)
				editable = mEditable.getBoolean(TAG_ENABLED);
			
			IMemento mRefLang = memento.getChild(TAG_REFERENCE_LANG);
			if (mRefLang != null)
				referenceLanguage = mRefLang.getString(TAG_VALUE);
			
			IMemento mGlossaryFile = memento.getChild(TAG_GLOSSARY_FILE);
			if (mGlossaryFile != null)
				glossaryFile = mGlossaryFile.getString(TAG_VALUE);
			
			IMemento memDispLoc = memento.getChild(TAG_DISPLAYED_LOCALES);
			if (memDispLoc != null) {
				displayLanguages = new ArrayList<String>();
				for (IMemento locale : memDispLoc.getChildren(TAG_LOCALE)) {
					displayLanguages.add(locale.getString(TAG_VALUE));
				}
			}
		} catch (Exception e) {
			
		}
	}
	
	public GlossaryViewState(List<Locale> visibleLocales,
			SortInfo sortings, boolean fuzzyMatchingEnabled, 
			String selectedBundleId) {
		super();
		this.sortings = sortings;
		this.fuzzyMatchingEnabled = fuzzyMatchingEnabled;
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

	public void setSearchString(String searchString) {
		this.searchString = searchString;
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
	
	public void setMatchingPrecision (float value) {
		this.matchingPrecision = value;
	}

	public String getReferenceLanguage() {
		return this.referenceLanguage;
	}
	
	public void setReferenceLanguage (String refLang) {
		this.referenceLanguage = refLang;
	}
	
	public List<String> getDisplayLanguages() {
		return displayLanguages;
	}
	
	public void setDisplayLanguages(List<String> displayLanguages) {
		this.displayLanguages = displayLanguages;
	}
	
	public void setDisplayLangArr (String[] displayLanguages) {
		this.displayLanguages = new ArrayList<String>();
		for (String dl : displayLanguages) {
			this.displayLanguages.add(dl);
		}
	}

	public void setGlossaryFile(String absolutePath) {
		this.glossaryFile = absolutePath;
	}
	
	public String getGlossaryFile() {
		return glossaryFile;
	}
	
	public void setSelectiveViewEnabled(boolean selectiveViewEnabled) {
		this.selectiveViewEnabled = selectiveViewEnabled;
	}
	
	public boolean isSelectiveViewEnabled() {
		return selectiveViewEnabled;
	}
}
