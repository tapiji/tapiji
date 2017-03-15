package org.eclipse.e4.tapiji.git.ui.part.left.properties;


import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.part.left.properties.PropertiesContract.View;


@Creatable
@Singleton
public class PropertiesPresenter implements PropertiesContract.Presenter {

    @Inject
    IGitService service;

    private View view;

    @Override
    public void setView(View view) {
        this.view = view;
    }
}
