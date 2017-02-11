package org.eclipse.e4.tapiji.git.ui.staged;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitFile;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.ui.staged.StagedContract.View;
import org.eclipse.e4.tapiji.logger.Log;


@Creatable
@Singleton
public class StagedPresenter implements StagedContract.Presenter {

    private static final String TAG = StagedPresenter.class.getSimpleName();

    @Inject
    IGitService service;

    private View view;

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setView(StagedContract.View view) {
        this.view = view;
    }

    @Override
    public void loadStagedFiles() {
        new Job("load staged changes") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                service.uncommittedChanges("E:/cloni/.git", new IGitServiceCallback<Map<GitStatus, Set<String>>>() {

                    @Override
                    public void onSuccess(GitServiceResult<Map<GitStatus, Set<String>>> response) {
                        Log.d(TAG, "STAGED FILES( " + response.getResult().toString() + ")");
                        List<GitFile> files = null;
                        if (response == null || response.getResult() == null || response.getResult().isEmpty()) {
                            files = Collections.emptyList();
                        } else {
                            files = response.getResult()
                                .entrySet()
                                .stream()
                                .filter(entry -> entry.getKey() == GitStatus.ADDED || entry.getKey() == GitStatus.CHANGED)
                                .flatMap(entry -> entry.getValue().stream().map(f -> new GitFile(f, entry.getKey())))
                                .collect(Collectors.toList());
                        }
                        view.showStagedChanges(files);
                    }

                    @Override
                    public void onError(GitServiceException exception) {
                        view.showError(exception);
                    }
                });
                return Status.OK_STATUS;
            }
        }.schedule();
    }

    @Override
    public void unstageChanges() {

    }
}
