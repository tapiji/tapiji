/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Martin Reiterer - initial API and implementation
 * Christian Behon - refactor from e3 to e4
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.ui.treeviewer;


import java.util.Locale;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipselabs.e4.tapiji.translator.ui.providers.TreeViewerContentProvider;
import org.eclipselabs.e4.tapiji.translator.ui.providers.TreeViewerLabelProvider;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.dnd.GlossaryDragSource;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.dnd.GlossaryDropTarget;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.dnd.TermTransfer;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.filter.ExactMatcher;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.filter.FuzzyMatcher;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.filter.SelectiveMatcher;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.sorter.SortInfo;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.sorter.TreeViewerSortOrder;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.storage.StoreInstanceState;
import org.eclipselabs.e4.tapiji.utils.LocaleUtils;


public final class TreeViewerWidget extends Composite implements IResourceChangeListener, ITreeViewerWidget {

    private static final int TREE_VIEWER_EDITOR_FEATURE = ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                    | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION;

    private static final String TAG = TreeViewerWidget.class.getSimpleName();
    private static final float DEFAULT_MATCHING_PRECISION = .75f;

    private boolean isColumnEditable;
    private boolean isFuzzyMatchingEnabled = false;
    private final boolean selectiveViewEnabled = false;

    private final StoreInstanceState storeInstanceState;
    private final IGlossaryService glossaryService;
    private final TreeViewer treeViewer;
    private final Tree tree;

    private SortInfo sortInfo;
    private String referenceLanguage;
    private TreeViewerLabelProvider treeViewerLabelProvider;
    private String[] translations;
    private TextCellEditor textCellEditor;
    private TreeViewerSortOrder columnSorter;
    private ExactMatcher matcher;

    private TreeViewerWidget(final Composite parent, final IGlossaryService glossaryService, final StoreInstanceState storeInstanceState) {
        super(parent, SWT.FILL);
        this.glossaryService = glossaryService;
        this.storeInstanceState = storeInstanceState;
        translations = glossaryService.getTranslations();

        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        treeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
        TreeViewerEditor.create(treeViewer, createFocusCellManager(), createColumnActivationStrategy(), TREE_VIEWER_EDITOR_FEATURE);
        tree = treeViewer.getTree();

        tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        dragAndDrop();
    }

    private void initializeWidget() {
        if (translations.length > 0) {
            textCellEditor = new TextCellEditor(tree);
            treeViewerLabelProvider = TreeViewerLabelProvider.newInstance(treeViewer, translations);
            treeViewerLabelProvider.isSearchEnabled(isFuzzyMatchingEnabled);
            createLocaleColumns();

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
            final TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
            column.getColumn().setWidth(200);
            column.getColumn().setMoveable(true);
            column.getColumn().setText(locale.getDisplayName());
            column.getColumn().addSelectionListener(createColumnSelectionListener(columnIndex));
            column.setEditingSupport(createEditingSupportFor(treeViewer, textCellEditor, columnIndex, languageCode));
            column.getColumn().setMoveable(true);
            columnIndex++;
        }
    }

