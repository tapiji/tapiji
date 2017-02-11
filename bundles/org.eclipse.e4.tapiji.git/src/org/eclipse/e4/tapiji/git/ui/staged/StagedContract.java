package org.eclipse.e4.tapiji.git.ui.staged;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.GitFile;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface StagedContract {

    interface View {

        void showError(GitServiceException exception);

        void showStagedChanges(List<GitFile> files);

        void setCursorWaitVisibility(boolean visibility);

        void sendUIEvent(String topic);

    }

    interface Presenter extends BasePresenter<View> {

        void loadStagedFiles();

        void unstageChanges();
    }
}
