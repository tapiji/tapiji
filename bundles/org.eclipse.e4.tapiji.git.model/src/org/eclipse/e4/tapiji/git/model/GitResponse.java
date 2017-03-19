package org.eclipse.e4.tapiji.git.model;


import java.util.Collections;
import java.util.List;
import org.eclipse.e4.tapiji.git.model.push.GitPushMessage;


public class GitResponse<T> {

    private T body;
    private List<GitPushMessage> messages;

    public GitResponse(T result) {
        this(result, Collections.emptyList());
    }

    public GitResponse(T body, List<GitPushMessage> messages) {
        super();
        this.body = body;
        this.messages = messages;
    }

    public T body() {
        return body;
    }

    public List<GitPushMessage> getMessages() {
        return messages;
    }
}
