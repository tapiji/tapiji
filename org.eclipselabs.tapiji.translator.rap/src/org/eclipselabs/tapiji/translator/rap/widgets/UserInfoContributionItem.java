package org.eclipselabs.tapiji.translator.rap.widgets;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipselabs.tapiji.translator.actions.LogoutAction;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UIUtils;

public class UserInfoContributionItem extends ContributionItem {
	
	private ToolBar toolBar; 
	
	public UserInfoContributionItem() {
	}

	public UserInfoContributionItem(String id) {
		super(id);
	}
    
	@Override
	public void fill(ToolBar toolBar, int index) {
		if (UserUtils.isUserLoggedIn()) {
					
			final ToolItem userItem = new ToolItem(toolBar, SWT.DROP_DOWN | SWT.BORDER, index);
			userItem.setText(UserUtils.getUser().getUsername());
			userItem.setData(WidgetUtil.CUSTOM_VARIANT, "userItem");
			userItem.setImage(UIUtils.getImageDescriptor(UIUtils.IMAGE_HOME).createImage());
		    userItem.addSelectionListener(new SelectionAdapter() {
			    @Override
			    public void widgetSelected( SelectionEvent event ) {
				    int yOffset = (event.detail == SWT.ARROW) ? 0 : userItem.getParent().getSize().y;	   
				    Menu dropDownMenu = new Menu(userItem.getParent().getShell(), SWT.PUSH | SWT.POP_UP);
				        
				    final MenuItem logoutItem = new MenuItem(dropDownMenu, SWT.LEFT);
				    logoutItem.setText("Logout");
				    logoutItem.setImage(UIUtils.getImageDescriptor(UIUtils.IMAGE_LOGOUT).createImage());
				    logoutItem.addSelectionListener( new SelectionAdapter() {
					    @Override
					    public void widgetSelected( SelectionEvent event ) {
					    	new LogoutAction().run(null);
					    }
				    });
				    
				    dropDownMenu.setLocation(userItem.getParent().toDisplay(event.x, event.y + yOffset));
				    dropDownMenu.setVisible(true);
			    }
		    });	    
		}
	}
}
