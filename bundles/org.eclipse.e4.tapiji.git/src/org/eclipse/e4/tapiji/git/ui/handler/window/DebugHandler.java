package org.eclipse.e4.tapiji.git.ui.handler.window;


import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.GitStatus;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.swt.widgets.Shell;


public class DebugHandler {

    @Execute
    public void debug(final IEclipseContext context, Shell shell, IGitService service) {

        service.uncommittedChanges("E:/cloni/.git", new IGitServiceCallback<Map<GitStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitStatus, Set<String>>> result) {
                Log.d("STATES: ", result.getResult().toString());

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        service.stageAll("E:/cloni/.git", new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> gitServiceResult) {
                Log.d("STAGE ALL OK", "OK");

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });
        service.uncommittedChanges("E:/cloni/.git", new IGitServiceCallback<Map<GitStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitStatus, Set<String>>> result) {
                Log.d("STATES: ", result.getResult().toString());

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        service.unstageAll("E:/cloni/.git", new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> gitServiceResult) {
                Log.d("UNSTAGE ALL OK", "OK");
            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });
        service.uncommittedChanges("E:/cloni/.git", new IGitServiceCallback<Map<GitStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitStatus, Set<String>>> result) {
                Log.d("STATES: ", result.getResult().toString());
            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        service.tags("E:/cloni/.git", new IGitServiceCallback<List<String>>() {

            @Override
            public void onSuccess(GitServiceResult<List<String>> response) {
                // TODO Auto-generated method stub
                Log.d("TAGS: ", response.getResult().toString());
            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        service.showFileDiff("E:/cloni/.git", new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

    }
}
