/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.views.widgets.provider;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.tapiji.translator.model.Glossary;
import org.eclipselabs.tapiji.translator.model.Term;

public class GlossaryContentProvider implements ITreeContentProvider {

	private Glossary glossary;
	private boolean grouped = false;

	public GlossaryContentProvider(Glossary glossary) {
		this.glossary = glossary;
	}

	public Glossary getGlossary() {
		return glossary;
	}

	public void setGrouped(boolean grouped) {
		this.grouped = grouped;
	}

	@Override
	public void dispose() {
		this.glossary = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof Glossary)
			this.glossary = (Glossary) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (!grouped)
			return getAllElements(glossary.terms).toArray(
			        new Term[glossary.terms.size()]);

		if (glossary != null)
			return glossary.getAllTerms();

		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (!grouped)
			return null;

		if (parentElement instanceof Term) {
			Term t = (Term) parentElement;
			return t.getAllSubTerms();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Term) {
			Term t = (Term) element;
			return t.getParentTerm();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (!grouped)
			return false;

		if (element instanceof Term) {
			Term t = (Term) element;
			return t.hasChildTerms();
		}
		return false;
	}

	public List<Term> getAllElements(List<Term> terms) {
		List<Term> allTerms = new ArrayList<Term>();

		if (terms != null) {
			for (Term term : terms) {
				allTerms.add(term);
				allTerms.addAll(getAllElements(term.subTerms));
			}
		}

		return allTerms;
	}

}
