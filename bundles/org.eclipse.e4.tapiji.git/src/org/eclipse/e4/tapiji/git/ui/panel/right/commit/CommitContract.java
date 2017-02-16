package org.eclipse.e4.tapiji.git.ui.panel.right.commit;


import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface CommitContract {

    interface View {

        void disableCommitButton();

        void enableCommitButton();

        void sendUIEvent(String topic);

        void resetCommitView();

        void setCursorWaitVisibility(boolean visibility);

        void showError(GitServiceException exception);

    }

    interface Presenter extends BasePresenter<View> {

        void checkTextSummary(String text);

        void commitChanges(String summary, String description);

    }
}
