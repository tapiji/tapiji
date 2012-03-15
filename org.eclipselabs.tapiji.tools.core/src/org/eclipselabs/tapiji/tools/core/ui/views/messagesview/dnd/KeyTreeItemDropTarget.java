package org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd;

import org.eclipse.babel.editor.api.MessagesBundleFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.model.exception.ResourceBundleException;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.ui.widgets.provider.ResKeyTreeContentProvider;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IAbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundle;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;

public class KeyTreeItemDropTarget extends DropTargetAdapter {
	private final TreeViewer target;
	
	public KeyTreeItemDropTarget (TreeViewer viewer) {
		super();
		this.target = viewer;
	}
	
	public void dragEnter (DropTargetEvent event) {
//		if (((DropTarget)event.getSource()).getControl() instanceof Tree)
//			event.detail = DND.DROP_MOVE;
	}
	
	private void addBundleEntry (final String keyPrefix, // new prefix
								 final String key, 		// leaf
								 final String oldKey, // f.q. key
								 final IMessagesBundleGroup bundleGroup, 
								 final boolean removeOld) {
		
 	   try {
 			String newKey = keyPrefix + "." + key;
 			boolean rem = keyPrefix.contains(oldKey) ? false : removeOld;
 			
			for (IMessage message : bundleGroup.getMessages(oldKey)) {
				IMessagesBundle messagesBundle = bundleGroup.getMessagesBundle(message.getLocale());
				IMessage m = MessagesBundleFactory.createMessage(newKey, message.getLocale());
				m.setText(message.getValue());
				m.setComment(message.getComment());
				messagesBundle.addMessage(m);
			}
 			
 			if (rem) {
 				bundleGroup.removeMessages(oldKey);
 			}
 			
 	   } catch (Exception e) { Logger.logError(e); }

	}
	
	public void drop (final DropTargetEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
            public void run() {
         	   try {
		
					if (TextTransfer.getInstance().isSupportedType (event.currentDataType)) {
						String newKeyPrefix = "";
						
						if (event.item instanceof TreeItem &&
							((TreeItem) event.item).getData() instanceof IValuedKeyTreeNode) {
							newKeyPrefix = ((IValuedKeyTreeNode) ((TreeItem) event.item).getData()).getMessageKey();
						}
							
						String message = (String)event.data;
						String oldKey = message.replaceAll("\"", "");
						
						String[] keyArr = (oldKey).split("\\."); 
						String key = keyArr[keyArr.length-1];
						
						ResKeyTreeContentProvider contentProvider = (ResKeyTreeContentProvider) target.getContentProvider();
						IAbstractKeyTreeModel keyTree = (IAbstractKeyTreeModel) target.getInput();
						
						IMessagesBundleGroup bundleGroup = contentProvider.getBundle();
						if (!bundleGroup.containsKey(oldKey)) {
							event.detail = DND.DROP_COPY;
							return;
						}
						
						// Adopt and add new bundle entries
						addBundleEntry (newKeyPrefix, key, oldKey, bundleGroup, event.detail == DND.DROP_MOVE);
						
						// Store changes
						ResourceBundleManager manager = contentProvider.getManager();
						try {
							manager.saveResourceBundle(contentProvider.getBundleId(), bundleGroup);
						} catch (ResourceBundleException e) {
							Logger.logError(e);
						}
						
						target.refresh();
					} else
						event.detail = DND.DROP_NONE;
		
         	   } catch (Exception e) { Logger.logError(e); }
            }
         });
	}
}