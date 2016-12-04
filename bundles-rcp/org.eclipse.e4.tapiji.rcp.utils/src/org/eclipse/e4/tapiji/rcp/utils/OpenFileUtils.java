package org.eclipse.e4.tapiji.rcp.utils;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class OpenFileUtils {

	public static String[] openFiles(final Shell shell, final String title, final int dialogOptions,
			final String[] endings) {
		final FileDialog dialog = createFileDialog(shell, title, dialogOptions, endings);
		final String filepath = dialog.open();

		// if single option, return path
		if ((dialogOptions & SWT.SINGLE) == SWT.SINGLE) {
			return new String[] { filepath };
		} else {
			final String path = dialog.getFilterPath();
			final String[] filenames = dialog.getFileNames();

			if (!path.isEmpty()) {
				for (int i = 0; i < filenames.length; i++) {
					filenames[i] = path + File.separator + filenames[i];
				}

			}
			if (filenames.length > 0) {
				return filenames;
			}
			return null;
		}
	}

	private static FileDialog createFileDialog(Shell shell, String title, int dialogOptions, String[] endings) {
		final FileDialog dialog = new FileDialog(shell, dialogOptions);
		dialog.setText(title);
		dialog.setFilterExtensions(endings);
		return dialog;
	}
}