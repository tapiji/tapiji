package org.eclipse.tapiji.rap.translator;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.tapiji.rap.translator.views.GlossaryView;


public class Perspective implements IPerspectiveFactory {
	
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
		
		initEditorArea 	(layout);
		initViewsArea 	(layout);
	}

	private void initViewsArea(IPageLayout layout) {
		layout.addStandaloneView(GlossaryView.ID, false, IPageLayout.BOTTOM, .6f, IPageLayout.ID_EDITOR_AREA);
	}

	private void initEditorArea(IPageLayout layout) {
		
	}

}
