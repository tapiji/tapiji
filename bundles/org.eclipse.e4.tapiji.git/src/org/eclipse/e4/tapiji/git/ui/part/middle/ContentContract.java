package org.eclipse.e4.tapiji.git.ui.part.middle;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface ContentContract {

    interface View {

        void showContentDiff(DiffFile result);

        void showMergeView(DiffFile result);

        void showError(GitException exception);

        void clearScrollView();

        void sendUIEvent(String topic);

        void showError(Exception exception);

        void showLogs(List<GitLog> result);

    }

    interface Presenter extends BasePresenter<View> {

        void loadFileContentDiff(String file);

        void reloadLastSelctedFile();

        void stageResolvedFile(DiffFile file, String selectedFile);

        String getSelectedFileName();

        void loadLogs();

        void watchService();

    }
}
