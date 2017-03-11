package org.eclipse.e4.tapiji.git.ui.part.middle;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.commitlog.CommitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface FileDiffContract {

    interface View {

        void showContentDiff(DiffFile result);

        void showMergeView(DiffFile result);

        void showError(GitServiceException exception);

        void clearScrollView();

        void sendUIEvent(String topic);

        void showError(Exception exception);

        void showLogs(List<CommitLog> result);

    }

    interface Presenter extends BasePresenter<View> {

        void loadFileContentDiff(String file);

        void reloadLastSelctedFile();

        void stageResolvedFile(String selectedFile);

        String getSelectedFileName();

        void loadLogs();

    }
}
