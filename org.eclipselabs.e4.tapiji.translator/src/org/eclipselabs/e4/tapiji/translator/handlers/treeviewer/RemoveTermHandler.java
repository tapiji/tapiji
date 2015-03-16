package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;


public final class RemoveTermHandler {

    private static final String TAG = RemoveTermHandler.class.getSimpleName();

    @Inject
    IEventBroker eventBroker;

    @Execute
    public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) final Term term,
                    @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final IGlossaryService glossaryService) {
        if (term != null) {
            removeTermAsync(glossaryService, term);
            Log.d(TAG, String.format("Selected Term: %s ", term.toString()));
        } else {
            MessageDialog.openInformation(shell, "Deletion not possible", "No term selected");
        }
    }

    private void removeTermAsync(final IGlossaryService glossaryService, final Term term) {
        final Job job = new Job("removing") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                glossaryService.removeTerm(term);
                eventBroker.send(GlossaryServiceConstants.TOPIC_GLOSSARY_RELOAD, "ignored");
                Log.d(TAG, String.format("Removed Term: %s ", term.toString()));
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}