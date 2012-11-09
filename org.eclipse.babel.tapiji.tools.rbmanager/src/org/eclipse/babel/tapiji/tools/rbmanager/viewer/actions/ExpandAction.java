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
package org.eclipse.babel.tapiji.tools.rbmanager.viewer.actions;

import java.util.Iterator;

import org.eclipse.babel.tapiji.tools.rbmanager.ImageUtils;
import org.eclipse.babel.tapiji.tools.rbmanager.RBManagerActivator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonViewer;

public class ExpandAction extends Action implements IAction {
    private CommonViewer viewer;

    public ExpandAction(CommonViewer viewer) {
        this.viewer = viewer;
        setText("Expand Node");
        setToolTipText("expand node");
        setImageDescriptor(RBManagerActivator
                .getImageDescriptor(ImageUtils.EXPAND));
    }

    @Override
    public boolean isEnabled() {
        IStructuredSelection sSelection = (IStructuredSelection) viewer
                .getSelection();
        if (sSelection.size() >= 1)
            return true;
        else
            return false;
    }

    @Override
    public void run() {
        IStructuredSelection sSelection = (IStructuredSelection) viewer
                .getSelection();
        Iterator<?> it = sSelection.iterator();
        while (it.hasNext()) {
            viewer.expandToLevel(it.next(), AbstractTreeViewer.ALL_LEVELS);
        }

    }
}
