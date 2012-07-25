package org.eclipselabs.tapiji.translator.views;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.tapiji.translator.actions.FileOpenAction;
import org.eclipselabs.tapiji.translator.rap.dialogs.LoginDialog;
import org.eclipselabs.tapiji.translator.rap.dialogs.RegisterDialog;
import org.eclipselabs.tapiji.translator.rap.model.user.File;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.utils.FileRAPUtils;
import org.eclipselabs.tapiji.translator.rap.utils.StorageUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UIUtils;
import org.eclipselabs.tapiji.translator.rap.utils.UserUtils;
import org.eclipselabs.tapiji.translator.utils.FileUtils;
import org.eclipselabs.tapiji.translator.views.menus.StorageMenuEntryContribution;
import org.eclipselabs.tapiji.translator.views.widgets.provider.StorageContentProvider;
import org.eclipselabs.tapiji.translator.views.widgets.provider.StorageLabelProvider;

public class StorageView extends ViewPart {
	private Composite parent = null;
	private Composite main;
	private TableViewer tableViewer;
	private IWorkbenchPage page;
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
				FileOpenAction fileOpenAction = new FileOpenAction();
				fileOpenAction.init(page.getWorkbenchWindow());
				fileOpenAction.run(null);
				fileOpenAction.dispose();
				
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
		
