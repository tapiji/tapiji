package org.eclipse.e4.tapiji.git.ui.panel.left.stash;


import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.panel.left.stash.StashContract.View;


@Creatable
@Singleton
public class StashPresenter implements StashContract.Presenter {

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

    public void loadStashes() {
        //service.stash(callback);
    }

}
