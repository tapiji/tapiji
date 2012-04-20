/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;


public class FragmentProjectSelectionDialog extends ListDialog{
	private IProject hostproject;
	private List<IProject> allProjects;
	
	
	public FragmentProjectSelectionDialog(Shell parent, IProject hostproject, List<IProject> fragmentprojects){
		super(parent);
		this.hostproject = hostproject;
		this.allProjects = new ArrayList<IProject>(fragmentprojects);
		allProjects.add(0,hostproject);
		
		init();
	}

	private void init() {
		this.setAddCancelButton(true);
		this.setMessage("Select one of the following plug-ins:");
		this.setTitle("Project Selector");
		this.setContentProvider(new IProjectContentProvider());
		this.setLabelProvider(new IProjectLabelProvider());
		
		this.setInput(allProjects);
	}

	public IProject getSelectedProject() {
		Object[] selection = this.getResult();
		if (selection != null && selection.length > 0)
			return (IProject) selection[0];
		return null;
	}

	
	//private classes--------------------------------------------------------
	class IProjectContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			List<IProject> resources = (List<IProject>) inputElement;
			return resources.toArray();
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
		}
		
	}
	
	class IProjectLabelProvider implements ILabelProvider {

		@Override
		public Image getImage(Object element) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_PROJECT);
		}

		@Override
		public String getText(Object element) {
			IProject p = ((IProject) element);
			String text = p.getName();
			if (p.equals(hostproject)) text += " [host project]";
			else text += " [fragment project]";
			return text;
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
