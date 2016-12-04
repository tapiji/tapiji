package org.eclipse.e4.tapiji.glossary.ui.window;


import java.io.File;
import javax.inject.Named;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.tapiji.glossary.preference.StoreInstanceState;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.tapiji.utils.FileUtils;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;


public abstract class AOpenGlossaryHandler {

    private static final String TAG = AOpenGlossaryHandler.class.getSimpleName();


    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final IGlossaryService glossaryService, final StoreInstanceState instanceState) {
        final String[] fileNames = recentlyOpenedFiles(shell);
        if (fileNames != null) {
            final String fileName = fileNames[0];
            if (FileUtils.isGlossary(fileName)) {
                final File file = new File(fileName);
                System.out.println("LOAD ASYNC");
                loadGlossaryAsync(glossaryService, file);
                instanceState.setGlossaryFile(file.toString());
            } else {
                showErrorDialog(shell, fileName);
                Log.i(TAG, String.format("Cannot open Glossary %s", fileName));
                return;
            }
        }
    }

    private void loadGlossaryAsync(final IGlossaryService glossaryService, final File file) {
        final Job job = new Job("loading") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
            	System.out.println("OPEN GLOSSARYS");
            	Log.d(TAG, "OPEN GLOSSARY");
                glossaryService.openGlossary(file);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }


    private void showErrorDialog(final Shell shell, final String fileName) {
        MessageDialog.openError(shell, String.format("Cannot open Glossary %s", fileName), "The choosen file does not represent a Glossary!");
    }
    
    protected abstract String[] recentlyOpenedFiles(Shell shell);
}
