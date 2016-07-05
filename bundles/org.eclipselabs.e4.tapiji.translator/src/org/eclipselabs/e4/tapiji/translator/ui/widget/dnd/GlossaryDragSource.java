/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon - refactor e3 to e4
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.ui.widget.dnd;


import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.ui.provider.TreeViewerContentProvider;


public final class GlossaryDragSource implements DragSourceListener {

    private final TreeViewer source;
    private final IGlossaryService glossaryService;
    private final List<Term> selectionList;

    private GlossaryDragSource(final TreeViewer sourceView, final IGlossaryService manager) {
        super();
        this.source = sourceView;
        this.glossaryService = manager;
        this.selectionList = new ArrayList<Term>();
    }

    @Override
    public void dragFinished(final DragSourceEvent event) {
        final TreeViewerContentProvider contentProvider = ((TreeViewerContentProvider) source.getContentProvider());
        final Glossary glossary = contentProvider.getGlossary();
        for (final Term selectionObject : selectionList) {
            glossary.removeTerm(selectionObject);
        }
        this.glossaryService.updateGlossary(glossary);
        this.source.refresh();
    }

    @Override
    public void dragSetData(final DragSourceEvent dragSourceEvent) {
        selectionList.clear();
        for (final Object selectionObject : ((IStructuredSelection) source.getSelection()).toList()) {
            selectionList.add((Term) selectionObject);
        }
        dragSourceEvent.data = selectionList.toArray(new Term[selectionList.size()]);
    }

    @Override
    public void dragStart(final DragSourceEvent event) {
        event.doit = !source.getSelection().isEmpty();
    }

    public static GlossaryDragSource create(final TreeViewer sourceView, final IGlossaryService glossaryService) {
        return new GlossaryDragSource(sourceView, glossaryService);
    }
}
