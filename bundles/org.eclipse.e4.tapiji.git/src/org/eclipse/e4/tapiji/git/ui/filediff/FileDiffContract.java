package org.eclipse.e4.tapiji.git.ui.filediff;


import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface FileDiffContract {

    interface View {

        void showFileDiff(DiffFile result);

        void showError(GitServiceException exception);

    }

    interface Presenter extends BasePresenter<View> {

        void loadFileDiffFrom(String file);

    }
}
