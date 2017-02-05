package org.eclipse.e4.tapiji.git.core.api;


import java.io.File;
import java.nio.file.Path;
import java.util.List;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;


public interface IGitService {

    void cloneRepository(String url, String directory, IGitServiceCallback<File> callback);

    void commitAllChanges();

    void commitFile();

    void discardChanges();

    void findPropertyFiles(String directory, String filePattern, IGitServiceCallback<List<Path>> callback);

}
