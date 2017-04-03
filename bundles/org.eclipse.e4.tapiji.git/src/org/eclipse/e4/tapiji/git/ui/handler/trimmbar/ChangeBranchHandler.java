package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Shell;


public class ChangeBranchHandler {

    @Inject
    IGitService service;

    @Inject
    IEventBroker eventBroker;

    @Inject
    UISynchronize sync;

    @Inject
    Preferences prefs;

    @AboutToShow
    public void aboutToShow(final List<MMenuElement> items, final EModelService modelService, MApplication app) {

        //        try {
        //            service.branches().stream().forEach(branch -> {
        //                MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
        //                dynamicItem.setLabel(branch.getName());
        //                dynamicItem.setContributionURI(UIEventConstants.MENU_CHANGE_BRANCH_CONTRIBUTION_URI);
        //                dynamicItem.setContainerData(branch.getName());
        //                dynamicItem.setTooltip(branch.getName());
        //                dynamicItem.setType(ItemType.PUSH);
        //                items.add(dynamicItem);
        //
        //            });
        //        } catch (IOException e) {
        //
        //        }
    }

    @Execute
    public void execute(MDirectMenuItem item, final EModelService modelService, MApplication app, Shell shell) {
        service.checkout(item.getContainerData());
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
