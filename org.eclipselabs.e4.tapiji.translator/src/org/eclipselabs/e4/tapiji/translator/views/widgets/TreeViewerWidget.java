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


import java.awt.ComponentOrientation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.ViewLabelProvider;
import org.eclipselabs.e4.tapiji.translator.views.widgets.filter.ExactMatcher;
import org.eclipselabs.e4.tapiji.translator.views.widgets.filter.FuzzyMatcher;
import org.eclipselabs.e4.tapiji.translator.views.widgets.provider.GlossaryContentProvider;
import org.eclipselabs.e4.tapiji.translator.views.widgets.sorter.GlossaryEntrySorter;
import org.eclipselabs.e4.tapiji.translator.views.widgets.sorter.SortInfo;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchPartSite;


//import org.eclipselabs.tapiji.translator.compat.SwtRapCompatibilitySWT;

public class TreeViewerWidget extends Composite implements IResourceChangeListener {

    protected static final String TAG = TreeViewerWidget.class.getSimpleName();
    private final int TERM_COLUMN_WEIGHT = 1;
    private final int DESCRIPTION_COLUMN_WEIGHT = 1;

    private boolean editable;

    //private IWorkbenchPartSite site;
    private TreeColumnLayout basicLayout;
    private TreeViewer treeViewer;
    private TreeColumn termColumn;
    private boolean grouped = true;
    private final boolean fuzzyMatchingEnabled = false;
    private boolean selectiveViewEnabled = false;
    private float matchingPrecision = .75f;
    private String referenceLocale;
    private List<String> displayedTranslations;
    private String[] translationsToDisplay;

    private SortInfo sortInfo;
    private Glossary glossary;
    private IGlossaryService manager;

    private GlossaryContentProvider contentProvider;
    private ViewLabelProvider labelProvider;

    /*** MATCHER ***/
    ExactMatcher matcher;

    /*** SORTER ***/
    GlossaryEntrySorter sorter;

    /*** ACTIONS ***/
    private Action doubleClickAction;

