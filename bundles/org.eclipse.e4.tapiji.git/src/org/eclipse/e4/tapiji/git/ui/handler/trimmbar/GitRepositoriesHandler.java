package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import java.util.List;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;


public class GitRepositoriesHandler {

    private static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.e4.tapiji.git/org.eclipse.e4.tapiji.git.ui.handler.trimmbar.GitRepositoriesHandler";

    @AboutToShow
    public void aboutToShow(final List<MMenuElement> items, final EModelService modelService, final IGlossaryService glossaryService, final Preferences prefs) {
        List<String> repositories = prefs.getRepositories();
        repositories.forEach(repository -> {
            MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
            dynamicItem.setLabel(repositoryName(repository));
            dynamicItem.setContributionURI(CONTRIBUTION_URI);
            dynamicItem.setContainerData(repository);
            dynamicItem.setType(ItemType.PUSH);
            items.add(dynamicItem);
        });
    }

    @Execute
    public void execute() {

    }

    private String repositoryName(String directory) {
        String ss = " E:/cloni/.git";

        return null;
    }
}
