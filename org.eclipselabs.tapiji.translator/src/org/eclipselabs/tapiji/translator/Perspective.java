/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipselabs.tapiji.translator.views.GlossaryView;

public class Perspective implements IPerspectiveFactory {

    public void createInitialLayout(IPageLayout layout) {
	layout.setEditorAreaVisible(true);
	layout.setFixed(true);

	initEditorArea(layout);
	initViewsArea(layout);
    }

    private void initViewsArea(IPageLayout layout) {
	layout.addStandaloneView(GlossaryView.ID, false, IPageLayout.BOTTOM,
		.6f, IPageLayout.ID_EDITOR_AREA);
    }

    private void initEditorArea(IPageLayout layout) {

    }

}
