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
package org.eclipselabs.e4.tapiji.translator.ui;


import java.io.File;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.constant.TranslatorConstant;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;
import org.eclipselabs.e4.tapiji.translator.ui.handler.treeviewer.LanguageVisibilityChangedHandler.LanguageViewHolder;
import org.eclipselabs.e4.tapiji.translator.ui.provider.TreeViewerContentProvider;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.ITreeViewerWidget;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.TreeViewerWidget;


public final class GlossaryPart implements ModifyListener, Listener {

    private static final String TREE_VIEWER_MENU_ID = "org.eclipselabs.e4.tapiji.translator.popupmenu.treeview";
    private static final String EXPRESSION_TRANSLATION_REFERENCE = "org.eclipselabs.e4.tapiji.translator.popupmenu.TRANSLATION_REFERENCE";
    private static final String EXPRESSION_TRANSLATION_VISIBILITY = "org.eclipselabs.e4.tapiji.translator.popupmenu.TRANSLATION_VISIBILITY";

    private static final String TAG = GlossaryPart.class.getSimpleName();

    private ITreeViewerWidget treeViewerWidget;
    private Scale fuzzyScaler;
    private Label labelScale;
    private Text inputFilter;

    @Inject
    private IGlossaryService glossaryService;

    @Inject
    private ESelectionService selectionService;

    @Inject
    private StoreInstanceState storeInstanceState;

    @Inject
    private EMenuService menuService;

    @Inject
    private IEclipseContext eclipseContext;

    @PostConstruct
    public void createPartControl(final Composite parent) {
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
        fuzzyScaler.addListener(SWT.Selection, this);
        fuzzyScaler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        onRestoreInstance();
        initializeTreeViewerWidget(parent);
        Log.d(TAG, String.format("Array: %s", storeInstanceState.toString()));
    }

    @Inject
    @Optional
    private void onError(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_ERROR) final String[] error, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
        MessageDialog.openError(shell, "Deletion not possible", "No term selected");
    }

    @Inject
    @Optional
    private void onEditModeChanged(@UIEventTopic(TranslatorConstant.TOPIC_EDIT_MODE) final boolean isEditable) {
        if (treeViewerWidget != null) {
            treeViewerWidget.setColumnEditable(isEditable);
        }
    }

    @Inject
    @Optional
    private void onSearchSelected(@UIEventTopic(TranslatorConstant.TOPIC_SHOW_FUZZY_MATCHING) final boolean isVisible) {
        showHideFuzzyMatching(isVisible);
    }

    @Inject
    @Optional
    private void onReloadGlossary(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD) final Glossary glossary) {
        if (glossary != null) {
            eclipseContext.set(EXPRESSION_TRANSLATION_REFERENCE, glossary.info.translations.size());
            eclipseContext.set(EXPRESSION_TRANSLATION_VISIBILITY, glossary.info.translations.size() - 1);
            treeViewerWidget.updateView(glossary);
        }
    }

    @Inject
    @Optional
    private void onLanguageVisibilityChanged(@UIEventTopic(TranslatorConstant.TOPIC_SHOW_LANGUAGE) final LanguageViewHolder languageVisibility) {
        if (languageVisibility.isVIsible) {
            treeViewerWidget.showTranslationColumn(languageVisibility.locale);
        } else {
            treeViewerWidget.hideTranslationColumn(languageVisibility.locale);
        }
    }

    @Inject
    @Optional
    private void onRefrenceChanged(@UIEventTopic(TranslatorConstant.TOPIC_REFERENCE_LANGUAGE) final String referenceLanguage) {
        treeViewerWidget.setReferenceLanguage(referenceLanguage);
        treeViewerWidget.updateView(((TreeViewerContentProvider) treeViewerWidget.getTreeViewer().getContentProvider()).getGlossary());
    }

    protected void initializeTreeViewerWidget(final Composite parent) {
        treeViewerWidget = TreeViewerWidget.create(parent, glossaryService, storeInstanceState);
        treeViewerWidget.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                final IStructuredSelection selection = (IStructuredSelection) treeViewerWidget.getTreeViewer().getSelection();
                selectionService.setSelection(selection.getFirstElement());
                Log.d(TAG, "Selection:" + selection.getFirstElement());
            }
        });

        menuService.registerContextMenu(treeViewerWidget.getTreeViewer().getControl(), TREE_VIEWER_MENU_ID);
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

    private float getFuzzyPrecission() {
        final String value = String.valueOf((fuzzyScaler.getMaximum() - fuzzyScaler.getSelection()) + fuzzyScaler.getMinimum());
        return 1f - (Float.parseFloat(value) / 100.f);
    }

    private void onRestoreInstance() {
        if (!storeInstanceState.getGlossaryFile().isEmpty()) {
            glossaryService.loadGlossary(new File(storeInstanceState.getGlossaryFile()));
        }
        eclipseContext.set(EXPRESSION_TRANSLATION_REFERENCE, 0);
        eclipseContext.set(EXPRESSION_TRANSLATION_VISIBILITY, 0);
        inputFilter.setText(storeInstanceState.getSearchValue());
        fuzzyScaler.setSelection((int) storeInstanceState.getMatchingPrecision());
        showHideFuzzyMatching(storeInstanceState.isFuzzyMode());
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
        if (null != treeViewerWidget) {
            treeViewerWidget.enableFuzzyMatching(isVisible);
        }
    }

    @Override
    public void modifyText(final ModifyEvent modifyEvent) {
        if (null != treeViewerWidget) {
            treeViewerWidget.setSearchString(inputFilter.getText());
        }
    }

    @Override
    public void handleEvent(final Event event) {
        if (null != fuzzyScaler) {
            treeViewerWidget.setMatchingPrecision(getFuzzyPrecission());
        }
    }

    @Focus
    public void setFocus() {
    }

    @PreDestroy
    public void preDestroy() {
        if (treeViewerWidget != null) {
            treeViewerWidget.getTreeViewer().getTree().dispose();
        }
        if (null != fuzzyScaler) {
            fuzzyScaler.dispose();
        }
        if (null != inputFilter) {
            inputFilter.dispose();
        }
        if (null != labelScale) {
            labelScale.dispose();
        }
    }
}
