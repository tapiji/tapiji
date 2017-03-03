package org.eclipse.e4.tapiji.git.ui.filediff;


import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface FileDiffContract {

    interface View {

        void showContentDiff(DiffFile result);

        void showMergeView(DiffFile result);

        void showError(GitServiceException exception);

        void clearScrollView();

    }

    interface Presenter extends BasePresenter<View> {

        void loadFileContentDiff(String file);

        void reloadLastSelctedFile();

        void stageResolvedFile(String selectedFile);

        String getSelectedFileName();

    }
}
