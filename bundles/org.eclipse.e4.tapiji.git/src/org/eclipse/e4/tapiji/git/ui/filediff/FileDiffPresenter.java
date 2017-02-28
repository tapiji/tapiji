package org.eclipse.e4.tapiji.git.ui.filediff;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.filediff.FileDiffContract.View;
import org.eclipse.e4.tapiji.logger.Log;


@Creatable
@Singleton
public class FileDiffPresenter implements FileDiffContract.Presenter {

    @Inject
    IGitService service;

    private View view;

    @PostConstruct
    public void create() {
        Log.d("ON", "CREATE");
    }

    @PreDestroy
    public void dispose() {
        Log.d("ON", "DISPOSE");
    }

    @Override
    public void setView(FileDiffContract.View view) {
        this.view = view;
    }

    @Override
    public void loadFileDiffFrom(String file) {
        service.diffFromFile(file, new IGitServiceCallback<DiffFile>() {

            @Override
            public void onSuccess(GitServiceResult<DiffFile> response) {
                view.showFileDiff(response.getResult());
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }

    public void loadConflictDif(String name, GitFileStatus conflict) {
        service.diffFromConflictFile(name, conflict, new IGitServiceCallback<DiffFile>() {

            @Override
            public void onSuccess(GitServiceResult<DiffFile> response) {
                view.showFileDiff(response.getResult());
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });

    }

}
