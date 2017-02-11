package org.eclipse.e4.tapiji.git.model;


import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;


public class GitFile {

    private final String name;
    private final GitStatus status;
    private String image;

    public GitFile(String name, GitStatus status) {
        super();
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public GitStatus getStatus() {
        return status;
    }

    public String getImage() {
        switch (status) {
            case ADDED:
                return TapijiResourceConstants.IMG_GIT_ADDED;
            case MISSING:
                return TapijiResourceConstants.IMG_GIT_MISSING;
            case MODIFIED:
                return TapijiResourceConstants.IMG_GIT_MODIFIED;
            case UNTRACKED:
                return TapijiResourceConstants.IMG_GIT_UNTRACKED;
            case CHANGED:
                return TapijiResourceConstants.IMG_GIT_MODIFIED;
            case REMOVED:
                return TapijiResourceConstants.IMG_GIT_MISSING;
            case UNCOMMITTED:
                break;
            case UNTRACKED_FOLDERS:
                break;
            default:
                break;
        }
        return image;
    }

    @Override
    public String toString() {
        return "GitFile [name=" + name + ", image=" + image + ", status=" + status + "]";
    }
}
