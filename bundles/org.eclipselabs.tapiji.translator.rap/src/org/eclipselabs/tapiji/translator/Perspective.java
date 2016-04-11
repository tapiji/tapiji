package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IPageLayout;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class Perspective extends AbstractPerspective {

	@Override
	protected void initViewsArea(IPageLayout layout) {
		layout.addShowViewShortcut(StorageView.ID);
		layout.addView(StorageView.ID, IPageLayout.LEFT, .25f, IPageLayout.ID_EDITOR_AREA);
		layout.getViewLayout(StorageView.ID).setCloseable(true);		
	}
}
