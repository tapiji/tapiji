package org.eclipselabs.tapiji.translator.rap.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class LoginDialog extends Dialog {

	private Text usernameText;
	private String username = "";
	private Text passwordText;
	private Label incorrectUsernamePassword;
	
	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public LoginDialog(Shell parentShell, String username) {
		this(parentShell);
		this.username = username;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Login");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout) comp.getLayout();
	    layout.numColumns = 2;
		
		Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText("Username: ");
		
		usernameText = new Text(comp, SWT.SINGLE | SWT.BORDER);
		usernameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		usernameText.setText(username);
		
		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText("Password: ");
		
		passwordText = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		incorrectUsernamePassword = new Label(comp, SWT.NONE);
		incorrectUsernamePassword.setForeground(comp.getDisplay().getSystemColor(SWT.COLOR_RED));
		incorrectUsernamePassword.setVisible(false);
		incorrectUsernamePassword.setText("Incorrect username and/or password!");
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_END);
	    gridData.horizontalSpan = 2;
	    gridData.horizontalAlignment = GridData.FILL;
		incorrectUsernamePassword.setLayoutData(gridData);
		
		return comp;
	}
	
	@Override
	protected void okPressed() {
		incorrectUsernamePassword.setVisible(false);
		
		String username = usernameText.getText();
		String password = passwordText.getText();
		
		User user = UserUtils.loginUser(username, password);
		
		if (user == null) {
			// username, password incorrect
			incorrectUsernamePassword.setVisible(true);
		} else {
			super.okPressed();
		}
	}

}
