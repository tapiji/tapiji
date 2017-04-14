package org.eclipse.e4.tapiji.git.ui.part.right.unstaged;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.model.file.GitFile;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface UnstagedContract {

    interface View {

        void showUnCommittedChanges(List<GitFile> files);

        void showError(GitException exception);

        void sendUIEvent(String topic, String payload);

        void setCursorWaitVisibility(boolean visibility);

        void showConflictHeader(int fileCnt);

        void showUnstageHeader(int fileCnt);

    }

    interface Presenter extends BasePresenter<View> {

        void loadUnCommittedChanges();

        void onClickStageChanges();

        void onClickDiscardChanges();
    }
}