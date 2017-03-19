package org.eclipse.e4.tapiji.git.model.exception;


public class GitException extends Exception {

    private static final long serialVersionUID = 1L;
    private int code;

    public GitException(String message, Throwable cause) {
        super(message, cause);
    }

    public GitException(String message) {
        super(message);
    }

    public GitException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int code() {
        return code;
    }
}
