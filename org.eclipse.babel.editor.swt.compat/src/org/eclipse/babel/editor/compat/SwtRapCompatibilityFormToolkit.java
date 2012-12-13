package org.eclipse.babel.editor.compat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SwtRapCompatibilityFormToolkit extends FormToolkit {

    public SwtRapCompatibilityFormToolkit(FormColors colors) {
        super(colors);
    }

    public SwtRapCompatibilityFormToolkit(Display display) {
        super(display);
    }
}
