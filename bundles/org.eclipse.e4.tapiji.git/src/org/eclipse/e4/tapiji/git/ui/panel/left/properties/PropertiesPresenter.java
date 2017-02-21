package org.eclipse.e4.tapiji.git.ui.panel.left.properties;


import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.ui.panel.left.properties.PropertiesContract.View;


@Creatable
@Singleton
public class PropertiesPresenter implements PropertiesContract.Presenter {

    protected static final String TAG = null;

    private View view;

    @Override
    public void setView(View view) {
        this.view = view;
    }

}
