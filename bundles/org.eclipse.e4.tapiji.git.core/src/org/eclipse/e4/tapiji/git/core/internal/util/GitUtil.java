package org.eclipse.e4.tapiji.git.core.internal.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.eclipse.e4.tapiji.git.core.internal.diff.TapijiDiffFormatter;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.e4.tapiji.git.model.push.GitPushMessage;
import org.eclipse.e4.tapiji.git.model.push.GitRemoteStatus;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;


public class GitUtil {

    private static final int DIFF_CONTEXT = 3;
    private static final int CONFLICT_CONTEXT = 100000;
    private static final String TAG = GitUtil.class.getSimpleName();
    private static final String GIT_DIRECTORY_ENDING = ".git";

    private GitUtil() {
        // only static access allowed
    }

    /**
     * Returns the diff between the primary parent (HEAD) and
     * current work tree.
     *
     * @param file
     *            If file is not specified, the diff returns the entire commit otherwise it is restricted to
     *            that file.
     * @param repository
     *            Represents the current git repository.
     * @param conflict
     *            If merge conflict exists
     * @return DiffFile
     *         Represents the diff between old and new file
     * @throws IOException
     */
    public static DiffFile getDiff(final Repository repository, final String file, boolean conflict) throws IOException {

        try (ByteArrayOutputStream diffOutputStream = new ByteArrayOutputStream(); RevWalk walk = new RevWalk(repository); TapijiDiffFormatter formatter = new TapijiDiffFormatter(diffOutputStream, getContextDiff(conflict))) {

            RevCommit root = walk.parseCommit(getDefaultBranch(repository));
            RevTree rootTree = walk.parseTree(root.getTree().getId());

            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            try (ObjectReader oldReader = repository.newObjectReader()) {
                oldTreeParser.reset(oldReader, rootTree.getId());
            }

            formatter.setRepository(repository);

            final List<DiffEntry> diffEntries = formatter.scan(oldTreeParser, new FileTreeIterator(repository));
            if (file != null && file.length() > 0) {
                Optional<DiffEntry> diffEntry = diffEntries.stream().filter(entry -> {
                    if (entry.getChangeType() == ChangeType.DELETE) {
                        return entry.getOldPath().equalsIgnoreCase(file);
                    } else {
                        return entry.getNewPath().equalsIgnoreCase(file);
                    }
                }).findFirst();
                if (diffEntry.isPresent()) {
                    formatter.format(diffEntry.get());
                } else {
                    formatter.format(diffEntries);
                }
            } else {
                formatter.format(diffEntries);
            }
            walk.dispose();
            DiffFile diff = formatter.get();

            diff.setFile(repository.getDirectory().getParent() + File.separator + file);
            return diff;
        }
    }

    /**
     * Change the number of lines of context to display.
     *
     * @param if conflict exists we want to see all lines from file
     * @return lineCount
     *         number of lines of context to see before the first
     *         modification and after the last modification within a hunk of
     *         the modified file.
     */
    private static int getContextDiff(boolean conflict) {
        if (conflict) {
            return CONFLICT_CONTEXT;
        } else {
            return DIFF_CONTEXT;
        }
    }

    public static void getDiff(final Repository repository, RevCommit baseCommit, RevCommit commit) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {
        try (RevWalk revWalk = new RevWalk(repository)) {

            // and using commit's tree find the path
            RevTree tree = commit.getTree();
            System.out.println("Having tree: " + tree);

            // now try to find a specific file
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create("README - Copy.md"));
                if (!treeWalk.next()) {
                    throw new IllegalStateException("Did not find expected file 'README.md'");
                }

                ObjectId objectId = treeWalk.getObjectId(0);
                ObjectLoader loader = repository.open(objectId);

                // and then one can the loader to read the file
                loader.copyTo(System.out);
            }

