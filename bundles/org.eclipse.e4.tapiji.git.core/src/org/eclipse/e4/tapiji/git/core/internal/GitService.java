package org.eclipse.e4.tapiji.git.core.internal;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.core.internal.file.FileService;
import org.eclipse.e4.tapiji.git.core.internal.util.GitUtil;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.UserProfile;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.transport.FetchResult;


public class GitService implements IGitService {

    private static final String TAG = GitService.class.getSimpleName();

    @Inject
    FileService fileService;

    @Inject
    GitPush push;

    @Inject
    GitRepository git;

    private ExecutorService executorService;

    public GitService() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void setPublicKeyPath(String keyPath) {
        Log.d(TAG, "setPublicKeyPath called with [" + keyPath + "]");
    }

    @Override
    public void setPrivateKeyPath(String keyPath) {
        Log.d(TAG, "privateKeyPath called with [" + keyPath + "]");
    }

    @Override
    public File getDirectory() {
        return new File(git.getDirectory().getPath().replace(".git", ""));
    }

    @Override
    public String getUrl() {
        return git.getUrl();
    }

    @Override
    public void commitChanges(String summary, String description, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.commit(summary, description);
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stageAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.stageAll();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void discardChanges(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.discardChanges();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void logs(IGitServiceCallback<List<GitLog>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<GitLog> logs = null;
            try {
                logs = git.logs(150);
            } catch (GitAPIException | IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<List<GitLog>>(logs);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void fileStates(IGitServiceCallback<Map<GitFileStatus, Set<String>>> callback) {
        CompletableFuture.supplyAsync(() -> {
            Map<GitFileStatus, Set<String>> states = null;
            try {
                states = git.states();
            } catch (NoWorkTreeException | GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Map<GitFileStatus, Set<String>>>(states);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void tags(IGitServiceCallback<List<Reference>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<Reference> tags = null;
            try {
                tags = git.tags(150);
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<List<Reference>>(tags);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void openRepository(String directory) throws IOException {
        final File gitDirectory = GitUtil.getGitDirectory(directory);
        if (!gitDirectory.exists()) {
            throw new IllegalStateException("Git repository not available at " + directory);
        } else {
            git.openRepository(gitDirectory);
        }
    }

    @Override
    public void profile(IGitServiceCallback<UserProfile> callback) {
        CompletableFuture.supplyAsync(() -> {
            return new GitResponse<UserProfile>(new UserProfile(GitUtil.getConfig(git.getRepository(), "user", "name"), GitUtil.getConfig(git.getRepository(), "user", "email")));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void saveProfile(IGitServiceCallback<Void> callback, UserProfile profile) {
        CompletableFuture.supplyAsync(() -> {
            try {
                GitUtil.setConfig(git.getRepository(), "user", "name", profile.getName());
                GitUtil.setConfig(git.getRepository(), "user", "email", profile.getEmail());
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public List<Reference> localBranches() {
        List<Reference> branches = null;
        try {
            branches = git.localBranches(150);
        } catch (IOException exception) {
            throwAsUnchecked(exception);
        }
        return branches;
    }

    @Override
    public void remoteBranches(IGitServiceCallback<List<Reference>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<Reference> branches = null;
            try {
                branches = git.remoteBranches(150);
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<List<Reference>>(branches);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stashes(IGitServiceCallback<List<CommitReference>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<CommitReference> stashes = null;
            try {
                stashes = git.stashes().stream().map(commit -> {
                    return new CommitReference(commit.getFullMessage(), commit.getCommitTime(), commit.getType(), commit.getName());
                }).collect(Collectors.toList());
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<List<CommitReference>>(stashes);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void deleteFile(File file) {
        if (file.delete()) {
            System.out.println(file.getName() + " is deleted!");
        } else {
            System.out.println("Delete operation is failed.");
        }
    }

    @Override
    public void cloneRepository(String url, String directory, IGitServiceCallback<File> callback) {
        Log.d(TAG, "cloneRepository called with [url: " + url + ", directory: " + directory + "]");
        File localPath = new File(directory, "");
        if (!localPath.exists()) {
            localPath.mkdir();
        }

        if (!localPath.delete()) {
            callback.onError(new GitException("Could not delete temporary file " + localPath));
        }

        CompletableFuture.supplyAsync(() -> {
            try {
                git.cloneRepository(url, localPath);
            } catch (IllegalStateException | GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<File>(git.getDirectory());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void findPropertyFiles(String filePattern, IGitServiceCallback<List<PropertyDirectory>> callback) {
        String dir = git.getDirectory().getAbsolutePath();
        if (dir.endsWith(".git")) {
            dir = git.getDirectory().getAbsolutePath().replace(".git", "");
        }
        fileService.searchPropertyFile(dir, filePattern, callback, executorService);
    }

    @Override
    public void unstageAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.reset();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void fileContent(String file, IGitServiceCallback<DiffFile> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {

                DiffFile diffFile = GitUtil.getDiff(git.getRepository(), file, false);
                return new GitResponse<DiffFile>(diffFile);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<DiffFile>(new DiffFile());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void dropStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.dropStash(git.stashReference(hash));
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stash(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.stash();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void applyStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.applyStash(hash);
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    private final <T> BiConsumer<GitResponse<T>, Throwable> onCompleteAsync(IGitServiceCallback<T> callback) {
        return (result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitException(exception.getMessage(), exception.getCause()));
            }
        };
    }

    @Override
    public void popFirst(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.popStashAt(0);
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void fetch(IGitServiceCallback<String> callback) {
        CompletableFuture.supplyAsync(() -> {
            FetchResult result = null;
            try {
                result = git.fetch();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<String>(result.getMessages());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void checkout(String branch) throws GitAPIException {
        try {
            git.checkout(branch, false);
        } catch (GitAPIException exception) {
            throw exception;
        }
    }

    @Override
    public void checkoutRemoteBranch(String branch) throws GitAPIException {
        try {
            git.checkout(branch, true);
        } catch (GitAPIException exception) {
            throw exception;
        }
    }

    @Override
    public void pull(IGitServiceCallback<Boolean> callback) {
        CompletableFuture.supplyAsync(() -> {
            PullResult result = null;
            try {
                result = git.pull();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Boolean>(result.isSuccessful());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void fileMergeDiff(String file, GitFileStatus conflict, IGitServiceCallback<DiffFile> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                DiffFile diffFile = git.mergeDiff(file);
                return new GitResponse<DiffFile>(diffFile);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<DiffFile>(new DiffFile());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stageMergedFile(String fileName, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.addFile(fileName);
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

    /// ============================== OOOOOLLLLLLLLLLLLLLLLLLLLLLLLLDDDDDDDDDDDDDDDDDDDDDDDD
    @Override
    public void pushAll(IGitServiceCallback<Void> callback) {
        //     push.pushAll(git, privateKeyPath, callback, executorService);
    }

    @Override
    public void pushChangesWithCredentials(String password, String username, String directory, IGitServiceCallback<Void> callback) {

        //        CompletableFuture.supplyAsync(() -> {
        //            Iterable<PushResult> results = null;
        //            try {
        //                results = git.push().setRemote("origin").setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
        //            } catch (GitAPIException exception) {
        //                if (exception.getCause() instanceof TransportException) {
        //
        //                }
        //                throwAsUnchecked(exception);
        //            }
        //            return new GitResponse<Void>(null, GitUtil.parsePushResults(results));
        //        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    @Deprecated
    public void showFileDiff(IGitServiceCallback<Void> callback) {

    }

    @Override
    public void popStash(String hash, IGitServiceCallback<Void> callback) {
        //        CompletableFuture.supplyAsync(() -> {
        //            try {
        //                StashReference stashReference = GitUtil.stashRef(hash, git);
        //                if (stashReference != null) {
        //                    StashApplyCommand apply = git.stashApply().setStashRef(hash);
        //                    apply.call();
        //                    git.stashDrop().setStashRef(stashReference.getReference()).call();
        //                } else {
        //                    throw new IllegalArgumentException("Invalid stash reference " + hash);
        //                }
        //            } catch (GitAPIException exception) {
        //                throwAsUnchecked(exception);
        //            }
        //            return new GitResponse<Void>(null);
        //        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void pullWithMerge() {
        CompletableFuture.supplyAsync(() -> {
            try {
                //MergeCommand pull = git.merge();
                //MergeResult result = pull.set.setFastForward(FastForwardMode.FF).call();

                return new GitResponse<Void>(null);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<DiffFile>(new DiffFile());
        }, executorService).whenCompleteAsync((r, e) -> System.out.println("sddd"));
    }

    @Override
    public void mergeStatus(IGitServiceCallback<Void> callback) {
        //        CompletableFuture.supplyAsync(() -> {
        //            try {
        //
        //                MergeResult merge = git.merge().include(repository.exactRef(Constants.HEAD)).call();
        //
        //                Log.d(TAG, "" + merge.getMergeStatus());
        //                Log.d(TAG, "" + merge.getConflicts());
        //
        //                Stream.of(merge.getMergedCommits()).forEach(commit -> {
        //                    try {
        //                        RevCommit revCommit = GitUtil.parseCommitFrom(repository, commit);
        //                        GitUtil.getDiff(repository, null, GitUtil.parseCommitFrom(repository, commit));
        //                        //GitUtil.getDiff(repository, "README - Copy.md");
        //                    } catch (MissingObjectException e) {
        //                        e.printStackTrace();
        //                    } catch (IncorrectObjectTypeException e) {
        //                        e.printStackTrace();
        //                    } catch (IOException e) {
        //                        e.printStackTrace();
        //                    }
        //                });
        //
        //            } catch (Exception exception) {
        //                throwAsUnchecked(exception);
        //            }
        //            return new GitResponse<Void>(null);
        //        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void pullWithRebase() {
        //        CompletableFuture.supplyAsync(() -> {
        //            try {
        //                PullCommand pull = git.pull();
        //                PullResult result = pull.setRebase(true).call();
        //
        //                Log.d(TAG, "REBASE: " + result.getRebaseResult());
        //
        //                return new GitResponse<Void>(null);
        //            } catch (Exception exception) {
        //                throwAsUnchecked(exception);
        //            }
        //            return new GitResponse<DiffFile>(new DiffFile());
        //        }, executorService).whenCompleteAsync((r, e) -> System.out.println("sddd"));

    }

    @Override
    @Deprecated
    public void unmount() {
        //        Log.d(TAG, "unmount(" + directory + ")");
        //        if (repository != null) {
        //            repository.close();
        //            repository = null;
        //        }
        //        if (git != null) {
        //            git.close();
        //            git = null;
        //        }
        //        directory = null;
    }

    @Override
    @Deprecated
    public void dispose() {
        unmount();
        executorService.shutdown();
    }

}
