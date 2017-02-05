package org.eclipse.e4.tapiji.git.ui.dialog;


import java.lang.reflect.InvocationTargetException;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class CloneRepositoryDialog extends Dialog implements SelectionListener, ModifyListener {

    @Inject
    IGitService service;

    @Inject
    UISynchronize sync;

    private Shell shell;

    private Text txtRepoPath;

    private Text txtRepoUrl;

    private Button btnCloneRepo;

    public CloneRepositoryDialog(Shell parent) {
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

        GridLayout gl_parent = new GridLayout(3, false);
        shell.setLayout(gl_parent);

        Label lblWhereToClone = new Label(shell, SWT.NONE);
        lblWhereToClone.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblWhereToClone.setText("Where to clone to");

        txtRepoPath = new Text(shell, SWT.BORDER);
        txtRepoPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        txtRepoPath.addModifyListener(this);

        Button btnBrowse = new Button(shell, SWT.NONE);
        btnBrowse.setText("Browse");
        btnBrowse.addSelectionListener(this);
        btnBrowse.setData("btn_browse");

        Label lblUrl = new Label(shell, SWT.NONE);
        lblUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblUrl.setText("URL");

        txtRepoUrl = new Text(shell, SWT.BORDER);
        txtRepoUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtRepoUrl.addModifyListener(this);

        new Label(shell, SWT.NONE);
        new Label(shell, SWT.NONE);

        btnCloneRepo = new Button(shell, SWT.NONE);
        btnCloneRepo.setText("Clone the repo");
        btnCloneRepo.addSelectionListener(this);
        btnCloneRepo.setData("btn_clone");
        btnCloneRepo.setEnabled(false);
        new Label(shell, SWT.NONE);

    }

    @Override
    public void widgetSelected(SelectionEvent e) {

        final Button btn = ((Button) e.widget);
        if (btn.getData().equals("btn_clone")) {

            try {
                new ProgressMonitorDialog(shell).run(true, true, new LongRunningOperation(txtRepoUrl.getText(), service, new CloneRepositoryCallback() {

                    @Override
                    public void onSuccess() {
                        sync.syncExec(new Runnable() {

                            @Override
                            public void run() {
                                shell.close();
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                }));
            } catch (InvocationTargetException invocationTargetException) {
                MessageDialog.openError(shell, "Error: ", invocationTargetException.getMessage());
            } catch (InterruptedException interruptedException) {
                MessageDialog.openInformation(shell, "Cancelled: ", interruptedException.getMessage());
            }

        } else if (btn.getData().equals("btn_browse")) {
            showDirectoryDialog();
        }
    }

    private void showDirectoryDialog() {
        DirectoryDialog dialog = new DirectoryDialog(shell);
        String result = dialog.open();
        if (result != null) {
            txtRepoPath.setText(result);
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no-op
    }

    public static void show(final IEclipseContext context, final Shell shell) {
        CloneRepositoryDialog dialog = new CloneRepositoryDialog(shell);
        ContextInjectionFactory.inject(dialog, context);
        dialog.open();
    }

    @Override
    public void modifyText(ModifyEvent e) {
        if (txtRepoUrl.getText() != null && !txtRepoUrl.getText().isEmpty() && txtRepoPath.getText() != null && !txtRepoPath.getText().isEmpty()) {
            btnCloneRepo.setEnabled(true);
        } else {
            btnCloneRepo.setEnabled(false);
        }
    }

    static class LongRunningOperation implements IRunnableWithProgress {

        private String repoUrl;
        private IGitService gitService;
        private CloneRepositoryCallback callback;

        public LongRunningOperation(String repoUrl, IGitService service, CloneRepositoryCallback callback) {
            this.repoUrl = repoUrl;
            this.gitService = service;
            this.callback = callback;
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            monitor.beginTask("Cloning repo: " + repoUrl, IProgressMonitor.UNKNOWN);

            Thread.sleep(800);

            gitService.cloneRepository();

            if (monitor.isCanceled()) {
                throw new InterruptedException("Cloning operation was cancelled!");
            }

            callback.onSuccess();
            monitor.done();
        }
    }

    public interface CloneRepositoryCallback {

        void onSuccess();

        void onError();

    }

}
