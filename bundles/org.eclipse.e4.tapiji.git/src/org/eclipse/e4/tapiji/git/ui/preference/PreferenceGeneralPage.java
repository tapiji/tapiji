package org.eclipse.e4.tapiji.git.ui.preference;


import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;


public class PreferenceGeneralPage extends FieldEditorPreferencePage {

    public PreferenceGeneralPage() {
        super(GRID);
        setTitle("Git");
    }

    @Override
    protected void createFieldEditors() {
        ColorFieldEditor commentColor = new ColorFieldEditor("PRESF", "C&omments:", getFieldEditorParent());
        ColorFieldEditor commentColor2 = new ColorFieldEditor("PRESF", "C&omments:", getFieldEditorParent());
        addField(commentColor2);
        addField(commentColor);
        setVisible(false);
    }

}
