package org.eclipse.e4.tapiji.git.ui.panel.right.unstaged;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface UnstagedContract {

    interface View {

        void showUnCommittedChanges(List<GitFile> files);

        void showError(GitServiceException exception);

        void sendUIEvent(String topic);

        void setCursorWaitVisibility(boolean visibility);

    }

    interface Presenter extends BasePresenter<View> {

        void loadUnCommittedChanges();

        void stageChanges();
    }
}
