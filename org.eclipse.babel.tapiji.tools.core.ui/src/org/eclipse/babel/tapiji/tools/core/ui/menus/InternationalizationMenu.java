/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.menus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.builder.InternationalizationNature;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.AddLanguageDialoge;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.FragmentProjectSelectionDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.GenerateBundleAccessorDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.RemoveLanguageDialoge;
import org.eclipse.babel.tapiji.tools.core.ui.utils.LanguageUtils;
import org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils;
import org.eclipse.babel.tapiji.tools.core.util.FragmentProjectUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class InternationalizationMenu extends ContributionItem {
	private boolean excludeMode = true;
	private boolean internationalizationEnabled = false;

	private MenuItem mnuToggleInt;
	private MenuItem excludeResource;
	private MenuItem addLanguage;
	private MenuItem removeLanguage;

	public InternationalizationMenu() {
	}

	public InternationalizationMenu(String id) {
		super(id);
	}

	@Override
	public void fill(Menu menu, int index) {
		if (getSelectedProjects().size() == 0 || !projectsSupported()) {
			return;
		}

		// Toggle Internatinalization
		mnuToggleInt = new MenuItem(menu, SWT.PUSH);
		mnuToggleInt.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runToggleInt();
			}

		});

		// Exclude Resource
		excludeResource = new MenuItem(menu, SWT.PUSH);
		excludeResource.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runExclude();
			}

		});

		new MenuItem(menu, SWT.SEPARATOR);

		// Add Language
		addLanguage = new MenuItem(menu, SWT.PUSH);
		addLanguage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runAddLanguage();
			}

		});

		// Remove Language
		removeLanguage = new MenuItem(menu, SWT.PUSH);
		removeLanguage.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runRemoveLanguage();
			}

		});

		menu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
				updateStateToggleInt(mnuToggleInt);
				// updateStateGenRBAccessor (generateAccessor);
				updateStateExclude(excludeResource);
				updateStateAddLanguage(addLanguage);
				updateStateRemoveLanguage(removeLanguage);
			}
		});
	}

	protected void runGenRBAccessor() {
		GenerateBundleAccessorDialog dlg = new GenerateBundleAccessorDialog(
		        Display.getDefault().getActiveShell());
		if (dlg.open() != InputDialog.OK) {
			return;
		}
	}

	protected void updateStateGenRBAccessor(MenuItem menuItem) {
		Collection<IPackageFragment> frags = getSelectedPackageFragments();
		menuItem.setEnabled(frags.size() > 0);
	}

	protected void updateStateToggleInt(MenuItem menuItem) {
		Collection<IProject> projects = getSelectedProjects();
		boolean enabled = projects.size() > 0;
		menuItem.setEnabled(enabled);
		setVisible(enabled);
		internationalizationEnabled = InternationalizationNature
		        .hasNature(projects.iterator().next());
		// menuItem.setSelection(enabled && internationalizationEnabled);

		if (internationalizationEnabled) {
			menuItem.setText("Disable Internationalization");
		} else {
			menuItem.setText("Enable Internationalization");
		}
	}

	private Collection<IPackageFragment> getSelectedPackageFragments() {
		Collection<IPackageFragment> frags = new HashSet<IPackageFragment>();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		        .getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) selection)
			        .iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (elem instanceof IPackageFragment) {
					IPackageFragment frag = (IPackageFragment) elem;
					if (!frag.isReadOnly()) {
						frags.add(frag);
					}
				}
			}
		}
		return frags;
	}

	private Collection<IProject> getSelectedProjects() {
		Collection<IProject> projects = new HashSet<IProject>();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		        .getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) selection)
			        .iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (!(elem instanceof IResource)) {
					if (!(elem instanceof IAdaptable)) {
						continue;
					}
					elem = ((IAdaptable) elem).getAdapter(IResource.class);
					if (!(elem instanceof IResource)) {
						continue;
					}
				}
				if (!(elem instanceof IProject)) {
					elem = ((IResource) elem).getProject();
					if (!(elem instanceof IProject)) {
						continue;
					}
				}
				if (((IProject) elem).isAccessible()) {
					projects.add((IProject) elem);
				}

			}
		}
		return projects;
	}

	protected boolean projectsSupported() {
		Collection<IProject> projects = getSelectedProjects();
		for (IProject project : projects) {
			if (!InternationalizationNature.supportsNature(project)) {
				return false;
			}
		}

		return true;
	}

	protected void runToggleInt() {
		Collection<IProject> projects = getSelectedProjects();
		for (IProject project : projects) {
			toggleNature(project);
		}
	}

	private void toggleNature(IProject project) {
		if (InternationalizationNature.hasNature(project)) {
			InternationalizationNature.removeNature(project);
		} else {
			InternationalizationNature.addNature(project);
		}
	}

	protected void updateStateExclude(MenuItem menuItem) {
		Collection<IResource> resources = getSelectedResources();
		menuItem.setEnabled(resources.size() > 0 && internationalizationEnabled);
		ResourceBundleManager manager = null;
		excludeMode = false;

		for (IResource res : resources) {
			if (manager == null || (manager.getProject() != res.getProject())) {
				manager = ResourceBundleManager.getManager(res.getProject());
			}
			try {
				if (!ResourceBundleManager.isResourceExcluded(res)) {
					excludeMode = true;
				}
			} catch (Exception e) {
			}
		}

		if (!excludeMode) {
			menuItem.setText("Include Resource");
		} else {
			menuItem.setText("Exclude Resource");
		}
	}

	private Collection<IResource> getSelectedResources() {
		Collection<IResource> resources = new HashSet<IResource>();
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		        .getActiveWorkbenchWindow();
		ISelection selection = window.getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) selection)
			        .iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (elem instanceof IProject) {
					continue;
				}

				if (elem instanceof IResource) {
					resources.add((IResource) elem);
				} else if (elem instanceof IJavaElement) {
					resources.add(((IJavaElement) elem).getResource());
				}
			}
		}
		return resources;
	}

	protected void runExclude() {
		final Collection<IResource> selectedResources = getSelectedResources();

		IWorkbench wb = PlatformUI.getWorkbench();
		IProgressService ps = wb.getProgressService();
		try {
			ps.busyCursorWhile(new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor pm) {

					ResourceBundleManager manager = null;
					pm.beginTask("Including resources to Internationalization",
					        selectedResources.size());

					for (IResource res : selectedResources) {
						if (manager == null
						        || (manager.getProject() != res.getProject())) {
							manager = ResourceBundleManager.getManager(res
							        .getProject());
						}
						if (excludeMode) {
							manager.excludeResource(res, pm);
						} else {
							manager.includeResource(res, pm);
						}
						pm.worked(1);
					}
					pm.done();
				}
			});
		} catch (Exception e) {
		}
	}

	protected void updateStateAddLanguage(MenuItem menuItem) {
		Collection<IProject> projects = getSelectedProjects();
		boolean hasResourceBundles = false;
		for (IProject p : projects) {
			ResourceBundleManager rbmanager = ResourceBundleManager
			        .getManager(p);
			hasResourceBundles = rbmanager.getResourceBundleIdentifiers()
			        .size() > 0 ? true : false;
		}

		menuItem.setText("Add Language To Project");
		menuItem.setEnabled(projects.size() > 0 && hasResourceBundles);
	}

	protected void runAddLanguage() {
		AddLanguageDialoge dialog = new AddLanguageDialoge(new Shell(
		        Display.getCurrent()));
		if (dialog.open() == InputDialog.OK) {
			final Locale locale = dialog.getSelectedLanguage();

			Collection<IProject> selectedProjects = getSelectedProjects();
			for (IProject project : selectedProjects) {
				// check if project is fragmentproject and continue working with
				// the hostproject, if host not member of selectedProjects
				if (FragmentProjectUtils.isFragment(project)) {
					IProject host = FragmentProjectUtils
					        .getFragmentHost(project);
					if (!selectedProjects.contains(host)) {
						project = host;
					} else {
						continue;
					}
				}

				List<IProject> fragments = FragmentProjectUtils
				        .getFragments(project);

				if (!fragments.isEmpty()) {
					FragmentProjectSelectionDialog fragmentDialog = new FragmentProjectSelectionDialog(
					        Display.getCurrent().getActiveShell(), project,
					        fragments);

					if (fragmentDialog.open() == InputDialog.OK) {
						project = fragmentDialog.getSelectedProject();
					}
				}

				final IProject selectedProject = project;
				BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
					@Override
					public void run() {
						LanguageUtils.addLanguageToProject(selectedProject,
						        locale);
					}

				});

			}
		}
	}

	protected void updateStateRemoveLanguage(MenuItem menuItem) {
		Collection<IProject> projects = getSelectedProjects();
		boolean hasResourceBundles = false;
		if (projects.size() == 1) {
			IProject project = projects.iterator().next();
			ResourceBundleManager rbmanager = ResourceBundleManager
			        .getManager(project);
			hasResourceBundles = rbmanager.getResourceBundleIdentifiers()
			        .size() > 0 ? true : false;
		}
		menuItem.setText("Remove Language From Project");
		menuItem.setEnabled(projects.size() == 1 && hasResourceBundles/*
																	 * && more
																	 * than one
																	 * common
																	 * languages
																	 * contained
																	 */);
	}

	protected void runRemoveLanguage() {
		final IProject project = getSelectedProjects().iterator().next();
		RemoveLanguageDialoge dialog = new RemoveLanguageDialoge(project,
		        new Shell(Display.getCurrent()));

		if (dialog.open() == InputDialog.OK) {
			final Locale locale = dialog.getSelectedLanguage();
			if (locale != null) {
				if (MessageDialog.openConfirm(Display.getCurrent()
				        .getActiveShell(), "Confirm",
				        "Do you really want remove all properties-files for "
				                + locale.getDisplayName() + "?")) {
					BusyIndicator.showWhile(Display.getCurrent(),
					        new Runnable() {
						        @Override
						        public void run() {
							        RBFileUtils.removeLanguageFromProject(
							                project, locale);
						        }
					        });
				}

			}
		}
	}

}
