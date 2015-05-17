package org.eclipselabs.e4.tapiji.translator.ui.dialog;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class AboutDialog extends Dialog implements SelectionListener {

    private Shell shell;
    private Button btnNewButton;

    public AboutDialog(Shell parent) {
        super(parent);
        createContents();
    }

    public void open() {
        createContents();
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }


    private void createContents() {
        shell = new Shell(getParent(), SWT.CLOSE | SWT.TITLE);
        shell.setSize(450, 236);
        shell.setText(getText());

        CBanner banner = new CBanner(shell, SWT.NONE);
        banner.setBounds(130, 75, 0, 0);

        btnNewButton = new Button(shell, SWT.NONE);
        btnNewButton.setBounds(355, 170, 83, 29);
        btnNewButton.addSelectionListener(this);
        btnNewButton.setData("btn_ok");
        btnNewButton.setText("OK");

        Button btnNewButton_1 = new Button(shell, SWT.NONE);
        btnNewButton_1.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        btnNewButton_1.setBounds(255, 170, 83, 29);
        btnNewButton_1.setText("License");

        Label lblNewLabel = new Label(shell, SWT.NONE);
        //lblNewLabel.setImage(getClass().getResource("/media/nucle/xubuntu/tapiji/tapiji/org.eclipselabs.e4.tapiji.resources/icons/128x128/tapiji.png"));
        lblNewLabel.setBounds(10, 10, 138, 135);

        Label lblTapijiTranslator = new Label(shell, SWT.NONE);
        //lblTapijiTranslator.setFont(SWTResourceManager.getFont("Sans", 12, SWT.NORMAL));
        lblTapijiTranslator.setBounds(154, 24, 284, 30);
        lblTapijiTranslator.setText("Tapiji - Translator");

        Label lblVersion = new Label(shell, SWT.NONE);
        lblVersion.setBounds(154, 60, 284, 15);
        lblVersion.setText("Version: 1.0.0");

        Label lblStefanStroblMartin = new Label(shell, SWT.NONE);
        lblStefanStroblMartin.setBounds(154, 82, 284, 63);
        lblStefanStroblMartin.setText("Stefan Strobl, Martin Reiterer, Christian Behon");

    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        Button btn = ((Button) e.widget);
        if (btn.getData().equals("btn_ok")) {
            shell.close();
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // TODO Auto-generated method stub

    }
}
