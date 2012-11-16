package org.eclipselabs.tapiji.translator.rap.dialogs;

import java.io.IOException;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class ShareDialog extends Dialog {
	private ResourceBundle resourceBundle;
	private Table userTable;
	private static final int USERNAME_COLUMN_INDEX = 1;
		
	public ShareDialog(Shell parentShell, ResourceBundle rb) {
		super(parentShell);
		resourceBundle = rb;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Share resource bundle with other users");
		newShell.setSize(340, 500);
	}
	
	@Override
	   protected void createButtonsForButtonBar(Composite parent) {
	    super.createButtonsForButtonBar(parent);
	    // rename ok button to save
	    Button ok = getButton(IDialogConstants.OK_ID);
	    ok.setText("Save");
	 }
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite comp = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout) comp.getLayout();
	    layout.numColumns = 3;
		
	    Label usernameLabel = new Label(comp, SWT.NONE);
	    usernameLabel.setText("Username:");
	    
	    final Combo usernameCombo = new Combo(comp, SWT.READ_ONLY | SWT.DROP_DOWN);	    
	    usernameCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
	    fillCombo(usernameCombo);
	    		
		final Button addButton = new Button(comp, SWT.PUSH);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// verify username exists
				String username = usernameCombo.getText();
				if (! username.isEmpty()) {
					// add user to table
					TableItem tableItem = new TableItem(userTable, SWT.NONE);
					tableItem.setChecked(true);
					tableItem.setText(USERNAME_COLUMN_INDEX, username);		
					// remove user from combo
					usernameCombo.remove(username);
				}
			}
		});
	    
		usernameCombo.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent event) {
				String username = usernameCombo.getText();
				if (username.isEmpty()) {
					addButton.setEnabled(false);
				} else {
					addButton.setEnabled(true);
				}
			}
		});

	    createTable(comp);
	    fillTable();
	    
		return comp;
	}
	
    private void fillCombo(Combo combo) {
    	List<User> sharedUsers = resourceBundle.getSharedUsers();
    	for (User user : DBUtils.getAllRegisteredUsers()) {
    		// don't add already shared users and owner of rb
    		if (! sharedUsers.contains(user) && ! user.equals(resourceBundle.getOwner()))
    			combo.add(user.getUsername());
    	}
    		
    }

	private void createTable(Composite parent) {
		userTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
	    userTable.setHeaderVisible(true);
	    userTable.setLayout(new GridLayout());	    
	    GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalSpan = 3;
        userTable.setLayoutData(gridData);
        
	    TableColumn columnShare = new TableColumn(userTable, SWT.NONE);
	    columnShare.setText("Share?");
	    TableColumn columnUsername = new TableColumn(userTable, SWT.NONE);
	    columnUsername.setText("Username");
	    
	    columnShare.pack();
	    columnUsername.pack();
	}
	
	private void fillTable() {
		List<User> sharedUsers = resourceBundle.getSharedUsers();
		
		for (User sharedUser : sharedUsers) {
			TableItem tableItem = new TableItem(userTable, SWT.NONE);
			tableItem.setChecked(true);
			tableItem.setText(USERNAME_COLUMN_INDEX, sharedUser.getUsername());
		}
	}
	
	@Override
	protected void okPressed() {		
		List<User> sharedUsers = resourceBundle.getSharedUsers();
		
		for (TableItem item : userTable.getItems()) {
			User user = UserUtils.getUser(item.getText(USERNAME_COLUMN_INDEX));
			if (item.getChecked()) {
				if (! sharedUsers.contains(user)) {
					sharedUsers.add(user);
					user.getStoredRBs().add(resourceBundle);
				}
				
			} else {
				sharedUsers.remove(user);
				user.getStoredRBs().remove(resourceBundle);
			}
		}
		
		// save to DB
		try {
			resourceBundle.getOwner().eResource().save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		super.okPressed();
	}
}
