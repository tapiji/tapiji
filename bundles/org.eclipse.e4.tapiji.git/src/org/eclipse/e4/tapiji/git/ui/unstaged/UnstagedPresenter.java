package org.eclipse.e4.tapiji.git.ui.unstaged;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.unstaged.UnstagedContract.View;
import org.eclipse.e4.tapiji.logger.Log;


@Creatable
@Singleton
public class UnstagedPresenter implements UnstagedContract.Presenter {

    private static final String TAG = UnstagedPresenter.class.getSimpleName();

    @Inject
    IEclipseContext context;

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
        view.setCursorWaitVisibility(true);
        service.uncommittedChanges(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitFileStatus, Set<String>>> response) {
                Log.d(TAG, "FILES( " + response.getResult().toString() + ")");
                List<GitFile> files = null;
                if (response == null || response.getResult() == null || response.getResult().isEmpty()) {
                    files = Collections.emptyList();
                } else {
                    files = response.getResult()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() == GitFileStatus.MODIFIED || entry.getKey() == GitFileStatus.UNTRACKED || entry.getKey() == GitFileStatus.MISSING)
                        .flatMap(entry -> entry.getValue().stream().map(f -> new GitFile(f, entry.getKey())))
                        .collect(Collectors.toList());
                }
                view.setCursorWaitVisibility(false);
                view.showUnCommittedChanges(files);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });
    }

    @Override
    public void stageChanges() {
        view.setCursorWaitVisibility(true);
        service.stageAll(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                view.setCursorWaitVisibility(false);
                loadUnCommittedChanges();
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_STAGED_FILE);
                view.sendUIEvent(UIEventConstants.TOPIC_FILES_STAGED);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }

        });
    }

    public void discardChanges() {
        view.setCursorWaitVisibility(true);
        service.discardChanges(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                view.setCursorWaitVisibility(false);
                loadUnCommittedChanges();
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });

    }
}
