package org.eclipse.e4.tapiji.git.core.internal;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.internal.util.GitUtil;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.StashApplyCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.StashApplyFailureException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;


@Creatable
public class GitRepository {

    private static final String TAG = GitRepository.class.getSimpleName();

    private Repository repository;

    /**
     * Opens a local git repository
     *
     * @param directory
     *            location of the git repositiory
     * @return directory of the git repository
     * @throws IOException
     */
    public File openRepository(File directory) throws IOException {
        Log.d(TAG, "openRepository called with: [directory: " + directory + "]");
        this.repository = new FileRepositoryBuilder().setGitDir(directory).readEnvironment().findGitDir().build();
        return this.repository.getDirectory();
    }

    /**
     * Clones a git repository
     * Creates arepsoitory and clone all branches from the origin repository into
     * the local repository.
     *
     * @param fromUrl
     *            url of the origin repository
     * @param directory
     *            where to clone the origin repository
     * @return directory of the git repository
     * @throws InvalidRemoteException
     * @throws TransportException
     * @throws IllegalStateException
     * @throws GitAPIException
     */
    public File cloneRepository(String fromUrl, File directory) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException {
        Log.d(TAG, "cloneRepository called with: [ url: " + fromUrl + ", diryctory: " + directory + "]");
        try (Git result = Git.cloneRepository().setURI(fromUrl).setBare(false).setCloneAllBranches(true).setDirectory(directory).call()) {
            this.repository = result.getRepository();
            return result.getRepository().getDirectory();
        }
    }

    /**
     * Clones a git repository
     * Creates arepsoitory and clone all branches from the origin repository into
     * the local repository.
     *
     * @param fromUrl
     *            url of the origin repository
     * @param directory
     *            where to clone the origin repository
     * @param userName
     * @param password
     * @return
     * @return directory of the git repository
     * @throws InvalidRemoteException
     * @throws TransportException
     * @throws IllegalStateException
     * @throws GitAPIException
     */
    public File cloneRepository(String fromUrl, File directory, String userName, String password) throws InvalidRemoteException, TransportException, IllegalStateException, GitAPIException {
        Log.d(TAG, "cloneRepository called with [ url: " + fromUrl + ", directory: " + directory + ", username:" + userName + ", password: " + password + "]");
        try (Git result = Git.cloneRepository()
            .setURI(fromUrl)
            .setBare(false)
            .setCloneAllBranches(true)
            .setDirectory(directory)
            .setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName, password))
            .call()) {
            return result.getRepository().getDirectory();
        }
    }

    /**
     * Returns all logs up to the given max value
     *
     * @param max
     *            logs to return
     * @return a list of logs
     */
    public List<GitLog> logs(int max) {
        Log.d(TAG, "logs called with: [max: " + max + "]");
        List<GitLog> logs = new LinkedList<>();
        try (Git git = new Git(repository)) {
            LogCommand command = git.log();
            command.setMaxCount(max);
            Iterable<RevCommit> commitLogs = command.call();

            try (RevWalk walk = new RevWalk(repository)) {
                for (RevCommit rev : commitLogs) {
                    RevCommit commit = walk.parseCommit(rev.getId());
                    logs.add(new GitLog(commit.getShortMessage(), commit.getFullMessage(), commit.getAuthorIdent().getName(), commit.getAuthorIdent().getEmailAddress(), commit
                        .getCommitterIdent().getWhen()));
                }
                walk.dispose();
            } catch (IOException exception) {
                throwAsUnchecked(exception);
            }

        } catch (GitAPIException exception) {
            throwAsUnchecked(exception);
        }
        return logs;
    }

    /**
     * Retuns stashed commits of the repository
     *
     * @return stashed sommits
     * @throws InvalidRefNameException
     * @throws GitAPIException
     */
    public Collection<RevCommit> stashes() throws InvalidRefNameException, GitAPIException {
        try (Git git = new Git(repository)) {
            return git.stashList().call();
        }
    }

    /**
     * Try to stash all files inclusive untracked files
     *
     * @throws GitAPIException
     */
    public void stash() throws GitAPIException {
        try (Git git = new Git(repository)) {
            git.stashCreate().setIncludeUntracked(true).call();
        }
    }

    /**
     * Delete stash from repository
     *
     * @param reference
     * @throws GitAPIException
     */
    public void dropStash(int reference) throws GitAPIException {
        try (Git git = new Git(repository)) {
            git.stashDrop().setStashRef(reference).call();
        }
    }

    /**
     * @param hash
     * @throws GitAPIException
     * @throws StashApplyFailureException
     * @throws NoHeadException
     * @throws WrongRepositoryStateException
     */
    public void applyStash(String hash) throws WrongRepositoryStateException, NoHeadException, StashApplyFailureException, GitAPIException {
        try (Git git = new Git(repository)) {
            StashApplyCommand apply = git.stashApply().setStashRef(hash);
            apply.setApplyUntracked(true);
            apply.call();
        }
    }

    /**
     * Returns stash position
     *
     * @param hash of the commit
     * @return stash position
     * @throws InvalidRefNameException
     * @throws GitAPIException
     */
    public int stashReference(String hash) throws InvalidRefNameException, GitAPIException {
        try (Git git = new Git(repository)) {
            return GitUtil.stashRef(git, hash);
        }
    }

    /**
     * Checks out the branch
     *
     * @param branchName
     *            name of a branch to be checked out
     * @param createBranch
     *            should be true for new and remote branches
     * @throws GitAPIException
     * @throws CheckoutConflictException
     * @throws InvalidRefNameException
     * @throws RefNotFoundException
     * @throws RefAlreadyExistsException
     */
    public void checkout(String branchName, boolean createBranch) throws RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, CheckoutConflictException, GitAPIException {
        Log.d(TAG, "checkout called with: [ branchName: " + branchName + ", createBranch:" + createBranch + " ]");
        try (Git git = new Git(repository)) {
            git.checkout().setName(branchName).setCreateBranch(createBranch).call();
        }
    }

    /**
     * Returns the list of local branches in the repository.
     *
     * @param limit
     *            The number of elements the branches should be limited to
     * @return list
     *         List of local branches
     * @throws IOException
     *             The reference space cannot be accessed.
     */
    public List<Reference> getBranches(int limit) throws IOException {
        return getRefs(Constants.R_HEADS, limit);
    }

    /**
     * Returns a list of references.
     *
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
    public List<Reference> getRefs(String ref, int limit) throws IOException {
        return repository.getRefDatabase()
            .getRefs(ref)
            .entrySet()
            .stream()
            .limit(limit < 0 ? Integer.MAX_VALUE : limit)
            .map(entry -> new Reference(entry.getValue().getName(), entry.getValue().toString()))
            .collect(Collectors.toList());
    }

    public void close() {
        if (repository != null) {
            repository.close();
        }
    }

    public Repository getRepository() {
        return repository;
    }

    public File getDirectory() {
        return this.repository.getDirectory();
    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

}
//if (directory != null) {
//    if (!directory.endsWith(".git") && !directory.endsWith(".git/")) {
//        directory = directory + "/.git";
//    }
//    if (!new File(directory).exists()) {
//        throw new IllegalStateException("Git repository not available at " + directory);
//    }
//    if (!directory.equals(this.directory)) {
//
//} else {
//    throw new IllegalStateException("Directory must not be null.");
//}
//File localPath = new File(directory, "");
//if (!localPath.exists()) {
//    localPath.mkdir();
//}
//
//if (!localPath.delete()) {
//    callback.onError(new GitException("Could not delete temporary file " + localPath));
//}
