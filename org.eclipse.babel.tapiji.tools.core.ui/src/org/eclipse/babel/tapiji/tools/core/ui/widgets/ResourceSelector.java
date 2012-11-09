/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.widgets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.core.message.tree.IAbstractKeyTreeModel;
import org.eclipse.babel.core.message.tree.IKeyTreeNode;
import org.eclipse.babel.core.message.tree.TreeType;
import org.eclipse.babel.editor.api.KeyTreeFactory;
import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.widgets.event.ResourceSelectionEvent;
import org.eclipse.babel.tapiji.tools.core.ui.widgets.listener.IResourceSelectionListener;
import org.eclipse.babel.tapiji.tools.core.ui.widgets.provider.ResKeyTreeContentProvider;
import org.eclipse.babel.tapiji.tools.core.ui.widgets.provider.ResKeyTreeLabelProvider;
import org.eclipse.babel.tapiji.tools.core.ui.widgets.provider.ValueKeyTreeLabelProvider;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class ResourceSelector extends Composite {

    public static final int DISPLAY_KEYS = 0;
    public static final int DISPLAY_TEXT = 1;

    private Locale displayLocale = null; // default
    private int displayMode;
    private String resourceBundle;
    private String projectName;
    private boolean showTree = true;

    private TreeViewer viewer;
    private TreeColumnLayout basicLayout;
    private TreeColumn entries;
    private Set<IResourceSelectionListener> listeners = new HashSet<IResourceSelectionListener>();

    // Viewer model
    private TreeType treeType = TreeType.Tree;
    private StyledCellLabelProvider labelProvider;

    public ResourceSelector(Composite parent, int style) {
        super(parent, style);

        initLayout(this);
        initViewer(this);
    }

    protected void updateContentProvider(IMessagesBundleGroup group) {
        // define input of treeviewer
        if (!showTree || displayMode == DISPLAY_TEXT) {
            treeType = TreeType.Flat;
        }

        ResourceBundleManager manager = ResourceBundleManager
                .getManager(projectName);

        IAbstractKeyTreeModel model = KeyTreeFactory.createModel(manager
                .getResourceBundle(resourceBundle));
        ResKeyTreeContentProvider contentProvider = (ResKeyTreeContentProvider) viewer
                .getContentProvider();
        contentProvider.setProjectName(manager.getProject().getName());
        contentProvider.setBundleId(resourceBundle);
        contentProvider.setTreeType(treeType);
        if (viewer.getInput() == null) {
            viewer.setUseHashlookup(true);
        }

        // viewer.setAutoExpandLevel(AbstractTreeViewer.ALL_LEVELS);
        org.eclipse.jface.viewers.TreePath[] expandedTreePaths = viewer
                .getExpandedTreePaths();
        viewer.setInput(model);
        viewer.refresh();
        viewer.setExpandedTreePaths(expandedTreePaths);
    }

    protected void updateViewer(boolean updateContent) {
        IMessagesBundleGroup group = ResourceBundleManager.getManager(
                projectName).getResourceBundle(resourceBundle);

        if (group == null)
            return;

        if (displayMode == DISPLAY_TEXT) {
            labelProvider = new ValueKeyTreeLabelProvider(
                    group.getMessagesBundle(displayLocale));
            treeType = TreeType.Flat;
            ((ResKeyTreeContentProvider) viewer.getContentProvider())
                    .setTreeType(treeType);
        } else {
            labelProvider = new ResKeyTreeLabelProvider(null);
            treeType = TreeType.Tree;
            ((ResKeyTreeContentProvider) viewer.getContentProvider())
                    .setTreeType(treeType);
        }

        viewer.setLabelProvider(labelProvider);
        if (updateContent)
            updateContentProvider(group);
    }

    protected void initLayout(Composite parent) {
        basicLayout = new TreeColumnLayout();
        parent.setLayout(basicLayout);
    }

    protected void initViewer(Composite parent) {
        viewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE
                | SWT.FULL_SELECTION);
        Tree table = viewer.getTree();

        // Init table-columns
        entries = new TreeColumn(table, SWT.NONE);
        basicLayout.setColumnData(entries, new ColumnWeightData(1));

        viewer.setContentProvider(new ResKeyTreeContentProvider());
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                String selectionSummary = "";
                String selectedKey = "";
                ResourceBundleManager manager = ResourceBundleManager
                        .getManager(projectName);

                if (selection instanceof IStructuredSelection) {
                    Iterator<IKeyTreeNode> itSel = ((IStructuredSelection) selection)
                            .iterator();
                    if (itSel.hasNext()) {
                        IKeyTreeNode selItem = itSel.next();
                        IMessagesBundleGroup group = manager
                                .getResourceBundle(resourceBundle);
                        selectedKey = selItem.getMessageKey();

                        if (group == null)
                            return;
                        Iterator<Locale> itLocales = manager
                                .getProvidedLocales(resourceBundle).iterator();
                        while (itLocales.hasNext()) {
                            Locale l = itLocales.next();
                            try {
                                selectionSummary += (l == null ? ResourceBundleManager.defaultLocaleTag
                                        : l.getDisplayLanguage())
                                        + ":\n";
                                selectionSummary += "\t"
                                        + group.getMessagesBundle(l)
                                                .getMessage(
                                                        selItem.getMessageKey())
                                                .getValue() + "\n";
                            } catch (Exception e) {
                            }
                        }
                    }
                }

                // construct ResourceSelectionEvent
                ResourceSelectionEvent e = new ResourceSelectionEvent(
                        selectedKey, selectionSummary);
                fireSelectionChanged(e);
            }
        });

        // we need this to keep the tree expanded
        viewer.setComparer(new IElementComparer() {

            @Override
            public int hashCode(Object element) {
                final int prime = 31;
                int result = 1;
                result = prime * result
                        + ((toString() == null) ? 0 : toString().hashCode());
                return result;
            }

            @Override
            public boolean equals(Object a, Object b) {
                if (a == b) {
                    return true;
                }
                if (a instanceof IKeyTreeNode && b instanceof IKeyTreeNode) {
                    IKeyTreeNode nodeA = (IKeyTreeNode) a;
                    IKeyTreeNode nodeB = (IKeyTreeNode) b;
                    return nodeA.equals(nodeB);
                }
                return false;
            }
        });
    }

    public Locale getDisplayLocale() {
        return displayLocale;
    }

    public void setDisplayLocale(Locale displayLocale) {
        this.displayLocale = displayLocale;
        updateViewer(false);
    }

    public int getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
        updateViewer(true);
    }

    public void setResourceBundle(String resourceBundle) {
        this.resourceBundle = resourceBundle;
        updateViewer(true);
    }

    public String getResourceBundle() {
        return resourceBundle;
    }

    public void addSelectionChangedListener(IResourceSelectionListener l) {
        listeners.add(l);
    }

    public void removeSelectionChangedListener(IResourceSelectionListener l) {
        listeners.remove(l);
    }

    private void fireSelectionChanged(ResourceSelectionEvent event) {
        Iterator<IResourceSelectionListener> itResList = listeners.iterator();
        while (itResList.hasNext()) {
            itResList.next().selectionChanged(event);
        }
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isShowTree() {
        return showTree;
    }

    public void setShowTree(boolean showTree) {
        if (this.showTree != showTree) {
            this.showTree = showTree;
            this.treeType = showTree ? TreeType.Tree : TreeType.Flat;
            updateViewer(false);
        }
    }

}
