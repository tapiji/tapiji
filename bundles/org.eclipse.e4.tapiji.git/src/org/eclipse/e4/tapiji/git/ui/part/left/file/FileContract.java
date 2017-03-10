package org.eclipse.e4.tapiji.git.ui.part.left.file;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface FileContract {

    interface View {

        void showFiles(List<PropertyDirectory> result, int cntFiles);

        void showError(GitServiceException exception);

        FilePresenter getPresenter();

    }

    interface Presenter extends BasePresenter<View> {

        void loadFiles();

        void loadLogs();

    }
}
