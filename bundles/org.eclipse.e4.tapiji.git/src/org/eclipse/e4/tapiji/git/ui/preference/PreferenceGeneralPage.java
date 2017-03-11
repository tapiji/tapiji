package org.eclipse.e4.tapiji.git.ui.preference;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;


public class PreferenceGeneralPage extends FieldEditorPreferencePage {

    public PreferenceGeneralPage() {
        super(GRID);
        setTitle("Git");
    }

    @Override
    protected void createFieldEditors() {
        StringFieldEditor profileName = new StringFieldEditor("prefColor", "Profile Name: ", getFieldEditorParent());
        profileName.setStringValue("dsdds");
        addField(profileName);
        addField(new StringFieldEditor("prefBoolean", "Name : ", getFieldEditorParent()));
        addField(new StringFieldEditor("prefString", "Email: ", getFieldEditorParent()));

    }

}
