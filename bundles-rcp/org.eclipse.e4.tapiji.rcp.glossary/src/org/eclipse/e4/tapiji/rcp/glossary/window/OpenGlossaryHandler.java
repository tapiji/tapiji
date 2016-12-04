package org.eclipse.e4.tapiji.rcp.glossary.window;

import org.eclipse.e4.tapiji.glossary.ui.window.AOpenGlossaryHandler;
import org.eclipse.e4.tapiji.rcp.utils.OpenFileUtils;
import org.eclipse.e4.tapiji.utils.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class OpenGlossaryHandler extends AOpenGlossaryHandler {
	@Override
	protected String[] recentlyOpenedFiles(Shell shell) {
		return OpenFileUtils.openFiles(shell, "Open Glossary", SWT.OPEN, FileUtils.XML_FILE_ENDINGS);
	}
}
