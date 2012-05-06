/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.preferences;

import org.eclipse.babel.tapiji.tools.core.ui.Activator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class BuilderPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {
	private static final int INDENT = 20;

	private Button checkSameValueButton;
	private Button checkMissingValueButton;
	private Button checkMissingLanguageButton;

	private Button rbAuditButton;

	private Button sourceAuditButton;

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		IPreferenceStore prefs = getPreferenceStore();
		Composite composite = new Composite(parent, SWT.SHADOW_OUT);

		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		Composite field = createComposite(parent, 0, 10);
		Label descriptionLabel = new Label(composite, SWT.NONE);
		descriptionLabel.setText("Select types of reported problems:");

		field = createComposite(composite, 0, 0);
		sourceAuditButton = new Button(field, SWT.CHECK);
		sourceAuditButton.setSelection(prefs
		        .getBoolean(TapiJIPreferences.AUDIT_RESOURCE));
		sourceAuditButton
		        .setText("Check source code for non externalizated Strings");

		field = createComposite(composite, 0, 0);
		rbAuditButton = new Button(field, SWT.CHECK);
		rbAuditButton
		        .setSelection(prefs.getBoolean(TapiJIPreferences.AUDIT_RB));
		rbAuditButton
		        .setText("Check ResourceBundles on the following problems:");
		rbAuditButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				setRBAudits();
			}
		});

		field = createComposite(composite, INDENT, 0);
		checkMissingValueButton = new Button(field, SWT.CHECK);
		checkMissingValueButton.setSelection(prefs
		        .getBoolean(TapiJIPreferences.AUDIT_UNSPEZIFIED_KEY));
		checkMissingValueButton.setText("Missing translation for a key");

		field = createComposite(composite, INDENT, 0);
		checkSameValueButton = new Button(field, SWT.CHECK);
		checkSameValueButton.setSelection(prefs
		        .getBoolean(TapiJIPreferences.AUDIT_SAME_VALUE));
		checkSameValueButton
		        .setText("Same translations for one key in diffrent languages");

		field = createComposite(composite, INDENT, 0);
		checkMissingLanguageButton = new Button(field, SWT.CHECK);
		checkMissingLanguageButton.setSelection(prefs
		        .getBoolean(TapiJIPreferences.AUDIT_MISSING_LANGUAGE));
		checkMissingLanguageButton
		        .setText("Missing languages in a ResourceBundle");

		setRBAudits();

		composite.pack();

		return composite;
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore prefs = getPreferenceStore();

		sourceAuditButton.setSelection(prefs
		        .getDefaultBoolean(TapiJIPreferences.AUDIT_RESOURCE));
		rbAuditButton.setSelection(prefs
		        .getDefaultBoolean(TapiJIPreferences.AUDIT_RB));
		checkMissingValueButton.setSelection(prefs
		        .getDefaultBoolean(TapiJIPreferences.AUDIT_UNSPEZIFIED_KEY));
		checkSameValueButton.setSelection(prefs
		        .getDefaultBoolean(TapiJIPreferences.AUDIT_SAME_VALUE));
		checkMissingLanguageButton.setSelection(prefs
		        .getDefaultBoolean(TapiJIPreferences.AUDIT_MISSING_LANGUAGE));
	}

	@Override
	public boolean performOk() {
		IPreferenceStore prefs = getPreferenceStore();

		prefs.setValue(TapiJIPreferences.AUDIT_RESOURCE,
		        sourceAuditButton.getSelection());
		prefs.setValue(TapiJIPreferences.AUDIT_RB, rbAuditButton.getSelection());
		prefs.setValue(TapiJIPreferences.AUDIT_UNSPEZIFIED_KEY,
		        checkMissingValueButton.getSelection());
		prefs.setValue(TapiJIPreferences.AUDIT_SAME_VALUE,
		        checkSameValueButton.getSelection());
		prefs.setValue(TapiJIPreferences.AUDIT_MISSING_LANGUAGE,
		        checkMissingLanguageButton.getSelection());

		return super.performOk();
	}

	private Composite createComposite(Composite parent, int marginWidth,
	        int marginHeight) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout indentLayout = new GridLayout(1, false);
		indentLayout.marginWidth = marginWidth;
		indentLayout.marginHeight = marginHeight;
		indentLayout.verticalSpacing = 0;
		composite.setLayout(indentLayout);

		return composite;
	}

	protected void setRBAudits() {
		boolean selected = rbAuditButton.getSelection();
		checkMissingValueButton.setEnabled(selected);
		checkSameValueButton.setEnabled(selected);
		checkMissingLanguageButton.setEnabled(selected);
	}
}
