package org.eclipselabs.tapiji.translator.rap.dialogs;

import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.UserUtils;

public class RegisterDialog extends Dialog {
	
	private Text usernameText;
	private Text passwordText;
	private Text passwordConfirmText;
	private Label errorLabel;
	private User registeredUser;
	
	
	public RegisterDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Register");
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
		
		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText("Password: ");		
		passwordText = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label passwordConfirmLabel = new Label(comp, SWT.RIGHT);
		passwordConfirmLabel.setText("Password Confirm: ");		
		passwordConfirmText = new Text(comp, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordConfirmText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		errorLabel = new Label(comp, SWT.NONE);
		errorLabel.setForeground(comp.getDisplay().getSystemColor(SWT.COLOR_RED));
		errorLabel.setVisible(false);
		errorLabel.setText("The password and the confirmation aren't equal!");
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_END);
	    gridData.horizontalSpan = 2;
	    gridData.horizontalAlignment = GridData.FILL;
		errorLabel.setLayoutData(gridData);
		
		return comp;
	}
	
	@Override
	protected void okPressed() {
		errorLabel.setVisible(false);
		
		String username = usernameText.getText();
		String password = passwordText.getText();
		String passwordConfirm = passwordConfirmText.getText();
		
		if (! password.equals(passwordConfirm)) {
			errorLabel.setText("The password and the confirmation aren't equal!");
			errorLabel.setVisible(true);
		} else {
			try {
				registeredUser = UserUtils.registerUser(username, password);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (registeredUser == null) {
				// username exists already
				errorLabel.setText("The username exists already!");
				errorLabel.setVisible(true);
			} else {
				super.okPressed();
			}
		}
	}
	
	public User getRegisteredUser() {
		return registeredUser;
	}
}
