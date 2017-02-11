package org.eclipse.e4.tapiji.git.ui.dialog;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class LoginDialog extends Dialog {

    @Inject
    IGitService service;

    @Inject
    Preferences prefs;

    @Inject
    UISynchronize sync;

    private Shell shell;

    public LoginDialog(Shell parent) {
        super(parent);
    }

    public void open() {
        shell.open();
        shell.layout();
        final Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    @PostConstruct
    private void createContents() {
        shell = new Shell(getParent(), SWT.CLOSE | SWT.TITLE);
        shell.setSize(450, 136);
        shell.setText(getText());

        shell.setLayout(new GridLayout(1, false));

        Label lblPleaseLoginTo = new Label(shell, SWT.NONE);
        lblPleaseLoginTo.setText("Please login to continue");

        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label lblUserName = new Label(composite, SWT.NONE);
        lblUserName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblUserName.setText("User name:");

        Text text = new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(composite, SWT.NONE);
        lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblPassword.setText("Password:");

        Text text_1 = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(composite, SWT.NONE);

        Composite composite_1 = new Composite(composite, SWT.NONE);
        composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

        Button btnNewButton = new Button(composite_1, SWT.NONE);
        btnNewButton.setText("Log In");

        Button btnNewButton_1 = new Button(composite_1, SWT.NONE);
        btnNewButton_1.setText("Cancel");
    }

    public static void show(final IEclipseContext context, final Shell shell) {
        LoginDialog dialog = new LoginDialog(shell);
        ContextInjectionFactory.inject(dialog, context);
        dialog.open();
    }
}
