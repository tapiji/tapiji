package org.eclipse.e4.tapiji.git.model;

import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;

public interface IGitServiceCallback<T> {

    void onSuccess(GitServiceResult<T> response);

    void onError(GitServiceException exception);
}
