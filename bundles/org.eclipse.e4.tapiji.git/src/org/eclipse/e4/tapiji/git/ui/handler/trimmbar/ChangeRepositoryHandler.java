package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitRepository;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.tapiji.git.util.JsonParserUtil;
import org.eclipse.e4.tapiji.logger.Log;
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


@SuppressWarnings("restriction")
public class ChangeRepositoryHandler {

    @Inject
    IGitService service;

    @Inject
    IEventBroker eventBroker;

    @Inject
    Preferences prefs;

    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    @AboutToShow
    public void aboutToShow(final List<MMenuElement> items, final EModelService modelService) {
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
            setBranch(modelService, app);
            eventBroker.post(UIEventConstants.TOPIC_RELOAD_UNSTAGE_VIEW, "");
            eventBroker.post(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW, "");

            executeCommand();
        } catch (Exception exception) {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        }
    }

    private void setBranch(EModelService modelService, MApplication app) throws IOException {
        List<Reference> branches = service.branches();
        Optional<Reference> foundMaster = branches.stream().filter(branch -> branch.getName().toLowerCase().contains("master")).findAny();
        MUIElement dropDownMenu = modelService.find(UIEventConstants.MENU_CHANGE_BRANCH_ID, app);
        if (dropDownMenu instanceof HandledToolItemImpl) {
            if (foundMaster.isPresent()) {
                ((HandledToolItemImpl) dropDownMenu).setLabel(foundMaster.get().getName());
            } else if (branches.size() >= 1) {
                ((HandledToolItemImpl) dropDownMenu).setLabel(branches.get(0).getName());
            } else {
                Log.i("BRANCH", "NO BRANCH AVAILABLE");
            }
        }
    }

    private void executeCommand() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("org.eclipse.e4.tapiji.git.commandparameter.perspective", "org.eclipse.e4.tapiji.git.perspective.git");
        Command cmd = commandService.getCommand("org.eclipse.e4.tapiji.git.command.perspective.git");
        ParameterizedCommand pCmd = ParameterizedCommand.generateCommand(cmd, parameters);
        if (handlerService.canExecute(pCmd)) {
            handlerService.executeHandler(pCmd);
        }
    }

    @CanExecute
    public boolean canExecute(Preferences prefs) {
        return !prefs.getRepositories().isEmpty();
    }
}
