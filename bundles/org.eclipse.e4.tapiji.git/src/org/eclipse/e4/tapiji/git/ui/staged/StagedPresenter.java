package org.eclipse.e4.tapiji.git.ui.staged;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
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
        view.setCursorWaitVisibility(true);
        service.uncommittedChanges(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitFileStatus, Set<String>>> response) {
                view.setCursorWaitVisibility(false);
                Log.d(TAG, "STAGED FILES( " + response.getResult().toString() + ")");
                List<GitFile> files = null;
                if (response == null || response.getResult() == null || response.getResult().isEmpty()) {
                    files = Collections.emptyList();
                } else {
                    files = response.getResult()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() == GitFileStatus.ADDED || entry.getKey() == GitFileStatus.REMOVED ||entry.getKey() == GitFileStatus.CHANGED)
                        .flatMap(entry -> entry.getValue().stream().map(f -> new GitFile(f, entry.getKey())))
                        .collect(Collectors.toList());
                }
                view.showStagedChanges(files);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });
    }

    @Override
    public void unstageChanges() {
        view.setCursorWaitVisibility(true);
        service.unstageAll(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                loadStagedFiles();
                view.setCursorWaitVisibility(false);
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_UNSTAGED_FILE);
                view.sendUIEvent(UIEventConstants.TOPIC_ON_FILES_UNSTAGED);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });
    }
}
