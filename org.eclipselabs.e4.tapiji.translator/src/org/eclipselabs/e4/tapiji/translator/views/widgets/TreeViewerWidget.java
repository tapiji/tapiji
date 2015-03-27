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


import java.util.Locale;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.providers.TreeViewerContentProvider;
import org.eclipselabs.e4.tapiji.translator.views.providers.TreeViewerLabelProvider;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;
import org.eclipselabs.e4.tapiji.utils.LocaleUtils;


public final class TreeViewerWidget extends Composite implements IResourceChangeListener {

    private static final int TREE_VIEWER_EDITOR_FEATURE = ColumnViewerEditor.TABBING_HORIZONTAL
                    | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
                    | ColumnViewerEditor.KEYBOARD_ACTIVATION;
    private final int TERM_COLUMN_WEIGHT = 1;
    private final int DESCRIPTION_COLUMN_WEIGHT = 1;
    private static final String TAG = TreeViewerWidget.class.getSimpleName();
    private TreeViewer treeViewer;
    private Tree tree;

    private boolean isColumnEditable = true;
    private String referenceLanguage;
    private String[] translationsToDisplay;
    private TreeViewerLabelProvider reeViewerLabelProvider;
    private TreeColumnLayout basicLayout;
    private String[] translations;

    private TreeViewerWidget(final Composite parent, IGlossaryService glossaryService,
                    StoreInstanceState storeInstanceState) {
        super(parent, SWT.FILL);
        
        translations = glossaryService.getTranslations();
        if (storeInstanceState.getReferenceLanguage().isEmpty()) {
            this.referenceLanguage = storeInstanceState.getReferenceLanguage();
        } else {
            this.referenceLanguage =     translations[0];
        }
        
        if(storeInstanceState.getHiddenLocales().isEmpty()) {
            this.translationsToDisplay = translations;
        } else {
            /* final List<String> hiddenTranslations = storeInstanceState.getHiddenLocales();
             hiddenTranslations.removeAll(Arrays.asList(translations));
             this.translationsToDisplay = hiddenTranslations.toArray();*/
            this.translationsToDisplay = translations;
        }
         
        
        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);
        createControls(parent, translations);
    }


    private void createControls(final Composite parent, String[] translations) {


        treeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);

        TreeViewerEditor.create(treeViewer, createFocusCellManager(), createColumnActivationStrategy(), TREE_VIEWER_EDITOR_FEATURE);
        tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        createLocaleColumns();

        treeViewer.setContentProvider(TreeViewerContentProvider.newInstance());

        reeViewerLabelProvider = TreeViewerLabelProvider.newInstance(treeViewer, translations);
        treeViewer.setLabelProvider(reeViewerLabelProvider);




    }

    private void createLocaleColumns() {

        final TextCellEditor textCellEditor = new TextCellEditor(tree);

        int columnCnt = 0;
        for (final String languageCode : translations) {

            final Locale locale = LocaleUtils.getLocaleFromLanguageCode(languageCode);
            TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
            column.getColumn().setWidth(200);
            column.getColumn().setMoveable(true);
            column.getColumn().setText(locale.getDisplayName());
            column.setEditingSupport(createEditingSupportFor(treeViewer, textCellEditor, columnCnt));
            columnCnt++;
        }


        /* basicLayout = new TreeColumnLayout();
         this.setLayout(basicLayout);
         basicLayout.setColumnData(termColumn, new ColumnWeightData(TERM_COLUMN_WEIGHT));*/



    }


    private TreeViewerFocusCellManager createFocusCellManager() {
        return new TreeViewerFocusCellManager(treeViewer, new FocusCellOwnerDrawHighlighter(treeViewer));
    }

    private ColumnViewerEditorActivationStrategy createColumnActivationStrategy() {
        return new ColumnViewerEditorActivationStrategy(treeViewer) {

            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                                || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
                                || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
                                || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };
    }

    private EditingSupport createEditingSupportFor(final TreeViewer viewer, final TextCellEditor textCellEditor, final int columnCnt) {
        return new EditingSupport(viewer) {

            @Override
            protected boolean canEdit(Object element) {
                return isColumnEditable;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return textCellEditor;
            }

            @Override
            protected Object getValue(Object element) {
                return reeViewerLabelProvider.getColumnText(element, columnCnt);
            }

            @Override
            protected void setValue(Object element, Object value) {
                Log.d(TAG, "EDIT COLUMN:" + value);
            }
        };
    }

    public void updateView(final Glossary glossary) {
        treeViewer.setInput(glossary);
        treeViewer.refresh();
        // createColumns(glossary);
    }


    public void setTreeStructure(boolean grouped) {
        ((TreeViewerContentProvider) treeViewer.getContentProvider()).setGrouped(grouped);
        if (treeViewer.getInput() == null) {
            treeViewer.setUseHashlookup(false);
        }
        //updateView();
    }


    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    public static TreeViewerWidget create(final Composite parent, IGlossaryService glossaryService, StoreInstanceState storeInstanceState) {
        return new TreeViewerWidget(parent, glossaryService, storeInstanceState);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
    }

    public void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
        if (treeViewer != null) {
            treeViewer.addSelectionChangedListener(selectionChangedListener);
        }
    }

    public void setColumnEditable(final boolean isEditable) {
        this.isColumnEditable = isEditable;
    }


}
