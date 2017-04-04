package org.eclipse.e4.tapiji.git.ui.part.left.remotebranch;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface RemoteBranchContract {

    interface View {

        void collapseView();

        void showBranches(List<Reference> data);

        void showError(GitException exception);

        RemoteBranchPresenter getPresenter();

    }

    interface Presenter extends BasePresenter<View> {

        void loadRemoteBranches();

    }
}
