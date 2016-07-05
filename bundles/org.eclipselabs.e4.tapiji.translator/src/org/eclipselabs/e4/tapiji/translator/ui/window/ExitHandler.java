package org.eclipselabs.e4.tapiji.translator.ui.window;


import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;


public class ExitHandler {

    private static final String TAG = ExitHandler.class.getSimpleName();

    @Execute
    public void execute(final IWorkbench workbench, final EPartService partService, final IEclipseContext context) {
        final IEclipseContext activeWindowContext = context.getActiveChild();
        final Shell shell = new Shell(SWT.SHELL_TRIM);
        if ((null != activeWindowContext) && !partService.getDirtyParts().isEmpty()) {// TODO Does this work?
            final boolean result = showDialog(shell, "Unsaved", "Unsaved data, do you want to save?");
            if (result) {
                partService.saveAll(false);
                workbench.close();
                Log.i(TAG, "Data saved. Close workbench!");
                return;
            }
        }

        if (showDialog(shell, "Confirmation", "Do you want to exit?")) {
            Log.i(TAG, "Close workbench!");
            workbench.close();
        }
    }

    public boolean showDialog(final Shell shell, final String title, final String message) {
        return MessageDialog.openConfirm(shell, title, message);
    }
}
