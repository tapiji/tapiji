package org.eclipse.e4.tapiji.git.core.api;


import java.io.File;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;


public interface IGitService {

    void cloneRepository(String url, String directory, IGitServiceCallback<File> callback);

    void commitAllChanges();

    void commitFile();

    void discardChanges();

}