    public TreeViewerWidget(final Composite parent, final int style, final IGlossaryService manager,
                    final String refLang, final List<String> dls) {
        super(parent, SWT.BORDER);


        // setLayout(new GridLayout(1, false));
        //this.site = site;

        setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));


        if (manager != null) {
            this.manager = manager;
            this.glossary = manager.getGlossary();

            if (glossary == null) {
                return;
            }
            if (refLang != null) {
                this.referenceLocale = refLang;
            } else {
                this.referenceLocale = glossary.info.getTranslations()[0];
            }

            if (dls != null) {
                this.translationsToDisplay = dls.toArray(new String[dls.size()]);
            } else {
                this.translationsToDisplay = glossary.info.getTranslations();
            }
        }

        constructWidget();

        if (this.glossary != null) {
            initTreeViewer();
            // initMatchers();
            //   initSorters();
        }

        //hookDragAndDrop();
        // registerListeners();
    }

    protected void registerListeners() {
        treeViewer.getControl().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(final KeyEvent event) {
                if ((event.character == SWT.DEL) && (event.stateMask == 0)) {
                    deleteSelectedItems();
                }
            }
        });

        // Listen resource changes
        //ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }


    public TreeViewer getTreeView() {
        return treeViewer;
    }

    protected void initSorters() {
        sorter = new GlossaryEntrySorter(treeViewer, sortInfo, glossary.getIndexOfLocale(referenceLocale),
                        glossary.info.translations);
        treeViewer.setSorter(sorter);
    }

    /*public void enableFuzzyMatching(boolean enable) {
        String pattern = "";
        if (matcher != null) {
            pattern = matcher.getPattern();

            if (!fuzzyMatchingEnabled && enable) {
                if (matcher.getPattern().trim().length() > 1 && matcher.getPattern().startsWith("*")
                                && matcher.getPattern().endsWith("*"))
                    pattern = pattern.substring(1).substring(0, pattern.length() - 2);
                matcher.setPattern(null);
            }
        }
        fuzzyMatchingEnabled = enable;
        initMatchers();

        matcher.setPattern(pattern);
        treeViewer.refresh();
    }*/
    /*
        public boolean isFuzzyMatchingEnabled() {
            return fuzzyMatchingEnabled;
        }*/

    protected void initMatchers() {
        treeViewer.resetFilters();

        final String patternBefore = matcher != null ? matcher.getPattern() : "";

        if (fuzzyMatchingEnabled) {
            matcher = new FuzzyMatcher(treeViewer);
            ((FuzzyMatcher) matcher).setMinimumSimilarity(matchingPrecision);
        } else {
            matcher = new ExactMatcher(treeViewer);
        }

        matcher.setPattern(patternBefore);

        //	if (this.selectiveViewEnabled)
        //		new SelectiveMatcher(treeViewer, site.getPage());
    }


    protected void initTreeViewer() {
        // init content provider
        contentProvider = new GlossaryContentProvider(this.glossary);
        treeViewer.setContentProvider(contentProvider);

        // init label provider
        /* try {
             Class<?> clazz = Class.forName(AbstractGlossaryLabelProvider.INSTANCE_CLASS);
             Constructor<?> constr = clazz.getConstructor(int.class, List.class, IWorkbenchPage.class);
             labelProvider = (AbstractGlossaryLabelProvider) constr.newInstance(
                             this.displayedTranslations.indexOf(referenceLocale), this.displayedTranslations,
                             site.getPage());
         } catch (Exception e) {
             e.printStackTrace();
         }*/
        labelProvider = new ViewLabelProvider(treeViewer, displayedTranslations.indexOf(referenceLocale),
                        displayedTranslations);

        treeViewer.setLabelProvider(labelProvider);


        setTreeStructure(grouped);
    }

    public void setTreeStructure(final boolean grouped) {
        this.grouped = grouped;
        ((GlossaryContentProvider) treeViewer.getContentProvider()).setGrouped(this.grouped);
        if (treeViewer.getInput() == null) {
            treeViewer.setUseHashlookup(false);
        }
        treeViewer.setInput(this.glossary);
        treeViewer.refresh();
    }

    @Inject
    private ESelectionService selectionService;
    private Tree tree;


    private void createWidget() {
        /*  tree = new Tree(this, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
          tree.setLinesVisible(true);
          tree.setHeaderVisible(true);
          reeViewer = new TreeViewer(tree);*/
    }


    protected void constructWidget() {
        basicLayout = new TreeColumnLayout();

        this.setLayout(basicLayout);

        treeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);

        /* treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

             @Override
             public void selectionChanged(final SelectionChangedEvent event) {
                 final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

                 Log.d(TAG, "" + ((IStructuredSelection) event.getSelection()).getFirstElement());


                 //  IKeyTreeNode selectedItem = (IKeyTreeNode) selection.iterator().next();


                 // Log.d(TAG, "" + selectedItem);
                 //selectionService.setSelection(selection.getFirstElement());
             }
         });*/

        final Tree tree = treeViewer.getTree();

        if (glossary != null) {
            tree.setHeaderVisible(true);
            tree.setLinesVisible(true);

            // create tree-columns
            constructTreeColumns(tree);
        } else {
            tree.setHeaderVisible(false);
            tree.setLinesVisible(false);
        }

        makeActions();
        hookDoubleClickAction();

        // register messages table as selection provider
        //site.setSelectionProvider(treeViewer);
    }

    /**
     * Gets the orientation suited for a given locale.
     *
     * @param locale the locale
     * @return <code>SWT.RIGHT_TO_LEFT</code> or <code>SWT.LEFT_TO_RIGHT</code>
     */
    private int getOrientation(final Locale locale) {
        if (locale != null) {
            final ComponentOrientation orientation = ComponentOrientation.getOrientation(locale);
            if (orientation == ComponentOrientation.RIGHT_TO_LEFT) {
                return 0;//SwtRapCompatibilitySWT.RIGHT_TO_LEFT;
            }
        }
        return SWT.LEFT_TO_RIGHT;
    }

    protected void constructTreeColumns(final Tree tree) {
        tree.removeAll();
        if (this.displayedTranslations == null) {
            this.displayedTranslations = new ArrayList<String>();
        }

        this.displayedTranslations.clear();

        /** Reference term */
        final String[] refDef = referenceLocale.split("_");
        Locale l = refDef.length < 3 ? (refDef.length < 2 ? new Locale(refDef[0]) : new Locale(refDef[0], refDef[1]))
                        : new Locale(refDef[0], refDef[1], refDef[2]);

        this.displayedTranslations.add(referenceLocale);
        termColumn = new TreeColumn(tree, 0/* SwtRapCompatibilitySWT.RIGHT_TO_LEFT getOrientation(l) */);

        termColumn.setText(l.getDisplayName());
        final TreeViewerColumn termCol = new TreeViewerColumn(treeViewer, termColumn);
        termCol.setEditingSupport(new EditingSupport(treeViewer) {

            TextCellEditor editor = null;

            @Override
            protected void setValue(final Object element, final Object value) {
                if (element instanceof Term) {
                    final Term term = (Term) element;
                    final Translation translation = term.getTranslation(referenceLocale);

                    if (translation != null) {
                        translation.value = (String) value;
                        final Glossary gl = ((GlossaryContentProvider) treeViewer.getContentProvider()).getGlossary();
                        //manager.setGlossary(gl);
                        try {
                            //manager.saveGlossary();
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                    treeViewer.refresh();
                }
            }

            @Override
            protected Object getValue(final Object element) {
                return labelProvider.getColumnText(element, 0);
            }

            @Override
            protected CellEditor getCellEditor(final Object element) {
                if (editor == null) {
                    final Composite tree = (Composite) treeViewer.getControl();
                    editor = new TextCellEditor(tree);
                }
                return editor;
            }

            @Override
            protected boolean canEdit(final Object element) {
                return editable;
            }
        });
        termColumn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                updateSorter(0);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {
                updateSorter(0);
            }
        });
        basicLayout.setColumnData(termColumn, new ColumnWeightData(TERM_COLUMN_WEIGHT));

        /** Translations */
        final String[] allLocales = this.translationsToDisplay;

        int iCol = 1;
        for (final String locale : allLocales) {
            final int ifCall = iCol;
            final String sfLocale = locale;
            if (locale.equalsIgnoreCase(this.referenceLocale)) {
                continue;
            }

            // trac the rendered translation
            this.displayedTranslations.add(locale);

            final String[] locDef = locale.split("_");
            l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0], locDef[1]))
                            : new Locale(locDef[0], locDef[1], locDef[2]);

            // Add editing support to this table column
            final TreeColumn descriptionColumn = new TreeColumn(tree, SWT.NONE);
            final TreeViewerColumn tCol = new TreeViewerColumn(treeViewer, descriptionColumn);
            tCol.setEditingSupport(new EditingSupport(treeViewer) {

                TextCellEditor editor = null;

                @Override
                protected void setValue(final Object element, final Object value) {
                    if (element instanceof Term) {
                        final Term term = (Term) element;
                        final Translation translation = term.getTranslation(sfLocale);

                        if (translation != null) {
                            translation.value = (String) value;
                            final Glossary gl = ((GlossaryContentProvider) treeViewer.getContentProvider())
                                            .getGlossary();
                            //  manager.setGlossary(gl);
                            try {
                                // manager.saveGlossary();
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }
                        treeViewer.refresh();
                    }
                }

                @Override
                protected Object getValue(final Object element) {
                    return labelProvider.getColumnText(element, ifCall);
                }

                @Override
                protected CellEditor getCellEditor(final Object element) {
                    if (editor == null) {
                        final Composite tree = (Composite) treeViewer.getControl();
                        editor = new TextCellEditor(tree);
                    }
                    return editor;
                }

                @Override
                protected boolean canEdit(final Object element) {
                    return editable;
                }
            });

            descriptionColumn.setText(l.getDisplayName());
            descriptionColumn.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    updateSorter(ifCall);
                }

                @Override
                public void widgetDefaultSelected(final SelectionEvent e) {
                    updateSorter(ifCall);
                }
            });
            basicLayout.setColumnData(descriptionColumn, new ColumnWeightData(DESCRIPTION_COLUMN_WEIGHT));
            iCol++;
        }

    }

    protected void updateSorter(final int idx) {
        final SortInfo sortInfo = sorter.getSortInfo();
        if (idx == sortInfo.getColIdx()) {
            sortInfo.setDESC(!sortInfo.isDESC());
        } else {
            sortInfo.setColIdx(idx);
            sortInfo.setDESC(false);
        }
        sorter.setSortInfo(sortInfo);
        setTreeStructure(idx == 0);
        treeViewer.refresh();
    }

    @Override
    public boolean setFocus() {
        // return treeViewer.getControl().setFocus();
        return false;
    }

    /*** DRAG AND DROP ***/
    protected void hookDragAndDrop() {
        /* GlossaryDragSource source = new GlossaryDragSource(treeViewer, manager);
         GlossaryDropTarget target = new GlossaryDropTarget(treeViewer, manager);

         // Initialize drag source for copy event
         DragSource dragSource = new DragSource(treeViewer.getControl(), DND.DROP_MOVE);
         dragSource.setTransfer(new Transfer[] {TermTransfer.getInstance()});
         dragSource.addDragListener(source);

         // Initialize drop target for copy event
         DropTarget dropTarget = new DropTarget(treeViewer.getControl(), DND.DROP_MOVE);
         dropTarget.setTransfer(new Transfer[] {TermTransfer.getInstance()});
         dropTarget.addDropListener(target);*/
    }

    /*** ACTIONS ***/

    private void makeActions() {
        doubleClickAction = new Action() {

            @Override
            public void run() {
                // implement the cell edit event
            }

        };
    }

    private void hookDoubleClickAction() {
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(final DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    /*** SELECTION LISTENER ***/

    private void refreshViewer() {
        treeViewer.refresh();
    }

    public StructuredViewer getViewer() {
        return this.treeViewer;
    }

    public void setSearchString(final String pattern) {
        matcher.setPattern(pattern);
        if (matcher.getPattern().trim().length() > 0) {
            grouped = false;
        } else {
            grouped = true;
        }
        //labelProvider.setSearchEnabled(!grouped);
        this.setTreeStructure(grouped && (sorter != null) && (sorter.getSortInfo().getColIdx() == 0));
        treeViewer.refresh();
    }

    public SortInfo getSortInfo() {
        if (this.sorter != null) {
            return this.sorter.getSortInfo();
        } else {
            return null;
        }
    }

    public void setSortInfo(final SortInfo sortInfo) {
        if (sorter != null) {
            sorter.setSortInfo(sortInfo);
            setTreeStructure(sortInfo.getColIdx() == 0);
            treeViewer.refresh();
        }
    }

    public String getSearchString() {
        return matcher.getPattern();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    public void deleteSelectedItems() {
        final List<String> ids = new ArrayList<String>();
        this.glossary = ((GlossaryContentProvider) treeViewer.getContentProvider()).getGlossary();

        /*
         * ISelection selection = site.getSelectionProvider().getSelection(); if (selection instanceof IStructuredSelection)
         * { for (Iterator<?> iter = ((IStructuredSelection) selection) .iterator(); iter.hasNext();) { Object elem =
         * iter.next(); if (elem instanceof Term) { this.glossary.removeTerm((Term) elem);
         * this.manager.setGlossary(this.glossary); try { this.manager.saveGlossary(); } catch (Exception e) {
         * e.printStackTrace(); } } } }
         */
        this.refreshViewer();
    }

    public void addNewItem() {
        // event.feedback = DND.FEEDBACK_INSERT_BEFORE;
        final Term parentTerm = null;

        /*
         * ISelection selection = site.getSelectionProvider().getSelection(); if (selection instanceof IStructuredSelection)
         * { for (Iterator<?> iter = ((IStructuredSelection) selection) .iterator(); iter.hasNext();) { Object elem =
         * iter.next(); if (elem instanceof Term) { parentTerm = ((Term) elem); break; } } }
         */

        final InputDialog dialog = new InputDialog(this.getShell(), "Neuer Begriff", "Please, define the new term:",
                        "", null);

        if (dialog.open() == Window.OK) {
            if ((dialog.getValue() != null) && (dialog.getValue().trim().length() > 0)) {
                this.glossary = ((GlossaryContentProvider) treeViewer.getContentProvider()).getGlossary();

                // Construct a new term
                final Term newTerm = new Term();
                final Translation defaultTranslation = new Translation();
                defaultTranslation.id = referenceLocale;
                defaultTranslation.value = dialog.getValue();
                newTerm.translations.add(defaultTranslation);

                this.glossary.addTerm(parentTerm, newTerm);

                //  this.manager.setGlossary(this.glossary);
                try {
                    //  this.manager.saveGlossary();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.refreshViewer();
    }

    public void setMatchingPrecision(final float value) {
        matchingPrecision = value;
        if (matcher instanceof FuzzyMatcher) {
            ((FuzzyMatcher) matcher).setMinimumSimilarity(value);
            treeViewer.refresh();
        }
    }

    public float getMatchingPrecision() {
        return matchingPrecision;
    }

    public Control getControl() {
        return treeViewer.getControl();
    }

    public Glossary getGlossary() {
        return this.glossary;
    }

    public void addSelectionChangedListener(final ISelectionChangedListener listener) {
        treeViewer.addSelectionChangedListener(listener);
    }

    public String getReferenceLanguage() {
        return referenceLocale;
    }

    public void setReferenceLanguage(final String lang) {
        this.referenceLocale = lang;
    }

    public void bindContentToSelection(final boolean enable) {
        this.selectiveViewEnabled = enable;
        initMatchers();
    }

    public boolean isSelectiveViewEnabled() {
        return selectiveViewEnabled;
    }

    @Override
    public void dispose() {
        super.dispose();
        //ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        initMatchers();
        this.refreshViewer();
    }

}
