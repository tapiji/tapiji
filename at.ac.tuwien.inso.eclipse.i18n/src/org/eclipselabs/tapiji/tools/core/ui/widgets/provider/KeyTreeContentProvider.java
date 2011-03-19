/*
 * Copyright (C) 2003, 2004  Pascal Essiembre, Essiembre Consultant Inc.
 * 
 * This file is part of Essiembre ResourceBundle Editor.
 * 
 * Essiembre ResourceBundle Editor is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * Essiembre ResourceBundle Editor is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Essiembre ResourceBundle Editor; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
package org.eclipselabs.tapiji.tools.core.ui.widgets.provider;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.tapiji.translator.rbe.model.tree.IKeyTree;
import org.eclipselabs.tapiji.translator.rbe.model.tree.IKeyTreeItem;

/**
 * Content provider for key tree viewer.
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author: nl_carnage $ $Revision: 1.7 $ $Date: 2007/09/12 15:38:36 $
 */
public class KeyTreeContentProvider implements 
        ITreeContentProvider {

    /** Represents empty objects. */
    private static Object[] EMPTY_ARRAY = new Object[0];
    /** Viewer this provided act upon. */
    protected TreeViewer treeViewer;
    
    /**
     * @see ITreeContentProvider#dispose()
     */
    public void dispose() {}

    /**
     * @see ITreeContentProvider#getChildren(Object)
     */
    public Object[] getChildren(Object parentElement) {
        if(parentElement instanceof IKeyTree) {
            return ((IKeyTree) parentElement).getRootKeyItems().toArray(); 
        } else if (parentElement instanceof IKeyTreeItem) {
            return ((IKeyTreeItem) parentElement).getChildren().toArray(); 
        }
        return EMPTY_ARRAY;
    }
    
    /**
     * @see ITreeContentProvider#getParent(Object)
     */
    public Object getParent(Object element) {
        if(element instanceof IKeyTreeItem) {
            return ((IKeyTreeItem) element).getParent();
        }
        return null;
    }

    /**
     * @see ITreeContentProvider#hasChildren(Object)
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /**
     * @see ITreeContentProvider#getElements(Object)
     */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }
    
    /**
     * Gets the selected key tree item.
     * @return key tree item
     */
    private IKeyTreeItem getTreeSelection() {
        IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
        return ((IKeyTreeItem) selection.getFirstElement());
    }

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}
    
}
