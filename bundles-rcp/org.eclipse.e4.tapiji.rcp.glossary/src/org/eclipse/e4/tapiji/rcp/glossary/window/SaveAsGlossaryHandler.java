package org.eclipse.e4.tapiji.rcp.glossary.window;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

public class SaveAsGlossaryHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final IGlossaryService glossaryService) {
		Log.d("TAG", "sdasdasdsadada");

	}
}
