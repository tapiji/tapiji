package org.eclipselabs.tapiji.translator.views;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.tapiji.translator.actions.LogoutAction;
import org.eclipselabs.tapiji.translator.rap.dialogs.DownloadDialog;
import org.eclipselabs.tapiji.translator.rap.dialogs.LoginDialog;
import org.eclipselabs.tapiji.translator.rap.dialogs.NewLocaleDialog;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.EditorUtils;
import org.eclipselabs.tapiji.translator.rap.utils.FileRAPUtils;
import org.eclipselabs.tapiji.translator.rap.utils.StorageUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UIUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UserUtils;
import org.eclipselabs.tapiji.translator.views.menus.StorageMenuEntryContribution;
import org.eclipselabs.tapiji.translator.views.widgets.provider.StorageTreeContentProvider;
import org.eclipselabs.tapiji.translator.views.widgets.provider.StorageTreeLabelProvider;

public class StorageView extends ViewPart {
	private Composite parent = null;
	private Composite main;
	private TreeViewer treeViewer;
	private IWorkbenchPage page;
	
	private List<ResourceBundle> model = new ArrayList<ResourceBundle>();
	
	private Map<String,ResourceBundle> sessionRBsMap = new HashMap<String,ResourceBundle>();
	private Map<String,ResourceBundle> userRBsMap = new HashMap<String,ResourceBundle>();
	
	public final static String ID = "org.eclipselabs.tapiji.translator.views.StorageView";
	
