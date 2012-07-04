package org.eclipselabs.tapiji.translator.views.widgets.provider;

import java.util.List;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.IWorkbenchPage;

public class GlossaryLabelProvider extends AbstractGlossaryLabelProvider {
	
	private static final long serialVersionUID = -4483186763283604766L;

	public GlossaryLabelProvider(int referenceColumn,
			List<String> translations, IWorkbenchPage page) {
		super(referenceColumn, translations, page);
	}

	@Override
	public void update(ViewerCell cell) {
		Object element = cell.getElement();
		int columnIndex = cell.getColumnIndex();
		cell.setText(this.getColumnText(element, columnIndex));

		if (isCrossRefRegion(cell.getText())) {
			cell.setFont(bold);
			cell.setBackground(info_crossref);
			cell.setForeground(info_crossref_foreground);
		} else {
			cell.setFont(this.getColumnFont(element, columnIndex));
			cell.setBackground(transparent);
		}

		if (isSearchEnabled()) {
			if (isMatchingToPattern(element, columnIndex)) {
				// TODO [RAP] workaround
				/* List<StyleRange> styleRanges = new ArrayList<StyleRange>();
				FilterInfo filterInfo = (FilterInfo) ((Term) element).getInfo();

				for (Region reg : filterInfo
				        .getFoundInTranslationRanges(translations
				                .get(columnIndex < referenceColumn ? columnIndex + 1
				                        : columnIndex))) {
					styleRanges.add(new StyleRange(reg.getOffset(), reg
					        .getLength(), black, info_color, SWT.BOLD));
				}

				cell.setStyleRanges(styleRanges
				        .toArray(new StyleRange[styleRanges.size()])); */
			} else {
				cell.setForeground(gray);
			}
		}
	}
}
