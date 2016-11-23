/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon - refactor from e3 to e4
 ******************************************************************************/
package org.eclipse.e4.tapiji.glossary.ui.treeviewer.handler.provider;


import java.util.Locale;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;


public final class LocaleLabelProvider implements ILabelProvider {

    @Override
    public String getText(final Object element) {
        if ((element != null) && (element instanceof Locale)) {
            return ((Locale) element).getDisplayName();
        }
        return "";
    }

    @Override
    public void addListener(final ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    @Override
    public void removeListener(final ILabelProviderListener listener) {
    }

    @Override
    public Image getImage(final Object element) {
        return null;
    }

}
