package org.eclipse.e4.tapiji.git.ui.preference;


import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;


public class PreferenceGeneralPage extends FieldEditorPreferencePage {

    private ColorFieldEditor colorLineAdded;

    public PreferenceGeneralPage() {
        super(GRID);
        setTitle("Git");
    }

    @Override
    protected void createFieldEditors() {
        // colorLineAdded = new ColorFieldEditor("pref_line_color_added", "Color line added:", getFieldEditorParent());
        // addField(colorLineAdded);

        //addField(new ColorFieldEditor("pref_text_color", "Color line removed:", getFieldEditorParent()));
        //addField(new ColorFieldEditor("pref_link_color", "Color line added:", getFieldEditorParent()));
    }

}
