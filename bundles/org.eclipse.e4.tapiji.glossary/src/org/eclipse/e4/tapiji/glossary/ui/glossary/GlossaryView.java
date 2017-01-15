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
package org.eclipse.e4.tapiji.glossary.ui.glossary;


import java.io.File;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.tapiji.glossary.model.Glossary;
import org.eclipse.e4.tapiji.glossary.model.constants.GlossaryServiceConstants;
import org.eclipse.e4.tapiji.glossary.preference.StoreInstanceState;
import org.eclipse.e4.tapiji.glossary.ui.treeviewer.TreeViewerContract;
import org.eclipse.e4.tapiji.glossary.ui.treeviewer.TreeViewerView;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.translator.i18n.Messages;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public final class GlossaryView implements Listener, GlossaryContract.View {

    private static final String EXPRESSION_TRANSLATION_REFERENCE = "org.eclipse.e4.tapiji.glossary.popupmenu.TRANSLATION_REFERENCE";
    private static final String EXPRESSION_TRANSLATION_VISIBILITY = "org.eclipse.e4.tapiji.glossary.popupmenu.TRANSLATION_VISIBILITY";

    private static final String TAG = GlossaryView.class.getSimpleName();

    private TreeViewerContract.View treeViewer;
    private Scale fuzzyScaler;
    private Label labelScale;
    private Text inputFilter;

    @Inject
    private GlossaryPresenter presenter;

    @Inject
    private StoreInstanceState storeInstanceState;

    @PostConstruct
    public void createPartControl(final Composite parent, @Translation Messages message) {
        parent.setLayout(new GridLayout(1, false));
        presenter.setView(this);

        final Composite parentComp = new Composite(parent, SWT.BORDER);
        parentComp.setLayout(new GridLayout(2, false));
        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        final Label labelSearch = new Label(parentComp, SWT.NONE);
        labelSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelSearch.setText(message.glossarySearchExpression);

        inputFilter = new Text(parentComp, SWT.BORDER | SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);
        inputFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        if(presenter.hasGlossaryTerms()) {
            inputFilter.setEnabled(false);
        }
        inputFilter.addModifyListener(e -> {
            if (treeViewer != null) {
                treeViewer.setSearchString(inputFilter.getText());
            }
        });

        labelScale = new Label(parentComp, SWT.NONE);
        labelScale.setText(message.glossarySearchPrecision);
        labelScale.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false, 1, 1));

        fuzzyScaler = new Scale(parentComp, SWT.NONE);
        fuzzyScaler.setMaximum(100);
        fuzzyScaler.setMinimum(0);
        fuzzyScaler.setIncrement(1);
        fuzzyScaler.setPageIncrement(5);
        fuzzyScaler.addListener(SWT.Selection, this);
        fuzzyScaler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        onRestoreInstance();
        treeViewer = TreeViewerView.create(parent, presenter.getContext());

        Log.d(TAG, String.format("Array: %s", storeInstanceState.toString()));
    }

    @Override
    public TreeViewerContract.View getTreeViewerView() {
        return this.treeViewer;
    }

    @Inject
    @Optional
    private void onError(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_ERROR) final String[] error, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
        MessageDialog.openError(shell, "Deletion not possible", "No term selected");
    }

    @Inject
    @Optional
    private void onReloadGlossary(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD) final Glossary glossary) {
        if (glossary != null) {
            presenter.getContext().set(EXPRESSION_TRANSLATION_REFERENCE, glossary.info.translations.size());
            presenter.getContext().set(EXPRESSION_TRANSLATION_VISIBILITY, glossary.info.translations.size() - 1);
            treeViewer.updateView(glossary);
            if(presenter.hasGlossaryTerms()) {
                inputFilter.setEnabled(false);
            } else {
                inputFilter.setEnabled(true);
            }
        }
    }


    private void onRestoreInstance() {
        if (!storeInstanceState.getGlossaryFile().isEmpty()) {
            presenter.openGlossary(new File(storeInstanceState.getGlossaryFile()));
        }
        presenter.getContext().set(EXPRESSION_TRANSLATION_REFERENCE, 0);
        presenter.getContext().set(EXPRESSION_TRANSLATION_VISIBILITY, 0);
        inputFilter.setText(storeInstanceState.getSearchValue());
        fuzzyScaler.setSelection((int) storeInstanceState.getMatchingPrecision());
        showHideFuzzyMatching(storeInstanceState.isFuzzyMode());
    }


    @Override
    public void showHideFuzzyMatching(final boolean isVisible) {
        ((GridData) labelScale.getLayoutData()).exclude = !isVisible;
        ((GridData) fuzzyScaler.getLayoutData()).exclude = !isVisible;

        labelScale.setVisible(isVisible);
        fuzzyScaler.setVisible(isVisible);
        
        labelScale.getParent().getParent().layout();
        if (treeViewer != null) {
            treeViewer.enableFuzzyMatching(isVisible);
        }
    }

    @Override
    public void handleEvent(final Event event) {
        if (null != fuzzyScaler) {
            treeViewer.setMatchingPrecision(presenter.getFuzzyPrecission((fuzzyScaler.getMaximum() - fuzzyScaler.getSelection()) + fuzzyScaler.getMinimum()));
        }
    }

    @Focus
    public void setFocus() {
        treeViewer.getTreeViewer().getControl().setFocus();
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

    @Persist
    public void onSave() {
        presenter.saveGlossary();
    }

    @PreDestroy
    public void preDestroy() {
        if (treeViewer != null) {
            treeViewer.getTreeViewer().getTree().dispose();
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
