/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.prefrences;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.Activator;
import org.eclipse.babel.tapiji.tools.core.model.preferences.CheckItem;
import org.eclipse.babel.tapiji.tools.core.model.preferences.TapiJIPreferences;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreatePatternDialoge;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FilePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {	

	private Table table;
	protected Object dialoge;
	
	private Button editPatternButton;
	private Button removePatternButton;

	@Override
	public void init(IWorkbench workbench) {
		 setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected Control createContents(Composite parent) {
		IPreferenceStore prefs = getPreferenceStore();
		Composite composite = new Composite(parent, SWT.SHADOW_OUT);

		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		
		Label descriptionLabel = new Label(composite, SWT.WRAP);
		GridData descriptionData = new GridData(SWT.FILL, SWT.TOP, false, false);
		descriptionData.horizontalSpan=2;
		descriptionLabel.setLayoutData(descriptionData);
		descriptionLabel.setText("Properties-files which match the following pattern, will not be interpreted as ResourceBundle-files");
		
		table = new Table (composite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(data);
		
		table.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selection = table.getSelection();
				if (selection.length > 0){
					editPatternButton.setEnabled(true);
					removePatternButton.setEnabled(true);
				}else{
					editPatternButton.setEnabled(false);
					removePatternButton.setEnabled(false);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		List<CheckItem> patternItems = TapiJIPreferences.getNonRbPatternAsList();
		for (CheckItem s : patternItems){
			s.toTableItem(table);
		}
		
		Composite sitebar = new Composite(composite, SWT.NONE);
		sitebar.setLayout(new GridLayout(1,false));
		sitebar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true));
		
		Button addPatternButton = new Button(sitebar, SWT.NONE);
		addPatternButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		addPatternButton.setText("Add Pattern");
		addPatternButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseDown(MouseEvent e) {
				String pattern = "^.*/<BASENAME>"+"((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?"+ "\\.properties$";
				CreatePatternDialoge dialog = new CreatePatternDialoge(Display.getDefault().getActiveShell(),pattern);
				if (dialog.open() == InputDialog.OK) {
					pattern = dialog.getPattern();
					
					TableItem item  = new TableItem(table, SWT.NONE);
					item.setText(pattern);
					item.setChecked(true);
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		editPatternButton = new Button(sitebar, SWT.NONE);
		editPatternButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		editPatternButton.setText("Edit");
		editPatternButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseDown(MouseEvent e) {
				TableItem[] selection = table.getSelection();
				if (selection.length > 0){
					String pattern = selection[0].getText();
					
					CreatePatternDialoge dialog = new CreatePatternDialoge(Display.getDefault().getActiveShell(), pattern);
					if (dialog.open() == InputDialog.OK) {
						pattern = dialog.getPattern();
						TableItem item = selection[0];
						item.setText(pattern);
					}
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		
		removePatternButton = new Button(sitebar, SWT.NONE);
		removePatternButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		removePatternButton.setText("Remove");
		removePatternButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void mouseDown(MouseEvent e) {
				TableItem[] selection = table.getSelection();
				if (selection.length > 0)
					table.remove(table.indexOf(selection[0]));
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
			}
		});
		
		composite.pack();
		
		return composite;
	}

	@Override
	protected void performDefaults() {
		IPreferenceStore prefs = getPreferenceStore();
		
		table.removeAll();
		
		List<CheckItem> patterns = TapiJIPreferences.convertStringToList(prefs.getDefaultString(TapiJIPreferences.NON_RB_PATTERN));
		for (CheckItem s : patterns){
			s.toTableItem(table);
		}
	}
	
	@Override
	public boolean performOk() {
		IPreferenceStore prefs = getPreferenceStore();
		List<CheckItem> patterns =new LinkedList<CheckItem>();
		for (TableItem i : table.getItems()){
			patterns.add(new CheckItem(i.getText(), i.getChecked()));
		}
		
		prefs.setValue(TapiJIPreferences.NON_RB_PATTERN, TapiJIPreferences.convertListToString(patterns));
		
		return super.performOk();
	}
}
