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
package org.eclipselabs.e4.tapiji.translator.views.widgets.provider;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;


public final class GlossaryContentProvider implements ITreeContentProvider {

    private Glossary glossary;

    private boolean grouped = false;

    public GlossaryContentProvider(final Glossary glossary) {
        this.glossary = glossary;
    }

    public Glossary getGlossary() {
        return glossary;
    }

    public void setGrouped(final boolean grouped) {
        this.grouped = grouped;
    }

    @Override
    public void dispose() {
        this.glossary = null;
    }

    @Override
    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        if (newInput instanceof Glossary) {
            this.glossary = (Glossary) newInput;
        }
    }

    @Override
    public Object[] getElements(final Object inputElement) {
        if (!grouped) {
            return getAllElements(glossary.terms).toArray(new Term[glossary.terms.size()]);
        }

        if (glossary != null) {
            return glossary.getAllTerms();
        }

        return null;
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        if (grouped) {
            if (parentElement instanceof Term) {
                final Term t = (Term) parentElement;
                return t.getAllSubTerms();
            }
        }
        return null;
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
        if (grouped) {
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

}
