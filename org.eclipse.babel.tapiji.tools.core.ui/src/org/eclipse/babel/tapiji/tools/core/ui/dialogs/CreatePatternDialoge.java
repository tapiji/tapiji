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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CreatePatternDialoge extends Dialog{
	private String pattern;
	private Text patternText;
	
	
	public CreatePatternDialoge(Shell shell) {
		this(shell,"");
	}
	
	public CreatePatternDialoge(Shell shell, String pattern) {
		super(shell);
		this.pattern = pattern;
//		setShellStyle(SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setLayoutData(new GridData(SWT.FILL,SWT.TOP, false, false));
		
		Label descriptionLabel = new Label(composite, SWT.NONE);
		descriptionLabel.setText("Enter a regular expression:");
		
		patternText = new Text(composite, SWT.WRAP | SWT.MULTI);
		GridData gData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gData.widthHint = 400;
		gData.heightHint = 60;
		patternText.setLayoutData(gData);
		patternText.setText(pattern);
		

		
		return composite;
	}

	@Override
	protected void okPressed() {
		pattern =  patternText.getText();
		
		super.okPressed();
	}
	
	public String getPattern(){
		return pattern;
	}

}
