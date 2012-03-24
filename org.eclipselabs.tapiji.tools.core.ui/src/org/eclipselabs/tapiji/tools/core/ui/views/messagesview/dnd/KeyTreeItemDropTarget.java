package org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd;

import org.eclipse.babel.core.message.MessagesBundleGroup;
import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.editor.api.MessagesBundleFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.ui.widgets.provider.ResKeyTreeContentProvider;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IAbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
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
	
	private void addBundleEntries (final String keyPrefix, // new prefix
								 final IKeyTreeNode children,
								 final IMessagesBundleGroup bundleGroup) {
		
 	   try {
 		    String oldKey = children.getMessageKey();		   
 		    String key = children.getName();
 			String newKey = keyPrefix + "." + key;
 			
 			IMessage[] messages = bundleGroup.getMessages(oldKey);			
 			for (IMessage message : messages) {
				IMessagesBundle messagesBundle = bundleGroup.getMessagesBundle(message.getLocale());
				IMessage m = MessagesBundleFactory.createMessage(newKey, message.getLocale());
				m.setText(message.getValue());
				m.setComment(message.getComment());
				messagesBundle.addMessage(m);
			}
 			
 			if (messages.length == 0 ) {
 				bundleGroup.addMessages(newKey);
 			}
 			
			for (IKeyTreeNode childs : children.getChildren()) {
				addBundleEntries(keyPrefix+"."+key, childs, bundleGroup);
			}
			
 	   } catch (Exception e) { Logger.logError(e); }

	}
	
	private void remBundleEntries(IKeyTreeNode children, IMessagesBundleGroup group) {
		String key = children.getMessageKey();
		
		group.removeMessages(key);
		
		for (IKeyTreeNode childs : children.getChildren()) {
			remBundleEntries(childs, group);
		}
	}
	
	public void drop (final DropTargetEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
            public void run() {
         	   try {
		
					if (TextTransfer.getInstance().isSupportedType (event.currentDataType)) {
						String newKeyPrefix = "";
						String newName = "";
						
						
						if (event.item instanceof TreeItem &&
							((TreeItem) event.item).getData() instanceof IValuedKeyTreeNode) {
							IValuedKeyTreeNode targetTreeNode = (IValuedKeyTreeNode) ((TreeItem) event.item).getData();			
							newKeyPrefix = targetTreeNode.getMessageKey();
							newName = targetTreeNode.getName();
						}
							
						String message = (String)event.data;
						String oldKey = message.replaceAll("\"", "");
						
						String[] keyArr = (oldKey).split("\\."); 
						String key = keyArr[keyArr.length-1];
						
						// old key is new key, only possible if copy operation, otherwise key gets deleted
						if (oldKey.equals(newKeyPrefix + "." + key) && event.detail == DND.DROP_MOVE)
							return;
						
						// prevent cycle loop if drop parent into child node
						if (newKeyPrefix.contains(oldKey) && event.detail == DND.DROP_MOVE)
							return;
						
						ResKeyTreeContentProvider contentProvider = (ResKeyTreeContentProvider) target.getContentProvider();
						IAbstractKeyTreeModel keyTree = (IAbstractKeyTreeModel) target.getInput();
						
						IKeyTreeNode childrenTreeNode = keyTree.getChild(oldKey);
						
						IMessagesBundleGroup bundleGroup = contentProvider.getBundle();
					
						// Adopt and add new bundle entries
						addBundleEntries(newKeyPrefix, childrenTreeNode, bundleGroup);
						
						if (event.detail == DND.DROP_MOVE)
							remBundleEntries(childrenTreeNode, bundleGroup);
						
						// Store changes
						RBManager manager = RBManager.getInstance(((MessagesBundleGroup) bundleGroup).getProjectName());
						
						manager.writeToFile(bundleGroup);						
						
						target.refresh();
					} else
						event.detail = DND.DROP_NONE;
		
         	   } catch (Exception e) { Logger.logError(e); }
            }
         });
	}
}