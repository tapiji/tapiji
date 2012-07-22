package org.eclipselabs.tapiji.translator.views;


import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.tapiji.translator.rap.dialogs.LoginDialog;
import org.eclipselabs.tapiji.translator.rap.dialogs.RegisterDialog;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.DBUtils;

public class StorageView extends ViewPart {
	private Composite parent = null;
	
	private Composite main;
	
	public final static String ID = "org.eclipselabs.tapiji.translator.views.StorageView";
	
	public StorageView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		refresh();
	}

	private Composite createNoUserPart(final Composite parent) {
		Composite noUserComp = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		noUserComp.setLayout(layout);
		

		Label text = new Label(noUserComp, SWT.WRAP);
		text.setText("You need to be logged in to see your stored files.");
		
		GridData gridData = new GridData() ;
	    gridData.grabExcessHorizontalSpace = true ;
	    gridData.horizontalAlignment = SWT.FILL;

		text.setLayoutData(gridData);
		
		
		Label loginLink = new Label(noUserComp, SWT.NONE);
		loginLink.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		loginLink.setText("<a href=\"#\">Login</a>");
		loginLink.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				LoginDialog loginDialog = new LoginDialog(parent.getShell());
				loginDialog.open();
				
				refresh();
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Label registerLink = new Label(noUserComp, SWT.NONE);
		registerLink.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		registerLink.setText("<a href=\"#\">Register</a>");
		registerLink.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				RegisterDialog registerDialog = new RegisterDialog(parent.getShell());
				registerDialog.open();
				User user = registerDialog.getRegisteredUser();
				
				LoginDialog loginDialog = new LoginDialog(parent.getShell(), user.getUsername());
				loginDialog.open();
				
				refresh();
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return noUserComp;
	}
	
	
	private Composite createNoStoredFilesPart(Composite parent) {
		Composite noFilesComp = new Composite(parent, SWT.CENTER);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		noFilesComp.setLayout(layout);
		
		Label text = new Label(noFilesComp, SWT.WRAP);
		text.setText("There are no files to display.");
		
		GridData gridData = new GridData() ;
	    gridData.grabExcessHorizontalSpace = true ;
	    gridData.horizontalAlignment = SWT.FILL;

		text.setLayoutData(gridData);
		
		
		Label uploadLink = new Label(noFilesComp, SWT.NONE);
		uploadLink.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		uploadLink.setText("<a href=\"#\">Upload a file</a>");
		uploadLink.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				// TODO
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return noFilesComp;
	}
	
	private Composite createStoragePart(Composite parent, User user) {
		Composite storageComp = new Composite(parent, SWT.CENTER);
		
		storageComp.setLayout(new GridLayout());
		
		Label todo = new Label(storageComp, SWT.NONE);
		todo.setText("TODO: list files here");
		
		return storageComp;
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	
	public void refresh() {
		if (main != null)
			main.dispose();
			
		User user = (User) RWT.getSessionStore().getAttribute(DBUtils.SESSION_USER_ATT);
		
		if (user == null) {			
			main = createNoUserPart(parent);//(noUserComp == null ? createNoUserPart(parent) : noUserComp);
		} else if (user.getStoredFiles().isEmpty()) {
			main = createNoStoredFilesPart(parent);//(noFilesComp == null ? createNoStoredFilesPart(parent) : noFilesComp);
		} else {
			main = createStoragePart(parent, user);//(storageComp == null ? createStoragePart(parent,user) : storageComp);
		}
		parent.layout();
	}
}
