package org.eclipse.e4.tapiji.git.ui.handler.window;


import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitRepository;
import org.eclipse.e4.tapiji.git.ui.constants.UIEventConstants;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;


public class PerpectiveSwitchHandler {

    @Inject
    IEventBroker eventBroker;

    @SuppressWarnings("restriction")
    @Execute
    public void switchPerspective(IEclipseContext context, MApplication application, EPartService partService, EModelService modelService, @Named(UIEventConstants.PARAMETER_PERSPECTIVE_ID) String perspectiveId, IGitService service, Preferences prefs, MApplication app) {
        List<MPerspective> perspectives = modelService.findElements(application, perspectiveId, MPerspective.class, null);
        if (!perspectives.isEmpty()) {
            perspectives.forEach(perspective -> {
                if (perspective.getElementId().equals(UIEventConstants.PERSPECTIVE_MAIN)) {
                    partService.switchPerspective(perspective);
                } else if (perspective.getElementId().equals(UIEventConstants.PERSPECTIVE_GIT)) {
                    try {
                        GitRepository repository = prefs.getSelectedRepository();
                        if (repository != null) {
                            MUIElement dropDownMenu = modelService.find(UIEventConstants.MENU_CHANGE_REPOSITORY_ID, app);
                            if (dropDownMenu instanceof HandledToolItemImpl) {
                                ((HandledToolItemImpl) dropDownMenu).setLabel(repository.getName());
                            }
                            service.mount(repository.getDirectory());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    partService.switchPerspective(perspective);
                    eventBroker.post(UIEventConstants.TOPIC_RELOAD_UNSTAGE_VIEW, "");
                    eventBroker.post(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW, "");
                    eventBroker.post(UIEventConstants.TOPIC_RELOAD_VIEW, "");
                }
            });
        }
    }

    @CanExecute
    public boolean canExecute(Preferences prefs, IEclipseContext ictx) {
        //        final EclipseContext ctx = (EclipseContext) ictx.getParent();
        //        System.out.println("### START ###");
        //        for (final Entry<String, Object> entry : ctx.localData().entrySet()) {
        //            System.out.println(String.format("Key'%s', value: '%s'", entry.getKey(), entry.getValue()));
        //        }
        //        System.out.println("### END ###");
        return !prefs.getRepositories().isEmpty();
    }
}
