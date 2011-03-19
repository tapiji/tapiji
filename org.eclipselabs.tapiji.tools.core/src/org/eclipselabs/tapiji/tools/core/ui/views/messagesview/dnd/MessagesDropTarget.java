package org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipselabs.tapiji.translator.rbe.model.tree.IValuedKeyTreeItem;

public class MessagesDropTarget extends DropTargetAdapter {
	private final TreeViewer target;
	private final ResourceBundleManager manager;
	private String bundleName;
	
	public MessagesDropTarget (TreeViewer viewer, ResourceBundleManager manager, String bundleName) {
		super();
		target = viewer;
		this.manager = manager;
		this.bundleName = bundleName;
	}
	
	public void dragEnter (DropTargetEvent event) {
	}
	
	public void drop (DropTargetEvent event) {
		if (event.detail != DND.DROP_COPY)
			return;
		
		if (TextTransfer.getInstance().isSupportedType (event.currentDataType)) {
			//event.feedback = DND.FEEDBACK_INSERT_BEFORE;
			String newKeyPrefix = "";
			
			if (event.item instanceof TreeItem &&
				((TreeItem) event.item).getData() instanceof IValuedKeyTreeItem) {
				newKeyPrefix = ((IValuedKeyTreeItem) ((TreeItem) event.item).getData()).getId();
			}
				
			String message = (String)event.data;
			
			CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
					Display.getDefault().getActiveShell(),
					manager,
					newKeyPrefix.trim().length() > 0 ? newKeyPrefix + "." + "[Platzhalter]" : "",
					message,
					bundleName,
					""
				);
			if (dialog.open() != InputDialog.OK)
				return;
		} else
			event.detail = DND.DROP_NONE;
	}
}
