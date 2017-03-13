package org.eclipse.e4.tapiji.git.ui.handler.window;


import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preference.Preferences;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
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
    Preferences prefs;

    @Inject
    UISynchronize sync;

    @Execute
    public void exec(final IEclipseContext context, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
        prefs.addRepository("pesto", "E:/cloni");
        executeCommand();
        //
        //        DirectoryDialog dialog = new DirectoryDialog(shell);
        //        String result = dialog.open();
        //        if (result != null) {
        //            try {
        //                service.mount(result);
        //                prefs.addRepository("pesto", result);
        //                executeCommand();
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
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

}
