package org.eclipse.e4.tapiji.git.ui.panel.right.commit;


import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;


@Creatable
@Singleton
public class CommitPresenter implements CommitContract.Presenter {

    private static final String TAG = CommitPresenter.class.getSimpleName();

    @Inject
    IGitService service;

    private CommitContract.View view;

    @Override
    public void setView(CommitContract.View view) {
        this.view = view;
    }

    @Override
    public void commitChanges(String summary, String description) {
        view.setCursorWaitVisibility(true);
        service.commitChanges(summary, description, new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                view.setCursorWaitVisibility(false);
                view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_VIEW);
                view.resetCommitView();
            }

            @Override
            public void onError(GitServiceException exception) {
                view.setCursorWaitVisibility(false);
                view.showError(exception);
            }
        });
    }

    @Override
    public void checkTextSummary(String text) {
        if (text != null && !text.isEmpty()) {
            view.enableCommitButton();
        } else {
            view.disableCommitButton();
        }
    }
}
