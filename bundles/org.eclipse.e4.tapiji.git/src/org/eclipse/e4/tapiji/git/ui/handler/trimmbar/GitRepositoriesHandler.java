package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import java.util.List;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.model.GitRepository;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.git.util.JsonParserUtil;
import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;


public class GitRepositoriesHandler {

    private static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.e4.tapiji.git/org.eclipse.e4.tapiji.git.ui.handler.trimmbar.GitRepositoriesHandler";
    private static final String MENU_REPOSITORY_ID = "org.eclipse.e4.tapiji.git.handledtoolitem.selected.repository";

    @AboutToShow
    public void aboutToShow(final List<MMenuElement> items, final EModelService modelService, final IGlossaryService glossaryService, final Preferences prefs) {
        List<GitRepository> repositories = prefs.getRepositories();
        repositories.forEach(repository -> {
            MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
            dynamicItem.setLabel(repository.getName());
            dynamicItem.setContributionURI(CONTRIBUTION_URI);
            dynamicItem.setContainerData(JsonParserUtil.parseGitRepositoryString(repository));
            dynamicItem.setType(ItemType.PUSH);
            items.add(dynamicItem);
        });
    }

    @SuppressWarnings("restriction")
    @Execute
    public void execute(MDirectMenuItem item, final EModelService modelService, MApplication app) {
        GitRepository selectedRepository = JsonParserUtil.parseGitRepository(item.getContainerData());
        MUIElement ssd = modelService.find(MENU_REPOSITORY_ID, app);
        if (ssd instanceof HandledToolItemImpl) {
            ((HandledToolItemImpl) ssd).setLabel(selectedRepository.getName());
        }
    }
}
