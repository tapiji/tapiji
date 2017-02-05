package org.eclipse.e4.tapiji.git.core.internal;


import java.io.File;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceException;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;


public class GitService implements IGitService {

    private static final String TAG = GitService.class.getSimpleName();

    @Override
    public void cloneRepository(String url, String directory, IGitServiceCallback<File> callback) {
        Log.d(TAG, "cloneRepository(" + url + " to " + directory + ")");

        File localPath = new File(directory, "");
        if (!localPath.exists()) {
            localPath.mkdir();
        }

        if (!localPath.delete()) {
            callback.onError(new GitServiceException("Could not delete temporary file " + localPath));
        }
        try (Git result = Git.cloneRepository().setURI(url).setDirectory(localPath).call()) {
            callback.onSuccess(new GitServiceResult<File>(result.getRepository().getDirectory()));
        } catch (InvalidRemoteException e) {
            callback.onError(new GitServiceException(e.getMessage(), e.getCause()));
        } catch (TransportException e) {
            callback.onError(new GitServiceException(e.getMessage(), e.getCause()));
        } catch (GitAPIException e) {
            callback.onError(new GitServiceException(e.getMessage(), e.getCause()));
        }

    }

    @Override
    public void commitAllChanges() {
        // TODO Auto-generated method stub
        Log.d("WAHHH", "COMMIT ALL CHANGES");
    }

    @Override
    public void commitFile() {
        // TODO Auto-generated method stub

    }

    @Override
    public void discardChanges() {
        // TODO Auto-generated method stub
    }

}
