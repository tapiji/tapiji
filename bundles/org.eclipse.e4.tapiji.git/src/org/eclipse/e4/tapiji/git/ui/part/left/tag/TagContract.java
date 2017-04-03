package org.eclipse.e4.tapiji.git.ui.part.left.tag;


import java.util.List;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.BasePresenter;


public interface TagContract {

    interface View {

        void showTags(List<Reference> result);

        void showError(GitException exception);

        TagPresenter getPresenter();

        void collapseView();

    }

    interface Presenter extends BasePresenter<View> {

        void loadTags();

    }
}
