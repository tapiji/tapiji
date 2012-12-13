package org.eclipse.babel.editor.compat;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SwtRapCompatibilityFormToolkit extends FormToolkit {

	public SwtRapCompatibilityFormToolkit(Display display) {
		super(display);
	}
	
	public void paintBordersFor(Composite comp) {
	}
}
