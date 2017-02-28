package org.eclipse.e4.tapiji.git.ui.panel.right.unstaged;


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
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.panel.right.unstaged.UnstagedContract.View;


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
        service.uncommittedChanges(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitFileStatus, Set<String>>> response) {
                List<GitFile> files = new ArrayList<GitFile>();
                if (response == null || response.getResult() == null || response.getResult().isEmpty()) {
                    files = Collections.emptyList();
                } else {
                    files = response.getResult()
                        .entrySet()
                        .stream()
                        .filter(contains.apply(GitFileStatus.MODIFIED)
                            .or(contains.apply(GitFileStatus.UNTRACKED))
                            .or(contains.apply(GitFileStatus.MISSING))
                            .or(contains.apply(GitFileStatus.CONFLICT)))
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
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW);
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_VIEW);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }

        });
    }

    @Override
    public void discardChanges() {
        view.setCursorWaitVisibility(true);
        service.discardChanges(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                view.setCursorWaitVisibility(false);
                loadUnCommittedChanges();
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW);
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_VIEW);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });

    }
}
