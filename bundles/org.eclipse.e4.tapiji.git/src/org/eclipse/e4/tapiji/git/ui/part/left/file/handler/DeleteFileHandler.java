
package org.eclipse.e4.tapiji.git.ui.part.left.file.handler;


import java.io.File;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.property.PropertyFile;
import org.eclipse.e4.tapiji.git.ui.constant.UIEventConstants;
import org.eclipse.e4.ui.services.IServiceConstants;


public class DeleteFileHandler {

    @Inject
    IGitService service;

    @Inject
    IEventBroker eventBroker;

    @Execute
    public void execute(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) PropertyFile propertyFile) {
        service.deleteFile(new File(propertyFile.getPath()));
        eventBroker.post(UIEventConstants.TOPIC_RELOAD_STAGE_VIEW, "");
        eventBroker.post(UIEventConstants.TOPIC_RELOAD_UNSTAGE_VIEW, "");
    }

}
