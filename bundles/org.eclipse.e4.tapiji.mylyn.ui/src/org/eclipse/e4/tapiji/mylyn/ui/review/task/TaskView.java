package org.eclipse.e4.tapiji.mylyn.ui.review.task;


import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class TaskView implements TaskContract.View {

    private TaskContract.Presenter presenter;

    @Inject
    public TaskView(TaskPresenter presenter) {
        this.presenter = presenter;

    }

    @PostConstruct
    public void onPostConstruct(Composite parent) {
        GridLayout gl_parent = new GridLayout(1, false);
        parent.setLayout(gl_parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(21, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblNewLabel = new Label(composite, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.CENTER, true, false, 20, 1);
        gd_lblNewLabel.widthHint = 416;
        lblNewLabel.setLayoutData(gd_lblNewLabel);
        lblNewLabel.setBounds(0, 0, 55, 15);
        lblNewLabel.setText("New Label");

        Button btnNewButton = new Button(composite, SWT.NONE);
        btnNewButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        btnNewButton.setBounds(0, 0, 75, 25);
        btnNewButton.setText("New Button");

        Text text_1 = new Text(composite, SWT.BORDER);
        text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 21, 1));
        text_1.setBounds(0, 0, 76, 21);

        Composite composite_1 = new Composite(parent, SWT.NONE);
        RowLayout rl_composite_1 = new RowLayout(SWT.HORIZONTAL);
        rl_composite_1.marginWidth = 3;
        rl_composite_1.spacing = 20;
        composite_1.setLayout(rl_composite_1);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
        lblNewLabel_1.setText("Created");

        Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
        lblNewLabel_2.setText("Nov 13 2017");

        Label lblNewLabel_3 = new Label(composite_1, SWT.NONE);
        lblNewLabel_3.setText("Modified");

        Label lblNewLabel_4 = new Label(composite_1, SWT.NONE);
        lblNewLabel_4.setText("Dez 03 2017");

        Composite composite_3 = new Composite(parent, SWT.NONE);
        GridLayout gl_composite_3 = new GridLayout(1, false);
        composite_3.setLayout(gl_composite_3);
        composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite composite_4 = new Composite(composite_3, SWT.NONE);
        GridLayout gl_composite_4 = new GridLayout(1, false);
        gl_composite_4.marginWidth = 0;
        composite_4.setLayout(gl_composite_4);
        composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        composite_4.setBounds(0, 0, 64, 64);

        Label lblComments = new Label(composite_4, SWT.NONE);
        lblComments.setBounds(0, 0, 55, 15);
        lblComments.setText("Comments (4)");

        Composite composite_2 = new Composite(parent, SWT.NONE);
        composite_2.setLayout(new GridLayout(1, false));
        GridData gd_composite_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_composite_2.heightHint = 195;
        composite_2.setLayoutData(gd_composite_2);

        Label lblNewLabel_5 = new Label(composite_2, SWT.NONE);
        lblNewLabel_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel_5.setBounds(0, 0, 55, 15);
        lblNewLabel_5.setText("New Comment");

        Text text_2 = new Text(composite_2, SWT.BORDER);
        text_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        text_2.setBounds(0, 0, 76, 21);

    }

    @PreDestroy
    public void onDestroy() {

    }

}
