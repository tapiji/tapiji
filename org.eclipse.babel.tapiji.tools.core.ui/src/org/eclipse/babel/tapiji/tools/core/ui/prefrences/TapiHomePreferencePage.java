package org.eclipse.babel.tapiji.tools.core.ui.prefrences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class TapiHomePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1,true));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label description = new Label(composite, SWT.WRAP);
		description.setText("See sub-pages for settings.");
		
		return parent;
	}

}
