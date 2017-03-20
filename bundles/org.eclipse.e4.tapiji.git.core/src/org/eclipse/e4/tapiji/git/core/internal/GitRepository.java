package org.eclipse.e4.tapiji.git.core.internal;


import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.internal.util.GitUtil;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.StashApplyCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.StashApplyFailureException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.NoWorkTreeException;
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
     * Record changes to the repository
     *
     * @param summary short summary
     * @param description describing the changed
     */
    public void commit(String summary, String description) {
        try (Git git = new Git(repository)) {
            git.commit().setMessage(summary + "\n\n" + description).call();
        } catch (GitAPIException exception) {
            throwAsUnchecked(exception);
        }
    }

    /**
     * Telling where the working-tree, the index and the current HEAD differ
     * from each other. Map of different file states {@link GitFileStatus}
     *
     * @return map of file states
     */
    public Map<GitFileStatus, Set<String>> states() {
        Map<GitFileStatus, Set<String>> states = new HashMap<>();
        try (Git git = new Git(repository)) {
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
        return states;
    }

    /**
     * Discard a local changes
     */
    public void discardChanges() {
        try (Git git = new Git(repository)) {
            git.reset().setMode(ResetType.HARD).setRef(Constants.HEAD).call();
            git.clean().setCleanDirectories(true).setForce(true).call();
        } catch (NoWorkTreeException | GitAPIException exception) {
            throwAsUnchecked(exception);
        }
    }

    /**
     * Add all file to the index
     */
    public void stageAll() {
        try (Git git = new Git(repository)) {
            Status status = git.status().call();
            stageUntrackedFiles(git, status);
            stageMissingFiles(git, status);
        } catch (GitAPIException exception) {
            throwAsUnchecked(exception);
        }
    }

    private void stageMissingFiles(Git git, Status status) throws NoFilepatternException, GitAPIException {
        RmCommand rm = git.rm();
        if (!status.getMissing().isEmpty()) {
            status.getMissing().forEach(file -> rm.addFilepattern(file));
            rm.call();
        }
    }

    private void stageUntrackedFiles(Git git, Status status) throws NoFilepatternException, GitAPIException {
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
     * Apply a stashed commit
     *
     * @param hash from the commit
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
    public List<Reference> tags(int limit) throws IOException {
        return GitUtil.getRefs(repository, Constants.R_TAGS, limit);
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
    public List<Reference> branches(int limit) throws IOException {
        return GitUtil.getRefs(repository, Constants.R_HEADS, limit);
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
    public List<Reference> stashes(int limit) throws IOException {
        return GitUtil.getRefs(repository, Constants.R_STASH, limit);
    }

    /**
     * CLose current repository
     */
    public void close() {
        if (repository != null) {
            repository.close();
        }
    }

    /**
     * Return the remote url
     *
     * @return url
     */
    public String getUrl() {
        return repository.getConfig().getString("remote", "origin", "url");
    }

    /**
     * Returns opened or cloned repository
     *
     * @return repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Return the directory of the repository
     *
     * @return file
     */
    public File getDirectory() {
        return this.repository.getDirectory();
    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }
}
