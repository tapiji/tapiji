package org.eclipse.e4.tapiji.git.ui.unstaged;


import java.util.Map;
import java.util.Set;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface UnstagedContract {

    interface View {

        void showUnCommittedChanges(Map<GitStatus, Set<String>> result);

        void showError(GitServiceException exception);

    }

    interface Presenter extends BasePresenter<View> {

        void loadUnCommittedChanges();
    }
}
