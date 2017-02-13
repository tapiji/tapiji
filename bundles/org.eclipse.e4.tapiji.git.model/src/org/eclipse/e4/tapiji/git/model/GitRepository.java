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

    public String getName() {
        return parseGitName(url);
    }

    private String parseGitName(String repository) {
        int lastPos = repository.lastIndexOf('/');
        return repository.substring(lastPos + 1, repository.length()).replace(".git", "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((directory == null) ? 0 : directory.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GitRepository other = (GitRepository) obj;
        if (directory == null) {
            if (other.directory != null) return false;
        } else if (!directory.equals(other.directory)) return false;
        if (url == null) {
            if (other.url != null) return false;
        } else if (!url.equals(other.url)) return false;
        return true;
    }
}
