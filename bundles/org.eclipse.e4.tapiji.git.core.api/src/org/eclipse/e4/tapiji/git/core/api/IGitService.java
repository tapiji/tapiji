package org.eclipse.e4.tapiji.git.core.api;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.tapiji.git.model.GitFileStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;


public interface IGitService {

    void cloneRepository(String url, String directory, IGitServiceCallback<File> callback);

    void findPropertyFiles(String filePattern, IGitServiceCallback<List<Path>> callback);

    void commitChanges(String summary, String description, IGitServiceCallback<Void> callback);

    void stageAll(IGitServiceCallback<Void> callback);

    void unstageAll(IGitServiceCallback<Void> callback);

    void uncommittedChanges(IGitServiceCallback<Map<GitFileStatus, Set<String>>> callback);

    void tags(IGitServiceCallback<List<String>> callback);

    void showFileDiff(IGitServiceCallback<Void> callback);

    void discardChanges(IGitServiceCallback<Void> callback);

    void pushChanges(IGitServiceCallback<Void> callback);

    void pushChangesWithCredentials(String passowrd, String username, String directory, IGitServiceCallback<Void> callback);

    void mount(String directory) throws IOException;

    void unmount();

}
