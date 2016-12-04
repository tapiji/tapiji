package org.eclipse.e4.tapiji.rap.glossary.window;

import org.eclipse.e4.tapiji.glossary.ui.window.AOpenGlossaryHandler;
import org.eclipse.e4.tapiji.rap.utils.UploadFileUtils;
import org.eclipse.e4.tapiji.utils.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class UploadGlossaryHandler extends AOpenGlossaryHandler {
	
	@Override
	protected String[] recentlyOpenedFiles(Shell shell) {
		return UploadFileUtils.uploadFiles(shell, "Open Glossary", SWT.OPEN, FileUtils.XML_FILE_ENDINGS);
	}
}
