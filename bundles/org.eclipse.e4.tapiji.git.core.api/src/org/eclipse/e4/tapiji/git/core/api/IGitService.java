package org.eclipse.e4.tapiji.git.core.api;


import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;


public interface IGitService {

    void cloneRepository(String url, String directory, IGitServiceCallback<File> callback);

    void findPropertyFiles(String directory, String filePattern, IGitServiceCallback<List<Path>> callback);

    void commitChanges(String gitDirectory, String summary, String description, IGitServiceCallback<Void> callback);

    void stageAll(String directory, IGitServiceCallback<Void> callback);

    void unstageAll(String directory, IGitServiceCallback<Void> callback);

    void uncommittedChanges(String directory, IGitServiceCallback<Map<GitStatus, Set<String>>> callback);

    void tags(String directory, IGitServiceCallback<List<String>> callback);

    void showFileDiff(String directory, IGitServiceCallback<Void> callback);

    void discardChanges(String directory, IGitServiceCallback<Void> callback);

}
