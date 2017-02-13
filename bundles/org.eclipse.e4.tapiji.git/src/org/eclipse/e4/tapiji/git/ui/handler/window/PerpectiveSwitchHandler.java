package org.eclipse.e4.tapiji.git.ui.handler.window;


import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;


public class PerpectiveSwitchHandler {

    private static final String PARAMETER_PERSPECTIVE_ID = "org.eclipse.e4.tapiji.git.commandparameter.perspective";
    public static final String TOPIC_UPDATE_FILES = "UPDATEFILES";
    public static final String TOPIC_ALL = "org/eclipse/e4/ui/model/ui/UILabel/*"; //$NON-NLS-1$
    @Inject
    IEventBroker eventBroker;

    @Execute
    public void switchPerspective(IEclipseContext context, MApplication application, EPartService partService, EModelService modelService, @Named(PARAMETER_PERSPECTIVE_ID) String perspectiveId, IGitService service, Preferences prefs) {
        List<MPerspective> perspectives = modelService.findElements(application, perspectiveId, MPerspective.class, null);
        if (!perspectives.isEmpty()) {
            try {
                // TODO where to init git repo?
                if (prefs.getSelectedRepository() != null || !prefs.getSelectedRepository().isEmpty()) {
                    service.mount(prefs.getSelectedRepository());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            partService.switchPerspective(perspectives.get(0));
            eventBroker.send(TOPIC_UPDATE_FILES, null);

        }

    }
}
