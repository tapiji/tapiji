package org.eclipse.e4.tapiji.git.ui.part.right.commit;


import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface CommitContract {

    interface View {

        void disableCommitButton();

        void enableCommitButton();

        void sendUIEvent(String topic, String content);

        void resetCommitView();

        void setCursorWaitVisibility(boolean visibility);

        void showError(GitException exception);

    }

    interface Presenter extends BasePresenter<View> {

        void checkTextSummary(String text);

        void commitChanges(String summary, String description);

    }
}