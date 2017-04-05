package org.eclipse.e4.tapiji.git.core.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.eclipse.e4.tapiji.git.core.internal.GitRepository;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class GitRepositoryTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Git remote;
    private Git local;

    @Before
    public void beforeEach() throws Exception {
        remote = Git.init().setDirectory(tempFolder.newFolder("remote")).call();
        remote.commit().setMessage("Initial commit").call();
    }

    @Test
    public void cloneRepositoryTest() throws Exception {
        GitRepository repository = new GitRepository();
        File localRepo = getLocalRepositoryLocation();
        File file = repository.cloneRepository(fileUri(remote), localRepo);

        assertEquals(file, new File(localRepo.toString(), ".git"));
    }

    @Test
    public void logsTest() throws Exception {
        GitRepository repo = repository();
        List<GitLog> logs = repo.logs(10);

        assertEquals(1, logs.size());
        assertEquals("Initial commit", logs.get(0).getFullMessage());
    }

    @Test
    public void openRepositoryTest() throws Exception {
        GitRepository repo = new GitRepository();
        File localRepo = getLocalRepositoryLocation();
        repo.cloneRepository(fileUri(remote), localRepo);
        repo.close();

        repo = new GitRepository();
        File openFile = repo.openRepository(localRepo);

        assertEquals(localRepo, openFile);
    }

    @Test
    public void checkoutBranchTest() throws Exception {
        Ref branch = remote.branchCreate().setName("test").call();

        GitRepository repo = repository();
        repo.checkout("test", true);
        List<Reference> branches = repo.localBranches(10);

        assertTrue(branches.contains(new Reference(branch.getName(), branch.getTarget().toString())));
    }

    @Test
    public void stashTest() throws Exception {
        GitRepository repo = repository();

        createFile("git.txt", repo.getRepository());
        repo.stash();

        Collection<RevCommit> stashes = repo.stashes();
        assertEquals(1, stashes.size());

        createFile("git2.txt", repo.getRepository());

        repo.stash();

        stashes = repo.stashes();
        assertEquals(2, stashes.size());
    }

    @Test
    public void dropStashTest() throws Exception {
        GitRepository repo = repository();

        createFile("git.txt", repo.getRepository());
        repo.stash();

        Collection<RevCommit> stashes = repo.stashes();
        assertEquals(1, stashes.size());

        Optional<RevCommit> stashToDelete = stashes.stream().findAny();
        repo.dropStash(repo.stashReference(stashToDelete.get().getName()));

        stashes = repo.stashes();
        assertEquals(0, stashes.size());
        assertFalse(stashes.contains(stashToDelete));
    }

    @Test
    public void popStashTest() {

    }

    @Test
    public void applyStashTest() throws Exception {
        GitRepository repo = repository();

        createFile("git.txt", repo.getRepository());
        repo.stash();

        Collection<RevCommit> stashes = repo.stashes();
        assertEquals(1, stashes.size());

        Optional<RevCommit> stashToApply = stashes.stream().findAny();
        assertTrue(stashToApply.isPresent());

        repo.applyStash(stashToApply.get().getName());

        stashes = repo.stashes();
        assertEquals(1, stashes.size());

        Optional<RevCommit> stash = stashes.stream().findFirst();
        assertTrue(stash.isPresent());

        assertEquals(stash.get().getName(), stashToApply.get().getName());
    }

    @Test
    public void stateUntrackedTest() throws Exception {
        GitRepository repo = repository();
        createFile("git.txt", repo.getRepository());

        Map<GitFileStatus, Set<String>> fileStates = repo.states();

        Set<String> untracked = fileStates.get(GitFileStatus.UNTRACKED);
        assertEquals(1, untracked.size());
    }

    @Test
    public void stateAddedTest() throws Exception {
        GitRepository repo = repository();

        createFile("git.txt", repo.getRepository());

        Map<GitFileStatus, Set<String>> fileStates = repo.states();

        Set<String> untracked = fileStates.get(GitFileStatus.UNTRACKED);
        assertEquals(1, untracked.size());

        repo.stageAll();

        fileStates = repo.states();
        untracked = fileStates.get(GitFileStatus.UNTRACKED);
        assertEquals(0, untracked.size());

        Set<String> added = fileStates.get(GitFileStatus.ADDED);
        assertEquals(1, added.size());
    }

    public void commitTest() throws Exception {
        GitRepository repo = repository();

        createFile("commit_message.txt", repo.getRepository());

        repo.stageAll();

        repo.commit("Test commit", "Commit message");

        List<GitLog> logs = repo.logs(10);
        assertEquals(2, logs.size());
        assertEquals("Test commit", logs.get(1).getShortMessage());
        assertEquals("Commit message", logs.get(1).getFullMessage());
    }

    @Test
    public void discardChangesTest() throws Exception {
        GitRepository repo = repository();
        createFile("git.txt", repo.getRepository());

        Map<GitFileStatus, Set<String>> fileStates = repo.states();

        Set<String> untracked = fileStates.get(GitFileStatus.UNTRACKED);
        assertEquals(1, untracked.size());

        repo.discardChanges();

        fileStates = repo.states();
        untracked = fileStates.get(GitFileStatus.UNTRACKED);
        assertEquals(0, untracked.size());
    }

    private GitRepository repository() throws Exception {
        GitRepository repo = new GitRepository();
        File localRepo = getLocalRepositoryLocation();
        repo.cloneRepository(fileUri(remote), localRepo);
        return repo;
    }

    private String fileUri(Git git) {
        return "file://" + git.getRepository().getWorkTree().getAbsolutePath();
    }

    private String getRemoteUrl() throws IOException {
        return remote.getRepository().getDirectory().getCanonicalPath();
    }

    private File getLocalRepositoryLocation() throws IOException {
        return tempFolder.newFolder("local");
    }

    private File createFile(String name, Repository repository) throws IOException {
        final File file = new File(repository.getWorkTree(), name);
        file.createNewFile();
        return file;
    }

    @After
    public void tearDown() {
        if (remote != null) {
            remote.close();
        }
        if (local != null) {
            local.close();
        }
    }
}
