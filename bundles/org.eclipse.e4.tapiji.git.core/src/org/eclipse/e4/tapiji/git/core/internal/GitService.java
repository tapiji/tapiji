package org.eclipse.e4.tapiji.git.core.internal;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.core.internal.file.FileFinder;
import org.eclipse.e4.tapiji.git.model.GitFileStatus;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class GitService implements IGitService {

    private static final String TAG = GitService.class.getSimpleName();
    private ExecutorService executorService;
    private Repository repository;
    private Git git;
    private String directory;

    public GitService() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void mount(String directory) throws IOException {

        if (!directory.endsWith(".git") && !directory.endsWith(".git/")) {
            directory = directory + "/.git";
        }
        if (!new File(directory).exists()) {
            throw new IllegalStateException("Git repository not available at " + directory);
        }
        this.directory = directory;
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        this.repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build();
        this.git = new Git(repository);
    }

    @Override
    public void unmount() {
        if (repository != null) {
            repository.close();
        }
        if (git != null) {
            git.close();
        }
    }

    @Override
    public void pushChanges(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.push().setRemote("origin").call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void pushChangesWithCredentials(String password, String username, String directory, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.push().setRemote("origin").setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void cloneRepository(String url, String directory, IGitServiceCallback<File> callback) {
        Log.d(TAG, "cloneRepository(" + url + " to " + directory + ")");

        File localPath = new File(directory, "");
        if (!localPath.exists()) {
            localPath.mkdir();
        }

        if (!localPath.delete()) {
            callback.onError(new GitServiceException("Could not delete temporary file " + localPath));
        }

        try (Git result = Git.cloneRepository().setURI(url).setBare(false).setDirectory(localPath).call()) {
            try {
                mount(directory);
                callback.onSuccess(new GitServiceResult<File>(result.getRepository().getDirectory()));
            } catch (IOException e) {
                callback.onError(new GitServiceException(e.getMessage(), e.getCause()));
            }
        } catch (GitAPIException e) {
            callback.onError(new GitServiceException(e.getMessage(), e.getCause()));
        }
    }

    @Override
    public void commitChanges(String summary, String description, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.commit().setMessage(summary + "\n\n" + description).call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void stageAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.add().addFilepattern(".").call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void unstageAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.reset().call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void discardChanges(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {

                ResetCommand reset = git.reset();
                reset.setMode(ResetType.HARD);
                reset.setRef(Constants.HEAD);
                reset.call();

                git.clean().setCleanDirectories(true).call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void uncommittedChanges(IGitServiceCallback<Map<GitFileStatus, Set<String>>> callback) {
        CompletableFuture.supplyAsync(() -> {
            Map<GitFileStatus, Set<String>> states = new HashMap<>();
            try {
                Status status = git.status().call();
                states.put(GitFileStatus.ADDED, status.getAdded());
                states.put(GitFileStatus.CHANGED, status.getChanged());
                states.put(GitFileStatus.MISSING, status.getMissing());
                states.put(GitFileStatus.MODIFIED, status.getModified());
                states.put(GitFileStatus.REMOVED, status.getRemoved());
                states.put(GitFileStatus.UNCOMMITTED, status.getUncommittedChanges());
                states.put(GitFileStatus.UNTRACKED, status.getUntracked());
                states.put(GitFileStatus.UNTRACKED_FOLDERS, status.getUntrackedFolders());
            } catch (NoWorkTreeException | GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Map<GitFileStatus, Set<String>>>(states);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void tags(IGitServiceCallback<List<String>> callback) {
        try {

            List<Ref> refs = git.tagList().call();
            List<String> tags = new ArrayList<>(refs.size());
            for (Ref ref : refs) {
                tags.add(ref.getName().substring(ref.getName().lastIndexOf('/') + 1, ref.getName().length()));
            }
            callback.onSuccess(new GitServiceResult<List<String>>(tags));
        } catch (GitAPIException exception) {
            callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
        }
    }

    @Override
    public void findPropertyFiles(String filePattern, IGitServiceCallback<List<Path>> callback) {
        Path fileDir = Paths.get(directory);
        FileFinder finder = new FileFinder(filePattern);
        try {
            Files.walkFileTree(fileDir, finder);
        } catch (IOException ioException) {
            callback.onError(new GitServiceException(ioException.getMessage(), ioException.getCause()));
        }

        List<Path> paths = finder.paths();
        if (paths.size() > 0) {
            callback.onSuccess(new GitServiceResult<List<Path>>(paths));
        } else {
            callback.onSuccess(new GitServiceResult<List<Path>>(Collections.emptyList()));
        }
    }

    @Override
    public void showFileDiff(IGitServiceCallback<Void> callback) {

    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }
}
