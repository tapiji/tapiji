package org.eclipselabs.tapiji.tools.core.ui.dialogs;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

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
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.ui.widgets.ResourceSelector;
import org.eclipselabs.tapiji.tools.core.ui.widgets.event.ResourceSelectionEvent;
import org.eclipselabs.tapiji.tools.core.ui.widgets.listener.IResourceSelectionListener;


public class ResourceBundleEntrySelectionDialog extends TitleAreaDialog {

	private static int WIDTH_LEFT_COLUMN = 100;
	private static int SEARCH_FULLTEXT = 0;
	private static int SEARCH_KEY = 1;
	
	private ResourceBundleManager manager;
	private Collection<String> availableBundles;
	private int searchOption  = SEARCH_FULLTEXT;
	private String resourceBundle = "";
	
	private Combo cmbRB;
	
	private Button btSearchText;
	private Button btSearchKey;
	private Combo cmbLanguage;
	private ResourceSelector resourceSelector;
	private Text txtPreviewText;
	
	private Button okButton;
	private Button cancelButton;
	
	/*** DIALOG MODEL ***/
	private String selectedRB = "";
	private String preselectedRB = "";
	private Locale selectedLocale = null;
	private String selectedKey = "";
	
	
	public ResourceBundleEntrySelectionDialog(Shell parentShell, ResourceBundleManager manager, String bundleName) {
		super(parentShell);
		this.manager = manager;
		// init available resource bundles
		this.availableBundles = manager.getResourceBundleNames();
		this.preselectedRB = bundleName;
	}

	@Override
	protected Control createDialogArea(Composite parent) {		
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		initLayout (dialogArea);
		constructSearchSection (dialogArea);
		initContent ();
		return dialogArea;
	}

	protected void initContent() {
		// init available resource bundles
		cmbRB.removeAll();
		int i = 0;
		for (String bundle : availableBundles) {
			cmbRB.add(bundle);
			if (bundle.equals(preselectedRB)) {
				cmbRB.select(i);
				cmbRB.setEnabled(false);
			}
			i++;
		}
		
		if (availableBundles.size() > 0) {
			if (preselectedRB.trim().length() == 0) {
				cmbRB.select(0);
				cmbRB.setEnabled(true);
			}
		}
		
		cmbRB.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				//updateAvailableLanguages();
				updateResourceSelector ();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//updateAvailableLanguages();
				updateResourceSelector ();
			}
		});
		
		// init available translations
		//updateAvailableLanguages();
		
		// init resource selector
		updateResourceSelector();
		
		// update search options
		updateSearchOptions();
	}
	
	protected void updateResourceSelector () {
		resourceBundle = cmbRB.getText();
		resourceSelector.setResourceBundle(resourceBundle);
	}
	
	protected void updateSearchOptions () {
		searchOption = (btSearchKey.getSelection() ? SEARCH_KEY : SEARCH_FULLTEXT);
//		cmbLanguage.setEnabled(searchOption == SEARCH_FULLTEXT);
//		lblLanguage.setEnabled(cmbLanguage.getEnabled());
		
		// update ResourceSelector
		resourceSelector.setDisplayMode(searchOption == SEARCH_FULLTEXT ? ResourceSelector.DISPLAY_TEXT : ResourceSelector.DISPLAY_KEYS);
	}
	
	protected void updateAvailableLanguages () {
		cmbLanguage.removeAll();
		String selectedBundle = cmbRB.getText();
		
		if (selectedBundle.trim().equals(""))
			return;
		
		// Retrieve available locales for the selected resource-bundle
		Set<Locale> locales = manager.getProvidedLocales(selectedBundle);
		for (Locale l : locales) {
			String displayName = l.getDisplayName();
			if (displayName.equals(""))
				displayName = ResourceBundleManager.defaultLocaleTag;
			cmbLanguage.add(displayName);
		}
		
//		if (locales.size() > 0) {
//			cmbLanguage.select(0);
			updateSelectedLocale();
//		}
	}
	
	protected void updateSelectedLocale () {
		String selectedBundle = cmbRB.getText();
		
		if (selectedBundle.trim().equals(""))
			return;
		
		Set<Locale> locales = manager.getProvidedLocales(selectedBundle);
		Iterator<Locale> it = locales.iterator();
		String selectedLocale = cmbLanguage.getText();
		while (it.hasNext()) {
			Locale l = it.next();
			if (l.getDisplayName().equals(selectedLocale)) {
				resourceSelector.setDisplayLocale(l);
				break;
			}
		}
	}
	
	protected void initLayout(Composite parent) {
		final GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
	}
	
	protected void constructSearchSection (Composite parent) {
		final Group group = new Group (parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		group.setText("Resource selection");
		
		// define grid data for this group
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(2, false));
		// TODO export as help text
		
		final Label spacer = new Label (group, SWT.NONE | SWT.LEFT);
		spacer.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
		
		final Label infoLabel = new Label (group, SWT.NONE | SWT.LEFT);
		GridData infoGrid = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
		infoGrid.heightHint = 70;
		infoLabel.setLayoutData(infoGrid);
		infoLabel.setText("Select the resource that needs to be refrenced. This is accomplished in two\n" +
				"steps. First select the Resource-Bundle in which the resource is located. \n" +
				"In a last step you need to choose a particular resource.");
		
		// Resource-Bundle
		final Label  lblRB = new Label (group, SWT.NONE);
		lblRB.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
		lblRB.setText("Resource-Bundle:");
		
		cmbRB = new Combo (group, SWT.DROP_DOWN | SWT.SIMPLE);
		cmbRB.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		cmbRB.addModifyListener(new ModifyListener() {	
			@Override
			public void modifyText(ModifyEvent e) {
				selectedRB = cmbRB.getText();
				validate();
			}
		});
		
		// Search-Options
		final Label spacer2 = new Label (group, SWT.NONE | SWT.LEFT);
		spacer2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
	 	
		Composite searchOptions = new Composite(group, SWT.NONE);
		searchOptions.setLayout(new GridLayout (2, true));
		
		btSearchText = new Button (searchOptions, SWT.RADIO);
		btSearchText.setText("Flat");
		btSearchText.setSelection(searchOption == SEARCH_FULLTEXT);
		btSearchText.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSearchOptions();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateSearchOptions();
			}
		});
		
		btSearchKey = new Button (searchOptions, SWT.RADIO);
		btSearchKey.setText("Hierarchical");
		btSearchKey.setSelection(searchOption == SEARCH_KEY);
		btSearchKey.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSearchOptions();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateSearchOptions();
			}
		});
				
		// Sprache
