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
package org.eclipselabs.tapiji.translator.rap.babel.editor.tree.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.internal.MessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.tree.internal.AbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.tree.internal.KeyTreeNode;
import org.eclipselabs.tapiji.translator.rap.babel.editor.internal.MessagesEditor;


/**
 * @author Pascal Essiembre
 *
 */
public abstract class AbstractTreeAction extends Action {

//    private static final KeyTreeNode[] EMPTY_TREE_NODES = new KeyTreeNode[]{};
    
    protected final TreeViewer treeViewer;
    protected final MessagesEditor editor;
    
    /**
     * 
     */
    public AbstractTreeAction(
            MessagesEditor editor, TreeViewer treeViewer) {
        super();
        this.treeViewer = treeViewer;
        this.editor = editor;
    }
    /**
     * 
     */
    public AbstractTreeAction(
            MessagesEditor editor, TreeViewer treeViewer, int style) {
        super("", style);
        this.treeViewer = treeViewer;
        this.editor = editor;
    }

    protected KeyTreeNode getNodeSelection() {
        IStructuredSelection selection = 
                (IStructuredSelection) treeViewer.getSelection();
        return (KeyTreeNode) selection.getFirstElement();
    }
    protected KeyTreeNode[] getBranchNodes(KeyTreeNode node) {
        return ((AbstractKeyTreeModel) treeViewer.getInput()).getBranch(node);
//        
//        Set childNodes = new TreeSet();
//        childNodes.add(node);
//        Object[] nodes = getContentProvider().getChildren(node);
//        for (int i = 0; i < nodes.length; i++) {
//            childNodes.addAll(
//                    Arrays.asList(getBranchNodes((KeyTreeNode) nodes[i])));
//        }
//        return (KeyTreeNode[]) childNodes.toArray(EMPTY_TREE_NODES);
    }

    protected ITreeContentProvider getContentProvider() {
        return (ITreeContentProvider) treeViewer.getContentProvider();
    }
    
    protected MessagesBundleGroup getBundleGroup() {
        return editor.getBundleGroup();
    }
    
    protected TreeViewer getTreeViewer() {
        return treeViewer;
    }
    
    protected MessagesEditor getEditor() {
        return editor;
    }
    
    protected Shell getShell() {
        return treeViewer.getTree().getShell();
    }
}
