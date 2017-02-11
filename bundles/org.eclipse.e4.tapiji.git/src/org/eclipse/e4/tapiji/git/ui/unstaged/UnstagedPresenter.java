package org.eclipse.e4.tapiji.git.ui.unstaged;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitFile;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.ui.unstaged.UnstagedContract.View;
import org.eclipse.e4.tapiji.git.ui.util.UIEventConstants;
import org.eclipse.e4.tapiji.logger.Log;


@Creatable
@Singleton
public class UnstagedPresenter implements UnstagedContract.Presenter {

    private static final String TAG = UnstagedPresenter.class.getSimpleName();

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
    public void setView(UnstagedContract.View view) {
        this.view = view;
    }

    @Override
    public void loadUnCommittedChanges() {
        service.uncommittedChanges("E:/cloni/.git", new IGitServiceCallback<Map<GitStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitStatus, Set<String>>> response) {
                Log.d(TAG, "FILES( " + response.getResult().toString() + ")");
                List<GitFile> files = null;
                if (response == null || response.getResult() == null || response.getResult().isEmpty()) {
                    files = Collections.emptyList();
                } else {
                    files = response.getResult()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() == GitStatus.MODIFIED || entry.getKey() == GitStatus.UNTRACKED || entry.getKey() == GitStatus.MISSING)
                        .flatMap(entry -> entry.getValue().stream().map(f -> new GitFile(f, entry.getKey())))
                        .collect(Collectors.toList());
                }
                view.showUnCommittedChanges(files);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }

    @Override
    public void stageChanges() {
        service.stageAll("E:/cloni/.git", new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                loadUnCommittedChanges();
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_STAGED_FILE);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }

        });
    }

    public void discardChanges() {
        service.discardChanges("E:/cloni/.git", new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                loadUnCommittedChanges();
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });

    }
}
