package org.eclipse.e4.tapiji.git.ui.preference;


import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;


public class PreferenceAuthenticationPage extends FieldEditorPreferencePage {

    private String TAG = PreferenceAuthenticationPage.class.getSimpleName();

    public PreferenceAuthenticationPage() {
        super(GRID);
        setTitle("Auth");

    }

    @Override
    protected void createFieldEditors() {

        GridData gd = new GridData();
        gd.horizontalSpan = 3;
        Label label = new Label(getFieldEditorParent(), SWT.LEFT);
        label.setText("Set up SSH keys and stay logged in.");
        label.setLayoutData(gd);

        addField(new DirectoryFieldEditor("authToken", "SSH Private Key", getFieldEditorParent()));
        addField(new DirectoryFieldEditor("authToken", "SSH Public Key", getFieldEditorParent()));
    }
}
