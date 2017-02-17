package org.eclipse.e4.tapiji.git.ui.panel.left.tag;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.panel.left.tag.TagContract.View;


@Creatable
@Singleton
public class TagPresenter implements TagContract.Presenter {

    @Inject
    IGitService service;
    private View view;

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void loadTags() {
        service.tags(new IGitServiceCallback<List<String>>() {

            @Override
            public void onSuccess(GitServiceResult<List<String>> response) {
                view.showTags(response.getResult());
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }

}
