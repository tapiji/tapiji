package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitRepository;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class ChangeRepositoryHandler {

 

    @Inject
    IGitService service;

    @Inject
    IEventBroker eventBroker;

    @Inject
    Preferences prefs;

    @AboutToShow
    public void aboutToShow(final List<MMenuElement> items, final EModelService modelService, final IGlossaryService glossaryService) {
        List<GitRepository> repositories = prefs.getRepositories();
        repositories.forEach(repository -> {
            MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
            dynamicItem.setLabel(repository.getName());
            dynamicItem.setContributionURI(UIEventConstants.MENU_CHANGE_REPOSITORY_CONTRIBUTION_URI);
            dynamicItem.setContainerData(JsonParserUtil.parseGitRepositoryString(repository));
            dynamicItem.setTooltip(repository.getDirectory());
            dynamicItem.setType(ItemType.PUSH);
            items.add(dynamicItem);
        });
    }

    @SuppressWarnings("restriction")
    @Execute
    public void execute(MDirectMenuItem item, final EModelService modelService, MApplication app, Shell shell) {
        try {
            GitRepository selectedRepository = JsonParserUtil.parseGitRepository(item.getContainerData());
            service.mount(selectedRepository.getDirectory());
            prefs.setSelectedRepository(selectedRepository);

            MUIElement dropDownMenu = modelService.find(UIEventConstants.MENU_CHANGE_REPOSITORY_ID, app);
            if (dropDownMenu instanceof HandledToolItemImpl) {
                ((HandledToolItemImpl) dropDownMenu).setLabel(selectedRepository.getName());
            }

            eventBroker.post(UIEventConstants.TOPIC_RELOAD_PROPERTY_VIEW, "");
            eventBroker.post(UIEventConstants.TOPIC_RELOAD_UNSTAGE_VIEW, "");
            eventBroker.post(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW, "");
        } catch (Exception exception) {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        }
    }
}
