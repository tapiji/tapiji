package org.eclipse.e4.tapiji.git.ui.panel.left.tag;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface TagContract {

    interface View {

        void showTags(List<Reference> result);

        void showError(GitServiceException exception);

        TagPresenter getPresenter();

    }

    interface Presenter extends BasePresenter<View> {

        void loadTags();

    }
}
