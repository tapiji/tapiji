/*******************************************************************************
 * Copyright (c) 2012 Michael Gasser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.ui.view.toolbarItems;

import org.eclipse.babel.tapiji.tools.rbmanager.RBManagerActivator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.ICommonFilterDescriptor;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.ui.navigator.INavigatorFilterService;

public class ToggleFilterActionDelegate implements IViewActionDelegate {
    private INavigatorFilterService filterService;
    private boolean active;
    private static final String[] FILTER = { RBManagerActivator.PLUGIN_ID
            + ".filter.ProblematicResourceBundleFiles" };

    @Override
    public void run(IAction action) {
        if (active == true) {
            filterService.activateFilterIdsAndUpdateViewer(new String[0]);
            active = false;
        } else {
            filterService.activateFilterIdsAndUpdateViewer(FILTER);
            active = true;
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // Active when content change
    }

    @Override
    public void init(IViewPart view) {
        INavigatorContentService contentService = ((CommonNavigator) view)
                .getCommonViewer().getNavigatorContentService();

        filterService = contentService.getFilterService();
        filterService.activateFilterIdsAndUpdateViewer(new String[0]);
        active = false;
    }

    @SuppressWarnings("unused")
    private String[] getActiveFilterIds() {
        ICommonFilterDescriptor[] fds = filterService
                .getVisibleFilterDescriptors();
        String activeFilterIds[] = new String[fds.length];

        for (int i = 0; i < fds.length; i++)
            activeFilterIds[i] = fds[i].getId();

        return activeFilterIds;
    }

}
