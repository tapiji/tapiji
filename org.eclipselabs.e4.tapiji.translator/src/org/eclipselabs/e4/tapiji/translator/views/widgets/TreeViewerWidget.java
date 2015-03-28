/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Christian Behon - refactor from e3 to e4
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views.widgets;


import java.util.Locale;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.providers.TreeViewerContentProvider;
import org.eclipselabs.e4.tapiji.translator.views.providers.TreeViewerLabelProvider;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;
import org.eclipselabs.e4.tapiji.utils.LocaleUtils;


public final class TreeViewerWidget extends Composite implements IResourceChangeListener, ITreeViewerWidget {

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
    private TreeViewerLabelProvider treeViewerLabelProvider;
    private TreeColumnLayout basicLayout;
    private String[] translations;
    private TextCellEditor textCellEditor;
    private IGlossaryService glossaryService;

    private TreeViewerWidget(final Composite parent, IGlossaryService glossaryService,
                    StoreInstanceState storeInstanceState) {
        super(parent, SWT.FILL);
        this.glossaryService = glossaryService;
        translations = glossaryService.getTranslations();
        if (!storeInstanceState.getReferenceLanguage().isEmpty()) {

            this.referenceLanguage = storeInstanceState.getReferenceLanguage();
            Log.d(TAG, "REFERENCE LANGUAGE IS EMPTY" + referenceLanguage);
        } else {
            Log.d(TAG, "REFERENCE USE DEFAULT");
            this.referenceLanguage = translations[0];
            storeInstanceState.setReferenceLanguage(referenceLanguage);
        }
        /* 
        if(storeInstanceState.getHiddenLocales().isEmpty()) {
          this.translationsToDisplay = translations;
        } else {
           final List<String> hiddenTranslations = storeInstanceState.getHiddenLocales();
           hiddenTranslations.removeAll(Arrays.asList(translations));
           this.translationsToDisplay = hiddenTranslations.toArray();
          this.translationsToDisplay = translations;
        }*/


        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        createTreeViewer(parent);
    }


    private void createTreeViewer(final Composite parent) {
        treeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
        TreeViewerEditor.create(treeViewer, createFocusCellManager(), createColumnActivationStrategy(), TREE_VIEWER_EDITOR_FEATURE);
        tree = treeViewer.getTree();
        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        initializeWidget();
    }

    private void initializeWidget() {
        if (translations.length > 0) {
            textCellEditor = new TextCellEditor(tree);
            createLocaleColumns();
            treeViewerLabelProvider = TreeViewerLabelProvider.newInstance(treeViewer, translations);
            treeViewer.setLabelProvider(treeViewerLabelProvider);
            treeViewer.setContentProvider(TreeViewerContentProvider.newInstance());

            tree.setHeaderVisible(true);
            tree.setLinesVisible(true);

        } else {
            tree.setHeaderVisible(false);
            tree.setLinesVisible(false);
        }
    }

    private void createLocaleColumns() {
        int columnIndex = 0;
        for (final String languageCode : translations) {
            final Locale locale = LocaleUtils.getLocaleFromLanguageCode(languageCode);
            TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
            column.getColumn().setWidth(200);
            column.getColumn().setMoveable(true);
            column.getColumn().setText(locale.getDisplayName());
            column.setEditingSupport(createEditingSupportFor(treeViewer, textCellEditor, columnIndex, languageCode));
            column.getColumn().setMoveable(true);
            columnIndex++;
        }
    }

    private void showHideColumn(final String languageCode, boolean isVisible) {
        final String language = LocaleUtils.getLocaleFromLanguageCode(languageCode).getDisplayName();
        final TreeColumn[] columns = tree.getColumns();
        for (final TreeColumn column : columns) {
            if (language.equals(column.getText())) {
                if (isVisible) {
                    column.setWidth(200);
                    column.setResizable(true);
                } else {
                    column.setWidth(0);
                    column.setResizable(false);
                }
                break;
            }
        }
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
                                || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
                                                && event.keyCode == SWT.CR)
                                || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };
    }

    private EditingSupport createEditingSupportFor(final TreeViewer viewer, final TextCellEditor textCellEditor, final int columnCnt, final String languageCode) {
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
                return treeViewerLabelProvider.getColumnText(element, columnCnt);
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof Term) {
                    final Translation translation = ((Term) element).getTranslation(languageCode);
                    if (translation != null) {
                        Log.d(TAG, "EDIT COLUMN:" + value);
                        translation.value = (String) value;
                        getViewer().update(element, null);
                        saveGlossaryAsync();
                    }
                }
            }

            private void saveGlossaryAsync() {
                new Job("Update Glossary") {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        final Glossary glossary = ((TreeViewerContentProvider) treeViewer.getContentProvider()).getGlossary();
                        glossaryService.updateGlossary(glossary);
                        return Status.OK_STATUS;
                    }

                }.schedule();
            }
        };
    }


    private void showTranslationColumn(final String languageCode) {
        showHideColumn(languageCode, true);
    }

    private void hideTranslationColumn(final String languageCode) {
        showHideColumn(languageCode, false);
    }

    private void setTreeStructure(boolean grouped) {
        ((TreeViewerContentProvider) treeViewer.getContentProvider()).setGrouped(grouped);
        if (treeViewer.getInput() == null) {
            treeViewer.setUseHashlookup(false);
        }
        //updateView();
    }

    private void addSelectionChangedListener(ISelectionChangedListener selectionChangedListener) {
        if (treeViewer != null) {
            treeViewer.addSelectionChangedListener(selectionChangedListener);
        }
    }

    public static ITreeViewerWidget create(final Composite parent, IGlossaryService glossaryService, StoreInstanceState storeInstanceState) {
        return new TreeViewerWidget(parent, glossaryService, storeInstanceState);
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
    }

    @Override
    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    @Override
    public void setColumnEditable(final boolean isEditable) {
        this.isColumnEditable = isEditable;
    }

    @Override
    public void updateView(final Glossary glossary) {
        if (glossary != null) {
            tree.setRedraw(false);
            treeViewer.getTree().clearAll(true);
            for (TreeColumn column : tree.getColumns()) {
                column.dispose();
            }
            translations = glossary.info.getTranslations();
            initializeWidget();
            treeViewer.setInput(glossary);
            tree.setRedraw(true);
            treeViewer.refresh();
        }
    }
}
