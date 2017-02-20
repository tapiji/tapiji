package org.eclipse.e4.tapiji.git.core.internal.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.eclipse.e4.tapiji.git.core.internal.diff.TapijiDiffFormatter;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;


public class JGitUtils {

    private JGitUtils() {
        // only static access allowed
    }

    /**
     * Returns the diff between the primary parent (HEAD) and
     * current work tree.
     *
     * @param filePath
     *            If filePath is not specified, the diff returns the entire commit otherwise it is restricted to
     *            that file.
     * @param repository
     *            Represents the current git repository.
     * @return DiffFile
     *         Represents the diff between old and new file
     * @throws IOException
     */
    public static DiffFile getDiff(final Repository repository, final String filePath) throws IOException {
        try {
            try (ByteArrayOutputStream diffOutputStream = new ByteArrayOutputStream(); RevWalk walk = new RevWalk(repository); TapijiDiffFormatter formatter = new TapijiDiffFormatter(diffOutputStream)) {

                RevCommit root = walk.parseCommit(getDefaultBranch(repository));
                RevTree rootTree = walk.parseTree(root.getTree().getId());

                CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
                try (ObjectReader oldReader = repository.newObjectReader()) {
                    oldTreeParser.reset(oldReader, rootTree.getId());
                }

                formatter.setRepository(repository);
                final List<DiffEntry> diffEntries = formatter.scan(oldTreeParser, new FileTreeIterator(repository));
                if (filePath != null && filePath.length() > 0) {
                    Optional<DiffEntry> diffEntry = diffEntries.stream().filter(entry -> entry.getNewPath().equalsIgnoreCase(filePath)).findFirst();
                    if (diffEntry.isPresent()) {
                        formatter.format(diffEntry.get());
                    } else {
                        formatter.format(diffEntries);
                    }
                } else {
                    formatter.format(diffEntries);
                }
                walk.dispose();
                return formatter.get();
            }
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e.getCause());
        }
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
     */
    public static ObjectId getDefaultBranch(Repository repository) throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
        return repository.resolve(Constants.HEAD);
    }

}
