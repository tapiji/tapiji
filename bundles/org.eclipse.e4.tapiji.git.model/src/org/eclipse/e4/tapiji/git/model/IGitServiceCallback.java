package org.eclipse.e4.tapiji.git.model;

import org.eclipse.e4.tapiji.git.model.exception.GitException;

public interface IGitServiceCallback<T> {

    void onSuccess(GitResponse<T> response);

    void onError(GitException exception);
}
