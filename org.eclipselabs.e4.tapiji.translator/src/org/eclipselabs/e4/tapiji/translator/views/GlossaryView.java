/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.widgets.GlossaryWidget;


public final class GlossaryView {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.eclipselabs.tapiji.translator.views.GlossaryView";

    private static final String TAG = GlossaryView.class.getSimpleName();

    /*** Primary view controls ***/
    private GlossaryWidget treeViewer;
    private Scale fuzzyScaler;
    private Label labelScale;
    private Text inputFilter;


    /*** Parent component ***/
    private Composite parent;

    private IGlossaryService glossaryService;

    @Inject
    private MPart part;

    @Inject
    private ESelectionService selectionService;

    @Inject
    private IEventBroker eventBroker;


    @PostConstruct
    public void createPartControl(final Composite parent, final IGlossaryService glossaryService) {
        this.glossaryService = glossaryService;
        this.parent = parent;
        Log.i(TAG, "" + glossaryService.getGlossary());
        parent.setLayout(new GridLayout(1, false));
        initSearchBar(parent);
        initMessagesTree(parent);

        /*
         *  makeActions(); hookContextMenu(); contributeToActionBars(); initListener(parent);
         */
    }


    @Inject
    @Optional
    private void getNotified(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_NEW) final String s) {
        Log.d(TAG, "GOT NOTIFICATION %s " + s);

        /*  final File glossaryFile = event.getGlossaryFile();
          try {

         //   this.glossaryService = new GlossaryManager(glossaryFile, event.isNewGlossary());
            viewState.setGlossaryFile(glossaryFile.getAbsolutePath());

            referenceActions = null;
            displayActions = null;
            viewState.setDisplayLangArr(glossaryService.getGlossary().info.getTranslations());
            this.redrawTreeViewer();
          } catch (final Exception e) {
            // MessageDialog.openError(getViewSite().getShell(), "Cannot open Glossary",
            //       "The choosen file does not represent a valid Glossary!");
          }*/
    }

    @Inject
    @Optional
    private void fileOpened(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_OPEN) final String s) {
        Log.d(TAG, "GOT NOTIFICATION OPEN %s " + s);
        this.redrawTreeViewer();
    }

    protected void initListener(final Composite parent) {
        inputFilter.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(final ModifyEvent e) {
                if ((glossaryService != null) && (glossaryService.getGlossary() != null)) {
                    treeViewer.setSearchString(inputFilter.getText());
                }
            }
        });
    }

    protected void initSearchBar(final Composite parent) {

        final Composite parentComp = new Composite(parent, SWT.BORDER);
        parentComp.setLayout(new GridLayout(2, false));
        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        final Label labelSearch = new Label(parentComp, SWT.NONE);
        labelSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelSearch.setText("Search expression:");

        inputFilter = new Text(parentComp, SWT.BORDER | SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH); // TODO SAVE STATE LOGIC
        inputFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        labelScale = new Label(parentComp, SWT.NONE);
        labelScale.setText("Precision:");

        fuzzyScaler = new Scale(parentComp, SWT.NONE);
        fuzzyScaler.setMaximum(100);
        fuzzyScaler.setMinimum(0);
        fuzzyScaler.setIncrement(1);
        fuzzyScaler.setPageIncrement(5);
        fuzzyScaler.setSelection(0);
        /* fuzzyScaler.setSelection(Math.round((treeViewer != null ? treeViewer.getMatchingPrecision() : viewState
                         .getMatchingPrecision()) * 100.f));*/
        fuzzyScaler.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                super.widgetSelected(e);
                computeMatchingPrecision();

            }
        });
        fuzzyScaler.setData("fuzzyScaler");
        fuzzyScaler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }


    private void computeMatchingPrecision() {
        if ((treeViewer != null) && (fuzzyScaler != null)) {
            final float val = 1f - (Float
                            .parseFloat(((fuzzyScaler.getMaximum() - fuzzyScaler.getSelection()) + fuzzyScaler
                                            .getMinimum()) + "") / 100.f);
            treeViewer.setMatchingPrecision(val);
        }
    }

    protected void initMessagesTree(final Composite parent) {

        treeViewer = new GlossaryWidget(parent, SWT.NONE, glossaryService != null ? glossaryService : null,
 null, null);


        // define the grid data for the layout
        final GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        treeViewer.setLayoutData(gridData);
    }

    @Focus
    public void setFocus() {
        if (treeViewer != null) {
            treeViewer.setFocus();
        }
    }

    protected void redrawTreeViewer() {
        parent.setRedraw(false);
        if (treeViewer != null)
        treeViewer.dispose();

        initMessagesTree(parent);

        parent.setRedraw(true);
        parent.layout(true);
        treeViewer.layout(true);
    }
}
