package org.eclipse.babel.tapiji.translator.rbe.babel.bundle;



public interface IAbstractKeyTreeModel {

    IKeyTreeNode[] getChildren(IKeyTreeNode node);
    
    IKeyTreeNode getChild(String key);
    
    IKeyTreeNode[] getRootNodes();
    
    IKeyTreeNode getRootNode();
    
    IKeyTreeNode getParent(IKeyTreeNode node);
    
    void accept(IKeyTreeVisitor visitor, IKeyTreeNode node);
    
}
