package org.eclipse.e4.tapiji.git.core.internal;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.core.internal.diff.TapijiDiffFormatter;
import org.eclipse.e4.tapiji.git.core.internal.file.FileService;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.e4.tapiji.git.model.push.GitPushMessage;
import org.eclipse.e4.tapiji.git.model.push.GitRemoteStatus;
import org.eclipse.e4.tapiji.git.model.stash.StashReference;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;


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
            Iterable<PushResult> results = null;
            try {
                results = git.push().setRemote("origin").setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)).call();
            } catch (GitAPIException exception) {
                if (exception.getCause() instanceof TransportException) {

                }
                throwAsUnchecked(exception);
            }
            return new GitServiceResult<Void>(null, parsePushResults(results));
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
                git.add().setUpdate(true).addFilepattern(".").call();
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
    public void deleteFile(File file) {
        if (file.delete()) {
            System.out.println(file.getName() + " is deleted!");
        } else {
            System.out.println("Delete operation is failed.");
        }

    }

    @Override
    public void branches(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            ListBranchCommand branches = git.branchList();
            Log.d(TAG, "stash(" + branches + ")");
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
    public void fetch(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            FetchCommand fetch = git.fetch().setCheckFetchedObjects(true).setRemoveDeletedRefs(true);
            Log.d(TAG, "stash(" + fetch + ")");
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
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    @Override
    public void stashes(IGitServiceCallback<List<CommitReference>> callback) {
        readFile();
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
                git.stashApply().setStashRef(hash).call();
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
    public void popStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                StashReference stashReference = stashRef(hash);
                if (stashReference != null) {
                    git.stashApply().setStashRef(hash).call();
                    git.stashDrop().setStashRef(stashReference.getReference()).call();
                } else {
                    throw new IllegalArgumentException("Invalid stash reference " + hash);
                }
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
    public void dropStash(String hash, IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            try {
                StashReference stashReference = stashRef(hash);
                if (stashReference != null) {
                    git.stashDrop().setStashRef(stashReference.getReference()).call();
                } else {
                    throw new IllegalArgumentException("Invalid stash reference " + hash);
                }
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
    public void stash(IGitServiceCallback<Void> callback) {
        CompletableFuture.supplyAsync(() -> {
            RevCommit stash = null;
            try {
                stash = git.stashCreate().call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            Log.d(TAG, "stash(" + stash + ")");
            return new GitServiceResult<Void>(null);
        }, executorService).whenCompleteAsync((result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitServiceException(exception.getMessage(), exception.getCause()));
            }
        });
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            try (ObjectReader oldReader = repository.newObjectReader()) {
                oldTreeParser.reset(oldReader, tree.getId());
            }

            walk.dispose();
            return oldTreeParser;
        }
    }

    public void readFile() {
        try {
            ByteArrayOutputStream diffOutputStream = new ByteArrayOutputStream();
            TapijiDiffFormatter formatter = new TapijiDiffFormatter(diffOutputStream);

            formatter.setRepository(repository);
            //formatter.setPathFilter(PathFilter.create(this.pathFilter.replaceAll("\\\\", "/")));

            AbstractTreeIterator commitTreeIterator = prepareTreeParser(repository, repository.resolve("HEAD").getName());
            FileTreeIterator workTreeIterator = new FileTreeIterator(repository);

            // Scan gets difference between the two iterators.
            formatter.format(commitTreeIterator, workTreeIterator);

            String diffText = diffOutputStream.toString();
            String[] liness = diffText.split("\n");
            for (String line : liness) {
                Log.d("DIFF", "sds" + line);
            }

            Log.d("DIFF", "sds" + formatter.getHtml());
        } catch (Exception e) {

        }

        //    final String[] file = new File("README.md").list();
        //  for (String file : list) {
        //  if (new File(file).isDirectory()) {
        //     continue;
        // }
        //
        //            final BlameResult result = git.blame().setFilePath("README.md").call();
        //            final RawText rawText = result.getResultContents();
        //            for (int i = 0; i < rawText.size(); i++) {
        //                final PersonIdent sourceAuthor = result.getSourceAuthor(i);
        //                final RevCommit sourceCommit = result.getSourceCommit(i);
        //                System.out.println(sourceAuthor.getName() + (sourceCommit != null ? "/" + sourceCommit.getCommitTime() + "/" + sourceCommit.getName() : "") + ": " + rawText
        //                    .getString(i));
        //            }
        //            //   }
        //
        //            BlameCommand blamer = new BlameCommand(repository);
        //            ObjectId commitID;
        //
        //            commitID = repository.resolve("HEAD");
        //
        //            blamer.setStartCommit(commitID);
        //            blamer.setFilePath("README.md");
        //            BlameResult blame = blamer.call();
        //
        //            // read the number of lines from the commit to not look at changes in the working copy
        //            int lines = countFiles(repository, commitID, "README.md");
        //            for (int i = 0; i < lines; i++) {
        //                RevCommit commit = blame.getSourceCommit(i);
        //                System.out.println("Line: " + i + ": " + commit + " commit ");
        //            }
        //
        //            System.out.println("Displayed commits responsible for " + lines + " lines of README.md");

    }

    private static int countFiles(Repository repository, ObjectId commitID, String name) throws IOException {
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit commit = revWalk.parseCommit(commitID);
            RevTree tree = commit.getTree();
            System.out.println("Having tree: " + tree);

            // now try to find a specific file
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create(name));
                if (!treeWalk.next()) {
                    throw new IllegalStateException("Did not find expected file 'README.md'");
                }

                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // and then one can the loader to read the file
                loader.copyTo(stream);

                revWalk.dispose();

                return readLines(new ByteArrayInputStream(stream.toByteArray()), "UTF-8").size();
            }
        }
    }

    private StashReference stashRef(String commitHash) throws InvalidRefNameException, GitAPIException {
        int ref = 0;
        Collection<RevCommit> list = git.stashList().call();
        for (RevCommit revCommit : list) {
            if (revCommit.getName().equals(commitHash)) {
                return new StashReference(ref);
            } else {
                ref++;
            }
        }
        return null;
    }

    public static List readLines(InputStream input, String encoding) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, encoding);
        return readLines(reader);
    }

    public static List readLines(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List list = new ArrayList();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

}
