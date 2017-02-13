package org.eclipse.e4.tapiji.git.model;


import java.util.Collections;
import java.util.List;
import org.eclipse.e4.tapiji.git.model.push.GitPushMessage;


public class GitServiceResult<T> {

    private T result;
    private List<GitPushMessage> messages;

    public GitServiceResult(T result) {
        this(result, Collections.emptyList());
    }

    public GitServiceResult(T result, List<GitPushMessage> messages) {
        super();
        this.result = result;
        this.messages = messages;
    }

    public T getResult() {
        return result;
    }

    public List<GitPushMessage> getMessages() {
        return messages;
    }
}
