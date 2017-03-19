package org.eclipse.e4.tapiji.git.ui.part.right.staged;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.part.right.staged.StagedContract.View;


@Creatable
@Singleton
public class StagedPresenter implements StagedContract.Presenter {

    private static final String TAG = StagedPresenter.class.getSimpleName();

    @Inject
    IGitService service;

    private View view;

    @Override
    public void setView(StagedContract.View view) {
        this.view = view;
    }

    private Function<GitFileStatus, Predicate<Map.Entry<GitFileStatus, Set<String>>>> contains = fileStatus -> entry -> entry.getKey() == fileStatus;

    @Override
    public void loadStagedFiles() {
        view.setCursorWaitVisibility(true);
        service.uncommittedChanges(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitResponse<Map<GitFileStatus, Set<String>>> response) {
                view.setCursorWaitVisibility(false);
                List<GitFile> files = null;
                if (response == null || response.body() == null || response.body().isEmpty()) {
                    files = Collections.emptyList();
                } else {
                    files = response.body()
                        .entrySet()
                        .stream()
                        .filter(contains.apply(GitFileStatus.ADDED).or(contains.apply(GitFileStatus.REMOVED)).or(contains.apply(GitFileStatus.CHANGED)))
                        .flatMap(entry -> entry.getValue().stream().map(f -> new GitFile(f, entry.getKey())))
                        .collect(Collectors.toList());
                    if (files.isEmpty()) {
                        view.sendUIEvent(UIEventConstants.TOPIC_STAGED_UNSTAGED, "unstaged");
                    } else {
                        view.sendUIEvent(UIEventConstants.TOPIC_STAGED_UNSTAGED, "staged");
                    }
                }
                view.showStagedChanges(files);
            }

            @Override
            public void onError(GitException exception) {
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
            public void onSuccess(GitResponse<Void> response) {
                view.setCursorWaitVisibility(false);
            }

            @Override
            public void onError(GitException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });
    }
}
