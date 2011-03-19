package org.eclipselabs.tapiji.tools.core.ui.widgets.filter;

import java.util.Locale;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.tapiji.translator.rbe.model.analyze.ILevenshteinDistanceAnalyzer;

import com.essiembre.eclipse.rbe.api.AnalyzerFactory;
import com.essiembre.eclipse.rbe.api.ValuedKeyTreeItem;

public class FuzzyMatcher extends ExactMatcher {

	protected ILevenshteinDistanceAnalyzer lvda;
	protected float minimumSimilarity = 0.75f;
	
	public FuzzyMatcher(StructuredViewer viewer) {
		super(viewer);
		lvda = AnalyzerFactory.getLevenshteinDistanceAnalyzer();;
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
		
		ValuedKeyTreeItem vkti = (ValuedKeyTreeItem) element;
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
