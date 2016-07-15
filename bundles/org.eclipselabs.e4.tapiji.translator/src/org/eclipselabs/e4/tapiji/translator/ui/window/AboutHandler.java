package org.eclipselabs.e4.tapiji.translator.ui.window;


import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.translator.ui.dialog.AboutDialog;

public class AboutHandler {

	@Execute
	public void execute(final IEclipseContext context, Shell shell) {
		AboutDialog.show(context, shell);
	}
}
