package at.ac.tuwien.inso.eclipse.i18n.ui.widgets.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import at.ac.tuwien.inso.eclipse.i18n.ui.widgets.filter.FilterInfo;
import at.ac.tuwien.inso.eclipse.i18n.util.FontUtils;
import at.ac.tuwien.inso.eclipse.i18n.util.ImageUtils;
import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleEntry;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTreeItem;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IValuedKeyTreeItem;

import com.essiembre.eclipse.rbe.api.ValuedKeyTreeItem;


public class ResKeyTreeLabelProvider extends KeyTreeLabelProvider {

	private List<Locale> locales;
	private boolean searchEnabled = false;
	
	/*** COLORS ***/
	private Color gray = FontUtils.getSystemColor(SWT.COLOR_GRAY);
	private Color black = FontUtils.getSystemColor(SWT.COLOR_BLACK);
	private Color info_color = FontUtils.getSystemColor(SWT.COLOR_YELLOW); 

    /*** FONTS ***/
    private Font bold = FontUtils.createFont(SWT.BOLD);
    private Font bold_italic = FontUtils.createFont(SWT.BOLD | SWT.ITALIC);
    
	public ResKeyTreeLabelProvider (List<Locale> locales) {
		this.locales = locales;
	}
	
	//@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			IKeyTreeItem kti = (IKeyTreeItem) element;
			List<IBundleEntry> be = (List<IBundleEntry>) kti.getKeyTree().getBundleGroup().getBundleEntries(kti.getId());
			boolean incomplete = false;
			
			if (be.size() != kti.getKeyTree().getBundleGroup().getBundleCount()) 
				incomplete = true;
			else {
				for (IBundleEntry b : be) {
					if (b.getValue() == null || b.getValue().trim().length() == 0) {
						incomplete = true;
						break;
					}
				}
			}
			
			if (incomplete)
				return ImageUtils.getImage(ImageUtils.ICON_RESOURCE_INCOMPLETE);
			else
				return ImageUtils.getImage(ImageUtils.ICON_RESOURCE);
		}
		return null;
	}

	//@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0)
			return super.getText(element);
		
		if (columnIndex <= locales.size()) {
			IValuedKeyTreeItem item = (IValuedKeyTreeItem) element;
			String entry = item.getValue(locales.get(columnIndex-1));
			if (entry != null)
				return entry;
		}
		return "";
	}
	
	public void setSearchEnabled (boolean enabled) {
		this.searchEnabled = enabled;
	}
	
	public boolean isSearchEnabled () {
		return this.searchEnabled;
	}

	public void setLocales(List<Locale> visibleLocales) {
		locales = visibleLocales;
	}

	protected boolean isMatchingToPattern (Object element, int columnIndex) {
		boolean matching = false;
		
		if (element instanceof ValuedKeyTreeItem) {
			ValuedKeyTreeItem vkti = (ValuedKeyTreeItem) element;
			
			if (vkti.getInfo() == null)
				return false;
			
			FilterInfo filterInfo = (FilterInfo) vkti.getInfo();
			
			if (columnIndex == 0) {
				matching = filterInfo.isFoundInKey();
			} else {
				matching = filterInfo.hasFoundInLocale(locales.get(columnIndex-1));
			}
		}
		
		return matching;
	}

	protected boolean isSearchEnabled (Object element) {
		return (element instanceof ValuedKeyTreeItem && searchEnabled );
	}
	
	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		int columnIndex = cell.getColumnIndex();
		
		if (isSearchEnabled(element)) {
			if (isMatchingToPattern(element, columnIndex) ) {
				List<StyleRange> styleRanges = new ArrayList<StyleRange>();
				FilterInfo filterInfo = (FilterInfo) ((ValuedKeyTreeItem)element).getInfo();
				
				if (columnIndex > 0) {
					for (Region reg : filterInfo.getFoundInLocaleRanges(locales.get(columnIndex-1))) {
						styleRanges.add(new StyleRange(reg.getOffset(), reg.getLength(), black, info_color, SWT.BOLD));
					}
				} else {
					// check if the pattern has been found within the key section
					if (filterInfo.isFoundInKey()) {
						for (Region reg : filterInfo.getKeyOccurrences()) {
							StyleRange sr = new StyleRange(reg.getOffset(), reg.getLength(), black, info_color, SWT.BOLD);
							styleRanges.add(sr);
						}
					}
				}
				cell.setStyleRanges(styleRanges.toArray(new StyleRange[styleRanges.size()]));
			} else {
				cell.setForeground(gray);
			}
		} else if (columnIndex == 0)
			super.update(cell);
	
		cell.setImage(this.getColumnImage(element, columnIndex));
		cell.setText(this.getColumnText(element, columnIndex));
	}
	

}
