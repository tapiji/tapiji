package org.eclipse.e4.tapiji.rap.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class UploadFileUtils {
	
    public static String[] uploadFiles(final Shell shell, final String title, final int dialogOptions, final String[] endings) {
    	final FileDialog dialog = createFileDialog(shell,title, dialogOptions,endings);
        final String filepath = dialog.open();
        if ((dialogOptions & SWT.SINGLE) == SWT.SINGLE) {
            return new String[] {filepath};
        } else {
            final String[] filenames = dialog.getFileNames();
            if (filenames.length > 0) {
                return filenames;
            }
            return null;
        }
    }
    
    private static FileDialog createFileDialog(Shell shell, String title, int dialogOptions, String[] endings) {
        final FileDialog dialog = new FileDialog(shell, dialogOptions);
        dialog.setText(title);
        // Available in RAP 3.2
        // dialog.setFilterExtensions(endings);
		return dialog;
    }

}
