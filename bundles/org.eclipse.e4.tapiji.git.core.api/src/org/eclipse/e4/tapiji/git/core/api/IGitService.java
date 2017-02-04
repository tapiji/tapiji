package org.eclipse.e4.tapiji.git.core.api;


public interface IGitService {

    void cloneRepository();

    void commitAllChanges();

    void commitFile();

    void discardChanges();

}
