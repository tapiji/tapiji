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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;
import org.eclipselabs.e4.tapiji.translator.ui.provider.TreeViewerContentProvider;
import org.eclipselabs.e4.tapiji.translator.ui.provider.TreeViewerLabelProvider;
import org.eclipselabs.e4.tapiji.translator.ui.widget.dnd.GlossaryDragSource;
import org.eclipselabs.e4.tapiji.translator.ui.widget.dnd.GlossaryDropTarget;
import org.eclipselabs.e4.tapiji.translator.ui.widget.dnd.TermTransfer;
import org.eclipselabs.e4.tapiji.translator.ui.widget.filter.ExactMatcher;
import org.eclipselabs.e4.tapiji.translator.ui.widget.filter.FuzzyMatcher;
import org.eclipselabs.e4.tapiji.translator.ui.widget.filter.SelectiveMatcher;
import org.eclipselabs.e4.tapiji.translator.ui.widget.sorter.SortInfo;
import org.eclipselabs.e4.tapiji.translator.ui.widget.sorter.TreeViewerSortOrder;
import org.eclipselabs.e4.tapiji.utils.LocaleUtils;


@Creatable
@Singleton
public final class TreeViewerView extends Composite implements IResourceChangeListener, TreeViewerContract.View {

    private static final String TAG = TreeViewerView.class.getSimpleName();
    private static final String TREE_VIEWER_MENU_ID = "org.eclipselabs.e4.tapiji.translator.popupmenu.treeview";


    private static final float DEFAULT_MATCHING_PRECISION = .75f;
    protected static final String COMMAND_DELETE_KEY = "org.eclipselabs.e4.tapiji.translator.command.removeTerm";

    @Inject
    private StoreInstanceState storeInstanceState;

    @Inject
    private ESelectionService selectionService;

    @Inject
    private TreeViewerPresenter presenter;

    @Inject
    private EMenuService menuService;


    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;
    
    private boolean isColumnEditable;
    private boolean isFuzzyMatchingEnabled = false;
    private final boolean selectiveViewEnabled = false;


    private TreeViewer treeViewer;

    private SortInfo sortInfo;
    private String referenceLanguage;
    private TreeViewerLabelProvider treeViewerLabelProvider;
    private String[] translations;

    private TreeViewerSortOrder columnSorter;
    private ExactMatcher matcher;

    private final List<String> displayedTranslations = new ArrayList<String>();

    private Tree tree;


    public TreeViewerView(Composite parent) {
        super(parent, SWT.FILL);
        final GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }

