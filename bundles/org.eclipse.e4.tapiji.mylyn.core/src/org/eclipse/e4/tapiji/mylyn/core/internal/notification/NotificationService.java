package org.eclipse.e4.tapiji.mylyn.core.internal.notification;


import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.mylyn.model.Notification;
import org.eclipse.e4.tapiji.mylyn.model.NotificationStatus;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;


@Creatable
@Singleton
public class NotificationService {

    @Inject
    private ITapijiResourceProvider resource;

    @Inject
    private Shell shell;

    public void showNotification(Notification notification) {
        NotificationPopup popup = new NotificationPopup(shell.getDisplay());
        popup.setTitle(notification.getTitle());
        popup.setSubTitle(notification.getSubTitle());
        popup.setStatusImage(statusImage(notification.getStatus()));
        popup.open();
    }

    private Image statusImage(NotificationStatus status) {
        switch (status) {
            case ERROR:
                return resource.loadImage(TapijiResourceConstants.IMG_INFORMATION_32x32);
            case INFORMATION:
            default:
                return resource.loadImage(TapijiResourceConstants.IMG_INFORMATION_32x32);
        }
    }
}
