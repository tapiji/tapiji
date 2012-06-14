package org.eclipse.tapiji.rap.translator.views.widgets.sorter;

import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.tapiji.rap.translator.model.Term;
import org.eclipse.tapiji.rap.translator.model.Translation;


public class GlossaryEntrySorter extends ViewerSorter {

	private StructuredViewer 	viewer;
	private SortInfo 			sortInfo;
	private int 				referenceCol;
	private List<String>		translations;
	
	public GlossaryEntrySorter (StructuredViewer viewer,
								SortInfo sortInfo,
								int referenceCol,
								List<String> translations) {
		this.viewer = viewer;
		this.referenceCol = referenceCol;
		this.translations = translations;
		
		if (sortInfo != null)
			this.sortInfo = sortInfo;
		else
			this.sortInfo = new SortInfo();
	}

	public StructuredViewer getViewer() {
		return viewer;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	public SortInfo getSortInfo() {
		return sortInfo;
	}

	public void setSortInfo(SortInfo sortInfo) {
		this.sortInfo = sortInfo;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		try {
			if (!(e1 instanceof Term && e2 instanceof Term))
				return super.compare(viewer, e1, e2);
			Term comp1 = (Term) e1;
			Term comp2 = (Term) e2;
			
			int result = 0;

			if (sortInfo == null)
				return 0;
			
			if (sortInfo.getColIdx() == 0) {
				Translation transComp1 = comp1.getTranslation(translations.get(referenceCol));
				Translation transComp2 = comp2.getTranslation(translations.get(referenceCol));
				if (transComp1 != null && transComp2 != null)
					result = transComp1.value.compareTo(transComp2.value);
			} else {
				int col = sortInfo.getColIdx() < referenceCol ? sortInfo.getColIdx() + 1 : sortInfo.getColIdx();
				Translation transComp1 = comp1.getTranslation(translations.get(col));
				Translation transComp2 = comp2.getTranslation(translations.get(col));
				
				if (transComp1 == null)
					transComp1 = new Translation();
				if (transComp2 == null)
					transComp2 = new Translation();
				result = transComp1.value.compareTo(transComp2.value);
			}
			
			return result * (sortInfo.isDESC() ? -1 : 1);
		} catch (Exception e) {
			return 0;
		}
	}
	
}
