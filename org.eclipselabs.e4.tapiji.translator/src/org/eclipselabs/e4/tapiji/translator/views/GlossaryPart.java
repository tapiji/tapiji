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
package org.eclipselabs.e4.tapiji.translator.views;


import java.io.File;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.widgets.ITreeViewerWidget;
import org.eclipselabs.e4.tapiji.translator.views.widgets.TreeViewerWidget;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class GlossaryPart implements ModifyListener, SelectionListener {

    private static final String ID = "org.eclipselabs.tapiji.translator.views.GlossaryView";
    private static final String TREE_VIEWER_MENU_ID = "org.eclipselabs.e4.tapiji.translator.popupmenu.treeview";
    private static final String TAG = GlossaryPart.class.getSimpleName();
    @Inject
    private ESelectionService selectionService;
    @Inject
    private EMenuService menuService;
    @Inject
    private StoreInstanceState storeInstanceState;
    private ITreeViewerWidget treeViewerWidget;
    private Scale fuzzyScaler;
    private Label labelScale;
    private Text inputFilter;
    private IGlossaryService glossaryService;


    @PostConstruct
    public void createPartControl(final Composite parent, final IGlossaryService glossaryService) {
        this.glossaryService = glossaryService;

        parent.setLayout(new GridLayout(1, false));

        final Composite parentComp = new Composite(parent, SWT.BORDER);
        parentComp.setLayout(new GridLayout(2, false));
        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        final Label labelSearch = new Label(parentComp, SWT.NONE);
        labelSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelSearch.setText("Search expression:");

        inputFilter = new Text(parentComp, SWT.BORDER | SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);
        inputFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        inputFilter.addModifyListener(this);

        labelScale = new Label(parentComp, SWT.NONE);
        labelScale.setText("Precision:");
        labelScale.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false, 1, 1));

        fuzzyScaler = new Scale(parentComp, SWT.NONE);
        fuzzyScaler.setMaximum(100);
        fuzzyScaler.setMinimum(0);
        fuzzyScaler.setIncrement(1);
        fuzzyScaler.setPageIncrement(5);
        fuzzyScaler.setSelection(0);

        fuzzyScaler.addSelectionListener(this);
        fuzzyScaler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Log.d(TAG, String.format("Array: %s", storeInstanceState.toString()));
        // onRestoreInstance(storeInstanceState);
        initializeTreeViewerWidget(parent);
    }

    private void showHideFuzzyMatching(final boolean isVisible) {
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
    private void onError(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_ERROR) final String[] error,
                    @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
        MessageDialog.openError(shell, "Deletion not possible", "No term selected");
    }

    @Inject
    @Optional
    private void onEditModeChanged(@UIEventTopic(TranslatorConstants.TOPIC_EDIT_MODE) final boolean isEditable) {
        Log.d(TAG, "IsEditMode:" + isEditable);
        if (treeViewerWidget != null) {
            treeViewerWidget.setColumnEditable(isEditable);
        }
    }

    @Inject
    @Optional
    private void onSearchSelected(@UIEventTopic(TranslatorConstants.TOPIC_GUI) final boolean isVisible) {
        showHideFuzzyMatching(isVisible);
    }

    @Inject
    @Optional
    private void onReloadGlossary(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD) final Glossary glossary) {
        if (glossary != null) {
            treeViewerWidget.updateView(glossary);
        }
    }

    protected void initializeTreeViewerWidget(final Composite parent) {
        treeViewerWidget = TreeViewerWidget.create(parent, glossaryService, storeInstanceState);
        treeViewerWidget.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) treeViewerWidget.getTreeViewer()
                                .getSelection();
                selectionService.setSelection(selection.getFirstElement());
                Log.d(TAG, "Selection:" + selection.getFirstElement());
            }
        });
        menuService.registerContextMenu(treeViewerWidget.getTreeViewer().getControl(), TREE_VIEWER_MENU_ID);
    }


    @Focus
    public void setFocus() {
        if (treeViewerWidget != null) {
            // treeViewerWidget.getTreeViewer().setFocus();
        }
    }

    @PersistState
    public void saveInstanceState() {
        if (null != inputFilter) {
            storeInstanceState.setSearchValue(inputFilter.getText());
        }
        if (null != fuzzyScaler) {
            storeInstanceState.setMatchingPrecision(fuzzyScaler.getSelection());
        }
        Log.d(TAG, String.format("Array: %s", storeInstanceState.toString()));
    }

    private void onRestoreInstance(final StoreInstanceState storeInstanceState) {
        if (!storeInstanceState.getGlossaryFile().isEmpty()) {
            glossaryService.loadGlossary(new File(storeInstanceState.getGlossaryFile()));
        }
        inputFilter.setText(storeInstanceState.getSearchValue());
        fuzzyScaler.setSelection((int) storeInstanceState.getMatchingPrecision());
        showHideFuzzyMatching(storeInstanceState.isFuzzyMode());
    }

    @Override
    public void modifyText(final ModifyEvent e) {
        Log.d(TAG, String.format("Search string: %s", inputFilter.getText()));
        // if (glossary != null && glossary.getGlossary() != null)
        treeViewerWidget.setSearchString(inputFilter.getText());
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        // computeMatchingPrecision();
    }
}
