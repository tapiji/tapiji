package org.eclipse.e4.tapiji.git.ui.part.left.remotebranch;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.ui.part.left.remotebranch.RemoteBranchContract.View;


@Creatable
@Singleton
public class RemoteBranchPresenter implements RemoteBranchContract.Presenter {

    @Inject
    IGitService service;

    private RemoteBranchContract.View view;

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void loadRemoteBranches() {
        service.remoteBranches(new IGitServiceCallback<List<Reference>>() {

            @Override
            public void onSuccess(GitResponse<List<Reference>> response) {
                List<Reference> data = response.body();
                if (data == null || data.isEmpty()) {
                    view.collapseView();
                }
                view.showBranches(data);
            }

            @Override
            public void onError(GitException exception) {
                view.collapseView();
                view.showError(exception);
            }
        });
    }
}
