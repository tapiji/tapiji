/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer, Matthias Lettmayer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Matthias Lettmayer - adapt setInput, so only existing RB get displayed (fixed issue 40)
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.dialogs;

import java.util.List;

import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.tapiji.tools.core.ui.utils.ImageUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

public class ResourceBundleSelectionDialog extends ListDialog {

    private IProject project;

    public ResourceBundleSelectionDialog(Shell parent, IProject project) {
	super(parent);
	this.project = project;

	initDialog();
    }

    protected void initDialog() {
	this.setAddCancelButton(true);
	this.setMessage("Select one of the following Resource-Bundle to open:");
	this.setTitle("Resource-Bundle Selector");
	this.setContentProvider(new RBContentProvider());
	this.setLabelProvider(new RBLabelProvider());
	this.setBlockOnOpen(true);

	if (project != null)
	    this.setInput(RBManager.getInstance(project)
		    .getMessagesBundleGroupNames());
	else
	    this.setInput(RBManager.getAllMessagesBundleGroupNames());
    }

    public String getSelectedBundleId() {
	Object[] selection = this.getResult();
	if (selection != null && selection.length > 0)
	    return (String) selection[0];
	return null;
    }

    class RBContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
	    List<String> resources = (List<String>) inputElement;
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

    class RBLabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
	    // TODO Auto-generated method stub
	    return ImageUtils.getImage(ImageUtils.IMAGE_RESOURCE_BUNDLE);
	}

	@Override
	public String getText(Object element) {
	    // TODO Auto-generated method stub
	    return ((String) element);
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
