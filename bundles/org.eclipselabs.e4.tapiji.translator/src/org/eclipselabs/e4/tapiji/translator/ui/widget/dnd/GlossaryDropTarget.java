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
package org.eclipselabs.e4.tapiji.translator.ui.widget.dnd;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.provider.TreeViewerContentProvider;


public final class GlossaryDropTarget extends DropTargetAdapter {

    private static final String TAG = GlossaryDropTarget.class.getSimpleName();
    private final TreeViewer target;
    public boolean sameNode = false;

    public GlossaryDropTarget(final TreeViewer viewer) {
        super();
        this.target = viewer;
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

            if ((dropTargetEvent.item instanceof TreeItem) && (((TreeItem) dropTargetEvent.item).getData() instanceof Term)) {
                parentTerm = ((Term) ((TreeItem) dropTargetEvent.item).getData());
            }

            final Term[] moveTerm = (Term[]) dropTargetEvent.data;
            final Glossary glossary = ((TreeViewerContentProvider) target.getContentProvider()).getGlossary();

            if (moveTerm == null) {
                Log.w(TAG, "Move Term is null!!");
            } else {
                if (parentTerm == null) {
                    for (final Term t : moveTerm) {
                        glossary.terms.add(t);
                    }
                } else {
                    this.sameNode = false;
                    if (moveTerm.length == 1 && moveTerm[0].getAllSubTerms().length == 0) {
                        sameNode = parentTerm.equals(moveTerm[0]);
                    } else {
                        childrenOnSameNode(parentTerm, moveTerm);
                    }

                    if (!sameNode) {
                        for (final Term t : moveTerm) {
                            parentTerm.subTerms.add(t);
                        }
                    } else {
                        dropTargetEvent.feedback = DND.FEEDBACK_NONE;
                        dropTargetEvent.detail = DND.DROP_NONE;
                    }
                }
            }
        } else {
            dropTargetEvent.detail = DND.DROP_NONE;
        }
        super.drop(dropTargetEvent);
    }

    public void childrenOnSameNode(Term parentTerm, Term[] moveTerm) {
        if (!sameNode) {
            for (final Term t : moveTerm) {
                if (sameNode) {
                    break;
                }
                if (t.equals(parentTerm)) {
                    sameNode = true;
                    break;
                } else {
                    childrenOnSameNode(parentTerm, t.getAllSubTerms());
                }
            }
        }
    }

    public static GlossaryDropTarget create(final TreeViewer viewer) {
        return new GlossaryDropTarget(viewer);
    }
}
