package org.eclipse.e4.tapiji.git.ui.dialog;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
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

    private Text txtPassword;

    private Text txtUsername;

    private IGitServiceCallback<Void> callback;

    public LoginDialog(Shell parent, IGitServiceCallback<Void> callback) {
        super(parent);
        this.callback = callback;
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
        shell.setSize(450, 150);
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

        txtUsername = new Text(composite, SWT.BORDER);
        txtUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(composite, SWT.NONE);
        lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblPassword.setText("Password:");

        txtPassword = new Text(composite, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(composite, SWT.NONE);

        Composite compositeBtn = new Composite(composite, SWT.NONE);
        compositeBtn.setLayout(new FillLayout(SWT.HORIZONTAL));

        Button btnLogin = new Button(compositeBtn, SWT.NONE);
        btnLogin.addListener(SWT.MouseDown, listener -> pushChanges());
        btnLogin.setText("Log In");

        Button btnCancel = new Button(compositeBtn, SWT.NONE);
        btnCancel.addListener(SWT.MouseDown, listener -> shell.close());
        btnCancel.setText("Cancel");
    }

    private void pushChanges() {
        service.pushChangesWithCredentials(txtPassword.getText(), txtUsername.getText(), prefs.getSelectedRepository().getDirectory(), callback);
        shell.close();
    }

    public static void show(final IEclipseContext context, final Shell shell, IGitServiceCallback<Void> callback) {
        LoginDialog dialog = new LoginDialog(shell, callback);
        ContextInjectionFactory.inject(dialog, context);
        dialog.open();
    }
}
