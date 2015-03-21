/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Christian Behon
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.providers;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public final class LocaleContentProvider implements IStructuredContentProvider, ITreeContentProvider {

    private List<Locale> locales;

    public LocaleContentProvider() {
        locales = new ArrayList<Locale>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof List) {
            locales = (List<Locale>) inputElement;
            return locales.toArray(new Locale[locales.size()]);
        }
        return locales.toArray();
    }

    @Override
    public void dispose() {
        locales.clear();
        locales = null;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getParent(Object element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        // TODO Auto-generated method stub
        return false;
    }
}
