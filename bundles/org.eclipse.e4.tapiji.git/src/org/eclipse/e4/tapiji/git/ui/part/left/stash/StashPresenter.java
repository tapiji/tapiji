package org.eclipse.e4.tapiji.git.ui.part.left.stash;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.part.left.stash.StashContract.View;


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
            public void onSuccess(GitResponse<List<CommitReference>> response) {
                List<CommitReference> data = response.body();
                if (data == null || data.isEmpty()) {
                    view.collapseView();
                }
                view.showStashes(response.body());
            }

            @Override
            public void onError(GitException exception) {
                view.showError(exception);
                view.collapseView();
            }
        });
    }
}
