package org.eclipse.e4.tapiji.git.model;


public class GitServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public GitServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitServiceException(String message) {
        super(message);
    }

}
