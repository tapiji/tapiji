package org.eclipse.e4.tapiji.git.ui.part.middle;


import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.commitlog.CommitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.e4.tapiji.git.model.diff.DiffLineStatus;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.part.left.properties.FileWatchService;
import org.eclipse.e4.tapiji.git.ui.part.middle.ContentContract.View;
import org.eclipse.e4.ui.di.UISynchronize;


@Creatable
@Singleton
public class ContentPresenter implements ContentContract.Presenter, FileWatchService.FileWatcher {

    private final IGitService service;
    private final UISynchronize sync;
    private final FileWatchService watchService;

    private String selectedFileName;
    private boolean conflict;
    private DiffFile mergeFile;
    private View view;

    @Inject
    public ContentPresenter(IGitService service, FileWatchService watchService, UISynchronize sync) {
        this.service = service;
        this.watchService = watchService;
        this.sync = sync;
    }

    @Override
    public void setView(ContentContract.View view) {
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
                sync.asyncExec(() -> {
                    view.showContentDiff(response.getResult());
                });
            }

            @Override
            public void onError(GitServiceException exception) {
                sync.asyncExec(() -> {
                    view.showError(exception);
                });
            }
        });
    }

    public void loadFileMergeDiff(String file, GitFileStatus conflict) {
        this.selectedFileName = file;
        this.conflict = true;
        service.fileMergeDiff(file, conflict, new IGitServiceCallback<DiffFile>() {

            @Override
            public void onSuccess(GitServiceResult<DiffFile> response) {
                sync.asyncExec(() -> {
                    view.showMergeView(response.getResult());
                });
            }

            @Override
            public void onError(GitServiceException exception) {
                sync.syncExec(() -> {
                    view.showError(exception);
                });
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
                }

                @Override
                public void onError(GitServiceException exception) {
                    sync.syncExec(() -> {
                        view.showError(exception);
                    });
                }
            });
        } catch (Exception exception) {
            view.showError(exception);
        }
    }

    private Function<DiffLineStatus, Predicate<DiffLine>> checkLineStatus = status -> line -> line.getStatus() == status;

    @Override
    public void loadLogs() {
        service.logs(new IGitServiceCallback<List<CommitLog>>() {

            @Override
            public void onSuccess(GitServiceResult<List<CommitLog>> response) {
                sync.syncExec(() -> {
                    view.showLogs(response.getResult());
                });
            }

            @Override
            public void onError(GitServiceException exception) {
                sync.syncExec(() -> {
                    view.showError(exception);
                });
            }
        });
    }

    @Override
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
