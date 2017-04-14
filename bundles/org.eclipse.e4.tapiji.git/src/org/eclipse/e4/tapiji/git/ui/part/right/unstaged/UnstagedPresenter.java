package org.eclipse.e4.tapiji.git.ui.part.right.unstaged;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.part.right.unstaged.UnstagedContract.View;


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
    public void setView(UnstagedContract.View view) {
        this.view = view;
    }

    private Function<GitFileStatus, Predicate<Map.Entry<GitFileStatus, Set<String>>>> contains = fileStatus -> entry -> entry.getKey() == fileStatus;

    @Override
    public void loadUnCommittedChanges() {
        view.setCursorWaitVisibility(true);
        service.fileStates(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitResponse<Map<GitFileStatus, Set<String>>> response) {
                List<GitFile> files = new ArrayList<GitFile>();
                if (response == null || response.body() == null || response.body().isEmpty()) {
                    files = Collections.emptyList();
                } else {
                    files = response.body()
                        .entrySet()
                        .stream()
                        .filter(contains.apply(GitFileStatus.MODIFIED)
                            .or(contains.apply(GitFileStatus.UNTRACKED))
                            .or(contains.apply(GitFileStatus.MISSING))
                            .or(contains.apply(GitFileStatus.CONFLICT)))
                        .flatMap(entry -> entry.getValue().stream().map(f -> new GitFile(f, entry.getKey())))
                        .collect(Collectors.toList());

                    long conflictedFiles = files.stream().filter(file -> file.getStatus() == GitFileStatus.CONFLICT).count();
                    if (conflictedFiles >= 1) {
                        view.showConflictHeader((int) conflictedFiles);
                    } else {
                        view.showUnstageHeader(files.size());
                    }
                }
                view.setCursorWaitVisibility(false);
                view.showUnCommittedChanges(files);
            }

            @Override
            public void onError(GitException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });
    }

    @Override
    public void onClickStageChanges() {
        view.setCursorWaitVisibility(true);
        service.stageAll(new IGitServiceCallback<Void>() {

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

    @Override
    public void onClickDiscardChanges() {
        view.setCursorWaitVisibility(true);
        service.discardChanges(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitResponse<Void> response) {
                view.setCursorWaitVisibility(false);
                view.sendUIEvent(UIEventConstants.SWITCH_CONTENT_VIEW, null);
            }

            @Override
            public void onError(GitException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });

    }
}