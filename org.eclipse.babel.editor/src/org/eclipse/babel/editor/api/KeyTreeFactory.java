package org.eclipse.babel.editor.api;

import org.eclipse.babel.core.message.MessagesBundleGroup;
import org.eclipse.babel.core.message.tree.AbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IAbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;



public class KeyTreeFactory {

    public static IAbstractKeyTreeModel createModel(IMessagesBundleGroup messagesBundleGroup) {
        return new AbstractKeyTreeModel((MessagesBundleGroup)messagesBundleGroup);
    }
    
    public static IValuedKeyTreeNode createKeyTree(IKeyTreeNode parent, String name, String id, IMessagesBundleGroup bundle) {
        return new ValuedKeyTreeNode(parent, name, id, bundle);
    }
    
}
