package org.eclipse.e4.tapiji.git.core.internal;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.core.internal.util.GitUtil;
import org.eclipse.e4.tapiji.git.model.GitResponse;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;


@Creatable
public class GitPush {

    private final <T> BiConsumer<GitResponse<T>, Throwable> onCompleteAsync(IGitServiceCallback<T> callback) {
        return (result, exception) -> {
            if (exception == null) {
                callback.onSuccess(result);
            } else {
                callback.onError(new GitException(exception.getMessage(), exception.getCause()));
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <E extends Exception> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }

    public void pushAll(Git git, String privateKeyPath, IGitServiceCallback<Void> callback, ExecutorService executorService) {
        CompletableFuture.supplyAsync(() -> {
            Iterable<PushResult> results = null;
            try {
                PushCommand command = git.push();
                command.setRemote("origin");
                command.setPushAll();
                command.setPushTags();
                if (privateKeyPath != null && !privateKeyPath.isEmpty()) {
                    //  command.setTransportConfigCallback(transportConfig);
                }
                results = git.push().setRemote("origin").setPushAll().setPushTags().call();
            } catch (GitAPIException exception) {
                throwAsUnchecked(exception);
            }
            return new GitResponse<Void>(null, GitUtil.parsePushResults(results));
        }, executorService).whenCompleteAsync(onCompleteAsync(callback));
    }
}
