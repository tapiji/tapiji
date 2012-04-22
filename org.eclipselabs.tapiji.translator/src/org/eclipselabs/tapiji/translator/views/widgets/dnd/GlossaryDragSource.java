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
package org.eclipselabs.tapiji.translator.views.widgets.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipselabs.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.tapiji.translator.model.Glossary;
import org.eclipselabs.tapiji.translator.model.Term;
import org.eclipselabs.tapiji.translator.views.widgets.provider.GlossaryContentProvider;


public class GlossaryDragSource implements DragSourceListener {

	private final TreeViewer source;
	private final GlossaryManager manager;
	private List<Term> selectionList;
	
	public GlossaryDragSource (TreeViewer sourceView, GlossaryManager manager) {
		source = sourceView;
		this.manager = manager;
		this.selectionList = new ArrayList<Term>();
	}
	
	@Override
	public void dragFinished(DragSourceEvent event) {
		GlossaryContentProvider contentProvider = ((GlossaryContentProvider) source.getContentProvider());
		Glossary glossary = contentProvider.getGlossary();
		for (Term selectionObject : selectionList)
			glossary.removeTerm(selectionObject);
		manager.setGlossary(glossary);
		this.source.refresh();
		try {
			manager.saveGlossary();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		selectionList = new ArrayList<Term> ();
		for (Object selectionObject : ((IStructuredSelection)source.getSelection()).toList())
			selectionList.add((Term) selectionObject);
		
		event.data = selectionList.toArray(new Term[selectionList.size()]);
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !source.getSelection().isEmpty();
	}

}
