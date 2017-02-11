package org.eclipse.e4.tapiji.git.ui.unstaged;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.GitFile;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface UnstagedContract {

    interface View {

        void showUnCommittedChanges(List<GitFile> files);

        void showError(GitServiceException exception);

        void sendUIEvent(String topic);

    }

    interface Presenter extends BasePresenter<View> {

        void loadUnCommittedChanges();

        void stageChanges();
    }
}
