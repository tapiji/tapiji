package org.eclipse.e4.tapiji.git.ui.handler.window;


import java.io.File;
import java.io.IOException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;


public class CloneRepoHandler {

    @Execute
    public void exec() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        File localPath = File.createTempFile("TestGitRepository", "");
        if (!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }

        String REMOTE_URL = "https://github.com/tapiji/tapiji.github.io.git";
        // then clone
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        try (Git result = Git.cloneRepository().setURI(REMOTE_URL).setDirectory(localPath).call()) {
            // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
            System.out.println("Having repository: " + result.getRepository().getDirectory());
        }
    }
}
