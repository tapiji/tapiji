
package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import javax.inject.Inject;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.tapiji.mylyn.model.Notification;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.errors.StashApplyFailureException;
import org.eclipse.swt.widgets.Shell;


public class PopHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    UISynchronize sync;

    @Execute
    public void execute(final IEclipseContext context, final Shell shell, final IGitService service, final IMylynService mylyn) {
        service.popFirst(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                sync.asyncExec(() -> {
                    eventBroker.post(UIEventConstants.TOPIC_RELOAD_UNSTAGE_VIEW, "");
                    eventBroker.post(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW, "");
                    eventBroker.post(UIEventConstants.TOPIC_RELOAD_VIEW, "");
                    mylyn.sendNotification(new Notification("Pop and applied", "Index 0"));
                });
            }

            @Override
            public void onError(GitServiceException exception) {
                if (exception.getCause() instanceof StashApplyFailureException) {
                    sync.asyncExec(() -> MessageDialog.openError(shell, "Error Appliying Stash", "Can not apply stash while working dir is staged or unstaged."));
                } else {
                    sync.asyncExec(() -> MessageDialog.openError(shell, "Error: ", exception.getMessage()));
                }
            }
        });
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
