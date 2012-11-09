/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer, Alexej Strelzow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Alexej Strelzow - modified CreateResourceBundleEntryDialog instantiation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.views.messagesview.dnd;

import org.eclipse.babel.editor.api.IValuedKeyTreeNode;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog.DialogConfiguration;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

public class MessagesDropTarget extends DropTargetAdapter {
    private final String projectName;
    private String bundleName;

    public MessagesDropTarget(TreeViewer viewer, String projectName,
            String bundleName) {
        super();
        this.projectName = projectName;
        this.bundleName = bundleName;
    }

    public void dragEnter(DropTargetEvent event) {
    }

    public void drop(DropTargetEvent event) {
        if (event.detail != DND.DROP_COPY)
            return;

        if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
            // event.feedback = DND.FEEDBACK_INSERT_BEFORE;
            String newKeyPrefix = "";

            if (event.item instanceof TreeItem
                    && ((TreeItem) event.item).getData() instanceof IValuedKeyTreeNode) {
                newKeyPrefix = ((IValuedKeyTreeNode) ((TreeItem) event.item)
                        .getData()).getMessageKey();
            }

            String message = (String) event.data;

            CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
                    Display.getDefault().getActiveShell());

            DialogConfiguration config = dialog.new DialogConfiguration();
            config.setPreselectedKey(newKeyPrefix.trim().length() > 0 ? newKeyPrefix
                    + "." + "[Platzhalter]"
                    : "");
            config.setPreselectedMessage(message);
            config.setPreselectedBundle(bundleName);
            config.setPreselectedLocale("");
            config.setProjectName(projectName);

            dialog.setDialogConfiguration(config);

            if (dialog.open() != InputDialog.OK)
                return;
        } else
            event.detail = DND.DROP_NONE;
    }
}
