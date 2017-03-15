package org.eclipse.e4.tapiji.git.ui.preference;


import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;


public class PreferenceGeneralPage extends FieldEditorPreferencePage {

    public PreferenceGeneralPage() {
        super(GRID);
        setTitle("General");
    }

    @Override
    protected void createFieldEditors() {
        addField(new ColorFieldEditor("pref_text_color", "Text color:", getFieldEditorParent()));
        addField(new ColorFieldEditor("pref_link_color", "Link color:", getFieldEditorParent()));
    }
}
