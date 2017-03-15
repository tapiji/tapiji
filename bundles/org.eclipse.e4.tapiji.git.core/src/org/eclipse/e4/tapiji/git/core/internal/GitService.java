package org.eclipse.e4.tapiji.git.core.internal;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.core.internal.file.FileService;
import org.eclipse.e4.tapiji.git.core.internal.util.JGitUtils;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.ReferenceType;
import org.eclipse.e4.tapiji.git.model.UserProfile;
import org.eclipse.e4.tapiji.git.model.commitlog.CommitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.model.push.GitPushMessage;
import org.eclipse.e4.tapiji.git.model.push.GitRemoteStatus;
import org.eclipse.e4.tapiji.git.model.stash.StashReference;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.StashApplyCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


public class GitService implements IGitService {

    private static final String TAG = GitService.class.getSimpleName();

    @Inject
    FileService fileService;

    private ExecutorService executorService;
    private Repository repository;
    private Git git;
    private String directory;

    public GitService() {
        executorService = Executors.newFixedThreadPool(10);
    }

    @Override
    public File getDirectory() {
        String dir = repository.getDirectory().getPath().replace(".git", "");
        return new File(dir);
    }

    @Override
    public String getUrl() {
        return repository.getConfig().getString("remote", "origin", "url");
    }

    @Override
    public void mount(String directory) throws IOException {
        Log.d(TAG, "mount(" + directory + ")");
        if (directory != null) {
            if (!directory.endsWith(".git") && !directory.endsWith(".git/")) {
                directory = directory + "/.git";
            }
            if (!new File(directory).exists()) {
                throw new IllegalStateException("Git repository not available at " + directory);
            }
            if (!directory.equals(this.directory)) {
                unmount();
                this.directory = directory;
                this.repository = new FileRepositoryBuilder().setGitDir(new File(directory)).readEnvironment().findGitDir().build();
                this.git = new Git(repository);

                repository.getRemoteNames().forEach(remot -> System.out.println(remot));
            } else {
                Log.d(TAG, "Directory already mounted: " + directory);
            }
        } else {
            throw new IllegalStateException("Directory must not be null.");
        }
    }