    @PostConstruct
    private void onCreate() {
        this.presenter.setView(this);
        this.translations = presenter.getGlossary().getTranslations();
        this.treeViewer = new TreeViewer(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.SINGLE);
        this.treeViewer.getTree().setHeaderVisible(true);
        this.treeViewer.getTree().setLinesVisible(true);
        this.treeViewer.setAutoExpandLevel(2);

        treeViewer.addSelectionChangedListener((event) -> {
            final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

            selectionService.setSelection(selection.getFirstElement());
        });

        treeViewer.getTree().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick(MouseEvent event) {
                final Object element = selectionService.getSelection();
                if (treeViewer.isExpandable(element)) {
                    if (treeViewer.getExpandedState(element)) {
                        treeViewer.collapseToLevel(element, 1);
                    } else {
                        treeViewer.expandToLevel(element, 1);
                    }
                }
            }
        });


        treeViewer.getTree().addKeyListener(new KeyAdapter() {

            @SuppressWarnings("restriction")
            @Override
            public void keyReleased(KeyEvent event) {
                if (event.character == SWT.DEL) {
                    handlerService.executeHandler(commandService.createCommand(COMMAND_DELETE_KEY, Collections.emptyMap()));
                }
            }
        });


        TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(treeViewer, new FocusCellOwnerDrawHighlighter(treeViewer));

        this.tree = treeViewer.getTree();
        this.tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TreeViewerEditor.create(treeViewer, focusCellManager, createColumnActivationStrategy(), ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION | ColumnViewerEditor.KEEP_EDITOR_ON_DOUBLE_CLICK);
        dragAndDrop();
        registerTreeMenu();
    }

    private void initializeWidget() {
        if (translations.length > 0) {
            createLocaleColumns();
            treeViewerLabelProvider = TreeViewerLabelProvider.newInstance(treeViewer, displayedTranslations, displayedTranslations.indexOf(referenceLanguage));
            treeViewerLabelProvider.isSearchEnabled(isFuzzyMatchingEnabled);
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
        displayedTranslations.clear();
        final TextCellEditor textCellEditor = new TextCellEditor(tree);
        int columnIndex = 0;
        createColumn(referenceLanguage, columnIndex, textCellEditor);
        Log.d(TAG, "" + columnIndex + " " + referenceLanguage);
        columnIndex++;
        displayedTranslations.add(referenceLanguage);
        Log.d(TAG, "" + columnIndex);
        for (final String languageCode : translations) {
            if (languageCode.equalsIgnoreCase(referenceLanguage)) {
                continue;
            }
            displayedTranslations.add(languageCode);
            createColumn(languageCode, columnIndex, textCellEditor);
            columnIndex++;
            Log.d(TAG, "" + columnIndex + " " + languageCode);
        }
    }

    private void createColumn(final String languageCode, final int columnIndex, final TextCellEditor textCellEditor) {
        final Locale locale = LocaleUtils.getLocaleFromLanguageCode(languageCode);
        final TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
        column.getColumn().setWidth(200);
        column.getColumn().setMoveable(false);
        column.getColumn().setResizable(true);
        column.getColumn().setText(locale.getDisplayName());
        column.getColumn().addSelectionListener(createColumnSelectionListener(columnIndex));
        column.setEditingSupport(createEditingSupportFor(textCellEditor, columnIndex, languageCode));
    }

    private void showHideColumn(final String languageCode) {
        final String language = LocaleUtils.getLocaleFromLanguageCode(languageCode).getDisplayName();
        Stream.of(tree.getColumns()).filter(column -> language.equals(column.getText())).forEach(column -> {
            if (column.getWidth() == 0) {
                column.setWidth(200);
                column.setResizable(true);
            } else {
                column.setWidth(0);
                column.setResizable(false);
            }
        });
    }


    @Override
    public void registerTreeMenu() {
        this.menuService.registerContextMenu(this.treeViewer.getControl(), TREE_VIEWER_MENU_ID);
    }

    private void setTreeStructure(final boolean grouped) {
        ((TreeViewerContentProvider) treeViewer.getContentProvider()).setGrouped(grouped);
        if (treeViewer.getInput() == null) {
            treeViewer.setUseHashlookup(false);
        }
    }

    private void clearTreeViewer() {
        treeViewer.getTree().clearAll(true);
        for (final TreeColumn column : tree.getColumns()) {
            column.dispose();
        }
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
        treeViewer.addDragSupport(DND.DROP_MOVE, transferTypes, GlossaryDragSource.create(treeViewer, presenter.getGlossary()));
        treeViewer.addDropSupport(DND.DROP_MOVE, transferTypes, GlossaryDropTarget.create(treeViewer));
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
        treeViewer.setComparator(columnSorter);
    }

    private EditingSupport createEditingSupportFor(final TextCellEditor textCellEditor, final int columnCnt, final String languageCode) {
        return new EditingSupport(treeViewer) {

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
                    treeViewer.setSelection(new StructuredSelection(element), true);


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
                presenter.updateGlossary(((TreeViewerContentProvider) treeViewer.getContentProvider()).getGlossary());
            }
        };
    }

    private boolean isSearchTreeGrouped() {
        return (matcher.getPattern().trim().length() < 0) && (columnSorter.getSortInfo().getColumnIndex() == 0);
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
    public void setMatchingPrecision(final float value) {
        if (matcher instanceof FuzzyMatcher) {
            ((FuzzyMatcher) matcher).setMinimumSimilarity(value);
            treeViewer.refresh();
        }
    }

    @Override
    public void setReferenceLanguage(final String referenceLanguage) {
        this.referenceLanguage = referenceLanguage;
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
    }

    @Override
    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    @Override
    public void showHideTranslationColumn(final String languageCode) {
        showHideColumn(languageCode);
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

    @Focus
    public void focus() {
        treeViewer.getControl().setFocus();
    }

    public static TreeViewerContract.View create(final Composite parent, IEclipseContext eclipseContext) {
        final TreeViewerView view = new TreeViewerView(parent);
        ContextInjectionFactory.inject(view, eclipseContext);
        return view;
    }

    private ColumnViewerEditorActivationStrategy createColumnActivationStrategy() {
        return new ColumnViewerEditorActivationStrategy(treeViewer) {

            @Override
            protected boolean isEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {
                ViewerCell cell = (ViewerCell) event.getSource();
                boolean isEditorActivationEvent;
                if (cell.getColumnIndex() == 1) {
                    isEditorActivationEvent = event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == ' ') || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
                } else {
                    isEditorActivationEvent = event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == ' ') || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
                }
                return isEditorActivationEvent;
            }
        };
    }

    private SelectionListener createColumnSelectionListener(final int columnIndex) {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent selectionEvent) {
                updateColumnOrder(columnIndex);
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

        };
    }

    @Override
    public void addSelection(Term term) {
        Log.d(TAG, "SELECTION" + term);
        treeViewer.setSelection(new StructuredSelection(term), true);
    }
}
