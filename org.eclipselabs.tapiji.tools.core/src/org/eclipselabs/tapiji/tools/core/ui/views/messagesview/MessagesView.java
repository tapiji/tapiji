package org.eclipselabs.tapiji.tools.core.ui.views.messagesview;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipselabs.tapiji.tools.core.Activator;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.model.IResourceBundleChangedListener;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleChangedEvent;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.model.view.MessagesViewState;
import org.eclipselabs.tapiji.tools.core.ui.dialogs.ResourceBundleSelectionDialog;
import org.eclipselabs.tapiji.tools.core.ui.widgets.PropertyKeySelectionTree;
import org.eclipselabs.tapiji.tools.core.util.ImageUtils;


public class MessagesView extends ViewPart implements IResourceBundleChangedListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipselabs.tapiji.tools.core.views.MessagesView";

	// View State
	private IMemento memento;
	private MessagesViewState viewState;
	
	// Search-Bar
	private Text filter;
	
	// Property-Key widget
	private PropertyKeySelectionTree treeViewer;
	private Scale fuzzyScaler;
	private Label lblScale;
	
	/*** ACTIONS ***/
	private List<Action> visibleLocaleActions;
	private Action selectResourceBundle;
	private Action enableFuzzyMatching;
	private Action editable;
	
	// Parent component
	Composite parent;
	
	// context-dependent menu actions
	ResourceBundleEntry contextDependentMenu;
	
	/**
	 * The constructor.
	 */
	public MessagesView() {
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		initLayout (parent);
		initSearchBar (parent);
		initMessagesTree (parent);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		initListener (parent);
	}
	
	protected void initListener (Composite parent) {
		filter.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				treeViewer.setSearchString(filter.getText());
			}
		});
	}
	
	protected void initLayout (Composite parent) {
		GridLayout mainLayout = new GridLayout ();
		mainLayout.numColumns = 1;
		parent.setLayout(mainLayout);
		
	}
	
	protected void initSearchBar (Composite parent) {
		// Construct a new parent container
		Composite parentComp = new Composite(parent, SWT.BORDER);
		parentComp.setLayout(new GridLayout(4, false));
		parentComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblSearchText = new Label (parentComp, SWT.NONE);
		lblSearchText.setText("Search expression:");
		
		// define the grid data for the layout
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalSpan = 1;
		lblSearchText.setLayoutData(gridData);
		
		filter = new Text (parentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		if (viewState.getSearchString() != null) {
			if (viewState.getSearchString().length() > 1 && 
				viewState.getSearchString().startsWith("*") && viewState.getSearchString().endsWith("*"))
				filter.setText(viewState.getSearchString().substring(1).substring(0, viewState.getSearchString().length()-2));
			else
				filter.setText(viewState.getSearchString());
			
		}
		GridData gridDatas = new GridData();
		gridDatas.horizontalAlignment = SWT.FILL;
		gridDatas.grabExcessHorizontalSpace = true;
		gridDatas.horizontalSpan = 3;
		filter.setLayoutData(gridDatas);
		
		lblScale = new Label (parentComp, SWT.None);
		lblScale.setText("\nPrecision:");
		GridData gdScaler = new GridData();
		gdScaler.verticalAlignment = SWT.CENTER;
		gdScaler.grabExcessVerticalSpace = true;
		gdScaler.horizontalSpan = 1;
//		gdScaler.widthHint = 150;
		lblScale.setLayoutData(gdScaler);
		
		// Add a scale for specification of fuzzy Matching precision
		fuzzyScaler = new Scale (parentComp, SWT.None);
		fuzzyScaler.setMaximum(100);
		fuzzyScaler.setMinimum(0);
		fuzzyScaler.setIncrement(1);
		fuzzyScaler.setPageIncrement(5);
		fuzzyScaler.setSelection(Math.round((treeViewer != null ? treeViewer.getMatchingPrecision() : viewState.getMatchingPrecision())*100.f));
		fuzzyScaler.addListener (SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				float val = 1f-(Float.parseFloat(
								(fuzzyScaler.getMaximum() - 
								 fuzzyScaler.getSelection() + 
								 fuzzyScaler.getMinimum()) + "") / 100.f);
				treeViewer.setMatchingPrecision (val);
			}
		});
		fuzzyScaler.setSize(100, 10);
		
		GridData gdScalers = new GridData();
		gdScalers.verticalAlignment = SWT.BEGINNING;
		gdScalers.horizontalAlignment = SWT.FILL;
		gdScalers.horizontalSpan = 3;
		fuzzyScaler.setLayoutData(gdScalers);
		refreshSearchbarState();
	}
	
	protected void refreshSearchbarState () {
		lblScale.setVisible(treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled());
		fuzzyScaler.setVisible(treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled());
		if (treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled()) {
			((GridData)lblScale.getLayoutData()).heightHint = 40;
			((GridData)fuzzyScaler.getLayoutData()).heightHint = 40;
		} else {
			((GridData)lblScale.getLayoutData()).heightHint = 0;
			((GridData)fuzzyScaler.getLayoutData()).heightHint = 0;
		}

		lblScale.getParent().layout();
		lblScale.getParent().getParent().layout();
	}
	
	protected void initMessagesTree(Composite parent) {
		if (viewState.getSelectedProjectName() != null && viewState.getSelectedProjectName().trim().length() > 0 ) {
			try {
				ResourceBundleManager.getManager(viewState.getSelectedProjectName())
					.registerResourceBundleChangeListener(viewState.getSelectedBundleId(), this);
				
			} catch (Exception e) {}
		}
		treeViewer = new PropertyKeySelectionTree(getViewSite(), getSite(), parent, SWT.NONE, 
									viewState.getSelectedProjectName(), viewState.getSelectedBundleId(),
									viewState.getVisibleLocales());
		if (viewState.getSelectedProjectName() != null && viewState.getSelectedProjectName().trim().length() > 0 ) {
			if (viewState.getVisibleLocales() == null)
				viewState.setVisibleLocales(treeViewer.getVisibleLocales());
			
			if (viewState.getSortings() != null)
				treeViewer.setSortInfo(viewState.getSortings());
				
			treeViewer.enableFuzzyMatching(viewState.isFuzzyMatchingEnabled());
			treeViewer.setMatchingPrecision(viewState.getMatchingPrecision());
			treeViewer.setEditable(viewState.isEditable());
			
			if (viewState.getSearchString() != null)
				treeViewer.setSearchString(viewState.getSearchString());
		}
		// define the grid data for the layout
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		treeViewer.setLayoutData(gridData);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		treeViewer.setFocus();
	}
	
	protected void redrawTreeViewer () {
		parent.setRedraw(false);
		treeViewer.dispose();
		try {
			initMessagesTree(parent);
			makeActions();
			contributeToActionBars();
			hookContextMenu();
		} catch (Exception e) {
			Logger.logError(e);
		}
		parent.setRedraw(true);
		parent.layout(true);
		treeViewer.layout(true);
		refreshSearchbarState();
	}
	
	/*** ACTIONS ***/
	private void makeVisibleLocalesActions () {
	    if (viewState.getSelectedProjectName() == null) {
	        return;
	    }
	    
		visibleLocaleActions = new ArrayList<Action>();
		Set<Locale> locales = ResourceBundleManager.getManager(
				viewState.getSelectedProjectName()).getProvidedLocales(viewState.getSelectedBundleId());
		List<Locale> visibleLocales = treeViewer.getVisibleLocales();
		for (final Locale locale : locales) {
			Action langAction = new Action () {

				@Override
				public void run() {
					super.run();
					List<Locale> visibleL = treeViewer.getVisibleLocales();
					if (this.isChecked()) {
						if (!visibleL.contains(locale)) {
							visibleL.add(locale);
						}
					} else {
						visibleL.remove(locale);
					}
					viewState.setVisibleLocales(visibleL);
					redrawTreeViewer();
				}
				
			};
			if (locale != null && locale.getDisplayName().trim().length() > 0) {
				langAction.setText(locale.getDisplayName(Locale.US));
			} else {
				langAction.setText("Default");
			}
			langAction.setChecked(visibleLocales.contains(locale));
			visibleLocaleActions.add(langAction);
		}
	}
	
	private void makeActions() {
		makeVisibleLocalesActions();
		
		selectResourceBundle = new Action () {

			@Override
			public void run() {
				super.run();
				ResourceBundleSelectionDialog sd = new ResourceBundleSelectionDialog (getViewSite().getShell(), null);
				if (sd.open() == InputDialog.OK) {
					String resourceBundle = sd.getSelectedBundleId();
										
					if (resourceBundle != null) {
						int iSep = resourceBundle.indexOf("/");
						viewState.setSelectedProjectName(resourceBundle.substring(0, iSep));
						viewState.setSelectedBundleId(resourceBundle.substring(iSep +1));
						viewState.setVisibleLocales(null);
						redrawTreeViewer();
					}
				}
			}
		};
		
		selectResourceBundle.setText("Resource-Bundle ...");
		selectResourceBundle.setDescription("Allows you to select the Resource-Bundle which is used as message-source.");
		selectResourceBundle.setImageDescriptor(Activator.getImageDescriptor(ImageUtils.IMAGE_RESOURCE_BUNDLE));
	
		contextDependentMenu = new ResourceBundleEntry(treeViewer, !treeViewer.getViewer().getSelection().isEmpty());
		
		enableFuzzyMatching = new Action () {
			public void run () {
				super.run();
				treeViewer.enableFuzzyMatching(!treeViewer.isFuzzyMatchingEnabled());
				viewState.setFuzzyMatchingEnabled(treeViewer.isFuzzyMatchingEnabled());
				refreshSearchbarState();
			}
		};
		enableFuzzyMatching.setText("Fuzzy-Matching");
		enableFuzzyMatching.setDescription("Enables Fuzzy matching for searching Resource-Bundle entries.");
		enableFuzzyMatching.setChecked(viewState.isFuzzyMatchingEnabled());
		enableFuzzyMatching.setToolTipText(enableFuzzyMatching.getDescription());
		
		editable = new Action () {
			public void run () {
				super.run();
				treeViewer.setEditable(!treeViewer.isEditable());
				viewState.setEditable(treeViewer.isEditable());
			}
		};
		editable.setText("Editable");
		editable.setDescription("Allows you to edit Resource-Bundle entries.");
		editable.setChecked(viewState.isEditable());
		editable.setToolTipText(editable.getDescription());
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalPullDown(IMenuManager manager) {
		manager.removeAll();
		manager.add(selectResourceBundle);
		manager.add(enableFuzzyMatching);
		manager.add(editable);
		manager.add(new Separator());
		
		manager.add(contextDependentMenu);
		manager.add(new Separator());
		
		if (visibleLocaleActions == null) return;
		
		for (Action loc : visibleLocaleActions) {
			manager.add(loc);
		}
	}

	/*** CONTEXT MENU ***/
	private void hookContextMenu() {
		new UIJob("set PopupMenu"){
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				MenuManager menuMgr = new MenuManager("#PopupMenu");
				menuMgr.setRemoveAllWhenShown(true);
				menuMgr.addMenuListener(new IMenuListener() {
					public void menuAboutToShow(IMenuManager manager) {
						fillContextMenu(manager);
					}
				});
				Menu menu = menuMgr.createContextMenu(treeViewer.getViewer().getControl());
				treeViewer.getViewer().getControl().setMenu(menu);
				getViewSite().registerContextMenu(menuMgr, treeViewer.getViewer());
				
				return Status.OK_STATUS;
			}
		}.schedule();
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.removeAll();
		manager.add(selectResourceBundle);
		manager.add(enableFuzzyMatching);
		manager.add(editable);
		manager.add(new Separator());
		
		manager.add(new ResourceBundleEntry(treeViewer, !treeViewer.getViewer().getSelection().isEmpty()));
		manager.add(new Separator());
		
		for (Action loc : visibleLocaleActions) {
			manager.add(loc);
		}
		// Other plug-ins can contribute there actions here
		//manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(selectResourceBundle);
	}
	
	@Override
	public void saveState (IMemento memento) {
		super.saveState(memento);
		try {
			viewState.setEditable (treeViewer.isEditable());
			viewState.setSortings(treeViewer.getSortInfo());
			viewState.setSearchString(treeViewer.getSearchString());
			viewState.setFuzzyMatchingEnabled(treeViewer.isFuzzyMatchingEnabled());
			viewState.setMatchingPrecision (treeViewer.getMatchingPrecision());
			viewState.saveState(memento);
		} catch (Exception e) {}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
		
		// init Viewstate
		viewState = new MessagesViewState(null, null, false, null);
		viewState.init(memento);
	}

	@Override
	public void resourceBundleChanged(ResourceBundleChangedEvent event) {
		try {
			if (!event.getBundle().equals(treeViewer.getResourceBundle()))
				return;
			
			switch (event.getType()) {
				/*case ResourceBundleChangedEvent.ADDED:
					if ( viewState.getSelectedProjectName().trim().length() > 0 ) {
						try {
							ResourceBundleManager.getManager(viewState.getSelectedProjectName())
								.unregisterResourceBundleChangeListener(viewState.getSelectedBundleId(), this);
						} catch (Exception e) {}
					}
					
					new Thread(new Runnable() {
						
					      public void run() {
					         try { Thread.sleep(500); } catch (Exception e) { }
					            Display.getDefault().asyncExec(new Runnable() {
					               public void run() {
					            	   try {
					            		   redrawTreeViewer();
					            	   } catch (Exception e) { e.printStackTrace(); }
					               }
					            });
					         
					      }
					   }).start();
					break; */
				case ResourceBundleChangedEvent.ADDED:
					// update visible locales within the context menu
					makeVisibleLocalesActions();
					hookContextMenu();
					break;
				case ResourceBundleChangedEvent.DELETED:
				case ResourceBundleChangedEvent.EXCLUDED:
					if ( viewState.getSelectedProjectName().trim().length() > 0 ) {
						try {
							ResourceBundleManager.getManager(viewState.getSelectedProjectName())
								.unregisterResourceBundleChangeListener(viewState.getSelectedBundleId(), this);
							
						} catch (Exception e) {}
					}
					viewState = new MessagesViewState(null, null, false, null);
					
					new Thread(new Runnable() {
						
					      public void run() {
					         try { Thread.sleep(500); } catch (Exception e) { }
					            Display.getDefault().asyncExec(new Runnable() {
					               public void run() {
					            	   try {
					            		   redrawTreeViewer();
					            	   } catch (Exception e) { Logger.logError(e); }
					               }
					            });
					         
					      }
					   }).start();
			}
		} catch (Exception e) {
			Logger.logError(e);
		}
	}
	
	@Override
	public void dispose(){
		try {
			super.dispose();
			treeViewer.dispose();
			ResourceBundleManager.getManager(viewState.getSelectedProjectName()).unregisterResourceBundleChangeListener(viewState.getSelectedBundleId(), this);
		} catch (Exception e) {}
	}
}