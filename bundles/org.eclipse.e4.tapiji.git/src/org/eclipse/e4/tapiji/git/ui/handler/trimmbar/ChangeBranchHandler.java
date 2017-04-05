package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import java.io.IOException;
import java.util.List;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
import org.eclipse.e4.tapiji.git.ui.util.GitUtil;
import org.eclipse.e4.tapiji.git.ui.util.UIUtil;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.swt.widgets.Shell;


public class ChangeBranchHandler {

    @AboutToShow
    public void aboutToShow(IGitService service, final List<MMenuElement> items, final EModelService modelService, MApplication app) {
        service.localBranches().stream().forEach(branch -> {
            MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
            dynamicItem.setLabel(branch.getName());
            dynamicItem.setContributionURI(UIEventConstants.MENU_CHANGE_BRANCH_CONTRIBUTION_URI);
            dynamicItem.setContainerData(branch.getName());
            dynamicItem.setTooltip(branch.getName());
            dynamicItem.setType(ItemType.PUSH);
            items.add(dynamicItem);
        });
    }

    @Execute
    public void execute(IGitService service, MDirectMenuItem item, final EModelService modelService, MApplication app, Shell shell) throws IOException {
        String branchName = GitUtil.parseBranchName(item.getContainerData());
        try {
            service.checkout(branchName);
            UIUtil.setCurrentBranch(branchName, service, modelService, app);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return prefs.getSelectedRepository() != null;
    }
}
