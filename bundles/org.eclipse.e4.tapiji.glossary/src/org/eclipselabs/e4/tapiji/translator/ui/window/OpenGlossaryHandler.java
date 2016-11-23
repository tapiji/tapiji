package org.eclipselabs.e4.tapiji.translator.ui.window;


import java.io.File;
import javax.inject.Named;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.utils.FileUtils;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;


public class OpenGlossaryHandler {

    private static final String TAG = OpenGlossaryHandler.class.getSimpleName();


    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final IGlossaryService glossaryService, final StoreInstanceState instanceState) {
        final String[] fileNames = FileUtils.queryFileName(shell, "Open Glossary", SWT.OPEN, FileUtils.XML_FILE_ENDINGS);
        if (fileNames != null) {
            final String fileName = fileNames[0];
            if (FileUtils.isGlossary(fileName)) {
                final File file = new File(fileName);
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
                glossaryService.openGlossary(file);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }


    private void showErrorDialog(final Shell shell, final String fileName) {
        MessageDialog.openError(shell, String.format("Cannot open Glossary %s", fileName), "The choosen file does not represent a Glossary!");
    }
}
