package org.eclipse.e4.tapiji.mylyn.core.internal;


import javax.inject.Inject;
import org.eclipse.e4.tapiji.mylyn.core.api.IMylynService;
import org.eclipse.e4.tapiji.mylyn.core.internal.notification.NotificationService;
import org.eclipse.e4.tapiji.mylyn.model.Notification;


public class MylynService implements IMylynService {

    @Inject
    NotificationService notificationService;

    @Override
    public void sendNotification(Notification notification) {
        notificationService.showNotification(notification);
    }

}
