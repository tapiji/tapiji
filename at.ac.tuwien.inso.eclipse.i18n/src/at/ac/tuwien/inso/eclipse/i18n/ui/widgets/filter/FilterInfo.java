package at.ac.tuwien.inso.eclipse.i18n.ui.widgets.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class FilterInfo {

	private boolean foundInKey;
	private List<Locale> foundInLocales = new ArrayList<Locale> ();
	private List<Region> keyOccurrences = new ArrayList<Region> ();
	private Double keySimilarity;
	private Map<Locale, List<Region>> occurrences = new HashMap<Locale, List<Region>>();
	private Map<Locale, Double> localeSimilarity = new HashMap<Locale, Double>();
	
	public FilterInfo() {
	
	}
	
	public void setKeySimilarity (Double similarity) {
		keySimilarity = similarity;
	}
	
	public Double getKeySimilarity () {
		return keySimilarity;
	}
	
	public void addSimilarity (Locale l, Double similarity) {
		localeSimilarity.put (l, similarity);
	}

	public Double getSimilarityLevel (Locale l) {
		return localeSimilarity.get(l);
	}
	
	public void setFoundInKey(boolean foundInKey) {
		this.foundInKey = foundInKey;
	}

	public boolean isFoundInKey() {
		return foundInKey;
	}
	
	public void addFoundInLocale (Locale loc) {
		foundInLocales.add(loc);
	}
	
	public void removeFoundInLocale (Locale loc) {
		foundInLocales.remove(loc);
	}
	
	public void clearFoundInLocale () {
		foundInLocales.clear();
	}

	public boolean hasFoundInLocale (Locale l) {
		return foundInLocales.contains(l);
	}
	
	public List<Region> getFoundInLocaleRanges (Locale locale) {
		List<Region> reg = occurrences.get(locale);
		return (reg == null ? new ArrayList<Region>() : reg);
	}
	
	public void addFoundInLocaleRange (Locale locale, int start, int length) {
		List<Region> regions = occurrences.get(locale);
		if (regions == null)
			regions = new ArrayList<Region>();
		regions.add(new Region(start, length));
		occurrences.put(locale, regions);
	}
	
	public List<Region> getKeyOccurrences () {
		return keyOccurrences;
	}
	
	public void addKeyOccurrence (int start, int length) {
		keyOccurrences.add(new Region (start, length));
	}
}
