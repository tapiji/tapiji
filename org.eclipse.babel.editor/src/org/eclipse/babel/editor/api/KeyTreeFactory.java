package org.eclipse.babel.editor.api;

import org.eclipse.babel.core.message.MessagesBundleGroup;
import org.eclipse.babel.core.message.tree.AbstractKeyTreeModel;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IAbstractKeyTreeModel;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;


/**
 * Factory class for the tree or nodes of the tree.
 * @see IAbstractKeyTreeModel
 * @see IValuedKeyTreeNode
 * <br><br>
 * 
 * @author Alexej Strelzow
 */
public class KeyTreeFactory {

	/**
	 * @param messagesBundleGroup Input of the key tree model
	 * @return The {@link IAbstractKeyTreeModel}
	 */
    public static IAbstractKeyTreeModel createModel(IMessagesBundleGroup messagesBundleGroup) {
        return new AbstractKeyTreeModel((MessagesBundleGroup)messagesBundleGroup);
    }
    
    /**
     * @param parent The parent node
     * @param name The name of the node
     * @param id The id of the node (messages key)
     * @param bundleGroup The {@link IMessagesBundleGroup} 
     * @return A new instance of {@link IValuedKeyTreeNode}
     */
    public static IValuedKeyTreeNode createKeyTree(IKeyTreeNode parent, String name, String id, 
    		IMessagesBundleGroup bundleGroup) {
        return new ValuedKeyTreeNode(parent, name, id, bundleGroup);
    }
    
}
