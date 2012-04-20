/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.widgets.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.editor.api.KeyTreeFactory;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IAbstractKeyTreeModel;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeVisitor;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.TreeType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;



public class ResKeyTreeContentProvider implements ITreeContentProvider {

    private IAbstractKeyTreeModel keyTreeModel;
    private Viewer viewer; 
    
    private TreeType treeType = TreeType.Tree;
    
    /** Viewer this provided act upon. */
    protected TreeViewer treeViewer;
    
	private List<Locale> locales;
	private String bundleId;
	private String projectName;
	
	public ResKeyTreeContentProvider (List<Locale> locales, String projectName, String bundleId, TreeType treeType) {
		this.locales = locales;
		this.projectName = projectName;
		this.bundleId = bundleId;
		this.treeType = treeType;
	}
	
	public void setBundleId (String bundleId) {
		this.bundleId = bundleId;
	}
	
	public void setProjectName (String projectName) {
		this.projectName = projectName;
	}
		
	public ResKeyTreeContentProvider() {
		locales = new ArrayList<Locale>();
	}

	public void setLocales (List<Locale> locales) {
		this.locales = locales;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
	    IKeyTreeNode parentNode = (IKeyTreeNode) parentElement;
        switch (treeType) {
        case Tree:
            return convertKTItoVKTI(keyTreeModel.getChildren(parentNode));
        case Flat:
            return new IKeyTreeNode[0];
        default:
            // Should not happen
            return new IKeyTreeNode[0];
        }
	}
	
	protected Object[] convertKTItoVKTI (Object[] children) {
		Collection<IValuedKeyTreeNode> items = new ArrayList<IValuedKeyTreeNode>();
		IMessagesBundleGroup messagesBundleGroup = RBManager.getInstance(this.projectName).getMessagesBundleGroup(this.bundleId);
		
		for (Object o : children) {
			if (o instanceof IValuedKeyTreeNode)
				items.add((IValuedKeyTreeNode)o);
			else {
			    IKeyTreeNode kti = (IKeyTreeNode) o;
			    IValuedKeyTreeNode vkti = KeyTreeFactory.createKeyTree(kti.getParent(), kti.getName(), kti.getMessageKey(), messagesBundleGroup);

			    for (IKeyTreeNode k : kti.getChildren()) {
					vkti.addChild(k);
				}
				
				// init translations
				for (Locale l : locales) {
					try {
					    IMessage message = messagesBundleGroup.getMessagesBundle(l).getMessage(kti.getMessageKey());
					    if (message != null) {
					        vkti.addValue(l, message.getValue());
					    }
					} catch (Exception e) {}
				}
				items.add(vkti);
			}
		}
		
		return items.toArray();
	}

	@Override
	public Object[] getElements(Object inputElement) {
	    switch (treeType) {
	        case Tree:
	            return convertKTItoVKTI(keyTreeModel.getRootNodes());
	        case Flat:
	            final Collection<IKeyTreeNode> actualKeys = new ArrayList<IKeyTreeNode>();
	            IKeyTreeVisitor visitor = new IKeyTreeVisitor() {
	                public void visitKeyTreeNode(IKeyTreeNode node) {
	                    if (node.isUsedAsKey()) {
	                        actualKeys.add(node);
	                    }
	                }
	            };
	            keyTreeModel.accept(visitor, keyTreeModel.getRootNode());
	            
	            return actualKeys.toArray(); 
	        default:
	            // Should not happen
	            return new IKeyTreeNode[0];
	        }  
	}

	@Override
    public Object getParent(Object element) {
	    IKeyTreeNode node = (IKeyTreeNode) element;
        switch (treeType) {
        case Tree:
            return keyTreeModel.getParent(node);
        case Flat:
            return keyTreeModel;
        default:
            // Should not happen
            return null;
        }
	}
	
	 /**
     * @see ITreeContentProvider#hasChildren(Object)
     */
    public boolean hasChildren(Object element) {
        switch (treeType) {
            case Tree:
                return keyTreeModel.getChildren((IKeyTreeNode) element).length > 0;
            case Flat:
                return false;
            default:
                // Should not happen
                return false;
         }
    }
    
    public int countChildren(Object element) {
        
        if (element instanceof IKeyTreeNode) {
            return ((IKeyTreeNode)element).getChildren().length;
        } else if (element instanceof IValuedKeyTreeNode) {
            return ((IValuedKeyTreeNode)element).getChildren().length;
        } else {
            System.out.println("wait a minute");
            return 1;
        }
    }
	
	/**
     * Gets the selected key tree item.
     * @return key tree item
     */
    private IKeyTreeNode getTreeSelection() {
        IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
        return ((IKeyTreeNode) selection.getFirstElement());
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer) viewer;
        this.keyTreeModel = (IAbstractKeyTreeModel) newInput;
    }
	
	public IMessagesBundleGroup getBundle() {
		return RBManager.getInstance(projectName).getMessagesBundleGroup(this.bundleId);
	}

	public String getBundleId() {
		return bundleId;
	}

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }
    
    public TreeType getTreeType() {
        return treeType;
    }

    public void setTreeType(TreeType treeType) {
        if (this.treeType != treeType) {
            this.treeType = treeType;
            if (viewer != null) {
            	viewer.refresh();
            }
        }
    }
}
