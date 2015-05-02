/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.widgets.filter;


import javax.inject.Inject;
import org.eclipse.babel.core.message.IMessage;
import org.eclipse.babel.core.message.tree.IKeyTreeNode;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;


public class SelectiveMatcher extends ViewerFilter implements ISelectionListener, ISelectionChangedListener {

    private static final String TAG = null;
    @Inject
    ESelectionService selectionService;
    protected final StructuredViewer viewer;
    protected String pattern = "";
    protected StringMatcher matcher;
    protected IKeyTreeNode selectedItem;

    // protected IWorkbenchPage page;

    public SelectiveMatcher(final StructuredViewer viewer) {
        this.viewer = viewer;
        // //if (page.getActiveEditor() != null) {
        // this.selectedItem = EditorUtil.getSelectedKeyTreeNode(page);
        // /}
        Log.d(TAG, "Selection Service: " + selectionService);
        selectionService.addSelectionListener(this);
        viewer.addFilter(this);
        viewer.refresh();
    }

    @Override
    public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
        if (null != selectedItem) {
            final Term term = (Term) element;
            for (final Translation translation : term.getAllTranslations()) {
                final String value = translation.value;
                if (value.trim().length() == 0) {
                    continue;
                }

                final IMessage[] messages = selectedItem.getMessagesBundleGroup().getMessages(
                                selectedItem.getMessageKey());
                for (final IMessage message : messages) {
                    final String text = message.getValue();
                    final String[] subValues = text.split("[\\s\\p{Punct}]+");
                    for (final String val : subValues) {
                        if (val.trim().equalsIgnoreCase(value.trim())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void selectionChanged(final SelectionChangedEvent event) {
        final ISelection selection = event.getSelection();

        if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
            return;
        }

        final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        selectedItem = (IKeyTreeNode) structuredSelection.iterator().next();
        viewer.refresh();
    }

    public void dispose() {
        selectionService.removeSelectionListener(this);
    }

    @Override
    public void selectionChanged(final MPart part, final Object selection) {
    }
}
