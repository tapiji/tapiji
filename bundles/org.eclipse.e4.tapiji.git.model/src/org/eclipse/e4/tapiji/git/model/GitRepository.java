package org.eclipse.e4.tapiji.git.model;


public class GitRepository {

    private String url;
    private String directory;

    public GitRepository(String url, String directory) {
        super();
        this.url = url;
        this.directory = directory;
    }

    public String getUrl() {
        return url;
    }

    public String getDirectory() {
        return directory;
    }
}
