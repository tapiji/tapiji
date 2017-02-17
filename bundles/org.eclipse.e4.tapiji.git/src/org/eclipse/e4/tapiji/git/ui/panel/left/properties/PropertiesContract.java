package org.eclipse.e4.tapiji.git.ui.panel.left.properties;


import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface PropertiesContract {

    interface View {

        void showError(GitServiceException exception);

    }

    interface Presenter extends BasePresenter<View> {

    }
}
