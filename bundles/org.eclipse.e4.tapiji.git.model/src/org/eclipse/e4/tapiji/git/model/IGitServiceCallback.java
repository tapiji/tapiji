package org.eclipse.e4.tapiji.git.model;


public interface IGitServiceCallback<T> {

    void onSuccess(GitServiceResult<T> gitServiceResult);

    void onError(GitServiceException exception);
}
