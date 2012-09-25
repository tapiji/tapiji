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
package org.eclipse.babel.tapiji.tools.core.ui.dialogs;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.utils.LocaleUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddLanguageDialoge extends Dialog {
    private Locale locale;
    private Shell shell;

    private Text titelText;
    private Text descriptionText;
    private Combo cmbLanguage;
    private Text language;
    private Text country;
    private Text variant;

    public AddLanguageDialoge(Shell parentShell) {
	super(parentShell);
	shell = parentShell;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	Composite titelArea = new Composite(parent, SWT.NO_BACKGROUND);
	Composite dialogArea = (Composite) super.createDialogArea(parent);
	GridLayout layout = new GridLayout(1, true);
	dialogArea.setLayout(layout);

	initDescription(titelArea);
	initCombo(dialogArea);
	initTextArea(dialogArea);

	titelArea.pack();
	dialogArea.pack();
	parent.pack();

	return dialogArea;
    }

    private void initDescription(Composite titelArea) {
	titelArea.setEnabled(false);
	titelArea.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true,
		1, 1));
	titelArea.setLayout(new GridLayout(1, true));
	titelArea.setBackground(new Color(shell.getDisplay(), 255, 255, 255));

	titelText = new Text(titelArea, SWT.LEFT);
	titelText.setFont(new Font(shell.getDisplay(), shell.getFont()
		.getFontData()[0].getName(), 11, SWT.BOLD));
	titelText.setText("Please, specify the desired language");

	descriptionText = new Text(titelArea, SWT.WRAP);
	descriptionText.setLayoutData(new GridData(450, 60)); // TODO improve
	descriptionText
		.setText("Note: "
			+ "In all ResourceBundles of the project/plug-in will be created a new properties-file with the basename of the ResourceBundle and the corresponding locale-extension. "
			+ "If the locale is just provided of a ResourceBundle, no new file will be created.");
    }

    private void initCombo(Composite dialogArea) {
	cmbLanguage = new Combo(dialogArea, SWT.DROP_DOWN);
	cmbLanguage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true,
		true, 1, 1));

	final Locale[] locales = Locale.getAvailableLocales();
	final Set<Locale> localeSet = new HashSet<Locale>();
	List<String> localeNames = new LinkedList<String>();

	for (Locale l : locales) {
	    localeNames.add(l.getDisplayName());
	    localeSet.add(l);
	}

	Collections.sort(localeNames);

	String[] s = new String[localeNames.size()];
	cmbLanguage.setItems(localeNames.toArray(s));
	cmbLanguage.add(ResourceBundleManager.defaultLocaleTag, 0);

	cmbLanguage.addSelectionListener(new SelectionListener() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		int selectIndex = ((Combo) e.getSource()).getSelectionIndex();
		if (!cmbLanguage.getItem(selectIndex).equals(
			ResourceBundleManager.defaultLocaleTag)) {
		    Locale l = LocaleUtils.getLocaleByDisplayName(localeSet,
			    cmbLanguage.getItem(selectIndex));

		    language.setText(l.getLanguage());
		    country.setText(l.getCountry());
		    variant.setText(l.getVariant());
		} else {
		    language.setText("");
		    country.setText("");
		    variant.setText("");
		}
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
	    }
	});
    }

    private void initTextArea(Composite dialogArea) {
	final Group group = new Group(dialogArea, SWT.SHADOW_ETCHED_IN);
	group.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1,
		1));
	group.setLayout(new GridLayout(3, true));
	group.setText("Locale");

	Label languageLabel = new Label(group, SWT.SINGLE);
	languageLabel.setText("Language");
	Label countryLabel = new Label(group, SWT.SINGLE);
	countryLabel.setText("Country");
	Label variantLabel = new Label(group, SWT.SINGLE);
	variantLabel.setText("Variant");

	language = new Text(group, SWT.SINGLE);
	country = new Text(group, SWT.SINGLE);
	variant = new Text(group, SWT.SINGLE);
    }

    @Override
    protected void okPressed() {
	locale = new Locale(language.getText(), country.getText(),
		variant.getText());

	super.okPressed();
    }

    public Locale getSelectedLanguage() {
	return locale;
    }
}
