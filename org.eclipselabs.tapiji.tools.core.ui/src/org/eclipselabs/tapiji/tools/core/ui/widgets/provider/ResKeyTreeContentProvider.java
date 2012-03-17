package org.eclipselabs.tapiji.tools.core.ui.widgets.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.eclipse.babel.editor.api.KeyTreeFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IAbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IKeyTreeVisitor;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.TreeType;



public class ResKeyTreeContentProvider implements ITreeContentProvider {

    private IAbstractKeyTreeModel keyTreeModel;
    private Viewer viewer; 
    
    private TreeType treeType = TreeType.Tree;
    
    /** Represents empty objects. */
    private static Object[] EMPTY_ARRAY = new Object[0];
    /** Viewer this provided act upon. */
    protected TreeViewer treeViewer;
    
	private IMessagesBundleGroup bundle;
	private List<Locale> locales;
	private ResourceBundleManager manager;
	private String bundleId;
	
	
	public ResKeyTreeContentProvider (IMessagesBundleGroup iBundleGroup, List<Locale> locales, 
	                                  ResourceBundleManager manager, String bundleId, TreeType treeType) {
		this.bundle = iBundleGroup;
		this.locales = locales;
		this.manager = manager;
		this.bundleId = bundleId;
		this.treeType = treeType;
	}
	
	public void setBundleGroup (IMessagesBundleGroup iBundleGroup) {
		this.bundle = iBundleGroup;
	}
		
	public ResKeyTreeContentProvider() {
		locales = new ArrayList<Locale>();
	}

	public void setLocales (List<Locale> locales) {
		this.locales = locales;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
	    // brauche sie als VKTI
//	    if(parentElement instanceof IAbstractKeyTreeModel) {
//            IAbstractKeyTreeModel model = (IAbstractKeyTreeModel) parentElement;
//            return convertKTItoVKTI(model.getRootNodes()); 
//	    } else if (parentElement instanceof IValuedKeyTreeNode) { // convert because we hold the children as IKeyTreeNodes
//	        return convertKTItoVKTI(((IValuedKeyTreeNode) parentElement).getChildren());
//        } 
	    //new code
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
	    //new code
//        return EMPTY_ARRAY;
	}
	
	protected Object[] convertKTItoVKTI (Object[] children) {
		Collection<IValuedKeyTreeNode> items = new ArrayList<IValuedKeyTreeNode>();
		
		for (Object o : children) {
			if (o instanceof IValuedKeyTreeNode)
				items.add((IValuedKeyTreeNode)o);
			else {
			    IKeyTreeNode kti = (IKeyTreeNode) o;
			    IValuedKeyTreeNode vkti = KeyTreeFactory.createKeyTree(kti.getParent(), kti.getName(), kti.getMessageKey(), bundle);

			    for (IKeyTreeNode k : kti.getChildren()) {
					vkti.addChild(k);
				}
				
				// init translations
				for (Locale l : locales) {
					try {
					    IMessage message = bundle.getMessagesBundle(l).getMessage(kti.getMessageKey());
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
//		return getChildren(inputElement);
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
//        Object[] parent = new Object[1];
//        
//        if(element instanceof IKeyTreeNode) {
//            return ((IKeyTreeNode) element).getParent();
//        }
//
//        if (parent[0] == null)
//            return null;
//
//        Object[] result = convertKTItoVKTI(parent);
//        if (result.length > 0)
//            return result[0];
//        else
//            return null;
	    
	    // new code
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
	    // new code
	}
	
	 /**
     * @see ITreeContentProvider#hasChildren(Object)
     */
    public boolean hasChildren(Object element) {
//        return countChildren(element) > 0;
        
        // new code
        switch (treeType) {
            case Tree:
                return keyTreeModel.getChildren((IKeyTreeNode) element).length > 0;
            case Flat:
                return false;
            default:
                // Should not happen
                return false;
         }
        // new code
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
		return bundle;
	}

	public ResourceBundleManager getManager() {
		return manager;
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
            viewer.refresh();
        }
    }
}