		return noFilesComp;
	}
	
	private Composite createStoragePart(Composite parent, User user) {
		Composite storageComp = new Composite(parent, SWT.CENTER);
		
		storageComp.setLayout(new GridLayout());
					
		tableViewer = new TableViewer(storageComp, SWT.SINGLE);getSite();
		final TableViewer viewer = tableViewer;
		
		viewer.setLabelProvider(new StorageLabelProvider());
		viewer.setContentProvider(new StorageContentProvider()); 
		viewer.setColumnProperties(new String[]{"column1"});
	    viewer.setCellEditors(new CellEditor[] {new TextCellEditor(viewer.getTable())} );
	    
	    StorageUtils.syncUserFilesWithProject();
	    
		List<Object> files = new ArrayList<Object>();
		files.addAll(user.getStoredFiles());
		
		IEditorReference[] editors = page.getEditorReferences();
	
		for (int i=0; i < editors.length; i++) {
			try {
				IEditorInput editorInput = editors[i].getEditorInput();			
				if (editorInput instanceof IFileEditorInput) {
					IFileEditorInput fileInput = (IFileEditorInput) editorInput;
					IFile ifile = fileInput.getFile();
					if (! ifile.getProject().equals(FileRAPUtils.getProject()) )
						files.add(ifile);
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
	    viewer.setInput( files );
		
	    viewer.addDoubleClickListener(new IDoubleClickListener() {			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (isSelectionUnstoredFile())
					return;
				File file = (File) getSelectedItem();
				try {
					page.openEditor(new FileEditorInput(StorageUtils.getIFile(file.getPath())), 
							FileOpenAction.RESOURCE_BUNDLE_EDITOR);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		});
	    
		return storageComp;
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	
	public void refresh() {
		if (main != null)
			main.dispose();
		
		if (page == null)
			page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		User user = (User) RWT.getSessionStore().getAttribute(UserUtils.SESSION_USER_ATT);
		
		if (user == null) {			
			main = createNoUserPart(parent);
		} else if (user.getStoredFiles().isEmpty() && ! isMsgEditorOpened()) {
			main = createNoStoredFilesPart(parent);
		} else {
			main = createStoragePart(parent, user);
		}
		parent.layout();
		
		hookContextMenu();
	}
	
	private boolean isMsgEditorOpened() {
		IEditorReference[] editors = page.getEditorReferences();
		
		for (int i=0; i < editors.length; i++) {
			try {
				IEditorInput editorInput = editors[i].getEditorInput();			
				if (editorInput instanceof IFileEditorInput) {
					return true;
				}
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/*** CONTEXT MENU ***/
	private void hookContextMenu() {
		if (tableViewer != null && ! tableViewer.getControl().isDisposed()) {		
			MenuManager menuMgr = new MenuManager("#PopupMenu");
		
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					fillTableContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
			tableViewer.getControl().setMenu(menu);
			getViewSite().registerContextMenu(menuMgr, tableViewer);
		}
		
		if (main != null && ! main.isDisposed()) {
			MenuManager menuMgr = new MenuManager("#PopupMenu");
			
			menuMgr.setRemoveAllWhenShown(true);
			menuMgr.addMenuListener(new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					fillMainContextMenu(manager);
				}
			});
			Menu menu = menuMgr.createContextMenu(main);
			main.setMenu(menu);
			//getViewSite().registerContextMenu(menuMgr, main);
		}
	}
	
	private void fillTableContextMenu(IMenuManager manager) {
		manager.removeAll();

		StorageMenuEntryContribution storageContribution = new StorageMenuEntryContribution(this);		
		manager.add(storageContribution);
		manager.add(getRefreshAction());
	}
	
	private void fillMainContextMenu(IMenuManager manager) {
		 manager.removeAll();
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

	public void storeSelectedItem() {
		Object selectedItem = getSelectedItem();
		if (selectedItem instanceof IFile) {
			IFile iFile = (IFile) selectedItem;
			
			// exist filename already ?
			
			
			File storedFile = StorageUtils.storeFile(iFile);
			
			// update model and refresh table
			int index = tableViewer.getTable().getSelectionIndex();
			((List) tableViewer.getInput()).set(index, storedFile);
			tableViewer.refresh();
			
			// close external file and open stored file in editor
			IEditorReference[] editors = page.getEditorReferences();
			
			for (int i=0; i < editors.length; i++) {
				try {
					IEditorInput editorInput = editors[i].getEditorInput();						
					if (editorInput instanceof IFileEditorInput) {
						IFileEditorInput fileInput = (IFileEditorInput) editorInput;
						if (fileInput.getFile().equals(iFile)) {
							page.closeEditor(editors[i].getEditor(false), false);
							page.openEditor(new FileEditorInput(StorageUtils.getIFile(storedFile.getPath())), 
									FileOpenAction.RESOURCE_BUNDLE_EDITOR);
							break;
						}
					}
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private Object getSelectedItem() {
		Object selection = null;		
		if (tableViewer != null) {
			ISelection sel = tableViewer.getSelection();			
			if (sel instanceof IStructuredSelection) {
				IStructuredSelection structSel = (IStructuredSelection) sel;
				selection = structSel.getFirstElement();
			}				
		}
		
		return selection;
	}
	
	public boolean isSelectionUnstoredFile() {		
		Object selection = getSelectedItem();		
		if (selection instanceof IFile)
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
		if (selectedItem instanceof File) {
			File file = (File) selectedItem;
			StorageUtils.unstoreFile(file);
		}
	}

	public void renameSelectedItem() {
		Object selectedItem = getSelectedItem();
		if (selectedItem instanceof File) {
			final File file = (File) selectedItem;
			tableViewer.setCellModifier(new ICellModifier() {
				private boolean enableEditing = true;
				@Override
				public boolean canModify(Object element, String property) {
					// only selected element can be modified
					return element.equals(file) && enableEditing;
				}
	
				@Override
				public Object getValue(Object element, String property) {
					File file = (File) element;
					return FileRAPUtils.getBundleName(file.getPath());
				}
	
				@Override
				public void modify(Object element, String property, Object value) {
					TableItem item = (TableItem) element;
					File file = (File) item.getData();
					String newName = value.toString().trim();
					String oldPath = file.getPath();
					
					// filename exists already
					if (StorageUtils.existsProjectFile(newName) || newName.isEmpty()) {
						enableEditing = false;
						return;
					}
					
					if (! FileRAPUtils.getBundleName(file.getPath()).equals(newName) 
							&& ! file.getName().equals(newName)) {			
					
						StorageUtils.renameFile(file, newName);
						
						// update model and refresh table
						int index = tableViewer.getTable().getSelectionIndex();
						((List) tableViewer.getInput()).set(index, file);
						tableViewer.refresh();
						
						// reopen editor if it's opened with file
						try {
							IEditorReference[] editors = page.getEditorReferences();
							for (IEditorReference editor : editors) {
								if (editor.getEditorInput() instanceof IFileEditorInput) {
									IFile editorFile = ((IFileEditorInput) editor.getEditorInput()).getFile();
									IFile oldIFile = StorageUtils.getIFile(oldPath);
									if (editorFile.equals(oldIFile)) {
										page.closeEditor(editor.getEditor(false),false);
										page.openEditor(new FileEditorInput(StorageUtils.getIFile(file.getPath())),
												FileOpenAction.RESOURCE_BUNDLE_EDITOR);
									}
								}
							}
						} catch (PartInitException e) {
							e.printStackTrace();
						}
					}
					
					// rename finished -> disable editing
					enableEditing = false;
				}
			});
			
			tableViewer.editElement(file, 0);
			refresh();
		}
	}
}