            revWalk.dispose();
        }
    }

    /**
     * Returns a list of references.
     *
     * @param repository
     *            Represents the current git repository.
     * @param ref
     *            From type {@link Constants.R_TAGS}, {@link Constants.R_STASH},
     *            {@link Constants.R_REFS}, {@link Constants.R_REMOTE}, {@link Constants.R_HEADS},
     *            {@link Constants.R_NOTES}
     * @param limit
     *            The number of elements the refs should be limited to
     *            If limit < 0 then all references are returned
     * @return list
     *         references of type {@link Reference}
     * @throws IOException
     *             The reference space cannot be accessed.
     */
    public static List<Reference> getRefs(Repository repository, String ref, int limit) throws IOException {
        return repository.getRefDatabase()
            .getRefs(ref)
            .entrySet()
            .stream()
            .limit(limit < 0 ? Integer.MAX_VALUE : limit)
            .map(entry -> new Reference(entry.getKey(), entry.getValue().toString()))
            .collect(Collectors.toList());
    }

    /**
     * Returns the default branch from Git repository, whatever
     * branch HEAD points to.
     *
     * @param repository
     *            Represents the current git repository
     * @return objectId
     *         ObjectId of the branch from the repository
     * @throws IOException
     * @throws IncorrectObjectTypeException
     * @throws AmbiguousObjectException
     * @throws RevisionSyntaxException
     *             the expression is not supported by this implementation, or
     *             does not meet the standard syntax.
     */
    public static ObjectId getDefaultBranch(Repository repository) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
        return repository.resolve(Constants.HEAD);
    }

    /**
     * @param repository
     * @param oid
     * @return
     * @throws MissingObjectException
     * @throws IncorrectObjectTypeException
     * @throws IOException
     */
    public static RevCommit parseCommitFrom(Repository repository, ObjectId oid) throws MissingObjectException, IncorrectObjectTypeException, IOException {
        RevCommit commit = null;
        try (RevWalk revWalk = new RevWalk(repository)) {
            commit = revWalk.parseCommit(oid);
        }
        return commit;
    }

    /**
     * Returns git config string value for the specified key.
     *
     * @param repository
     *            Represents the current git repository
     * @param section
     *            the section
     * @param key
     *            the key for the value
     * @return a String value from the config,<code>null</code>if not found
     */
    public static String getConfig(Repository repository, String section, String key) {
        StoredConfig config = repository.getConfig();
        return config.getString(section, null, key);
    }

    /**
     * Saves git config string value for the specified key
     *
     * @param repository
     *            Represents the current git repository
     * @param section
     *            the section
     * @param key
     *            the key for the value
     * @param value
     *            value to write
     * @throws IOException
     *             can not write git config
     */
    public static void setConfig(Repository repository, String section, String key, String value) throws IOException {
        StoredConfig config = repository.getConfig();
        config.setString(section, null, key, value);
        config.save();
    }

    /**
     * Returns directory path of a git repository
     *
     * @param project path
     * @return file
     */
    public static File getGitDirectory(String project) {
        if (project.endsWith(GIT_DIRECTORY_ENDING)) {
            return new File(project);
        } else {
            return new File(project + File.separator + GIT_DIRECTORY_ENDING);
        }
    }

    /**
     * @param exception
     */
    public static void wrapTransportException(TransportException exception) {
        String message = exception.getMessage();
        if (message.equals(JGitText.get().notAuthorized)) {
            new GitException("", 401);
        } else if (message.equals(JGitText.get().serviceNotPermitted)) {
            new GitException("", 403);
        } else {

        }

    }

    //    public static void cloneRepository(String fromUrl, String directory, IGitServiceCallback<File> callback) {
    //        Log.d(TAG, "cloneRepository(" + fromUrl + " to " + directory + ")");
    //
    //        File localPath = new File(directory, "");
    //        if (!localPath.exists()) {
    //            localPath.mkdir();
    //        }
    //
    //        if (!localPath.delete()) {
    //            callback.onError(new GitException("Could not delete temporary file " + localPath));
    //        }
    //
    //        try (Git result = Git.cloneRepository().setURI(url).setBare(false).setDirectory(localPath).call()) {
    //            try {
    //                //mount(directory);
    //                callback.onSuccess(new GitResponse<File>(result.getRepository().getDirectory()));
    //            } catch (IOException e) {
    //                callback.onError(new GitException(e.getMessage(), e.getCause()));
    //            }
    //        } catch (GitAPIException e) {
    //            callback.onError(new GitException(e.getMessage(), e.getCause()));
    //        }
    //    }

    public static List<GitPushMessage> parsePushResults(Iterable<PushResult> results) {
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

    /////////////////////// NEWWWWW

    /**
     * @param git
     * @param hash
     * @return
     * @throws InvalidRefNameException
     * @throws GitAPIException
     */
    public static int stashRef(Git git, String hash) throws InvalidRefNameException, GitAPIException {
        int ref = 0;
        Collection<RevCommit> list = git.stashList().call();
        for (RevCommit revCommit : list) {
            if (revCommit.getName().equals(hash)) {
                break;
            } else {
                ref++;
            }
        }
        return ref;
    }

}
