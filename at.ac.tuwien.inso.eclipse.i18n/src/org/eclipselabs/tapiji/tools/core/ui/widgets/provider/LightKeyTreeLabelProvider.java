package org.eclipselabs.tapiji.tools.core.ui.widgets.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class LightKeyTreeLabelProvider extends KeyTreeLabelProvider  implements ITableLabelProvider {
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		return super.getText(element);
	}
}
