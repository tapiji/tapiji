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
package org.eclipselabs.e4.tapiji.translator.views.widgets;


import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.views.providers.TreeViewerContentProvider;
import org.eclipselabs.e4.tapiji.translator.views.providers.TreeViewerLabelProvider;


public final class TreeViewerWidget extends Composite implements IResourceChangeListener {

    private static final String TAG = TreeViewerWidget.class.getSimpleName();
    private TreeViewer treeViewer;
    private Tree tree;
    private TreeColumn termColumn;



    private TreeViewerWidget(final Composite parent, final int style) {
        super(parent, SWT.FILL);
        setLayout(new GridLayout(1, false));


        setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
        createControls(parent);
    }


    private void createControls(final Composite parent) {
        treeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
        treeViewer.setContentProvider(TreeViewerContentProvider.newInstance());
        treeViewer.setLabelProvider(TreeViewerLabelProvider.newInstance(treeViewer));

        tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));


    }

    /*  private void createColumns(Glossary glossary) {
          String[] allLocales = glossary.terms.;

          int iCol = 1;
          for (String locale : allLocales) {
              final int ifCall = iCol;
              final String sfLocale = locale;
              if (locale.equalsIgnoreCase(this.referenceLocale))
                  continue;

              // trac the rendered translation
              this.displayedTranslations.add(locale);

              String[] locDef = locale.split("_");
              l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);

              // Add editing support to this table column
              TreeColumn descriptionColumn = new TreeColumn(tree, SWT.NONE);
              TreeViewerColumn tCol = new TreeViewerColumn(treeViewer, descriptionColumn);
             
              descriptionColumn.setText(l.getDisplayName());
             // basicLayout.setColumnData(descriptionColumn, new ColumnWeightData(DESCRIPTION_COLUMN_WEIGHT));
              iCol++;
          }
      }*/

    public void updateView(final Glossary glossary) {
        treeViewer.setInput(glossary);
        // createColumns(glossary);
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    public static TreeViewerWidget create(final Composite parent, final int style) {
        return new TreeViewerWidget(parent, style);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
    }

    public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
        if (treeViewer != null) {
            treeViewer.addSelectionChangedListener(selectionChangedListener);
        }
    }



}
