package org.eclipse.e4.tapiji.git.ui.commit;


import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;


@Creatable
@Singleton
public class CommitPresenter implements CommitContract.Presenter {

    private CommitContract.View view;

    @Override
    public void init() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setView(CommitContract.View view) {
        this.view = view;
    }

}
