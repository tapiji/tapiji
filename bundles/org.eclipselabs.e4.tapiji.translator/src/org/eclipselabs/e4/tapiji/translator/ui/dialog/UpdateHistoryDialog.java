package org.eclipselabs.e4.tapiji.translator.ui.dialog;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class UpdateHistoryDialog extends Dialog {

    private Shell shell;
    private Text text;

    public UpdateHistoryDialog(Shell parent) {
        super(parent);
    }

    private void readHistory(String changeLogFile) {
        URL url;
        try {
            url = new URL(changeLogFile);
            try (Scanner scanner = new Scanner(url.openStream(), "UTF-8")) {
                String out = scanner.useDelimiter("\\A").next();
                text.setText(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
    }

    public void open(String changeLogFile) {
        readHistory(changeLogFile);
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
        shell.setSize(450, 236);
        shell.setText(getText());
        shell.setLayout(new GridLayout());
        shell.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        text = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
        text.setEditable(false);
    }

    public static void show(final IEclipseContext context, final Shell shell, String changeLogFile) {
        UpdateHistoryDialog dialog = new UpdateHistoryDialog(shell);
        ContextInjectionFactory.inject(dialog, context);
        dialog.open(changeLogFile);
    }
}
