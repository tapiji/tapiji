package com.gknsintermetals.eclipse.resourcebundle.manager.viewer.toolbarItems;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.progress.UIJob;

public class ExpandAllActionDelegate implements IViewActionDelegate {
	private CommonViewer viewer;
	
	@Override
	public void run(IAction action) {
		Object data = viewer.getControl().getData();	

		for(final IProject p : ((IWorkspaceRoot)data).getProjects()){
			UIJob job = new UIJob("expand Projects") {
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					viewer.expandToLevel(p, AbstractTreeViewer.ALL_LEVELS);
					return Status.OK_STATUS;
				}
			};
			
			job.schedule();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(IViewPart view) {
		viewer = ((CommonNavigator) view).getCommonViewer();
	}

}
