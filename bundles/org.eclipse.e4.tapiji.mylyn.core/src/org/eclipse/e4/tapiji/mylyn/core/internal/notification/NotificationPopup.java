package org.eclipse.e4.tapiji.mylyn.core.internal.notification;


import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;


public class NotificationPopup extends AbstractNotificationPopup {

    private String title;
    private String subTitle;
    private Image image;

    public NotificationPopup(Display display) {
        super(display);
    }

    @Override
    protected void createContentArea(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        Label lblIcon = new Label(parent, SWT.NONE);
        lblIcon.setImage(image);

        Composite composite = new Composite(parent, SWT.NONE);
        FillLayout flComposite = new FillLayout(SWT.VERTICAL);
        flComposite.marginWidth = 6;
        flComposite.marginHeight = 6;
        composite.setLayout(flComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblTitle = new Label(composite, SWT.NONE);
        lblTitle.setFont(FontUtils.createFont(lblTitle, "Segoe UI", 10, SWT.BOLD));
        lblTitle.setText(title);

        Label lblSubtext = new Label(composite, SWT.NONE);
        lblSubtext.setFont(FontUtils.createFont(lblTitle, "Segoe UI", 9, SWT.NORMAL));
        lblSubtext.setText(subTitle);
    }

    @Override
    protected Image getPopupShellImage(int maximumHeight) {
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setStatusImage(Image image) {
        this.image = image;
    }

    @Override
    protected String getPopupShellTitle() {
        return "";
    }

    @Override
    public int open() {
        return super.open();
    }
}
