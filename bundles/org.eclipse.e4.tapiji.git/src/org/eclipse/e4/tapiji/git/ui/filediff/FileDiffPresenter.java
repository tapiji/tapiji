package org.eclipse.e4.tapiji.git.ui.filediff;


import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.e4.tapiji.git.model.diff.DiffLineStatus;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.filediff.FileDiffContract.View;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.di.UISynchronize;


@Creatable
@Singleton
public class FileDiffPresenter implements FileDiffContract.Presenter {

    private static final String TAG = FileDiffPresenter.class.getSimpleName();

    private String selectedFileName;
    private boolean conflict;

    @Inject
    UISynchronize sync;

    private DiffFile mergeFile;

    @Inject
    IGitService service;

    private View view;

    @PostConstruct
    public void create() {
        Log.d("ON", "CREATE");
    }

    @PreDestroy
    public void dispose() {
        selectedFileName = null;
    }

    @Override
    public void setView(FileDiffContract.View view) {
        this.view = view;
    }

    @Override
    public void reloadLastSelctedFile() {
        if (selectedFileName != null) {
            if (conflict) {
                loadFileMergeDiff(selectedFileName, GitFileStatus.CONFLICT);
            } else {
                loadFileContentDiff(selectedFileName);
            }
        }
    }

    @Override
    public void loadFileContentDiff(String file) {
        this.selectedFileName = file;
        this.conflict = false;
        service.fileContent(file, new IGitServiceCallback<DiffFile>() {

            @Override
            public void onSuccess(GitServiceResult<DiffFile> response) {
                view.showContentDiff(response.getResult());
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }

    public void loadFileMergeDiff(String file, GitFileStatus conflict) {
        this.selectedFileName = file;
        this.conflict = true;
        service.fileMergeDiff(file, conflict, new IGitServiceCallback<DiffFile>() {

            @Override
            public void onSuccess(GitServiceResult<DiffFile> response) {
                sync.asyncExec(() -> mergeFile = response.getResult());
                view.showMergeView(response.getResult());
            }

            @Override
            public void onError(GitServiceException exception) {
                view.showError(exception);
            }
        });
    }

    @Override
    public String getSelectedFileName() {
        return selectedFileName;
    }

    @Override
    public void stageResolvedFile(String selectedFile) {
        try {
            List<String> lines = mergeFile.getHunks()
                .get(0)
                .getLines()
                .stream()
                .filter(checkLineStatus.apply(DiffLineStatus.DEFAULT).or(checkLineStatus.apply(DiffLineStatus.CHECKED)))
                .map(line -> line.getText())
                .collect(Collectors.toList());

            File file = new File(mergeFile.getFile());
            Files.write(file.toPath(), lines, Charset.defaultCharset());
            service.stageFile(selectedFile, new IGitServiceCallback<Void>() {

                @Override
                public void onSuccess(GitServiceResult<Void> response) {
                    sync.syncExec(() -> {
                        selectedFileName = null;
                        mergeFile = null;
                    });
                    view.sendUIEvent(UIEventConstants.TOPIC_RELOAD_VIEW);
                }

                @Override
                public void onError(GitServiceException exception) {
                    view.showError(exception);
                }
            });
        } catch (Exception exception) {
            view.showError(exception);
        }
    }

    private Function<DiffLineStatus, Predicate<DiffLine>> checkLineStatus = status -> line -> line.getStatus() == status;

}
