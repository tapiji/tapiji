/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.widgets.TreeViewerWidget;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class GlossaryPart {

    public static final String ID = "org.eclipselabs.tapiji.translator.views.GlossaryView";
    private static final String TAG = GlossaryPart.class.getSimpleName();

    private TreeViewerWidget treeViewerWidget;
    private Scale fuzzyScaler;
    private Label labelScale;
    private Text inputFilter;
    private Composite parent;
    private IGlossaryService glossaryService;

    @Inject
    private MPart part;

    @Inject
    private ESelectionService selectionService;

    @Inject
    private IEventBroker eventBroker;


    @Inject
    EMenuService menuService;
    private Composite parentComp;

    @Inject
    StoreInstanceState storeInstanceState;

    @PostConstruct
    public void createPartControl(final Composite parent, final IGlossaryService glossaryService, final EMenuService menuService) {

        this.glossaryService = glossaryService;
        this.parent = parent;

        final Display display = Display.getCurrent();

        parent.setLayout(new GridLayout(1, false));
        parent.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));


        final Composite parentComp = new Composite(parent, SWT.BORDER);
        parentComp.setLayout(new GridLayout(2, false));
        parentComp.setBackground(display.getSystemColor(SWT.COLOR_DARK_CYAN));
        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        this.parentComp = parentComp;

        final Label labelSearch = new Label(parentComp, SWT.NONE);
        labelSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelSearch.setText("Search expression:");

        inputFilter = new Text(parentComp, SWT.BORDER | SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);
        inputFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        labelScale = new Label(parentComp, SWT.NONE);
        labelScale.setText("Precision:");

        fuzzyScaler = new Scale(parentComp, SWT.NONE);
        fuzzyScaler.setMaximum(100);
        fuzzyScaler.setMinimum(0);
        fuzzyScaler.setIncrement(1);
        fuzzyScaler.setPageIncrement(5);
        fuzzyScaler.setSelection(0);

        fuzzyScaler.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                super.widgetSelected(e);
                computeMatchingPrecision();

            }
        });
        fuzzyScaler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        initMessagesTree(parent);

        onRestoreInstance(storeInstanceState);
    }

    /* fuzzyScaler.setSelection(Math.round((treeViewer != null ? treeViewer.getMatchingPrecision() : viewState
                             .getMatchingPrecision()) * 100.f));*/



    private void showHideFuzzyMatching(boolean isVisible) {
        if (isVisible) {
            ((GridData) labelScale.getLayoutData()).heightHint = SWT.DEFAULT;
            ((GridData) fuzzyScaler.getLayoutData()).heightHint = SWT.DEFAULT;
        } else {
            ((GridData) labelScale.getLayoutData()).heightHint = 0;
            ((GridData) fuzzyScaler.getLayoutData()).heightHint = 0;
        }
        labelScale.getParent().layout();
        labelScale.getParent().getParent().layout();
    }

    @Inject
    @Optional
    private void onError(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_ERROR) final String[] error, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
        MessageDialog.openError(shell, "Deletion not possible", "No term selected");
    }

    @Inject
    @Optional
    private void onError(@UIEventTopic(TranslatorConstants.TOPIC_GUI) boolean isVisible) {
        showHideFuzzyMatching(isVisible);
    }

    @Inject
    @Optional
    private void onReloadGlossary(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD) final Glossary glossary) {
        if (glossary != null) {
            treeViewerWidget.updateView(glossary);
        }
    }

    protected void initListener(final Composite parent) {
        /*        inputFilter.addModifyListener(new ModifyListener() {

                    @Override
                    public void modifyText(final ModifyEvent e) {
                        if ((glossaryService != null) && (glossaryService.getGlossary() != null)) {
                            treeViewerWidget.setSearchString(inputFilter.getText());
                        }
                    }
                });*/
    }



    private void computeMatchingPrecision() {
        /* if ((treeViewerWidget != null) && (fuzzyScaler != null)) {
             final float val = 1f - (Float
                             .parseFloat(((fuzzyScaler.getMaximum() - fuzzyScaler.getSelection()) + fuzzyScaler
                                             .getMinimum()) + "") / 100.f);
             treeViewerWidget.setMatchingPrecision(val);
         }*/
    }

    protected void initMessagesTree(final Composite parent) {

        /* Composite composite = new Composite(this, SWT.NONE);
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
         composite.setLayout(new GridLayout(1, false));

         TreeViewer treeViewer = new TreeViewer(composite, SWT.NONE);
         Tree tree = treeViewer.getTree();
         tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

         TreeColumn trclmnTest = new TreeColumn(tree, SWT.NONE);
         trclmnTest.setWidth(100);
         trclmnTest.setText("Test");*/


        treeViewerWidget = TreeViewerWidget.create(parent, SWT.NONE);
        treeViewerWidget.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) treeViewerWidget.getTreeViewer().getSelection();
                selectionService.setSelection(selection.getFirstElement());
                Log.d(TAG, "Selection:" + selection.getFirstElement());
            }
        });

        treeViewerWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        menuService.registerContextMenu(treeViewerWidget.getTreeViewer().getControl(), "org.eclipselabs.e4.tapiji.translator.popupmenu.treeview");
    }

    @Focus
    public void setFocus() {
        if (treeViewerWidget != null) {
            treeViewerWidget.setFocus();
        }
    }

    protected void redrawTreeViewer() {
        /* parentComp.setRedraw(false);

         treeViewerWidget.dispose();

         if (treeViewerWidget != null) {
             treeViewerWidget.dispose();
         }

         initMessagesTree(parent);

         parentComp.setRedraw(true);
         parentComp.layout(true);
         treeViewerWidget. layout(true);*/
    }
    



    @PersistState
    public void saveInstanceState() {
        storeInstanceState.setSearchValue(inputFilter.getText());
        storeInstanceState.setMatchingPrecision(fuzzyScaler.getSelection());
        
        Log.d(TAG, String.format("Array: %s", storeInstanceState.toString()));
    }

    private void onRestoreInstance(StoreInstanceState storeInstanceState) {
        // showHideFuzzyMatching(storeInstanceState.isFuzzyMode());
        inputFilter.setText(storeInstanceState.getSearchValue());
        fuzzyScaler.setSelection((int) storeInstanceState.getMatchingPrecision());
    }

}
