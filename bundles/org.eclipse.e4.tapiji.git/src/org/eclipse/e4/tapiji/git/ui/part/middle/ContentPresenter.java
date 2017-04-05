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
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.diff.DiffLine;
import org.eclipse.e4.tapiji.git.model.diff.DiffLineStatus;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
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
            public void onSuccess(GitResponse<DiffFile> response) {
                sync.asyncExec(() -> {
                    view.showContentDiff(response.body());
                });
            }

            @Override
            public void onError(GitException exception) {
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
            public void onSuccess(GitResponse<DiffFile> response) {
                sync.asyncExec(() -> {
                    view.showMergeView(response.body());
                });
            }

            @Override
            public void onError(GitException exception) {
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
    public void stageResolvedFile(DiffFile diffFile, String selectedFile) {
        try {
            List<String> lines = diffFile.getHunks()
                .get(0)
                .getLines()
                .stream()
                .filter(checkLineStatus.apply(DiffLineStatus.DEFAULT).or(checkLineStatus.apply(DiffLineStatus.CHECKED)))
                .map(line -> line.getText())
                .collect(Collectors.toList());

            File file = new File(diffFile.getFile());
            Files.write(file.toPath(), lines, Charset.defaultCharset());
            service.stageMergedFile(selectedFile, new IGitServiceCallback<Void>() {

                @Override
                public void onSuccess(GitResponse<Void> response) {
                    loadLogs();
                    sync.syncExec(() -> {
                        selectedFileName = null;
                    });
                }

                @Override
                public void onError(GitException exception) {
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
        service.logs(new IGitServiceCallback<List<GitLog>>() {

            @Override
            public void onSuccess(GitResponse<List<GitLog>> response) {
                sync.syncExec(() -> {
                    view.showLogs(response.body());
                });
            }

            @Override
            public void onError(GitException exception) {
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
