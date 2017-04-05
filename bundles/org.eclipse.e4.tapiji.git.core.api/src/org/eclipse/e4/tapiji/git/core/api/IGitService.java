package org.eclipse.e4.tapiji.git.core.api;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.UserProfile;
import org.eclipse.e4.tapiji.git.model.commitlog.GitLog;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;
import org.eclipse.jgit.api.errors.GitAPIException;


public interface IGitService {

    void cloneRepository(String url, String directory, IGitServiceCallback<File> callback);

    void findPropertyFiles(String filePattern, IGitServiceCallback<List<PropertyDirectory>> callback);

    void commitChanges(String summary, String description, IGitServiceCallback<Void> callback);

    void stageAll(IGitServiceCallback<Void> callback);

    void unstageAll(IGitServiceCallback<Void> callback);

    void fileStates(IGitServiceCallback<Map<GitFileStatus, Set<String>>> callback);

    void tags(IGitServiceCallback<List<Reference>> callback);

    void showFileDiff(IGitServiceCallback<Void> callback);

    void discardChanges(IGitServiceCallback<Void> callback);

    void pushAll(IGitServiceCallback<Void> callback);

    void pushChangesWithCredentials(String passowrd, String username, String directory, IGitServiceCallback<Void> callback);

    void popFirst(IGitServiceCallback<Void> callback);

    void fetch(IGitServiceCallback<String> callback);

    void deleteFile(File file);

    void applyStash(String hash, IGitServiceCallback<Void> callback);

    void popStash(String hash, IGitServiceCallback<Void> callback);

    void dropStash(String hash, IGitServiceCallback<Void> callback);

    void stashes(IGitServiceCallback<List<CommitReference>> callback);

    void stash(IGitServiceCallback<Void> callback);

    void fileContent(String hash, IGitServiceCallback<DiffFile> callback);

    List<Reference> localBranches();

    void remoteBranches(IGitServiceCallback<List<Reference>> callback);

    void checkout(String branch) throws GitAPIException;

    void pullWithRebase();

    void pullWithMerge();

    void mergeStatus(IGitServiceCallback<Void> callback);

    void fileMergeDiff(String name, GitFileStatus conflict, IGitServiceCallback<DiffFile> iGitServiceCallback);

    void stageMergedFile(String fileName, IGitServiceCallback<Void> callback);

    void logs(IGitServiceCallback<List<GitLog>> callback);

    void profile(IGitServiceCallback<UserProfile> callback);

    void saveProfile(IGitServiceCallback<Void> callback, UserProfile profile);

    String getUrl();

    void setPublicKeyPath(String keyPath);

    void setPrivateKeyPath(String keyPath);

    void openRepository(String directory) throws IOException;

    File getDirectory();

    void dispose();

    void unmount();

    void checkoutRemoteBranch(String name) throws GitAPIException;

    void pull(IGitServiceCallback<Boolean> callback);
}
