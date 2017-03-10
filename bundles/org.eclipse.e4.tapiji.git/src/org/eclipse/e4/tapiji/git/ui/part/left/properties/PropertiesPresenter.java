package org.eclipse.e4.tapiji.git.ui.part.left.properties;


import java.nio.file.Path;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.part.left.properties.PropertiesContract.View;


@Creatable
@Singleton
public class PropertiesPresenter implements PropertiesContract.Presenter, FileWatchService.FileWatcher {

    @Inject
    IGitService service;

    @Inject
    FileWatchService watchService;

    protected static final String TAG = null;

    private View view;

    @Override
    public void setView(View view) {
        this.view = view;
    }

    public void watchService() {
        watchService.closeWatcher();
        watchService.startWatcher(service.getDirectory().toPath(), this);
    }

    @Override
    public void onFileChanged(Path path) {
        view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_VIEW);
    }

    @PreDestroy
    public void destroy() {
        service.dispose();
    }
}
