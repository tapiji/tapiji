package org.eclipse.e4.tapiji.git.ui.property.handler;


import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.babel.editor.ui.handler.window.AOpenResourceBundleHandler;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.tapiji.git.model.property.PropertyFile;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;


public class OpenResourceBundleHandler extends AOpenResourceBundleHandler {

    private static final String TAG = OpenResourceBundleHandler.class.getSimpleName();
    private PropertyFile propertyFile;
    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    @Override
    protected String[] recentlyOpenedFiles(Shell shell) {

        return new String[] {propertyFile.getPath()};
    }

    @Execute
    public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) PropertyFile propertyFile) {
        Log.d(TAG, "sdasdadasdasudhasuhdashdauszhd");
        this.propertyFile = propertyFile;
        executeCommand();
        super.execute();
    }

    @SuppressWarnings("restriction")
    private void executeCommand() {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("org.eclipse.e4.tapiji.git.commandparameter.perspective", "org.eclipse.e4.tapiji.translator.perspective.main");

        Command cmd = commandService.getCommand("org.eclipse.e4.tapiji.git.command.perspective.git");
        ParameterizedCommand pCmd = ParameterizedCommand.generateCommand(cmd, parameters);
        //ParameterizedCommand pCmd = new ParameterizedCommand(cmd, null);
        if (handlerService.canExecute(pCmd)) {
            handlerService.executeHandler(pCmd);
        }
    }
}
