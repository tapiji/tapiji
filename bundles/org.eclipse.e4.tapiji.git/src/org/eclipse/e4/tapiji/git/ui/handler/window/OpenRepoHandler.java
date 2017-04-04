package org.eclipse.e4.tapiji.git.ui.handler.window;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Shell;


@SuppressWarnings("restriction")
public class OpenRepoHandler {

    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    @Inject
    IGitService service;

    @Inject
    IEventBroker eventBroker;

    @Inject
    Preferences prefs;

    @Inject
    UISynchronize sync;

    @Execute
    public void exec(final EModelService modelService, MApplication app, final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        prefs.addRepository("pesto", "E:/cloni");
        executeCommand();
        try {
            setBranch(modelService, app);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //        DirectoryDialog dialog = new DirectoryDialog(shell);
        //        String result = dialog.open();
        //        if (result != null) {
        //            try {
        //                System.out.println("REPO: " + result);
        //                service.mount(result);
        //                prefs.addRepository(service.getUrl(), result);
        //                executeCommand();
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
    }

    private void setBranch(EModelService modelService, MApplication app) throws IOException {
        List<Reference> branches = service.localBranches();
        Optional<Reference> foundMaster = branches.stream().filter(branch -> branch.getName().toLowerCase().contains("master")).findAny();
        MUIElement dropDownMenu = modelService.find(UIEventConstants.MENU_CHANGE_BRANCH_ID, app);
        if (dropDownMenu instanceof HandledToolItemImpl) {
            HandledToolItemImpl toolItem = ((HandledToolItemImpl) dropDownMenu);
            if (foundMaster.isPresent()) {
                toolItem.setLabel(foundMaster.get().getName());
            } else if (branches.size() >= 1) {
                toolItem.setLabel(branches.get(0).getName());
            } else {
                // TODO select first branch
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
        eventBroker.post(UIEventConstants.SWITCH_CONTENT_VIEW, null);
    }

}
