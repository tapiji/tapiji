package org.eclipselabs.tapiji.translator.rap.dialogs;

import java.io.IOException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
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
	
	private Image errorImage = FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
	private Image requiredImage = FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED).getImage();
	private Image warningImage = FieldDecorationRegistry.getDefault().
			getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
	
	
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
		
		final ControlDecoration decorationErrUsername = new ControlDecoration(usernameText, SWT.TOP | SWT.LEFT);
	    decorationErrUsername.setImage(errorImage);
	    decorationErrUsername.hide();
	    final ControlDecoration decorationWarnUsername = new ControlDecoration(usernameText, SWT.TOP | SWT.LEFT);
	    decorationWarnUsername.setImage(warningImage);
	    decorationWarnUsername.hide();
	    
	    final ControlDecoration decorationWarnPassword = new ControlDecoration(passwordText, SWT.TOP | SWT.LEFT);
	    decorationWarnPassword.setImage(warningImage);
	    decorationWarnPassword.hide();
	    
	    final ControlDecoration decorationErrPasswordConfirm = new ControlDecoration(passwordConfirmText, SWT.TOP | SWT.LEFT);
	    decorationErrPasswordConfirm.setImage(errorImage);
	    decorationErrPasswordConfirm.hide();
	    
	    usernameText.addFocusListener(new FocusAdapter() {
	    	@Override
	    	public void focusLost(FocusEvent event) {
	    		errorLabel.setVisible(false);
	    		
	    		String content = usernameText.getText();
	        	content = content.trim().replace(" ", "");
	        	usernameText.setText(content);
	        	
	            if(content.isEmpty()) {
	            	usernameText.setBackground(new Color(usernameText.getDisplay(), 250, 200, 150));
	            	decorationWarnUsername.show();
	            	decorationWarnUsername.setDescriptionText("This field is mandatory.");
	            } else {
	            	usernameText.setBackground(null);
	            	decorationWarnUsername.hide();
	            	
	            	if (UserUtils.existsUser(content)) {
	            		usernameText.setBackground(new Color(usernameText.getDisplay(), 250, 200, 150));
		            	decorationErrUsername.show();
		            	decorationErrUsername.setDescriptionText("This username exists already. Please" +
		            			" choose another one.");
	            	} else {
	            		usernameText.setBackground(null);
	            		decorationErrUsername.hide();
	            	}
	            }
	    	}
		});	    
	    
	   passwordText.addFocusListener(new FocusAdapter() {
	    	@Override
	    	public void focusLost(FocusEvent event) {
	    		errorLabel.setVisible(false);
	    		
				String content = passwordText.getText();
	            if(content.trim().length() == 0) {
	            	passwordText.setBackground(new Color(passwordText.getDisplay(), 250, 200, 150));
	            	decorationWarnPassword.show();
	            	decorationWarnPassword.setDescriptionText("This field is mandatory.");
	            } else {
	            	passwordText.setBackground(null);
	            	decorationWarnPassword.hide();
	            	
	            	if(passwordConfirmText.getText().equals(content)) {		            	
		            	passwordConfirmText.setBackground(null);
		            	decorationErrPasswordConfirm.hide();
		            }
	            }
			}
		});
	    
	   passwordConfirmText.addFocusListener(new FocusAdapter() {
	    	@Override
	    	public void focusLost(FocusEvent event) {
	    		errorLabel.setVisible(false);
	    		
				String content = passwordConfirmText.getText();
	            if(! passwordText.getText().equals(content)) {
	            	passwordConfirmText.setBackground(new Color(passwordConfirmText.getDisplay(), 250, 200, 150));
	            	decorationErrPasswordConfirm.show();
	            	decorationErrPasswordConfirm.setDescriptionText("The confirmation isn't equal to the password!");
	            } else {
	            	passwordConfirmText.setBackground(null);
	            	decorationErrPasswordConfirm.hide();
	            }
			}
		});
	    
		
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
		} else if (! username.isEmpty() && ! password.isEmpty()) {
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