//		lblLanguage = new Label (group, SWT.NONE);
//		lblLanguage.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false, 1, 1));
//		lblLanguage.setText("Language (Country):");
//		
//		cmbLanguage = new Combo (group, SWT.DROP_DOWN | SWT.SIMPLE);
//		cmbLanguage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
//		cmbLanguage.addSelectionListener(new SelectionListener () {
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				updateSelectedLocale();
//			}
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateSelectedLocale();
//			}
//			
//		});
//		cmbLanguage.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				selectedLocale =  LocaleUtils.getLocaleByDisplayName(manager.getProvidedLocales(selectedRB), cmbLanguage.getText());
//				validate();
//			}
//		});

		// Filter
//		final Label  lblKey = new Label (group, SWT.NONE | SWT.RIGHT);
//		GridData lblKeyGrid = new GridData(GridData.END, GridData.CENTER, false, false, 1, 1);
//		lblKeyGrid.widthHint = WIDTH_LEFT_COLUMN;
//		lblKey.setLayoutData(lblKeyGrid);
//		lblKey.setText("Filter:");
//		
//		txtKey = new Text (group, SWT.BORDER);
//		txtKey.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1));
		
		// Add selector for property keys
		final Label  lblKeys = new Label (group, SWT.NONE);
		lblKeys.setLayoutData(new GridData(GridData.END, GridData.BEGINNING, false, false, 1, 1));
		lblKeys.setText("Resource:");
		
		resourceSelector = new ResourceSelector (group, SWT.NONE, manager, cmbRB.getText(), searchOption, null, true);
		GridData resourceSelectionData = new GridData(GridData.FILL, GridData.CENTER, true, false, 1, 1);
		resourceSelectionData.heightHint = 150;
		resourceSelectionData.widthHint = 400;
		resourceSelector.setLayoutData(resourceSelectionData);		
		resourceSelector.addSelectionChangedListener(new IResourceSelectionListener() {
			
			@Override
			public void selectionChanged(ResourceSelectionEvent e) {
				selectedKey = e.getSelectedKey();
				updatePreviewLabel(e.getSelectionSummary());
				validate();
			}
		});
		
//		final Label spacer = new Label (group, SWT.SEPARATOR | SWT.HORIZONTAL);
//		spacer.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, 2, 1));
		
		// Preview
		final Label  lblText = new Label (group, SWT.NONE | SWT.RIGHT);
		GridData lblTextGrid = new GridData(GridData.END, GridData.CENTER, false, false, 1, 1);
		lblTextGrid.heightHint = 120;
		lblTextGrid.widthHint = 100;
		lblText.setLayoutData(lblTextGrid);
		lblText.setText("Preview:"); 
		
		txtPreviewText = new Text (group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		txtPreviewText.setEditable(false);
		GridData lblTextGrid2 = new GridData(GridData.FILL, GridData.FILL, true, true, 1, 1);
		txtPreviewText.setLayoutData(lblTextGrid2);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select Resource-Bundle entry");
	}
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		super.create();
		this.setTitle("Select a Resource-Bundle entry");
		this.setMessage("Please, select a resource of a particular Resource-Bundle");
	}

	protected void updatePreviewLabel (String previewText) {
		txtPreviewText.setText(previewText);
	}
	
	protected void validate () {
		// Check Resource-Bundle ids
		boolean rbValid = false;
		boolean localeValid = false;
		boolean keyValid = false;
		
		for (String rbId : this.availableBundles) {
			if (rbId.equals(selectedRB)) {
				rbValid = true;
				break;
			}
		}
		
		if (selectedLocale != null) 
			localeValid = true;
		
		if (manager.isResourceExisting(selectedRB, selectedKey))
			keyValid = true;
		
		// print Validation summary
		String errorMessage = null;
		if (! rbValid)
			errorMessage = "The specified Resource-Bundle does not exist";
//		else if (! localeValid)
//			errorMessage = "The specified Locale does not exist for the selecte Resource-Bundle";
		else if (! keyValid)
			errorMessage = "No resource selected";
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
		
		okButton.setEnabled(false);
		cancelButton.setEnabled(true);
	}
	
	public String getSelectedResourceBundle () {
		return selectedRB;
	}
	
	public String getSelectedResource () {
		return selectedKey;
	}
	
	public Locale getSelectedLocale () {
		return selectedLocale;
	}
}
