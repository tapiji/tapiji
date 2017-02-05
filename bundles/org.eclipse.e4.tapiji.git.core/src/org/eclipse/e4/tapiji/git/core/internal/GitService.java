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
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.core.internal.file.FileFinder;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;


public class GitService implements IGitService {

    private static final String TAG = GitService.class.getSimpleName();

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
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                try (Git git = new Git(repository)) {
                    git.commit().setMessage(summary + "\n" + description).call();
                }
            }
        } catch (IOException | GitAPIException exception) {
            callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
        }
    }

    @Override
    public void stageAll(String directory, IGitServiceCallback<Void> callback) {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                try (Git git = new Git(repository)) {
                    git.add().addFilepattern(".").call();
                }
            }
        } catch (IOException | GitAPIException exception) {
            callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
        }
    }

    @Override
    public void unstageAll(String directory, IGitServiceCallback<Void> callback) {
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setGitDir(new File(directory)).readEnvironment().findGitDir().build()) {
                try (Git git = new Git(repository)) {
                    git.reset().call();
                }
            }
        } catch (IOException | GitAPIException exception) {
            callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
        }
    }

    @Override
    public void uncommittedChanges(String directory, IGitServiceCallback<Map<GitStatus, Set<String>>> callback) {
        Map<GitStatus, Set<String>> states = new HashMap<>();
        try {
            FileRepositoryBuilder builder = new FileRepositoryBuilder();
            try (Repository repository = builder.setMustExist(true).setGitDir(new File("E:/cloni/.git")).readEnvironment().findGitDir().build()) {
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
                    callback.onSuccess(new GitServiceResult<Map<GitStatus, Set<String>>>(states));
                }
            }
        } catch (IOException | GitAPIException exception) {
            callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
        }
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
}
