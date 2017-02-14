package org.eclipse.e4.tapiji.git.core.internal.file;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.internal.GitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;


@Creatable
@Singleton
public class FileService {

    private static final String TAG = FileService.class.getSimpleName();

    public void searchPropertyFile(String directory, String filePattern, IGitServiceCallback<List<PropertyDirectory>> callback, ExecutorService executorService) {
        CompletableFuture.supplyAsync(() -> {
            Path fileDir = Paths.get(directory);
            FileFinder finder = new FileFinder(filePattern);
            try {
                Files.walkFileTree(fileDir, finder);
            } catch (IOException exception) {
                GitService.throwAsUnchecked(exception);
            }
            return new GitServiceResult<List<PropertyDirectory>>(finder.directories());
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }
}
