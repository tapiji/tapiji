package org.eclipse.e4.tapiji.mylyn.model;


public class Notification {

    private String title;
    private String subTitle;
    private NotificationStatus status;

    public Notification(String title, String subTitle) {
        this(title, subTitle, NotificationStatus.INFORMATION);
    }

    public Notification(String title, String subTitle, NotificationStatus status) {
        super();
        this.title = title;
        this.subTitle = subTitle;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Notification [title=" + title + ", subTitle=" + subTitle + ", status=" + status + "]";
    }
}
