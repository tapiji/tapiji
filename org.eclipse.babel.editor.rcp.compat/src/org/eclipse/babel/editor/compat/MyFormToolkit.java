package org.eclipse.babel.editor.compat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class MyFormToolkit extends FormToolkit {

    public MyFormToolkit(FormColors colors) {
        super(colors);
    }

    public MyFormToolkit(Display display) {
        super(display);
    }
}
