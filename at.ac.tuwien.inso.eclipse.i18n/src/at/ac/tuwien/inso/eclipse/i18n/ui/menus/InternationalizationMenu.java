package at.ac.tuwien.inso.eclipse.i18n.ui.menus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import at.ac.tuwien.inso.eclipse.i18n.builder.InternationalizationNature;
import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.i18n.ui.dialogs.GenerateBundleAccessorDialog;

public class InternationalizationMenu extends ContributionItem {
	private boolean excludeMode = true;
	private boolean internationalizationEnabled = false;
	
	private MenuItem mnuToggleInt;
	private MenuItem excludeResource;
	//private MenuItem generateAccessor;
	
	public InternationalizationMenu() {
		// TODO Auto-generated constructor stub
	}

	public InternationalizationMenu(String id) {
		super(id);
	}

	@Override
	public void fill(Menu menu, int index) {
		if (getSelectedProjects().size() == 0)
			return;
		
		// Toggle Internatinalization
		mnuToggleInt = new MenuItem (menu, index);
		mnuToggleInt.addSelectionListener(new SelectionAdapter () {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runToggleInt();
			}

		});
				
		// Add Separator
		//new MenuItem (menu, SWT.SEPARATOR);
		
		// Generate Accessor
		/*generateAccessor = new MenuItem (menu, index);
		generateAccessor.setText("Generate Bundle-Accessor ...");
		generateAccessor.setEnabled(false);
		generateAccessor.addSelectionListener(new SelectionAdapter () {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runGenRBAccessor();
			}

		});*/
		
		// Exclude Resource
		excludeResource = new MenuItem (menu, index+1);
		excludeResource.addSelectionListener(new SelectionAdapter () {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runExclude();
			}

		});
		menu.addMenuListener(new MenuAdapter () {
			public void menuShown (MenuEvent e) {
				updateStateToggleInt (mnuToggleInt);
				//updateStateGenRBAccessor (generateAccessor);
				updateStateExclude (excludeResource);
			}
		});
	}
	
	protected void runGenRBAccessor () {
		GenerateBundleAccessorDialog dlg = new GenerateBundleAccessorDialog(Display.getDefault().getActiveShell());
		if (dlg.open() != InputDialog.OK)
			return;
	}
	
	protected void updateStateGenRBAccessor (MenuItem menuItem) {
		Collection<IPackageFragment> frags = getSelectedPackageFragments();
		menuItem.setEnabled(frags.size() > 0);
	}
	
	protected void updateStateToggleInt (MenuItem menuItem) {
		Collection<IProject> projects = getSelectedProjects();
		boolean enabled = projects.size() > 0;
		menuItem.setEnabled(enabled);
		setVisible(enabled);
		internationalizationEnabled = InternationalizationNature.hasNature(projects.iterator().next());
		//menuItem.setSelection(enabled && internationalizationEnabled);
		
		if (internationalizationEnabled)
			menuItem.setText("Disable Internationalization");
		else 
			menuItem.setText("Enable Internationalization");
	}

	private Collection<IPackageFragment> getSelectedPackageFragments () {
		Collection<IPackageFragment> frags = new HashSet<IPackageFragment> ();
		IWorkbenchWindow window = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection ();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection)selection).iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (elem instanceof IPackageFragment) {
					IPackageFragment frag = (IPackageFragment) elem;
					if (!frag.isReadOnly())
						frags.add (frag);
				}
			}
		}
		return frags;
	}
	
	private Collection<IProject> getSelectedProjects () {
		Collection<IProject> projects = new HashSet<IProject> ();
		IWorkbenchWindow window = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection ();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection)selection).iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (!(elem instanceof IResource)) {
					if (!(elem instanceof IAdaptable))
						continue;
					elem = ((IAdaptable) elem).getAdapter (IResource.class);
					if (!(elem instanceof IResource))
						continue;
				}
				if (!(elem instanceof IProject)) {
					elem = ((IResource) elem).getProject();
					if (!(elem instanceof IProject))
						continue;
				}
				if (((IProject)elem).isAccessible())
					projects.add ((IProject)elem);
				
			}
		}
		return projects;
	}
	
	protected void runToggleInt () {
		Collection<IProject> projects = getSelectedProjects ();
		for (IProject project : projects) {
			toggleNature (project);
		}
	}
	
	private void toggleNature (IProject project) {
		if (InternationalizationNature.hasNature (project)) {
			InternationalizationNature.removeNature (project);
		} else {
			InternationalizationNature.addNature (project);
		}
	}	
	protected void updateStateExclude (MenuItem menuItem) {
		Collection<IResource> resources = getSelectedResources();
		menuItem.setEnabled(resources.size() > 0 && internationalizationEnabled);
		ResourceBundleManager manager = null;
		excludeMode = false;
		
		for (IResource res : resources) {
			if (manager == null || (manager.getProject() != res.getProject()))
				manager = ResourceBundleManager.getManager(res.getProject());
			try {
				if (!ResourceBundleManager.isResourceExcluded(res)) {
					excludeMode = true;
				}
			} catch (Exception e) {	}
		}
		
		if (!excludeMode)
			menuItem.setText("Include Resource");
		else 
			menuItem.setText("Exclude Resource");
	}
	
	private Collection<IResource> getSelectedResources () {
		Collection<IResource> resources = new HashSet<IResource> ();
		IWorkbenchWindow window = 
			PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection ();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection)selection).iterator(); iter.hasNext();) {
				Object elem = iter.next();
				/*if (!(elem instanceof IResource || elem instanceof IJavaElement)) {
					if (!(elem instanceof IAdaptable))
						continue;
					elem = ((IAdaptable) elem).getAdapter (IResource.class);
					if (!(elem instanceof IResource || elem instanceof IJavaElement))
						continue;
				}*/
				if (elem instanceof IProject)
					continue;
				
				if (elem instanceof IResource) {
					resources.add ((IResource)elem);
				} else if (elem instanceof IJavaElement) {
					resources.add (((IJavaElement)elem).getResource());
				}
			}
		}
		return resources;
	}
	
	protected void runExclude () {
		final Collection<IResource> selectedResources = getSelectedResources ();
		
		IWorkbench wb = PlatformUI.getWorkbench();
		IProgressService ps = wb.getProgressService();
		try {
			ps.busyCursorWhile(new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) {
			
					ResourceBundleManager manager = null;
					pm.beginTask("Including resources to Internationalization", selectedResources.size());
					
					for (IResource res : selectedResources) {
						if (manager == null || (manager.getProject() != res.getProject()))
							manager = ResourceBundleManager.getManager(res.getProject());
						if (excludeMode)
							manager.excludeResource(res, pm);
						else
							manager.includeResource(res, pm);
						pm.worked(1);
					}
					pm.done();
				}
			});
		} catch (Exception e) {}
	}


	
}
