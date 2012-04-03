package org.eclipse.babel.tapiji.translator.rbe.babel.bundle;

import org.eclipse.jface.viewers.TreeViewer;


public interface IKeyTreeContributor {

    void contribute(final TreeViewer treeViewer);
    
    IKeyTreeNode getKeyTreeNode(String key);
    
    IKeyTreeNode[] getRootKeyItems();
}
