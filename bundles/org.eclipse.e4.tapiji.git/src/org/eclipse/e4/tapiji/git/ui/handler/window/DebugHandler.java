package org.eclipse.e4.tapiji.git.ui.handler.window;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitRepository;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.git.model.file.GitFileStatus;
import org.eclipse.e4.tapiji.git.util.ListUtil;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.swt.widgets.Shell;


public class DebugHandler {

    @Execute
    public void debug(final IEclipseContext context, Shell shell, IGitService service) {

        service.uncommittedChanges(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitFileStatus, Set<String>>> result) {
                Log.d("STATES: ", result.getResult().toString());

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        service.stageAll(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> gitServiceResult) {
                Log.d("STAGE ALL OK", "OK");

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });
        service.uncommittedChanges(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitFileStatus, Set<String>>> result) {
                Log.d("STATES: ", result.getResult().toString());

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        service.unstageAll(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> gitServiceResult) {
                Log.d("UNSTAGE ALL OK", "OK");
            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });
        service.uncommittedChanges(new IGitServiceCallback<Map<GitFileStatus, Set<String>>>() {

            @Override
            public void onSuccess(GitServiceResult<Map<GitFileStatus, Set<String>>> result) {
                Log.d("STATES: ", result.getResult().toString());
            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        service.tags(new IGitServiceCallback<List<String>>() {

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

        service.showFileDiff(new IGitServiceCallback<Void>() {

            @Override
            public void onSuccess(GitServiceResult<Void> response) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(GitServiceException exception) {
                // TODO Auto-generated method stub

            }
        });

        List<GitRepository> repos = new ArrayList<>();
        List<GitRepository> wahh = ListUtil.unpackGitRepositoryList(null);
        Log.d("Repos unpacked: ", wahh.toString());
        repos.add(new GitRepository("https://github.com/tapiji/git.extension.test.git", "E:/cloni/.git"));
        String result = ListUtil.packGitRepositoryList(repos);
        Log.d("Repos packed: ", result);
    }
}
