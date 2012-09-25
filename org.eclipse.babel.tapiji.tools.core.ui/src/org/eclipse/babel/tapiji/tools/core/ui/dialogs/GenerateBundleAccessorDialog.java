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

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GenerateBundleAccessorDialog extends TitleAreaDialog {

    private static int WIDTH_LEFT_COLUMN = 100;

    private Text bundleAccessor;
    private Text packageName;

    public GenerateBundleAccessorDialog(Shell parentShell) {
	super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
	Composite dialogArea = (Composite) super.createDialogArea(parent);
	initLayout(dialogArea);
	constructBASection(dialogArea);
	// constructDefaultSection (dialogArea);
	initContent();
	return dialogArea;
    }

    protected void initLayout(Composite parent) {
	final GridLayout layout = new GridLayout(1, true);
	parent.setLayout(layout);
    }

    protected void constructBASection(Composite parent) {
	final Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
	group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
		false, 1, 1));
	group.setText("Resource Bundle");

	// define grid data for this group
	GridData gridData = new GridData();
	gridData.horizontalAlignment = SWT.FILL;
	gridData.grabExcessHorizontalSpace = true;
	group.setLayoutData(gridData);
	group.setLayout(new GridLayout(2, false));

	final Label spacer = new Label(group, SWT.NONE | SWT.LEFT);
	spacer.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,
		false, false, 1, 1));

	final Label infoLabel = new Label(group, SWT.NONE | SWT.LEFT);
	infoLabel.setLayoutData(new GridData(GridData.BEGINNING,
		GridData.CENTER, false, false, 1, 1));
	infoLabel
		.setText("Diese Zeile stellt einen Platzhalter f�r einen kurzen Infotext dar.\nDiese Zeile stellt einen Platzhalter f�r einen kurzen Infotext dar.");

	// Schl�ssel
	final Label lblBA = new Label(group, SWT.NONE | SWT.RIGHT);
	GridData lblBAGrid = new GridData(GridData.END, GridData.CENTER, false,
		false, 1, 1);
	lblBAGrid.widthHint = WIDTH_LEFT_COLUMN;
	lblBA.setLayoutData(lblBAGrid);
	lblBA.setText("Class-Name:");

	bundleAccessor = new Text(group, SWT.BORDER);
	bundleAccessor.setLayoutData(new GridData(GridData.FILL,
		GridData.CENTER, true, false, 1, 1));

	// Resource-Bundle
	final Label lblPkg = new Label(group, SWT.NONE);
	lblPkg.setLayoutData(new GridData(GridData.END, GridData.CENTER, false,
		false, 1, 1));
	lblPkg.setText("Package:");

	packageName = new Text(group, SWT.BORDER);
	packageName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
		true, false, 1, 1));
    }

    protected void initContent() {
	bundleAccessor.setText("BundleAccessor");
	packageName.setText("a.b");
    }

    /*
     * protected void constructDefaultSection(Composite parent) { final Group
     * group = new Group (parent, SWT.SHADOW_ETCHED_IN); group.setLayoutData(new
     * GridData(GridData.FILL, GridData.CENTER, true, true, 1, 1));
     * group.setText("Basis-Text");
     * 
     * // define grid data for this group GridData gridData = new GridData();
     * gridData.horizontalAlignment = SWT.FILL;
     * gridData.grabExcessHorizontalSpace = true; group.setLayoutData(gridData);
     * group.setLayout(new GridLayout(2, false));
     * 
     * final Label spacer = new Label (group, SWT.NONE | SWT.LEFT);
     * spacer.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,
     * false, false, 1, 1));
     * 
     * final Label infoLabel = new Label (group, SWT.NONE | SWT.LEFT);
     * infoLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,
     * false, false, 1, 1)); infoLabel.setText(
     * "Diese Zeile stellt einen Platzhalter f�r einen kurzen Infotext dar.\nDiese Zeile stellt einen Platzhalter f�r einen kurzen Infotext dar."
     * );
     * 
     * // Text final Label lblText = new Label (group, SWT.NONE | SWT.RIGHT);
     * GridData lblTextGrid = new GridData(GridData.END, GridData.CENTER, false,
     * false, 1, 1); lblTextGrid.heightHint = 80; lblTextGrid.widthHint = 100;
     * lblText.setLayoutData(lblTextGrid); lblText.setText("Text:");
     * 
     * txtDefaultText = new Text (group, SWT.MULTI | SWT.BORDER);
     * txtDefaultText.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
     * true, true, 1, 1));
     * 
     * // Sprache final Label lblLanguage = new Label (group, SWT.NONE);
     * lblLanguage.setLayoutData(new GridData(GridData.END, GridData.CENTER,
     * false, false, 1, 1)); lblLanguage.setText("Sprache (Land):");
     * 
     * cmbLanguage = new Combo (group, SWT.DROP_DOWN | SWT.SIMPLE);
     * cmbLanguage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
     * true, false, 1, 1)); }
     */

    @Override
    protected void configureShell(Shell newShell) {
	super.configureShell(newShell);
	newShell.setText("Create Resource-Bundle Accessor");
    }

}
