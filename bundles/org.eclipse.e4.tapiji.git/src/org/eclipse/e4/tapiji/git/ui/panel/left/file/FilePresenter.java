package org.eclipse.e4.tapiji.git.ui.panel.left.file;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.commitlog.CommitLog;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.ui.panel.left.file.FileContract.View;


@Creatable
@Singleton
public class FilePresenter implements FileContract.Presenter {

    @Inject
    IGitService service;

    private View view;

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void loadFiles() {
        service.findPropertyFiles("*.properties", new IGitServiceCallback<List<PropertyDirectory>>() {

            @Override
            public void onSuccess(GitServiceResult<List<PropertyDirectory>> response) {
                List<PropertyDirectory> result = response.getResult();
                int cntFiles = result.stream().mapToInt(dir -> dir.getFiles().size()).sum();
                view.showFiles(response.getResult(), cntFiles);
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }

    @Override
    public void loadLogs() {
        service.logs(new IGitServiceCallback<List<CommitLog>>() {

            @Override
            public void onSuccess(GitServiceResult<List<CommitLog>> response) {

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });
    }

}
