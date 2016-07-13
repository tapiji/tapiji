package org.eclipselabs.e4.tapiji.translator.ui.window;


import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;


public final class CheckForUpdatesHandler {

    private static final String TAG = CheckForUpdatesHandler.class.getSimpleName();
    private static final String UPDATE_SITE_URL = System.getProperty("UpdateHandler.Repo", "http://localhost/tapiji/repository");

    @Execute
    public void execute(final IProvisioningAgent agent, final Shell shell, final UISynchronize sync, final IWorkbench workbench) {
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    checkForUpdates(agent, shell, sync, workbench, monitor);
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            Log.e(TAG, e);
        }
    }

    private void checkForUpdates(final IProvisioningAgent agent, final Shell shell, final UISynchronize sync, final IWorkbench workbench, IProgressMonitor monitor) {

        final ProvisioningSession session = new ProvisioningSession(agent);
        final UpdateOperation operation = new UpdateOperation(session);

        UpdateOperation updateOperation = configureUpdate(operation);

        if (updateOperation != null) {
            SubMonitor sub = SubMonitor.convert(monitor, "Checking for application updates...", 200);
            IStatus status = operation.resolveModal(sub.newChild(100));

            if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
                Log.i(TAG, "UpdateOperation: nothing to update]");
                showMessage(shell, sync);
            } else {
                if (status.getSeverity() == IStatus.ERROR) {
                    Log.w(TAG, "UpdateOperation: " + status);
                } else {
                    performUpdate(shell, sync, workbench, operation, monitor, sub);
                }
            }
        } else {
            Log.i(TAG, "UpdateOperation: Uri is null");
        }
    }

    private void performUpdate(final Shell shell, final UISynchronize sync, final IWorkbench workbench, UpdateOperation operation, IProgressMonitor monitor, SubMonitor sub) {
        sync.syncExec(new Runnable() {

            @Override
            public void run() {
                boolean performUpdate = MessageDialog.openQuestion(null, "Updates available", "There are updates available. Do you want to install them now?");

                if (performUpdate) {
                    final ProvisioningJob provisioningJob = operation.getProvisioningJob(monitor);
                    provisioningJob.runModal(sub.newChild(100));
                    provisioningJob.addJobChangeListener(new JobChangeAdapter() {

                        @Override
                        public void done(IJobChangeEvent event) {
                            if (event.getResult().isOK()) {
                                sync.syncExec(new Runnable() {

                                    @Override
                                    public void run() {
                                        boolean restart = MessageDialog.openQuestion(shell, "Updates installed, restart?", "Updates have been installe. Do you want to restart?");
                                        if (restart) {
                                            workbench.restart();
                                        }
                                    }
                                });
                            } else {
                                Log.i(TAG, "[Event Result: " + event.getResult() + " ]");
                            }
                            super.done(event);
                        }
                    });
                    provisioningJob.schedule();
                }
            }
        });
    }

    private void showMessage(final Shell parent, final UISynchronize sync) {
        sync.syncExec(new Runnable() {

            @Override
            public void run() {
                MessageDialog.openWarning(parent, "No update", "No updates for the current installation have been found.");
            }
        });
    }

    private UpdateOperation configureUpdate(final UpdateOperation operation) {
        try {
            final URI uri = new URI(UPDATE_SITE_URL);
            operation.getProvisioningContext().setArtifactRepositories(new URI[] {uri});
            operation.getProvisioningContext().setMetadataRepositories(new URI[] {uri});
        } catch (final URISyntaxException e) {
            Log.e(TAG, e);
            return null;
        }
        return operation;
    }
}
