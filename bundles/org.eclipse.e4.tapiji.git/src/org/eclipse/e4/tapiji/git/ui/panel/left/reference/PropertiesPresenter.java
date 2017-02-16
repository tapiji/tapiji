package org.eclipse.e4.tapiji.git.ui.panel.left.reference;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.ui.panel.left.reference.PropertiesContract.View;


@Creatable
@Singleton
public class PropertiesPresenter implements PropertiesContract.Presenter {

    protected static final String TAG = null;

    @Inject
    IGitService service;
    private View view;

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    public void loadProperties() {
        service.findPropertyFiles("*.properties", new IGitServiceCallback<List<PropertyDirectory>>() {

            @Override
            public void onSuccess(GitServiceResult<List<PropertyDirectory>> response) {
                List<PropertyDirectory> result = response.getResult();
                int cntFiles = result.stream().mapToInt(dir -> dir.getFiles().size()).sum();
                view.showProperties(response.getResult(), cntFiles);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }
}
