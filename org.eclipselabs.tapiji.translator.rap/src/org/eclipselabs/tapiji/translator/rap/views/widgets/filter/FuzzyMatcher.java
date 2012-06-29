package org.eclipselabs.tapiji.translator.rap.views.widgets.filter;

import org.eclipselabs.tapiji.translator.rap.babel.editor.api.AnalyzerFactory;
import org.eclipselabs.tapiji.translator.rap.babel.editor.api.ILevenshteinDistanceAnalyzer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.tapiji.translator.rap.model.Term;
import org.eclipselabs.tapiji.translator.rap.model.Translation;

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
