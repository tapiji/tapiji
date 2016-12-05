package org.eclipse.e4.tapiji.rap.glossary.window;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.rap.glossary.ui.dialog.DownloadGlossaryDialog;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

public class DownloadGlossaryHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell) {
		System.out.println("dsdasdasdasdas dasd asa dsasd ");
		DownloadGlossaryDialog dialog = new DownloadGlossaryDialog(shell);
		dialog.open();	
	}

}