    @Override
    public void pushAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            Iterable<PushResult> results = null;
            try {
                results = git.push().setRemote("origin").setPushAll().setPushTags().call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null, parsePushResults(results));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
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
            return new GitServiceResult<Void>(null, parsePushResults(results));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
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
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stageAll(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                Status status = git.status().call();
                stageUntrackedFiles(status);
                stageMissingFiles(status);
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    private void stageMissingFiles(Status status) throws NoFilepatternException, GitAPIException {
        RmCommand rm = git.rm();
        if (!status.getMissing().isEmpty()) {
            status.getMissing().forEach(file -> rm.addFilepattern(file));
            rm.call();
        }
    }

    private void stageUntrackedFiles(Status status) throws NoFilepatternException, GitAPIException {
        boolean empty = true;
        AddCommand add = git.add();
        if (!status.getUntracked().isEmpty()) {
            status.getUntracked().forEach(file -> add.addFilepattern(file));
            empty = false;
        }

        if (!status.getModified().isEmpty()) {
            status.getModified().forEach(file -> add.addFilepattern(file));
            empty = false;
        }

        if (!status.getConflicting().isEmpty()) {
            status.getConflicting().forEach(file -> add.addFilepattern(file));
            empty = false;
        }
        if (!empty) {
            add.call();
        }
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
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
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
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
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
                states.put(GitFileStatus.CONFLICT, status.getConflicting());

            } catch (NoWorkTreeException | GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Map<GitFileStatus, Set<String>>>(states);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void tags(IGitServiceCallback<List<Reference>> callback) {
        getReferences(callback, ReferenceType.TAGS);
    }

    @Override
    public List<Reference> branches() throws IOException {
        return JGitUtils.getBranches(repository, 10);
    }

    @Override
    public void branches(IGitServiceCallback<List<Reference>> callback) {
        getReferences(callback, ReferenceType.HEADS);
    }

    private void getReferences(IGitServiceCallback<List<Reference>> callback, ReferenceType referenceType) {
        CompletableFuture.supplyAsync(() -> {
            List<Reference> references = new ArrayList<>();
            try {
                switch (referenceType) {
                    case HEADS:
                        references = JGitUtils.getBranches(repository, -1);
                        break;
                    case TAGS:
                        references = JGitUtils.getTags(repository, -1);
                        break;
                    case STASHES:
                        references = JGitUtils.getStashes(repository, -1);
                        break;
                    default:
                        throw new IllegalArgumentException("Wrong reference type " + referenceType);
                }
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<List<Reference>>(references);
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
    public void showFileDiff(IGitServiceCallback<Void> callback) {

    }

    private List<GitPushMessage> parsePushResults(Iterable<PushResult> results) {
        if (results == null) {
            return Collections.emptyList();
        } else {
            return StreamSupport.stream(results.spliterator(), false).flatMap(result -> result.getRemoteUpdates().stream().map(update -> {
                GitPushMessage message = new GitPushMessage();
                message.setRemoteStatus(GitRemoteStatus.valueOf(update.getStatus().toString()));
                message.setRemoteName(update.getTrackingRefUpdate().getRemoteName());
                message.setLocalName(update.getTrackingRefUpdate().getLocalName());
                return message;
            })).collect(Collectors.toList());
        }
    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
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
    public void fetch(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            FetchCommand fetch = git.fetch().setCheckFetchedObjects(true).setRemoveDeletedRefs(true);
            Log.d(TAG, "stash(" + fetch + ")");
            return new GitServiceResult<Void>(null);
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
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stashes(IGitServiceCallback<List<CommitReference>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<CommitReference> stashes = null;
            try {
                stashes = git.stashList().call().stream().map(commit -> {
                    return new CommitReference(commit.getFullMessage(), commit.getCommitTime(), commit.getType(), commit.getName());
                }).collect(Collectors.toList());

            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<List<CommitReference>>(stashes);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
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
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void popStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                StashReference stashReference = JGitUtils.stashRef(hash, git);
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
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void dropStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                StashReference stashReference = JGitUtils.stashRef(hash, git);
                if (stashReference != null) {
                    git.stashDrop().setStashRef(stashReference.getReference()).call();
                } else {
                    throw new IllegalArgumentException("Invalid stash reference " + hash);
                }
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void stash(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                git.stashCreate().setIncludeUntracked(true).call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void pullWithMerge() {
        CompletableFuture.supplyAsync(() -> {
            try {
                //MergeCommand pull = git.merge();
                //MergeResult result = pull.set.setFastForward(FastForwardMode.FF).call();

                return new GitServiceResult<Void>(null);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<DiffFile>(new DiffFile());
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
                        RevCommit revCommit = JGitUtils.parseCommitFrom(repository, commit);
                        JGitUtils.getDiff(repository, null, JGitUtils.parseCommitFrom(repository, commit));
                        //JGitUtils.getDiff(repository, "README - Copy.md");
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
            return new GitServiceResult<Void>(null);
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
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void pullWithRebase() {
        CompletableFuture.supplyAsync(() -> {
            try {
                PullCommand pull = git.pull();
                PullResult result = pull.setRebase(true).call();

                Log.d(TAG, "REBASE: " + result.getRebaseResult());

                return new GitServiceResult<Void>(null);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<DiffFile>(new DiffFile());
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
            return new GitServiceResult<String>(result.getMessages());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    private final <T> BiConsumer<GitServiceResult<T>, Throwable> onCompleteAsync(IGitServiceCallback<T> callback) {
        return (result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        };
    }

    @Override
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
    public void dispose() {
        unmount();
        executorService.shutdown();
    }

    @Override
    public void fileMergeDiff(String file, GitFileStatus conflict, IGitServiceCallback<DiffFile> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                DiffFile diffFile = JGitUtils.getDiff(repository, file, true);
                return new GitServiceResult<DiffFile>(diffFile);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<DiffFile>(new DiffFile());
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void fileContent(String file, IGitServiceCallback<DiffFile> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                DiffFile diffFile = JGitUtils.getDiff(repository, file, false);
                return new GitServiceResult<DiffFile>(diffFile);
            } catch (Exception exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<DiffFile>(new DiffFile());
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
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void logs(IGitServiceCallback<List<CommitLog>> callback) {
        CompletableFuture.supplyAsync(() -> {
            List<CommitLog> logs = new ArrayList<>();
            try {
                LogCommand log = git.log();
                log.setMaxCount(30);
                Iterable<RevCommit> commitLogs = log.call();
                try (RevWalk walk = new RevWalk(repository)) {
                    for (RevCommit rev : commitLogs) {
                        RevCommit commit = walk.parseCommit(rev.getId());
                        logs.add(new CommitLog(commit.getShortMessage(), commit.getFullMessage(), commit.getAuthorIdent().getName(), commit.getAuthorIdent()
                            .getEmailAddress(), commit.getCommitterIdent().getWhen()));
                    }
                    walk.dispose();
                } catch (IOException exception) {
                    throwAsUnchecked(exception);
                }
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<List<CommitLog>>(logs);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void profile(IGitServiceCallback<UserProfile> callback) {
        CompletableFuture.supplyAsync(() -> {
            Config config = repository.getConfig();
            String name = config.getString("user", null, "name");
            String email = config.getString("user", null, "email");

            return new GitServiceResult<UserProfile>(new UserProfile(name, email));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

    @Override
    public void saveProfile(IGitServiceCallback<Void> callback, UserProfile profile) {
        CompletableFuture.supplyAsync(() -> {
            StoredConfig config = repository.getConfig();
            config.setString("user", null, "name", profile.getName());
            config.setString("user", null, "email", profile.getEmail());
            try {
                config.save();
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }

}
