package org.eclipse.e4.tapiji.git.ui.part.left.file;


import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.ui.part.left.file.FileContract.View;


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
            public void onSuccess(GitResponse<List<PropertyDirectory>> response) {
                List<PropertyDirectory> result = response.body();
                int cntFiles = result.stream().mapToInt(dir -> dir.getFiles().size()).sum();
                view.showFiles(response.body(), cntFiles);
            }

            @Override
            public void onError(GitException exception) {
                view.showError(exception);
            }
        });
    }

    @Override
    public void loadLogs() {
        service.logs(new IGitServiceCallback<List<GitLog>>() {

            @Override
            public void onSuccess(GitResponse<List<GitLog>> response) {

            }

            @Override
            public void onError(GitException exception) {
                // TODO Auto-generated method stub

            }
        });
    }

}
