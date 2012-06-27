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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.tapiji.translator.rap.babel.editor.internal.MessagesEditor;
import org.eclipselabs.tapiji.translator.rap.babel.editor.plugin.MessagesEditorPlugin;
import org.eclipselabs.tapiji.translator.rap.babel.editor.util.UIUtils;

/**
 * @author Pascal Essiembre
 *
 */
public class CollapseAllAction extends AbstractTreeAction {

    /**
     * @param editor
     * @param treeViewer
     */
    public CollapseAllAction(MessagesEditor editor, TreeViewer treeViewer) {
        super(editor, treeViewer);
        setText(MessagesEditorPlugin.getString("key.collapseAll")); //$NON-NLS-1$
        setImageDescriptor(
                UIUtils.getImageDescriptor(UIUtils.IMAGE_COLLAPSE_ALL));
        setToolTipText("Collapse all"); //TODO put tooltip
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        getTreeViewer().collapseAll();
    }
}
