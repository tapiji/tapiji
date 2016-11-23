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
package org.eclipse.e4.tapiji.glossary.ui.treeviewer.provider;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.tapiji.glossary.model.Glossary;
import org.eclipse.e4.tapiji.glossary.model.Term;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class TreeViewerContentProvider implements ITreeContentProvider {

    private boolean isGrouped;

    private Glossary glossary;


    public TreeViewerContentProvider() {
        super();
        this.glossary = new Glossary();
        this.isGrouped = true;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (newInput instanceof Glossary) {
            this.glossary = (Glossary) newInput;
        }
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        if (!isGrouped) {
            return getAllElements(glossary.terms).toArray(new Term[glossary.terms.size()]);
        }

        if (glossary != null) {
            return glossary.getAllTerms();
        }

        return null;
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        Term[] elements = new Term[] {};
        if (isGrouped) {
            if (parentElement instanceof Term) {
                elements = ((Term) parentElement).getAllSubTerms();
            }
        }
        return elements;
    }

    @Override
    public Object getParent(final Object element) {
        if (element instanceof Term) {
            return ((Term) element).getParentTerm();
        }
        return null;
    }

    @Override
    public boolean hasChildren(final Object element) {
        if (isGrouped) {
            if (element instanceof Term) {
                return ((Term) element).hasChildTerms();
            }
        }
        return false;
    }

    public List<Term> getAllElements(final List<Term> terms) {
        final List<Term> allTerms = new ArrayList<Term>();
        if (terms != null) {
            for (final Term term : terms) {
                allTerms.add(term);
                allTerms.addAll(getAllElements(term.subTerms));
            }
        }
        return allTerms;
    }

    @Override
    public void dispose() {
        this.glossary = null;
    }

    public static TreeViewerContentProvider newInstance() {
        return new TreeViewerContentProvider();
    }

    public void setGrouped(final boolean isGrouped) {
        this.isGrouped = isGrouped;
    }

    public Glossary getGlossary() {
        return glossary;
    }

}
