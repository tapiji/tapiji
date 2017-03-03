package org.eclipse.e4.tapiji.git.core.api;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.tapiji.git.model.CommitReference;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.diff.DiffFile;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.model.property.PropertyDirectory;


public interface IGitService {

    void cloneRepository(String url, String directory, IGitServiceCallback<File> callback);

    void findPropertyFiles(String filePattern, IGitServiceCallback<List<PropertyDirectory>> callback);

    void commitChanges(String summary, String description, IGitServiceCallback<Void> callback);

    void stageAll(IGitServiceCallback<Void> callback);

    void unstageAll(IGitServiceCallback<Void> callback);

    void uncommittedChanges(IGitServiceCallback<Map<GitFileStatus, Set<String>>> callback);

    void tags(IGitServiceCallback<List<Reference>> callback);

    void showFileDiff(IGitServiceCallback<Void> callback);

    void discardChanges(IGitServiceCallback<Void> callback);

    void pushAll(IGitServiceCallback<Void> callback);

    void pushChangesWithCredentials(String passowrd, String username, String directory, IGitServiceCallback<Void> callback);

    void popFirst(IGitServiceCallback<Void> callback);

    void fetch(IGitServiceCallback<Void> callback);

    void mount(String directory) throws IOException;

    void deleteFile(File file);

    void unmount();

    void applyStash(String hash, IGitServiceCallback<Void> callback);

    void popStash(String hash, IGitServiceCallback<Void> callback);

    void dropStash(String hash, IGitServiceCallback<Void> callback);

    void stashes(IGitServiceCallback<List<CommitReference>> callback);

    void stash(IGitServiceCallback<Void> callback);

    void fileContent(String hash, IGitServiceCallback<DiffFile> callback);

    void branches(IGitServiceCallback<List<Reference>> callback);

    void fetchAll(IGitServiceCallback<String> callback);

    List<Reference> branches() throws IOException;

    void checkout(String branch);

    File getDirectory();

    void dispose();

    void pullWithRebase();

    void pullWithMerge();

    void pullFastForward(IGitServiceCallback<Void> callback);

    void mergeStatus(IGitServiceCallback<Void> callback);

    void fileMergeDiff(String name, GitFileStatus conflict, IGitServiceCallback<DiffFile> iGitServiceCallback);

    void stageFile(String fileName, IGitServiceCallback<Void> callback);

}
