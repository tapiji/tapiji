package org.eclipse.e4.tapiji.git.ui.unstaged;


import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.ui.unstaged.UnstagedContract.View;
import org.eclipse.e4.ui.di.UISynchronize;


@Creatable
@Singleton
public class UnstagedPresenter implements UnstagedContract.Presenter {

    @Inject
    IGitService service;

    @Inject
    UISynchronize sync;

    private View view;

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setView(UnstagedContract.View view) {
        this.view = view;
    }

    @Override
    public void loadUnCommittedChanges() {
        new Job("uncommitted changes") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                service.uncommittedChanges("E:/cloni/.git", new IGitServiceCallback<Map<GitStatus, Set<String>>>() {

                    @Override
                    public void onSuccess(GitServiceResult<Map<GitStatus, Set<String>>> response) {
                        sync.syncExec(new Runnable() {

                            @Override
                            public void run() {
                                view.showUnCommittedChanges(response.getResult());
                            }
                        });

                    }

                    @Override
                    public void onError(GitServiceException exception) {
                        sync.syncExec(new Runnable() {

                            @Override
                            public void run() {
                                view.showError(exception);
                            }
                        });

                    }
                });
                return Status.OK_STATUS;
            }
        }.schedule();

    }

}
