package org.eclipse.e4.tapiji.git.ui.panel.left.stash;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface StashContract {

    interface View {

        void showStashes(List<CommitReference> result);

        void showError(GitServiceException exception);
    }

    interface Presenter extends BasePresenter<View> {

        void loadStashes();
    }
}
