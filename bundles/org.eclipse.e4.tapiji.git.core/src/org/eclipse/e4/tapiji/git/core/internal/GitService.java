package org.eclipse.e4.tapiji.git.core.internal;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import org.eclipse.e4.tapiji.git.model.stash.StashReference;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.StashApplyCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class GitService implements IGitService {

    private static final String TAG = GitService.class.getSimpleName();

    @Inject
    FileService fileService;

    @Inject
    GitPush push;

    @Inject
    GitRepository gitRepository;

    private ExecutorService executorService;
    private Repository repository;
    private Git git;
    private String directory;

    private String publicKeyPath;

    private String privateKeyPath;

    public GitService() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public void setPublicKeyPath(String keyPath) {
        Log.d(TAG, "setPublicKeyPath called with [" + keyPath + "]");
        this.publicKeyPath = keyPath;
    }

    @Override
    public void setPrivateKeyPath(String keyPath) {
        Log.d(TAG, "privateKeyPath called with [" + keyPath + "]");
        this.privateKeyPath = keyPath;
    }

    @Override
    public File getDirectory() {
        String dir = repository.getDirectory().getPath().replace(".git", "");
        return new File(dir);
    }

    @Override
    public String getUrl() {
        return gitRepository.getUrl();
    }

    @Override
    public void commitChanges(String summary, String description, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            gitRepository.commit(summary, description);
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stageAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            gitRepository.stageAll();
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void discardChanges(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            gitRepository.discardChanges();
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void logs(IGitServiceCallback<List<GitLog>> callback) {
        CompletableFuture.supplyAsync(() -> {
            return new GitResponse<List<GitLog>>(gitRepository.logs(150));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    @Deprecated
    public void uncommittedChanges(IGitServiceCallback<Map<GitFileStatus, Set<String>>> callback) {
        CompletableFuture.supplyAsync(() -> {
            return new GitResponse<Map<GitFileStatus, Set<String>>>(gitRepository.states());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void tags(IGitServiceCallback<List<Reference>> callback) {
        CompletableFuture.supplyAsync(() -> {
            return new GitResponse<List<Reference>>(gitRepository.tags(150));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void openRepository(String directory) throws IOException {
        final File gitDirectory = GitUtil.getGitDirectory(directory);
        if (!gitDirectory.exists()) {
            throw new IllegalStateException("Git repository not available at " + directory);
        } else {
            gitRepository.openRepository(gitDirectory);
        }
    }

    @Override
    public void profile(IGitServiceCallback<UserProfile> callback) {
        CompletableFuture.supplyAsync(() -> {
            return new GitResponse<UserProfile>(new UserProfile(GitUtil.getConfig(repository, "user", "name"), GitUtil.getConfig(repository, "user", "email")));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void saveProfile(IGitServiceCallback<Void> callback, UserProfile profile) {
        CompletableFuture.supplyAsync(() -> {
            try {
                GitUtil.setConfig(repository, "user", "name", profile.getName());
                GitUtil.setConfig(repository, "user", "email", profile.getEmail());
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void branches(IGitServiceCallback<List<Reference>> callback) {
        CompletableFuture.supplyAsync(() -> {
            return new GitResponse<List<Reference>>(gitRepository.branches(150));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stashes(IGitServiceCallback<List<CommitReference>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<CommitReference> stashes = null;
            stashes = git.stashList().call().stream().map(commit -> {
                return new CommitReference(commit.getFullMessage(), commit.getCommitTime(), commit.getType(), commit.getName());
            }).collect(Collectors.toList());
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
            gitRepository.cloneRepository(url, localPath);
            return new GitResponse<File>(gitRepository.getDirectory());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    /// ============================== OOOOOLLLLLLLLLLLLLLLLLLLLLLLLLDDDDDDDDDDDDDDDDDDDDDDDD
    @Override
    public void pushAll(IGitServiceCallback<Void> callback) {
        push.pushAll(git, privateKeyPath, callback, executorService);
    }

    @Override
    @Deprecated
    public void checkout(String branch) {
        try {
            Ref ref = git.checkout()
                .setCreateBranch(false)
                .setName(branch)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint(Constants.R_HEADS + branch)
                .call();
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void pushChangesWithCredentials(String password, String username, String directory, IGitServiceCallback<Void> callback) {

        CompletableFuture.supplyAsync(() -> {
            Iterable<PushResult> results = null;
            try {
                results = git.push().setRemote("origin").setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
            } catch (GitAPIException exception) {
                if (exception.getCause() instanceof TransportException) {

                }
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null, GitUtil.parsePushResults(results));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void unstageAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.reset().call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void findPropertyFiles(String filePattern, IGitServiceCallback<List<PropertyDirectory>> callback) {
        String dir = directory;
        if (dir.endsWith(".git")) {
            dir = directory.replace(".git", "");
        }
        fileService.searchPropertyFile(dir, filePattern, callback, executorService);
    }

    @Override
    @Deprecated
    public void showFileDiff(IGitServiceCallback<Void> callback) {

    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

    @Override
    public void fetch(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            FetchCommand fetch = git.fetch().setCheckFetchedObjects(true).setRemoveDeletedRefs(true);
            Log.d(TAG, "stash(" + fetch + ")");
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void popFirst(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Collection<RevCommit> stashes = git.stashList().call();
                git.stashApply().setStashRef(stashes.stream().findFirst().get().getName()).call();
                git.stashDrop().setStashRef(0).call();
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
                StashApplyCommand apply = git.stashApply().setStashRef(hash);
                apply.setApplyUntracked(true);
                apply.call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void popStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                StashReference stashReference = GitUtil.stashRef(hash, git);
                if (stashReference != null) {
                    StashApplyCommand apply = git.stashApply().setStashRef(hash);
                    apply.call();
                    git.stashDrop().setStashRef(stashReference.getReference()).call();
                } else {
                    throw new IllegalArgumentException("Invalid stash reference " + hash);
                }
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    @Deprecated
    public void dropStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                StashReference stashReference = GitUtil.stashRef(hash, git);
                if (stashReference != null) {
                    git.stashDrop().setStashRef(stashReference.getReference()).call();
                } else {
                    throw new IllegalArgumentException("Invalid stash reference " + hash);
                }
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    @Deprecated
    public void stash(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.stashCreate().setIncludeUntracked(true).call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
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
        CompletableFuture.supplyAsync(() -> {
            try {

                MergeResult merge = git.merge().include(repository.exactRef(Constants.HEAD)).call();

                Log.d(TAG, "" + merge.getMergeStatus());
                Log.d(TAG, "" + merge.getConflicts());

                Stream.of(merge.getMergedCommits()).forEach(commit -> {
                    try {
                        RevCommit revCommit = GitUtil.parseCommitFrom(repository, commit);
                        GitUtil.getDiff(repository, null, GitUtil.parseCommitFrom(repository, commit));
                        //GitUtil.getDiff(repository, "README - Copy.md");
                    } catch (MissingObjectException e) {
                        e.printStackTrace();
                    } catch (IncorrectObjectTypeException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void pullFastForward(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {

                // todo return merge result if conflict exists and handle view states
                PullResult pull = git.pull().call();

            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void pullWithRebase() {
        CompletableFuture.supplyAsync(() -> {
            try {
                PullCommand pull = git.pull();
                PullResult result = pull.setRebase(true).call();

                Log.d(TAG, "REBASE: " + result.getRebaseResult());

                return new GitResponse<Void>(null);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<DiffFile>(new DiffFile());
        }, executorService).whenCompleteAsync((r, e) -> System.out.println("sddd"));

    }

    @Override
    public void fetchAll(IGitServiceCallback<String> callback) {
        CompletableFuture.supplyAsync(() -> {
            FetchResult result = null;
            try {
                result = git.fetch().setRemoveDeletedRefs(true).setCheckFetchedObjects(true).call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<String>(result.getMessages());
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
    @Deprecated
    public void unmount() {
        Log.d(TAG, "unmount(" + directory + ")");
        if (repository != null) {
            repository.close();
            repository = null;
        }
        if (git != null) {
            git.close();
            git = null;
        }
        directory = null;
    }

    @Override
    @Deprecated
    public void dispose() {
        unmount();
        executorService.shutdown();
    }

    @Override
    public void fileMergeDiff(String file, GitFileStatus conflict, IGitServiceCallback<DiffFile> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                DiffFile diffFile = GitUtil.getDiff(repository, file, true);
                return new GitResponse<DiffFile>(diffFile);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<DiffFile>(new DiffFile());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void fileContent(String file, IGitServiceCallback<DiffFile> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                DiffFile diffFile = GitUtil.getDiff(repository, file, false);
                return new GitResponse<DiffFile>(diffFile);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<DiffFile>(new DiffFile());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stageFile(String fileName, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                AddCommand add = git.add();
                add.addFilepattern(fileName);
                add.call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

}
