package org.eclipse.e4.tapiji.git.ui.part.left.remotebranch.handler;


import java.io.IOException;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.util.UIUtil;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.swt.widgets.Shell;


public class CheckoutRemoteBranchHandler {

    @Execute
    public void execute(IGitService service, @Named(IServiceConstants.ACTIVE_SELECTION) String branchName, final EModelService modelService, MApplication app, Shell shell) {
        try {
            service.checkoutRemoteBranch(branchName);
        } catch (GitAPIException exception) {
            if (exception instanceof RefAlreadyExistsException) {
                try {
                    service.checkout(branchName);
                    UIUtil.setCurrentBranch(branchName, service, modelService, app);
                } catch (GitAPIException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
