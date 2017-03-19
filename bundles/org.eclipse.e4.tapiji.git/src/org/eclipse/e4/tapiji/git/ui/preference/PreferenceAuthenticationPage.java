package org.eclipse.e4.tapiji.git.ui.preference;


import javax.inject.Inject;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;


public class PreferenceAuthenticationPage extends FieldEditorPreferencePage {

    @Inject
    IGitService service;
    private String TAG = PreferenceAuthenticationPage.class.getSimpleName();
    private FileFieldEditor publicKey;
    private FileFieldEditor privateKey;

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

        privateKey = new FileFieldEditor("authToken", "SSH Private Key", getFieldEditorParent());
        addField(privateKey);
        publicKey = new FileFieldEditor("authToken", "SSH Public Key", getFieldEditorParent());
        addField(publicKey);
    }

    @Override
    protected void performApply() {
        service.setPrivateKeyPath(privateKey.getStringValue());
        service.setPublicKeyPath(publicKey.getStringValue());
        super.performApply();
    }
}
