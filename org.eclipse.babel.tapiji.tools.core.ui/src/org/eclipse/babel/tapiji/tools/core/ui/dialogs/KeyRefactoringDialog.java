/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexej Strelzow - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.dialogs;

import java.util.Locale;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.utils.LocaleUtils;
import org.eclipse.babel.tapiji.tools.core.ui.utils.ResourceUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The dialog between the user and the system. System wants to know
 * what the new name of the selected key is.
 * 
 * @author Alexej Strelzow
 */
public class KeyRefactoringDialog extends TitleAreaDialog {

	/*** Dialog Model ***/
	private DialogConfiguration config;
	private String selectedKey = "";
	private String selectedLocale = "";
	
	public static final String ALL_LOCALES = "All available";
	
	/** GUI */
	private Button okButton;
	private Button cancelButton;
	
	private Label projectLabel;
	private Label resourceBundleLabel;
	private Label oldKeyLabel;
	private Label newKeyLabel;
	private Label languageLabel;
	
	private Text oldKeyText;
	private Text newKeyText;
	private Text projectText;
	private Text resourceBundleText;
	private Combo languageCombo;
	
	/**
	 * Meta data for the dialog.
	 * 
	 * @author Alexej Strelzow
	 */
	public class DialogConfiguration {

		String projectName;
		String preselectedKey;
		String preselectedBundle;
		
		String newKey;
		String selectedLocale;

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

		public String getPreselectedBundle() {
			return preselectedBundle;
		}

		public void setPreselectedBundle(String preselectedBundle) {
			this.preselectedBundle = preselectedBundle;
		}
		
		public String getNewKey() {
			return newKey;
		}
		
		public void setNewKey(String newKey) {
			this.newKey = newKey;
		}
		
		public String getSelectedLocale() {
			return selectedLocale;
		}

		public void setSelectedLocale(String selectedLocale) {
			this.selectedLocale = selectedLocale;
		}
	}
	
	/**
	 * Constructor.
	 * @param parentShell The parent's shell
	 */
	public KeyRefactoringDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		initLayout(dialogArea);
		initContent();
		
		return super.createDialogArea(parent);
	}
	
	private void initContent() {
		ResourceBundleManager manager = ResourceBundleManager.getManager(config.getProjectName());
		// Retrieve available locales for the selected resource-bundle
		Set<Locale> locales = manager.getProvidedLocales(config.getPreselectedBundle());

		String displayName = ResourceBundleManager.defaultLocaleTag;
		
		// if only 1 locale available, then set this locale
		if (locales.size() == 1) {
			Locale l = locales.iterator().next();
			languageCombo.add(l == null ? displayName : l.getDisplayName());
		} else {
			languageCombo.add(ALL_LOCALES);
			for (Locale l : locales) {
				displayName = l == null ? ResourceBundleManager.defaultLocaleTag
						: l.getDisplayName();
				languageCombo.add(displayName);
				
			}
		}
		
		languageCombo.select(0);
		selectedLocale = languageCombo.getItem(0);
		newKeyText.setFocus();
		
		languageCombo.addModifyListener(new ModifyListener() {
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void modifyText(ModifyEvent e) {
				selectedLocale = languageCombo.getText();
				validate();
			}
		});
		
	}

	/**
	 * Initializes the layout
	 * @param parent The parent
	 */
	private void initLayout(Composite parent) {
		final GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		
		GridLayout gl = new GridLayout(2, true);
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		
		Composite master = new Composite(parent, SWT.NONE); 
		master.setLayout(gl);
		master.setLayoutData(gd);
		
		projectLabel = new Label(master, SWT.NONE);
		projectLabel.setText("Project:");
		
		projectText = new Text(master, SWT.BORDER);
		projectText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
		        true, true, 1, 1));
		projectText.setText(config.getProjectName());
		projectText.setEnabled(false);
		
		resourceBundleLabel = new Label(master, SWT.NONE);
		resourceBundleLabel.setText("Resource-Bundle:");
		
		resourceBundleText = new Text(master, SWT.BORDER);
		resourceBundleText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
		        true, true, 1, 1));
		resourceBundleText.setText(config.getPreselectedBundle());
		resourceBundleText.setEnabled(false);
		
		languageLabel = new Label(master, SWT.NONE);
		languageLabel.setText("Language (Country):");
		
		languageCombo = new Combo(master, SWT.BORDER);
		languageCombo.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
		        true, true, 1, 1));
		
		oldKeyLabel = new Label(master, SWT.NONE);
		oldKeyLabel.setText("Old key name:");
		
		oldKeyText = new Text(master, SWT.BORDER);
		oldKeyText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
		        true, true, 1, 1));
		oldKeyText.setText(config.getPreselectedKey());
		oldKeyText.setEnabled(false);
		
		newKeyLabel = new Label(master, SWT.NONE);
		newKeyLabel.setText("New key name:");
		
		newKeyText = new Text(master, SWT.BORDER);
		newKeyText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
		        true, true, 1, 1));
		
		newKeyText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				selectedKey = newKeyText.getText();
				validate();
			}
		});
	}

	/**
	 * @param config Sets the config
	 */
	public void setDialogConfiguration(DialogConfiguration config) {
		this.config = config;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Key refactoring");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		// TODO Auto-generated method stub
		super.create();
		this.setTitle("Key refactoring");
		this.setMessage("Please, specify the name of the new key. \r\n" +
				"The new value will automatically replace the old ones.");
	}

	/**
	 * @return The config 
	 */
	public DialogConfiguration getConfig() {
		return this.config;
	}
	
	/**
	 * Validates all inputs of the CreateResourceBundleEntryDialog
	 */
	protected void validate() {
		// Check Resource-Bundle ids
		boolean keyValid = false;
		boolean localeValid = true;
		boolean keyValidChar = ResourceUtils.isValidResourceKey(selectedKey);
		
		String resourceBundle = config.getPreselectedBundle();
		
		ResourceBundleManager manager = ResourceBundleManager
		        .getManager(config.getProjectName());
		
		
		if (!ALL_LOCALES.equals(selectedLocale)) {
			localeValid = LocaleUtils.containsLocaleByDisplayName(
		        manager.getProvidedLocales(resourceBundle), selectedLocale);
		}

		if (!manager.isResourceExisting(resourceBundle, selectedKey)) {
			keyValid = true;
		}
		// print Validation summary
		String errorMessage = null;
		if (selectedKey.trim().length() == 0) {
			errorMessage = "No resource key specified.";
		} else if (!keyValidChar) {
			errorMessage = "The specified resource key contains invalid characters.";
		} else if (!keyValid)
			errorMessage = "The specified resource key is already existing.";
		else if (!localeValid) {
			errorMessage = "The specified Locale does not exist for the selected Resource-Bundle.";
		}else {
			if (okButton != null)
				okButton.setEnabled(true);
		}

		setErrorMessage(errorMessage);
		if (okButton != null && errorMessage != null) {
			okButton.setEnabled(false);
		} else {
			this.config.setNewKey(selectedKey);
			this.config.setSelectedLocale(selectedLocale);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, OK, "Ok", true);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// Set return code
				setReturnCode(OK);
				close();
			}
		});

		cancelButton = createButton(parent, CANCEL, "Cancel", false);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});

		okButton.setEnabled(true);
		cancelButton.setEnabled(true);
	}
	
}
