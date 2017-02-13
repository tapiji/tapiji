package org.eclipse.e4.tapiji.mylyn.core.api;


import org.eclipse.e4.tapiji.mylyn.model.Notification;


public interface IMylynService {

    void sendNotification(Notification notification);
}
