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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.babel.core.util.BabelUtils;

/**
 * Class representing a node in the tree of keys.
 *
 * @author Pascal Essiembre
 */
public class KeyTreeNode implements Comparable<KeyTreeNode> {

    public static final KeyTreeNode[] EMPTY_KEY_TREE_NODES =
            new KeyTreeNode[] {};
    
    /**
     * the parent node, if any, which will have a <code>messageKey</code> field
     * the same as this object but with the last component (following the
     * last period) removed
     */
    private final KeyTreeNode parent;
    
    /**
     * the name, being the part of the full key that follows the last period
     */
    private final String name;
    
    /**
     * the full key, being a sequence of names separated by periods with the
     * last name being the name given by the <code>name</code> field of this
     * object 
     */
    private String messageKey;
    
    private final Map<String, KeyTreeNode> children = new TreeMap<String, KeyTreeNode>();
    
    /**
     * Constructor.
     * @param parent parent node
     * @param name node name
     * @param messageKey messages bundle key
     */
    public KeyTreeNode(
    		KeyTreeNode parent, String name, String messageKey) {
        super();
        this.parent = parent;
        this.name = name;
        this.messageKey = messageKey;
        if (parent != null) {
            parent.addChild(this);
        }
    }

    /**
     * @return the name, being the part of the full key that follows the last period
     */
    public String getName() {
        return name;
    }

    /**
     * @return the parent node, if any, which will have a <code>messageKey</code> field
     * the same as this object but with the last component (following the
     * last period) removed
     */
    public KeyTreeNode getParent() {
        return parent;
    }

    /**
     * @return the full key, being a sequence of names separated by periods with the
     * last name being the name given by the <code>name</code> field of this
     * object 
     */
    public String getMessageKey() {
        return messageKey;
    }
    
    /**
     * Gets all notes from root to this node.
     * @return all notes from root to this node
     */
    /*default*/ KeyTreeNode[] getPath() {
        List<KeyTreeNode> nodes = new ArrayList<KeyTreeNode>();
        KeyTreeNode node = this;
        while (node != null && node.getName() != null) {
            nodes.add(0, node);
            node = node.getParent();
        }
        return nodes.toArray(EMPTY_KEY_TREE_NODES);
    }

    /*default*/ KeyTreeNode[] getChildren() {
        return children.values().toArray(EMPTY_KEY_TREE_NODES);
    }
    /*default*/ boolean hasChildren() {
        return !children.isEmpty();
    }
    /*default*/ KeyTreeNode getChild(String childName) {
        return children.get(childName);
    }
    
    /**
     * @return the children without creating a new object
     */
    Collection<KeyTreeNode> getChildrenInternal() {
    	return children.values();
    }
    
    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(KeyTreeNode node) {
        if (parent == null && node.parent != null) {
            return -1;
        }
        if (parent != null && node.parent == null) {
            return 1;
        }
        return name.compareTo(node.name);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof KeyTreeNode)) {
            return false;
        }
        KeyTreeNode node = (KeyTreeNode) obj;
        return BabelUtils.equals(name, node.name)
                && BabelUtils.equals(parent, node.parent);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "KeyTreeNode=[[parent=" + parent //$NON-NLS-1$
              + "][name=" + name //$NON-NLS-1$
              + "][messageKey=" + messageKey + "]]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*default*/ void addChild(KeyTreeNode childNode) {
        children.put(childNode.getName(), childNode);
    }
    /*default*/ void removeChild(KeyTreeNode childNode) {
        children.remove(childNode.getName());
        //TODO remove parent on child node?
    }

	public Collection<KeyTreeNode> getDescendants() {
		Collection<KeyTreeNode> descendants = new ArrayList<KeyTreeNode>();
		for (KeyTreeNode child : children.values()) {
			descendants.add(child);
			descendants.addAll(child.getDescendants());
		}
		return descendants;
	}
    
}
