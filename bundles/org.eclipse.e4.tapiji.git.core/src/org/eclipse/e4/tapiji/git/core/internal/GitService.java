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
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;


public class GitService implements IGitService {

    private static final String REMOTE_URL = "ssh://<user>:<pwd>@<host>:22/<path-to-remote-repo>/";
    private static final String TAG = GitService.class.getSimpleName();
    private ExecutorService executorService;

    public GitService() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void pushChanges(String directory, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {

                    try (Git git = new Git(repository)) {

                        git.push().setRemote("origin").call();
                    }
                }
            } catch (IOException | GitAPIException exception) {
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
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                    try (Git git = new Git(repository)) {
                        git.push().setRemote("origin").setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
                    }
                }
            } catch (IOException | GitAPIException exception) {
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
    public void cloneRepository(String gitRepository, String directory, IGitServiceCallback<File> callback) {
        Log.d(TAG, "cloneRepository(" + gitRepository + " to " + directory + ")");

        File localPath = new File(directory, "");
        if (!localPath.exists()) {
            localPath.mkdir();
        }

        if (!localPath.delete()) {
            callback.onError(new GitServiceException("Could not delete temporary file " + localPath));
        }
        try (Git result = Git.cloneRepository().setURI(gitRepository).setBare(false).setDirectory(localPath).call()) {
            callback.onSuccess(new GitServiceResult<File>(result.getRepository().getDirectory()));
        } catch (GitAPIException e) {
            callback.onError(new GitServiceException(e.getMessage(), e.getCause()));
        }

    }

    @Override
    public void commitChanges(String directory, String summary, String description, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                    try (Git git = new Git(repository)) {
                        git.commit().setMessage(summary + "\n\n" + description).call();
                    }
                }
            } catch (IOException | GitAPIException exception) {
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
    public void stageAll(String directory, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                    try (Git git = new Git(repository)) {
                        git.add().addFilepattern(".").call();
                    }
                }
            } catch (IOException | GitAPIException exception) {
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
    public void unstageAll(String directory, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                    try (Git git = new Git(repository)) {
                        git.reset().call();
                    }
                }
            } catch (IOException | GitAPIException exception) {
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
    public void discardChanges(String directory, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                FileRepositoryBuilder builder = new FileRepositoryBuilder();
                try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                    try (Git git = new Git(repository)) {

                        ResetCommand reset = git.reset();
                        reset.setMode(ResetType.HARD);
                        reset.setRef(Constants.HEAD);
                        reset.call();

                        git.clean().setCleanDirectories(true).call();
                    }
                }
            } catch (IOException | GitAPIException exception) {
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
    public void uncommittedChanges(String directory, IGitServiceCallback<Map<GitStatus, Set<String>>> callback) {
        CompletableFuture.supplyAsync(() -> {
            Map<GitStatus, Set<String>> states = new HashMap<>();
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setMustExist(true).setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                try (Git git = new Git(repository)) {
                    Status status = git.status().call();
                    states.put(GitStatus.ADDED, status.getAdded());
                    states.put(GitStatus.CHANGED, status.getChanged());
                    states.put(GitStatus.MISSING, status.getMissing());
                    states.put(GitStatus.MODIFIED, status.getModified());
                    states.put(GitStatus.REMOVED, status.getRemoved());
                    states.put(GitStatus.UNCOMMITTED, status.getUncommittedChanges());
                    states.put(GitStatus.UNTRACKED, status.getUntracked());
                    states.put(GitStatus.UNTRACKED_FOLDERS, status.getUntrackedFolders());
                } catch (NoWorkTreeException | GitAPIException exception) {
                    throwAsUnchecked(exception);
                }
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Map<GitStatus, Set<String>>>(states);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void tags(String directory, IGitServiceCallback<List<String>> callback) {
        try {

            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                try (Git git = new Git(repository)) {
                    List<Ref> refs = git.tagList().call();
                    List<String> tags = new ArrayList<>(refs.size());
                    for (Ref ref : refs) {
                        tags.add(ref.getName().substring(ref.getName().lastIndexOf('/') + 1, ref.getName().length()));
                    }
                    callback.onSuccess(new GitServiceResult<List<String>>(tags));
                }
            }
        } catch (IOException | GitAPIException exception) {
            callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
        }
    }

    @Override
    public void findPropertyFiles(String directory, String filePattern, IGitServiceCallback<List<Path>> callback) {
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
    public void showFileDiff(String directory, IGitServiceCallback<Void> callback) {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                try (Git git = new Git(repository)) {

                    DiffFormatter formatter = new DiffFormatter(System.out);
                    formatter.setDiffComparator(RawTextComparator.DEFAULT);
                    formatter.setRepository(git.getRepository());
                    AbstractTreeIterator commitTreeIterator = prepareTreeParser(git.getRepository(), Constants.HEAD);
                    FileTreeIterator workTreeIterator = new FileTreeIterator(git.getRepository());
                    List<DiffEntry> diffEntries = formatter.scan(commitTreeIterator, workTreeIterator);

                    for (DiffEntry entry : diffEntries) {
                        System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
                        FileHeader fileHeader = formatter.toFileHeader(entry);
                        System.out.println(fileHeader.toEditList().toString());
                        formatter.format(entry);

                        // formatter.format(entry);
                    }
                }
            }
        } catch (IOException exception) {
            callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) {
        try {
            Ref head = repository.exactRef(ref);
            RevWalk walk = new RevWalk(repository);
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            ObjectReader oldReader = repository.newObjectReader();
            try {
                oldTreeParser.reset(oldReader, tree.getId());
            } finally {
                oldReader.close();
            }

            return oldTreeParser;
        } catch (Exception e) {

        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }
}
