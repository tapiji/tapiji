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
package org.eclipse.babel.tapiji.tools.core.ui.dialogs;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.model.exception.ResourceBundleException;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.util.LocaleUtils;
import org.eclipse.babel.tapiji.tools.core.util.ResourceUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class CreateResourceBundleEntryDialog extends TitleAreaDialog {

	private static int WIDTH_LEFT_COLUMN = 100;
	
	private static final String DEFAULT_KEY = "defaultkey";
	
	private String projectName;
	
	private Text txtKey;
	private Combo cmbRB;
	private Text txtDefaultText;
	private Combo cmbLanguage;
	
	private Button okButton;
	private Button cancelButton;
	
	/*** Dialog Model ***/
	String selectedRB = "";
	String selectedLocale = "";
	String selectedKey = "";
	String selectedDefaultText = "";
	
	/*** MODIFY LISTENER ***/
	ModifyListener rbModifyListener;
	
	public class DialogConfiguration {

		String projectName;
		
		String preselectedKey;
		String preselectedMessage;
		String preselectedBundle;
		String preselectedLocale;
		
		public String getProjectName() {
			return projectName;
		}
		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}
		public String getPreselectedKey() {
			return preselectedKey;
		}
		public void setPreselectedKey(String preselectedKey) {
			this.preselectedKey = preselectedKey;
		}
		public String getPreselectedMessage() {
			return preselectedMessage;
		}
		public void setPreselectedMessage(String preselectedMessage) {
			this.preselectedMessage = preselectedMessage;
		}
		public String getPreselectedBundle() {
			return preselectedBundle;
		}
		public void setPreselectedBundle(String preselectedBundle) {
			this.preselectedBundle = preselectedBundle;
		}
		public String getPreselectedLocale() {
			return preselectedLocale;
		}
		public void setPreselectedLocale(String preselectedLocale) {
			this.preselectedLocale = preselectedLocale;
		}
		
	}
	
	public CreateResourceBundleEntryDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public void setDialogConfiguration(DialogConfiguration config) {
		String preselectedKey = config.getPreselectedKey();
		this.selectedKey = preselectedKey != null ? preselectedKey.trim() : preselectedKey;
		if ("".equals(this.selectedKey)) {
			this.selectedKey = DEFAULT_KEY;
		}
		
		this.selectedDefaultText = config.getPreselectedMessage();
		this.selectedRB = config.getPreselectedBundle();
		this.selectedLocale = config.getPreselectedLocale();
		this.projectName = config.getProjectName();
	}

	public String getSelectedResourceBundle () {
		return selectedRB;
	}
	
	public String getSelectedKey () {
		return selectedKey;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		initLayout (dialogArea);
		constructRBSection (dialogArea);
		constructDefaultSection (dialogArea);
		initContent ();
		return dialogArea;
	}

	protected void initContent() {
		cmbRB.removeAll();
		int iSel = -1;
		int index = 0;
		
		Collection<String> availableBundles = ResourceBundleManager.getManager(projectName).getResourceBundleNames();
		
		for (String bundle : availableBundles) {
			cmbRB.add(bundle);
			if (bundle.equals(selectedRB)) {
				cmbRB.select(index);
				iSel = index;
				cmbRB.setEnabled(false);
			}
			index ++;
		}
		
		if (availableBundles.size() > 0 && iSel < 0) {
			cmbRB.select(0);
			selectedRB = cmbRB.getText();
			cmbRB.setEnabled(true);
		}
		
		rbModifyListener = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				selectedRB = cmbRB.getText();
				validate();
			}
		};
		cmbRB.removeModifyListener(rbModifyListener);
		cmbRB.addModifyListener(rbModifyListener);
		
		
		cmbRB.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedLocale = "";
				updateAvailableLanguages();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
				selectedLocale = "";
				updateAvailableLanguages();
			}
		});
		updateAvailableLanguages();
		validate();
	}
	
	protected void updateAvailableLanguages () {
		cmbLanguage.removeAll();
		String selectedBundle = cmbRB.getText();
		
		if ("".equals(selectedBundle.trim())) {
			return;
		}
		
		ResourceBundleManager manager = ResourceBundleManager.getManager(projectName);
		
		// Retrieve available locales for the selected resource-bundle
		Set<Locale> locales = manager.getProvidedLocales(selectedBundle);
		int index = 0;
		int iSel = -1;
		for (Locale l : manager.getProvidedLocales(selectedBundle)) {
			String displayName = l == null ? ResourceBundleManager.defaultLocaleTag : l.getDisplayName();
			if (displayName.equals(selectedLocale))
				iSel = index;
			if (displayName.equals(""))
				displayName = ResourceBundleManager.defaultLocaleTag;
			cmbLanguage.add(displayName);
			if (index == iSel)
				cmbLanguage.select(iSel);
			index++;
		}
		
		if (locales.size() > 0) {
			cmbLanguage.select(0);
			selectedLocale = cmbLanguage.getText();
		}
		
		cmbLanguage.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				selectedLocale = cmbLanguage.getText();
				validate();
			}
		});
	}
	
	protected void initLayout(Composite parent) {
		final GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
	}

	protected void constructRBSection(Composite parent) {
		final Group group = new Group (parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		group.setText("Resource Bundle");
		
		// define grid data for this group
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));
		
		final Label spacer = new Label (group, SWT.NONE | SWT.LEFT);
		spacer.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		
		final Label infoLabel = new Label (group, SWT.NONE | SWT.LEFT);
		infoLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		infoLabel.setText("Specify the key of the new resource as well as the Resource-Bundle in\n" +
				"which the resource" +
				"should be added.\n");
		
		// Schl�ssel
		final Label  lblKey = new Label (group, SWT.NONE | SWT.RIGHT);
		GridData lblKeyGrid = new GridData(GridData.END, GridData.CENTER, false, false, 1, 1);
		lblKeyGrid.widthHint = WIDTH_LEFT_COLUMN;
		lblKey.setLayoutData(lblKeyGrid);
		lblKey.setText("Key:");
		txtKey = new Text (group, SWT.BORDER);
		txtKey.setText(selectedKey);
		// grey ouut textfield  if there already is a preset key
		txtKey.setEditable(selectedKey.trim().length() == 0 || selectedKey.indexOf("[Platzhalter]")>=0 || selectedKey.equals(DEFAULT_KEY));
		txtKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		txtKey.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				selectedKey = txtKey.getText();
				validate();
			}
		});
		
		// Resource-Bundle
		final Label  lblRB = new Label (group, SWT.NONE);
		lblRB.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		lblRB.setText("Resource-Bundle:");
		
		cmbRB = new Combo (group, SWT.DROP_DOWN | SWT.SIMPLE);
		cmbRB.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
	}
	
	protected void constructDefaultSection(Composite parent) {
		final Group group = new Group (parent, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, true, 1, 1));
		group.setText("Default-Text");
		
		// define grid data for this group
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));
		
		final Label spacer = new Label (group, SWT.NONE | SWT.LEFT);
		spacer.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
				
		final Label infoLabel = new Label (group, SWT.NONE | SWT.LEFT);
		infoLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		infoLabel.setText("Define a default text for the specified resource. Moreover, you need to\n" +
				"select the locale for which the default text should be defined.");
		
		// Text
		final Label  lblText = new Label (group, SWT.NONE | SWT.RIGHT);
		GridData lblTextGrid = new GridData(GridData.END, GridData.CENTER, false, false, 1, 1);
		lblTextGrid.heightHint = 80;
		lblTextGrid.widthHint = 100;
		lblText.setLayoutData(lblTextGrid);
		lblText.setText("Text:");
		
		txtDefaultText = new Text (group, SWT.MULTI | SWT.BORDER);
		txtDefaultText.setText(selectedDefaultText);
		txtDefaultText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1));
		txtDefaultText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				selectedDefaultText = txtDefaultText.getText();
				validate();
			}
		});
		
		// Sprache
		final Label  lblLanguage = new Label (group, SWT.NONE);
		lblLanguage.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		lblLanguage.setText("Language (Country):");
		
		cmbLanguage = new Combo (group, SWT.DROP_DOWN | SWT.SIMPLE);
		cmbLanguage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
	}

	@Override
	protected void okPressed() {
		super.okPressed();
		// TODO debug
		ResourceBundleManager manager = ResourceBundleManager.getManager(projectName);
		// Insert new Resource-Bundle reference
		Locale locale = LocaleUtils.getLocaleByDisplayName( manager.getProvidedLocales(selectedRB), selectedLocale); // new Locale(""); // retrieve locale
		
		try {
			manager.addResourceBundleEntry (selectedRB, selectedKey, locale, selectedDefaultText);
		} catch (ResourceBundleException e) {
			Logger.logError(e);
		}
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create Resource-Bundle entry");
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		super.create();
		this.setTitle("New Resource-Bundle entry");
		this.setMessage("Please, specify details about the new Resource-Bundle entry");
	}
	
	/**
	 * Validates all inputs of the CreateResourceBundleEntryDialog
	 */
	protected void validate () {
		// Check Resource-Bundle ids
		boolean keyValid = false;
		boolean keyValidChar = ResourceUtils.isValidResourceKey(selectedKey);
		boolean rbValid = false;
		boolean textValid = false;
		ResourceBundleManager manager = ResourceBundleManager.getManager(projectName);
		boolean localeValid = LocaleUtils.containsLocaleByDisplayName(manager.getProvidedLocales(selectedRB), selectedLocale);
		
		for (String rbId : manager.getResourceBundleNames()) {
			if (rbId.equals(selectedRB)) {
				rbValid = true;
				break;
			}
		}
		
		if (!manager.isResourceExisting(selectedRB, selectedKey))
			keyValid = true;
		
		if (selectedDefaultText.trim().length() > 0)
			textValid = true;
		
		// print Validation summary
		String errorMessage = null;
		if (selectedKey.trim().length() == 0)
			errorMessage = "No resource key specified.";
		else if (! keyValidChar)
			errorMessage = "The specified resource key contains invalid characters.";
		else if (! keyValid)
			errorMessage = "The specified resource key is already existing.";
		else if (! rbValid)
			errorMessage = "The specified Resource-Bundle does not exist.";
		else if (! localeValid)
			errorMessage = "The specified Locale does not exist for the selected Resource-Bundle.";
		else if (! textValid)
			errorMessage = "No default translation specified.";
		else {
			if (okButton != null)
				okButton.setEnabled(true);
		}

		setErrorMessage(errorMessage);
		if (okButton != null && errorMessage != null)
			okButton.setEnabled(false);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {	
		okButton = createButton (parent, OK, "Ok", true);
		okButton.addSelectionListener (new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
		        // Set return code
		        setReturnCode(OK);
		        close();
		      }
		});
		
		cancelButton = createButton (parent, CANCEL, "Cancel", false);
		cancelButton.addSelectionListener (new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode (CANCEL);
				close();
			}
		});
		
		okButton.setEnabled(true);
		cancelButton.setEnabled(true);
	}
	
}
