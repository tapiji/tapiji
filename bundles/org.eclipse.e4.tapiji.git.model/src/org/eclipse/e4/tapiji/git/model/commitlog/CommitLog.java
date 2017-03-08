package org.eclipse.e4.tapiji.git.model.commitlog;


public class CommitLog {

    private String shortMessage;
    private String fullMessage;
    private String author;
    private String email;
    private int commitTime;

    public CommitLog(String shortMessage, String fullMessage, String author, String email, int commitTime) {
        super();
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
        this.author = author;
        this.email = email;
        this.commitTime = commitTime;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getFullMessage() {
        return fullMessage;
    }

    public String getAuthor() {
        return author;
    }

    public String getEmail() {
        return email;
    }

    public int getCommitTime() {
        return commitTime;
    }

    @Override
    public String toString() {
        return "CommitLog [shortMessage=" + shortMessage + ", fullMessage=" + fullMessage + ", author=" + author + ", email=" + email + ", commitTime=" + commitTime + "]";
    }

}
