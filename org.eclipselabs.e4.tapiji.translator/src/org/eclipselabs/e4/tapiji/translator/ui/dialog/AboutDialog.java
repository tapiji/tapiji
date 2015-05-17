package org.eclipselabs.e4.tapiji.translator.ui.dialog;


import org.eclipse.e4.tools.services.IResourcePool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.e4.tapiji.resource.TapijiResourceProvider;


@SuppressWarnings("restriction")
public final class AboutDialog extends Dialog implements SelectionListener {

    private Shell shell;
    private Button btnConfirm;
    private final IResourcePool resource;

    public AboutDialog(final Shell parent, final IResourcePool resource) {
        super(parent);
        this.resource = resource;
        createContents();
    }

    public void open() {
        createContents();
        shell.open();
        shell.layout();
        final Display display = getParent().getDisplay();
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

        final CBanner banner = new CBanner(shell, SWT.NONE);
        banner.setBounds(130, 75, 0, 0);

        btnConfirm = new Button(shell, SWT.NONE);
        btnConfirm.setBounds(355, 170, 83, 29);
        btnConfirm.addSelectionListener(this);
        btnConfirm.setData("btn_ok");
        btnConfirm.setText("OK");

        final Label lblNewLabel = new Label(shell, SWT.NONE);
        lblNewLabel.setImage(resource.getImageUnchecked(TapijiResourceProvider.IMG_TAPIJI_LOGO_128));
        lblNewLabel.setBounds(10, 10, 138, 135);

        final Label lblTapijiTranslator = new Label(shell, SWT.NONE);
        lblTapijiTranslator.setBounds(154, 24, 284, 30);
        lblTapijiTranslator.setText("Tapiji - Translator");

        final Label lblVersion = new Label(shell, SWT.NONE);
        lblVersion.setBounds(154, 60, 284, 15);
        lblVersion.setText("Version: 1.0.0");

        final Label lblStefanStroblMartin = new Label(shell, SWT.NONE);
        lblStefanStroblMartin.setBounds(154, 82, 284, 63);
        lblStefanStroblMartin.setText("Stefan Strobl, Martin Reiterer, Christian Behon");

    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        final Button btn = ((Button) e.widget);
        if (btn.getData().equals("btn_ok")) {
            shell.close();
        }
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent e) {
        // TODO Auto-generated method stub

    }
}
