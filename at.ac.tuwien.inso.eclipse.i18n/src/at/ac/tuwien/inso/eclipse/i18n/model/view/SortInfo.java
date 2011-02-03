package at.ac.tuwien.inso.eclipse.i18n.model.view;

import java.util.List;
import java.util.Locale;

import org.eclipse.ui.IMemento;


public class SortInfo {
	
	public static final String TAG_SORT_INFO = 		"sort_info";
	public static final String TAG_COLUMN_INDEX = 	"col_idx";
	public static final String TAG_ORDER = 			"order";
		
	private int colIdx;
	private boolean DESC;
	private List<Locale> visibleLocales;

	public void setDESC(boolean dESC) {
		DESC = dESC;
	}

	public boolean isDESC() {
		return DESC;
	}

	public void setColIdx(int colIdx) {
		this.colIdx = colIdx;
	}

	public int getColIdx() {
		return colIdx;
	}

	public void setVisibleLocales(List<Locale> visibleLocales) {
		this.visibleLocales = visibleLocales;
	}

	public List<Locale> getVisibleLocales() {
		return visibleLocales;
	}

	public void saveState (IMemento memento) {
		IMemento mCI = memento.createChild(TAG_SORT_INFO);
		mCI.putInteger(TAG_COLUMN_INDEX, colIdx);
		mCI.putBoolean(TAG_ORDER, DESC);
	}
	
	public void init (IMemento memento) {
		IMemento mCI = memento.getChild(TAG_SORT_INFO);
		if (mCI == null)
			return;
		colIdx = mCI.getInteger(TAG_COLUMN_INDEX);
		DESC = mCI.getBoolean(TAG_ORDER);
	}
}
