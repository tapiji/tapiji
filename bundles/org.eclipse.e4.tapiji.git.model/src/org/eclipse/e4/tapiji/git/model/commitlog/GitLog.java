package org.eclipse.e4.tapiji.git.model.commitlog;


import java.util.Date;


public class GitLog {

    private String shortMessage;
    private String fullMessage;
    private String author;
    private String email;
    private Date commitTime;

    public GitLog(String shortMessage, String fullMessage, String author, String email, Date commitTime) {
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

    public Date getCommitTime() {
        return commitTime;
    }

    @Override
    public String toString() {
        return "GitLog [shortMessage=" + shortMessage + ", fullMessage=" + fullMessage + ", author=" + author + ", email=" + email + ", commitTime=" + commitTime + "]";
    }

}
