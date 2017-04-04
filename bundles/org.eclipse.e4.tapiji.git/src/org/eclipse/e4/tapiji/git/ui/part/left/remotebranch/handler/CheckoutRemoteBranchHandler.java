package org.eclipse.e4.tapiji.git.ui.part.left.remotebranch.handler;


import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;


public class CheckoutRemoteBranchHandler {

    @Inject
    IEventBroker eventBroker;

    @Inject
    IGitService service;

    @Inject
    UISynchronize sync;

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) String name, final IMylynService mylyn, Shell shell) {
        System.out.println("EXECUTE CHECKOUT" + name);
        service.checkoutRemoteBranch(name);
    }
}
