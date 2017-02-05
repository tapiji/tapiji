package org.eclipse.e4.tapiji.git.model;


public class GitServiceResult<T> {

    private T result;

    public GitServiceResult(T result) {
        super();
        this.result = result;
    }

    public T getResult() {
        return result;
    }
}
