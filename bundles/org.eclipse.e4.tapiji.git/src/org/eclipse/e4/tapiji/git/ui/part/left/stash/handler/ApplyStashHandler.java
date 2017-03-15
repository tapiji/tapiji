
package org.eclipse.e4.tapiji.git.ui.part.left.stash.handler;


import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.tapiji.mylyn.model.Notification;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.errors.StashApplyFailureException;
import org.eclipse.swt.widgets.Shell;


public class ApplyStashHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    IGitService service;

    @Inject
    UISynchronize sync;

    @Execute
    public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) String commitHash, final IMylynService mylyn, Shell shell) {
        service.applyStash(commitHash, new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                sync.asyncExec(() -> mylyn.sendNotification(new Notification("Apply stash successful!", "TOOD")));
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

}