	public StorageView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		createStoragePart(parent);
		hookContextMenu();
	}
	
	private void createStoragePart(Composite parent) {				
		createTree();		
	    fillTree();
	}
	
	private void createTree() {
        treeViewer = new TreeViewer(main, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		treeViewer.setLabelProvider(new StorageTreeLabelProvider());
		treeViewer.setContentProvider(new StorageTreeContentProvider()); 
		treeViewer.setColumnProperties(new String[]{"column1"});
		treeViewer.setCellEditors( new CellEditor[] {new TextCellEditor(treeViewer.getTree())} );
	    
	    GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        treeViewer.getTree().setLayoutData(gridData);
        
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				EditorUtils.openEditorOfRB(getSelectedRB());
			}
		});
    }
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void fillTree() {
		model.clear();
		
		// add user rbs
	    User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);	    
	    if (user != null) {
		    StorageUtils.syncStorageWithDatabase();
		    for (ResourceBundle userRB : user.getStoredRBs()) {
		    	model.add(userRB);
		    	userRBsMap.put(userRB.getName(), userRB);
		    }
	    }
		
	    // add session rbs
	    for (ResourceBundle userRB : StorageUtils.getSessionRBs()) {
	    	model.add(userRB);
	    	sessionRBsMap.put(userRB.getName(), userRB);
	    }
	    
	    treeViewer.setInput(model);   
	}
	
	public void refresh() {		
		
		// sync maps with model, because rb changes (i.e. storing rb) are direct
		userRBsMap.clear(); sessionRBsMap.clear();
		for (ResourceBundle rb : model) {
			if (rb.isTemporary())
				sessionRBsMap.put(rb.getName(), rb);
			else
				userRBsMap.put(rb.getName(), rb);
		}
		
		// refresh RBs
		List<ResourceBundle> rbs = StorageUtils.getSessionRBs();
		List<ResourceBundle> rbsToRemove = new ArrayList<ResourceBundle>(model);

		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
	    if (user != null) {	 
	    	StorageUtils.syncStorageWithDatabase();	    	 
			rbs.addAll(user.getStoredRBs());						
	    }
    
	    //List<ResourceBundle> iter = new ArrayList<ResourceBundle>(rbs);
	   
	    // update model rbs
	    for (ResourceBundle rb : rbs) {
	    	Map<String, ResourceBundle> rbMap = rb.isTemporary() ? sessionRBsMap : userRBsMap;
	    	// rb out of date
    		if (! model.contains(rb)) {  
    			ResourceBundle modelRB = rbMap.get(rb.getName());    			
    			// rb exist already, but local files have changed
    			if (modelRB != null) {    				
    				// update model files
    				modelRB.getLocalFiles().clear();
    				modelRB.getLocalFiles().addAll(rb.getLocalFiles());
    				
    				rbsToRemove.remove(modelRB);
    			// rb doesn't exists yet
    			} else {
    				model.add(rb);
    				rbMap.put(rb.getName(), rb);
    			}
    		// rb is up to date
    		} else {
    			rbsToRemove.remove(rb);
    		}
	    }
	    
	    // remove old rbs from model
	    for (ResourceBundle rb : rbsToRemove) {
	    	model.remove(rb);
	    	if (rb.isTemporary())
	    		sessionRBsMap.remove(rb.getName());
	    	else
	    		userRBsMap.remove(rb.getName());	    		
	    }
	   
	    
	    // refresh tree viewer and keep expanded nodes
	    //TreePath[] expandedTreePaths = treeViewer.getExpandedTreePaths();
		treeViewer.refresh();
		//treeViewer.setExpandedTreePaths(expandedTreePaths);
		
		hookContextMenu();
	}
	
	private void refreshSelectedRB(ResourceBundle updatedRB) {
		TreeItem item = treeViewer.getTree().getSelection()[0];
		// get resourceBundle item
		if (! (item.getData() instanceof ResourceBundle))
			item = item.getParentItem();
		
		// get selected tree index = model index of rb
		int index = treeViewer.getTree().indexOf(item);
		
		// remove rb from tree if empty
		if (updatedRB.getLocalFiles().isEmpty())
			model.remove(index);
		// update rb
		else
			model.set(index, updatedRB);
//		((List) treeViewer.getInput()).set(index, updatedRB);
		treeViewer.refresh();
	}
		
	/*** CONTEXT MENU ***/
	private void hookContextMenu() {
		if (treeViewer != null && ! treeViewer.getControl().isDisposed()) {		
			MenuManager menuMgr = new MenuManager("#PopupMenu");
		
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					fillTreeContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
			treeViewer.getControl().setMenu(menu);
			getViewSite().registerContextMenu(menuMgr, treeViewer);
		}
	}
	
	private void fillTreeContextMenu(IMenuManager manager) {
		manager.removeAll();

		if (UserUtils.isUserLoggedIn())
			 manager.add(getLogoutAction());
		else
			 manager.add(getLoginAction());
		
		StorageMenuEntryContribution storageContribution = new StorageMenuEntryContribution(this);		
		manager.add(storageContribution);		
		manager.add(getRefreshAction());
	}
	
	private IAction getRefreshAction() {
		IAction refresh = new Action() {
			public void run() {
				refresh();
			}
		};
		refresh.setText("Refresh");
		refresh.setDescription("Refreshing Storage View");
		refresh.setToolTipText(refresh.getDescription());
		refresh.setImageDescriptor(UIUtils.getImageDescriptor(UIUtils.IMAGE_REFRESH));
		
		return refresh;
	}

	private IAction getLoginAction() {
		IAction login = new Action() {
			public void run() {
				LoginDialog dialog = new LoginDialog(parent.getShell());
				dialog.open();
				refresh();
			}
		};
		login.setText("Login ...");
		login.setDescription("Login ...");
		login.setToolTipText(login.getDescription());
		login.setImageDescriptor(UIUtils.getImageDescriptor(UIUtils.IMAGE_LOGIN));
		return login;
	}
	
	private IAction getLogoutAction() {
		IAction logout = new Action() {
			public void run() {
				new LogoutAction().run(null);
				refresh();
			}
		};
		logout.setText("Logout ...");
		logout.setDescription("Logout ...");
		logout.setToolTipText(logout.getDescription());
		logout.setImageDescriptor(UIUtils.getImageDescriptor(UIUtils.IMAGE_LOGOUT));
		return logout;
	}
	
	public void storeSelectedItem() {
		Object selectedItem = getSelectedItem();
		if (selectedItem instanceof ResourceBundle) {
			ResourceBundle rb = (ResourceBundle) selectedItem;
			
			if (StorageUtils.existsUserRBName(rb.getName()))
				// TODO
				return;
			
			EditorUtils.closeEditorOfRB(rb, true);
			
			StorageUtils.storeRB(rb);	
			
			refreshSelectedRB(rb);
			
			EditorUtils.openEditorOfRB(rb);			
		}
	}
	
	private Object getSelectedItem() {
		Object selection = null;		
		if (treeViewer != null) {
			ISelection sel = treeViewer.getSelection();			
			if (sel instanceof IStructuredSelection) {
				IStructuredSelection structSel = (IStructuredSelection) sel;
				selection = structSel.getFirstElement();
			}				
		}
		
		return selection;
	}
	
	public ResourceBundle getSelectedRB() {
		TreeItem item = treeViewer.getTree().getSelection()[0];
		// get resourceBundle item
		if (! (item.getData() instanceof ResourceBundle))
			item = item.getParentItem();
		
		return (ResourceBundle) item.getData();
	}
	
	public boolean isSelectionUnstoredRB() {		
		Object selection = getSelectedItem();		
		if (selection instanceof ResourceBundle) {
			if (((ResourceBundle) selection).isTemporary())
				return true;
		}
			
		return false;
	}
	
	public boolean isSelectionStoredRB() {		
		Object selection = getSelectedItem();		
		if (selection instanceof ResourceBundle) {
			if (! ((ResourceBundle) selection).isTemporary())
				return true;
		}			
		return false;
	}
	
	public boolean isSelectionPropertiesFile() {		
		Object selection = getSelectedItem();		
		if (selection instanceof PropertiesFile)
			return true;		
		return false;
	}
	
	public boolean isValidSelection() {
		if (getSelectedItem() == null)
			return false;		
		return true;
	}

	public void deleteSelectedItem() {
		Object selectedItem = getSelectedItem();
		
		ResourceBundle rb = null;
		List<PropertiesFile> deleteFiles = new ArrayList<PropertiesFile>();
		boolean openEditor = false;
		
		if (selectedItem instanceof ResourceBundle) {
			rb = (ResourceBundle) selectedItem;
			deleteFiles.addAll(rb.getLocalFiles());
			
		} else if (selectedItem instanceof PropertiesFile) {
			PropertiesFile file = (PropertiesFile) selectedItem;
			rb = file.getResourceBundle();
			deleteFiles.add(file);
		}
		
		// close editor if still opened
		openEditor = EditorUtils.closeEditorOfRB(rb, true);
		
		try {
			// delete files
			for (PropertiesFile file : deleteFiles) {
				// delete file from hd 
				IFile ifile = FileRAPUtils.getFile(file);
				ifile.delete(true, null);
				
				// remove file from resource bundle
				rb.getLocalFiles().remove(file);				
			}
			
			// update database
			if (! rb.isTemporary()) {
				User user = rb.getUser();
				// delete rb if all locals were deleted
				if (rb.getLocalFiles().isEmpty())
					user.getStoredRBs().remove(rb);
				// update/remove rb				
				user.eResource().save(null);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		// reopen editor if it was closed before
		if (openEditor && ! rb.getLocalFiles().isEmpty()) {
			EditorUtils.openEditorOfRB(rb);
		}
		
		// refresh tree
		refreshSelectedRB(rb);
	}

	public void renameSelectedItem() {
		Object selectedItem = getSelectedItem();
		if (selectedItem instanceof ResourceBundle) {
			final ResourceBundle rb = (ResourceBundle) selectedItem;
			treeViewer.setCellModifier(new ICellModifier() {
				private boolean enableEditing = true;
				@Override
				public boolean canModify(Object element, String property) {
					// only selected element can be modified
					return element.equals(rb) && enableEditing;
				}
	
				@Override
				public Object getValue(Object element, String property) {
					ResourceBundle rb = (ResourceBundle) element;
					return rb.getName();
				}
	
				@Override
				public void modify(Object element, String property, Object value) {
					TreeItem item = (TreeItem) element;
					ResourceBundle rb = (ResourceBundle) item.getData();
					String newBundleName = value.toString().trim();
					
					// filename exists already
					if ( newBundleName.isEmpty() || 
							( rb.isTemporary() ? StorageUtils.existsTempRBName(newBundleName) : 
								StorageUtils.existsUserRBName(newBundleName) )) {
						enableEditing = false;
						return;
					}
					
					if (! rb.getName().equals(newBundleName)) {								
						boolean reopenEditor = EditorUtils.closeEditorOfRB(rb, true);
						
						StorageUtils.renameRB(rb, newBundleName);						
						refreshSelectedRB(rb);
						
						if (reopenEditor)
							EditorUtils.openEditorOfRB(rb);
					}
					
					// rename finished -> disable editing
					enableEditing = false;
				}
			});
			
			treeViewer.editElement(rb, 0);
			//refresh();
		}
	}
	
	public void addNewLocaleToSelectedRB() {
		NewLocaleDialog newLocaleDialog = new NewLocaleDialog(getSite().getShell());
		if (newLocaleDialog.open() == Dialog.CANCEL)
			return;
		
		// create new properties file in project space
		Locale newLocal = newLocaleDialog.getSelectedLocal();	
		ResourceBundle rb = getSelectedRB();
		
		IFile ifile = FileRAPUtils.getFile(rb.getLocalFiles().get(0));
		IPath path = ifile.getProjectRelativePath();
		
		String bundleName = rb.getName();
		String localeStr = newLocal != null ? "_" + newLocal.toString() : "";
		String newFilename = bundleName + localeStr + "." + ifile.getFileExtension();
		
		IFile newFile = ifile.getProject().getFile(path.removeLastSegments(1).addTrailingSeparator()+newFilename);
		
		// exists locale already ?
		if (! newFile.exists()) {
			try {
				// create new ifile with an empty input stream
				newFile.create(new ByteArrayInputStream(new byte[0]), IResource.NONE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			
			// add file to rb
			PropertiesFile propFile = StorageUtils.createPropertiesFile(newFile);
			rb.getLocalFiles().add(propFile);
			
			// store in db
			if (! rb.isTemporary()) {
				try {
					rb.getUser().eResource().save(null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			// reopen editor if opened
			if (EditorUtils.isRBOpened(rb)) {
				EditorUtils.closeEditorOfRB(rb, true);
				EditorUtils.openEditorOfRB(rb);
			}
			
			refresh();
		}
	}
	
	public void downloadSelectedRB() {
		DownloadDialog dialog = new DownloadDialog(getSite().getShell(), getSelectedRB());
		dialog.open();
	}
}
