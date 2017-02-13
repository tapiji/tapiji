package org.eclipse.e4.tapiji.git.model.push;


public class GitPushMessage {

    private GitRemoteStatus remoteStatus;
    private String remoteName;
    private String localName;

    public GitPushMessage() {
        super();
    }

    public GitPushMessage(GitRemoteStatus remoteStatus, String remoteName, String localName) {
        super();
        this.remoteStatus = remoteStatus;
        this.remoteName = remoteName;
        this.localName = localName;
    }

    public GitRemoteStatus getRemoteStatus() {
        return remoteStatus;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public String getLocalName() {
        return localName;
    }

    public void setRemoteStatus(GitRemoteStatus branchStatus) {
        this.remoteStatus = branchStatus;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String toString() {
        return "GitResultMessage [branchStatus=" + remoteStatus + ", remoteName=" + remoteName + ", localName=" + localName + "]";
    }
}
