package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IPageLayout;
import org.eclipselabs.tapiji.translator.views.GlossaryView;

public class Perspective extends AbstractPerspective {

	@Override
	protected void initViewsArea(IPageLayout layout) {
		layout.addStandaloneView(GlossaryView.ID, false, IPageLayout.BOTTOM,
		        .6f, IPageLayout.ID_EDITOR_AREA);
	}
}
