package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IPageLayout;
import org.eclipselabs.tapiji.translator.views.GlossaryView;
import org.eclipselabs.tapiji.translator.views.StorageView;

public class Perspective extends AbstractPerspective {

	@Override
	protected void initViewsArea(IPageLayout layout) {
		layout.addShowViewShortcut(StorageView.ID);
		layout.addView(StorageView.ID, IPageLayout.LEFT, .2f, IPageLayout.ID_EDITOR_AREA);
		layout.getViewLayout(StorageView.ID).setCloseable(true);
		/*layout.addStandaloneView(GlossaryView.ID, false, IPageLayout.BOTTOM,
		        .6f, IPageLayout.ID_EDITOR_AREA);*/
	}
}
