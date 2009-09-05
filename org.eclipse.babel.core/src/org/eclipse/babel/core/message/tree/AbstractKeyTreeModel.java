/*******************************************************************************
 * Copyright (c) 2007 Pascal Essiembre.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pascal Essiembre - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.core.message.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.babel.core.message.MessagesBundleGroup;
import org.eclipse.babel.core.message.tree.visitor.IKeyTreeVisitor;


/**
 * Hierarchical representation of all keys making up a 
 * {@link MessagesBundleGroup}.

 * @author Pascal Essiembre
 */
public abstract class AbstractKeyTreeModel {

    private List<IKeyTreeModelListener> listeners = new ArrayList<IKeyTreeModelListener>();
    private Comparator<KeyTreeNode> comparator;
    
    protected static final KeyTreeNode[] EMPTY_NODES = new KeyTreeNode[]{};

    /**
     * Adds a key tree model listener.
     * @param listener key tree model listener
     */
    public void addKeyTreeModelListener(IKeyTreeModelListener listener) {
        listeners.add(0, listener);
    }

    /**
     * Removes a key tree model listener.
     * @param listener key tree model listener
     */
    public void removeKeyTreeModelListener(IKeyTreeModelListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that a node was added.
     * @param node added node
     */
    protected void fireNodeAdded(KeyTreeNode node)  {
        for (IKeyTreeModelListener listener : listeners) {
            listener.nodeAdded(node);
        }
    }
    /**
     * Notify all listeners that a node was removed.
     * @param node removed node
     */
    protected void fireNodeRemoved(KeyTreeNode node)  {
    	for (IKeyTreeModelListener listener : listeners) {
            listener.nodeRemoved(node);
        }
    }

    /**
     * Gets all nodes on a branch, starting (and including) with parent node.
     * This has the same effect of calling <code>getChildren(KeyTreeNode)</code>
     * recursively on all children.
     * @param parentNode root of a branch
     * @return all nodes on a branch
     */
    public KeyTreeNode[] getBranch(KeyTreeNode parentNode) {
        Set<KeyTreeNode> childNodes = new TreeSet<KeyTreeNode>();
        childNodes.add(parentNode);
        for (KeyTreeNode childNode : getChildren(parentNode)) {
            childNodes.addAll(
                    Arrays.asList(getBranch(childNode)));
        }
        return childNodes.toArray(EMPTY_NODES);
    }

    /**
     * Accepts the visitor, visiting the given node argument, along with all
     * its children.  Passing a <code>null</code> node will
     * walk the entire tree.
     * @param visitor the object to visit
     * @param node the starting key tree node
     */
    public void accept(IKeyTreeVisitor visitor, KeyTreeNode node) {
        if (node != null) {
            visitor.visitKeyTreeNode(node);
        }
        KeyTreeNode[] nodes = getChildren(node);
        for (int i = 0; i < nodes.length; i++) {
            accept(visitor, nodes[i]);
        }
    }

    public abstract KeyTreeNode[] getChildren(KeyTreeNode node);

	/**
     * Gets the comparator.
     * @return the comparator
     */
    public Comparator<KeyTreeNode> getComparator() {
        return comparator;
    }

    /**
     * Sets the node comparator for sorting sibling nodes.
     * @param comparator node comparator
     */
    public void setComparator(Comparator<KeyTreeNode> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * Depth first for the first leaf node that is not filtered.
     * It makes the entire branch not not filtered
     * 
     * @param filter The leaf filter.
     * @param node
     * @return true if this node or one of its descendant is in the filter (ie is displayed)
     */
    public boolean isBranchFiltered(IKeyTreeNodeLeafFilter filter, KeyTreeNode node) {
    	if (!node.hasChildren()) {
    		return filter.isFilteredLeaf(node);
    	} else {
    		//depth first:
    		for (KeyTreeNode childNode : node.getChildrenInternal()) {
    			if (isBranchFiltered(filter, childNode)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    
    
    public interface IKeyTreeNodeLeafFilter {
    	/**
    	 * @param leafNode A leaf node. Must not be called if the node has children
    	 * @return true if this node should be filtered.
    	 */
    	boolean isFilteredLeaf(KeyTreeNode leafNode);
    }
    
}
