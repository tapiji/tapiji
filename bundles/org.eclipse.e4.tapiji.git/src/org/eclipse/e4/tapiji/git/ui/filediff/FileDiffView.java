package org.eclipse.e4.tapiji.git.ui.filediff;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class FileDiffView implements FileDiffContract.View {

    @Inject
    FileDiffPresenter presenter;

    @PostConstruct
    public void createPartControl(final Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        Label lblNewLabel = new Label(parent, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        //lblNewLabel.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/progress/progress_ok@2x.png"));
        lblNewLabel.setText("FileName and FilePath");

        Text text = new Text(parent, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }
}
