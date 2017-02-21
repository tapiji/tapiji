package org.eclipse.e4.tapiji.git.ui.panel.left.stash;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.ui.panel.left.stash.StashContract.View;


@Creatable
@Singleton
public class StashPresenter implements StashContract.Presenter {

    @Inject
    IGitService service;
    private View view;

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void loadStashes() {
        service.stashes(new IGitServiceCallback<List<CommitReference>>() {

            @Override
            public void onSuccess(GitServiceResult<List<CommitReference>> response) {
                view.showStashes(response.getResult());

            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }
}