    private SelectionListener createColumnSelectionListener(final int columnIndex) {
        return new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent selectionEvent) {
                updateColumnOrder(columnIndex);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
                updateColumnOrder(columnIndex);
            }
        };
    }

    private TreeViewerFocusCellManager createFocusCellManager() {
        return new TreeViewerFocusCellManager(treeViewer, new FocusCellOwnerDrawHighlighter(treeViewer));
    }

    private ColumnViewerEditorActivationStrategy createColumnActivationStrategy() {
        return new ColumnViewerEditorActivationStrategy(treeViewer) {

            @Override
            protected boolean isEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {
                return (event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL) || (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
                                || ((event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) && (event.keyCode == SWT.CR))
                                || (event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC);
            }
        };
    }


    private void showTranslationColumn(final String languageCode) {
        showHideColumn(languageCode, true);
    }

    private void hideTranslationColumn(final String languageCode) {
        showHideColumn(languageCode, false);
    }

    private void showHideColumn(final String languageCode, final boolean isVisible) {
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

    private void setTreeStructure(final boolean grouped) {
        ((TreeViewerContentProvider) treeViewer.getContentProvider()).setGrouped(grouped);
        if (treeViewer.getInput() == null) {
            treeViewer.setUseHashlookup(false);
        }
    }

    private void addSelectionChangedListener(final ISelectionChangedListener selectionChangedListener) {
        if (treeViewer != null) {
            treeViewer.addSelectionChangedListener(selectionChangedListener);
        }
    }

    public static ITreeViewerWidget create(final Composite parent, final IGlossaryService glossaryService, final StoreInstanceState storeInstanceState) {
        return new TreeViewerWidget(parent, glossaryService, storeInstanceState);
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
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
            clearTreeViewer();
            translations = glossary.info.getTranslations();
            referenceLanguage();
            initializeWidget();
            columnSorter(glossary);
            initMatchers();
            treeViewer.setInput(glossary);
            tree.setRedraw(true);
            treeViewer.refresh();
        }
    }

    private void clearTreeViewer() {
        treeViewer.getTree().clearAll(true);
        for (final TreeColumn column : tree.getColumns()) {
            column.dispose();
        }
    }

    private void updateColumnOrder(final int columnIndex) {
        if (columnSorter != null) {
            final SortInfo sortInfo = columnSorter.getSortInfo();
            if (columnIndex == sortInfo.getColumnIndex()) {
                sortInfo.setDescending(!sortInfo.isDescending());
            } else {
                sortInfo.setColumnIndex(columnIndex);
                sortInfo.setDescending(false);
            }
            columnSorter.setSortInfo(sortInfo);
            setTreeStructure(columnIndex == 0);
            treeViewer.refresh();
        }
    }

    @Override
    public void enableFuzzyMatching(final boolean enable) {
        Log.d(TAG, String.format("Enable fuzzy logic: %s", enable));
        isFuzzyMatchingEnabled = enable;
        String pattern = "";
        if (matcher != null) {
            pattern = matcher.getPattern();
            if (!isFuzzyMatchingEnabled && enable) {
                if ((matcher.getPattern().trim().length() > 1) && matcher.getPattern().startsWith("*") && matcher.getPattern().endsWith("*")) {
                    pattern = pattern.substring(1).substring(0, pattern.length() - 2);
                }
                matcher.setPattern(null);
            }
        }
        initMatchers();
        if (treeViewerLabelProvider != null) {
            treeViewerLabelProvider.isSearchEnabled(enable);
        }
        matcher.setPattern(pattern);
        treeViewer.refresh();
    }


    private void initMatchers() {
        treeViewer.resetFilters();
        if (isFuzzyMatchingEnabled) {
            matcher = new FuzzyMatcher(treeViewer);
            ((FuzzyMatcher) matcher).setMinimumSimilarity(DEFAULT_MATCHING_PRECISION);
        } else {
            matcher = new ExactMatcher(treeViewer);
        }

        matcher.setPattern((matcher != null) ? matcher.getPattern() : "");

        if (this.selectiveViewEnabled) {
            new SelectiveMatcher(treeViewer);
        }
    }

    protected void dragAndDrop() {
        final Transfer[] transferTypes = new Transfer[] {TermTransfer.getInstance()};
        treeViewer.addDragSupport(DND.DROP_MOVE, transferTypes, GlossaryDragSource.create(treeViewer, glossaryService));
        treeViewer.addDropSupport(DND.DROP_MOVE, transferTypes, GlossaryDropTarget.create(treeViewer, glossaryService));
    }

    private void referenceLanguage() {
        if (!storeInstanceState.getReferenceLanguage().isEmpty()) {
            this.referenceLanguage = storeInstanceState.getReferenceLanguage();
            Log.d(TAG, "REFERENCE LANGUAGE FROM STORAGE" + referenceLanguage);
        } else {
            this.referenceLanguage = translations[0];
            storeInstanceState.setReferenceLanguage(referenceLanguage);
            Log.d(TAG, "REFERENCE USE DEFAULT" + referenceLanguage);
        }
    }


    private void columnSorter(final Glossary glossary) {
        columnSorter = new TreeViewerSortOrder(treeViewer, sortInfo, glossary.getIndexOfLocale(referenceLanguage), glossary.info.translations);
        treeViewer.setSorter(columnSorter);
    }


    private EditingSupport createEditingSupportFor(final TreeViewer viewer, final TextCellEditor textCellEditor, final int columnCnt, final String languageCode) {
        return new EditingSupport(viewer) {

            @Override
            protected boolean canEdit(final Object element) {
                return isColumnEditable;
            }

            @Override
            protected CellEditor getCellEditor(final Object element) {
                return textCellEditor;
            }

            @Override
            protected Object getValue(final Object element) {
                return treeViewerLabelProvider.getColumnText(element, columnCnt);
            }

            @Override
            protected void setValue(final Object element, final Object value) {
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
                    protected IStatus run(final IProgressMonitor monitor) {
                        final Glossary glossary = ((TreeViewerContentProvider) treeViewer.getContentProvider()).getGlossary();
                        glossaryService.updateGlossary(glossary);
                        return Status.OK_STATUS;
                    }

                }.schedule();
            }
        };
    }

    private boolean isSearchTreeGrouped() {
        return (matcher.getPattern().trim().length() < 0) && columnSorter.getSortInfo().getColumnIndex() == 0;
    }

    @Override
    public void setSearchString(final String searchString) {
        if (null != matcher) {
            if (searchString.isEmpty()) {
                matcher.setPattern(null);
                setTreeStructure(true);
                treeViewerLabelProvider.isSearchEnabled(false);
            } else {
                matcher.setPattern(searchString);
                setTreeStructure(isSearchTreeGrouped());
                treeViewerLabelProvider.isSearchEnabled(true);
            }
            treeViewer.refresh();
        }
    }

    @Override
    public void setMatchingPrecision(float value) {
        if (matcher instanceof FuzzyMatcher) {
            ((FuzzyMatcher) matcher).setMinimumSimilarity(value);
            treeViewer.refresh();
        }
    }
}
