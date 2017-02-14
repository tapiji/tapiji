package org.eclipse.e4.tapiji.git.ui.property;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface PropertyContract {

    interface View {

        void showError(GitServiceException exception);

        void showProperties(List<PropertyDirectory> directories, int cntFiles);

    }

    interface Presenter extends BasePresenter<View> {

    }
}
