package org.eclipse.e4.tapiji.git.ui.handler.window;


import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.ui.dialog.CloneRepositoryDialog;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;


public class CloneRepoHandler {

    @Execute
    public void exec(final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        CloneRepositoryDialog.show(context, shell);

        /*
         * List<GitRepository> repos = new ArrayList<>();
         * List<GitRepository> wahh = ListUtil.unpackGitRepositoryList(Preferences.TEST_REPO);
         * Log.d("asdasd", wahh.toString());
         * repos.add(new GitRepository("https://github.com/tapiji/git.extension.test.git", "E:/cloni/.git"));
         * String result = ListUtil.packGitRepositoryList(repos);
         * Log.d("asdasd", result);
         */
    }

}
