/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon - Refactor
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.dnd;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.ui.providers.TreeViewerContentProvider;


public final class GlossaryDropTarget extends DropTargetAdapter {

    private final TreeViewer target;
    private final IGlossaryService glossaryService;

    public GlossaryDropTarget(final TreeViewer viewer, final IGlossaryService glossaryService) {
        super();
        this.target = viewer;
        this.glossaryService = glossaryService;
    }


    @Override
    public void dragEnter(final DropTargetEvent dropTargetEvent) {
        if ((dropTargetEvent.detail == DND.DROP_MOVE) || (dropTargetEvent.detail == DND.DROP_DEFAULT)) {
            if ((dropTargetEvent.operations & DND.DROP_MOVE) != 0) {
                dropTargetEvent.detail = DND.DROP_MOVE;
            } else {
                dropTargetEvent.detail = DND.DROP_NONE;
            }
        }
        super.dragEnter(dropTargetEvent);
    }


    @Override
    public void drop(final DropTargetEvent dropTargetEvent) {

        if (TermTransfer.getInstance().isSupportedType(dropTargetEvent.currentDataType)) {
            Term parentTerm = null;

            dropTargetEvent.detail = DND.DROP_MOVE;
            dropTargetEvent.feedback = DND.FEEDBACK_INSERT_AFTER;

            if ((dropTargetEvent.item instanceof TreeItem)
                            && (((TreeItem) dropTargetEvent.item).getData() instanceof Term)) {
                parentTerm = ((Term) ((TreeItem) dropTargetEvent.item).getData());
            }

            final Term[] moveTerm = (Term[]) dropTargetEvent.data;
            final Glossary glossary = ((TreeViewerContentProvider) target.getContentProvider()).getGlossary();

            if (parentTerm == null) {
                for (final Term t : moveTerm) {
                    glossary.terms.add(t);
                }
            } else {
                for (final Term t : moveTerm) {
                    parentTerm.subTerms.add(t);
                }
            }
            glossaryService.updateGlossary(glossary);
            target.refresh();
        } else {
            dropTargetEvent.detail = DND.DROP_NONE;
        }
        super.drop(dropTargetEvent);
    }


    public static GlossaryDropTarget create(final TreeViewer viewer, final IGlossaryService glossaryService) {
        return new GlossaryDropTarget(viewer, glossaryService);
    }
}
