package org.eclipse.e4.tapiji.git.core.internal.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.e4.tapiji.git.core.internal.diff.TapijiDiffFormatter;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.stash.StashReference;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;


public class JGitUtils {

    private JGitUtils() {
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
     * @return DiffFile
     *         Represents the diff between old and new file
     * @throws IOException
     */
    public static DiffFile getDiff(final Repository repository, final String file, boolean withHooks) throws IOException {

        try (ByteArrayOutputStream diffOutputStream = new ByteArrayOutputStream(); RevWalk walk = new RevWalk(repository); TapijiDiffFormatter formatter = new TapijiDiffFormatter(diffOutputStream, withHooks)) {

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
            diff.setFile(file);
            return diff;
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
     * Returns the list of tags in the repository.
     *
     * @param repository
     *            Represents the current git repository.
     * @param limit
     *            The number of elements the tags should be limited to
     * @return list
     *         List of tags
     * @throws IOException
     *             The reference space cannot be accessed.
     */
    public static List<Reference> getTags(Repository repository, int limit) throws IOException {
        return getRefs(repository, Constants.R_TAGS, limit);
    }

    /**
     * Returns the list of local branches in the repository.
     *
     * @param repository
     *            Represents the current git repository.
     * @param limit
     *            The number of elements the branches should be limited to
     * @return list
     *         List of local branches
     * @throws IOException
     *             The reference space cannot be accessed.
     */
    public static List<Reference> getBranches(Repository repository, int limit) throws IOException {
        return getRefs(repository, Constants.R_HEADS, limit);
    }

    /**
     * Returns the list of stashes in the repository.
     *
     * @param repository
     *            Represents the current git repository.
     * @param limit
     *            The number of elements the stashes should be limited to
     * @return list
     *         List of stashes
     * @throws IOException
     *             The reference space cannot be accessed.
     */
    public static List<Reference> getStashes(Repository repository, int limit) throws IOException {
        return getRefs(repository, Constants.R_STASH, limit);
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

    /*
     *
     */
    public static StashReference stashRef(String commitHash, Git git) throws InvalidRefNameException, GitAPIException {
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

}
