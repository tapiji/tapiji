package org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd;

import org.eclipse.babel.core.configuration.DirtyHack;
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
		
		for (IKeyTreeNode childs : children.getChildren()) {
			remBundleEntries(childs, group);
		}
		
		group.removeMessagesAddParentKey(key);
	}
	
	public void drop (final DropTargetEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
            public void run() {
         	   try {
		
					if (TextTransfer.getInstance().isSupportedType (event.currentDataType)) {
						String newKeyPrefix = "";						
						
						if (event.item instanceof TreeItem &&
							((TreeItem) event.item).getData() instanceof IValuedKeyTreeNode) {
							IValuedKeyTreeNode targetTreeNode = (IValuedKeyTreeNode) ((TreeItem) event.item).getData();			
							newKeyPrefix = targetTreeNode.getMessageKey();
						}
							
						String message = (String)event.data;
						String oldKey = message.replaceAll("\"", "");
						
						String[] keyArr = (oldKey).split("\\."); 
						String key = keyArr[keyArr.length-1];
						
						ResKeyTreeContentProvider contentProvider = (ResKeyTreeContentProvider) target.getContentProvider();
						IAbstractKeyTreeModel keyTree = (IAbstractKeyTreeModel) target.getInput();
						
						// key gets dropped into it's parent node
						if (oldKey.equals(newKeyPrefix + "." + key))
							return;	// TODO: give user feedback
						
						// prevent cycle loop if key gets dropped into its child node
						if (newKeyPrefix.contains(oldKey))
							return; // TODO: give user feedback
						
						// source node already exists in target
						IKeyTreeNode targetTreeNode = keyTree.getChild(newKeyPrefix);
						for (IKeyTreeNode targetChild : targetTreeNode.getChildren()) {
							if (targetChild.getName().equals(key))
								return; // TODO: give user feedback
						}
						
						IKeyTreeNode sourceTreeNode = keyTree.getChild(oldKey);						
						
						IMessagesBundleGroup bundleGroup = contentProvider.getBundle();
						
						DirtyHack.setFireEnabled(false);
						DirtyHack.setEditorModificationEnabled(false); // editor won't get dirty
						
						// add new bundle entries of source node + all children
						addBundleEntries(newKeyPrefix, sourceTreeNode, bundleGroup);
						
						// if drag & drop is move event, delete source entry + it's children
						if (event.detail == DND.DROP_MOVE) {
							remBundleEntries(sourceTreeNode, bundleGroup);
						}
						
						// Store changes
						RBManager manager = RBManager.getInstance(((MessagesBundleGroup) bundleGroup).getProjectName());
						
						manager.writeToFile(bundleGroup);	
						manager.fireEditorChanged(); // refresh the View
						
						target.refresh();
					} else {
						event.detail = DND.DROP_NONE;
					}
		
         	   } catch (Exception e) { Logger.logError(e); }
         	   finally {
         		   DirtyHack.setFireEnabled(true);
         		   DirtyHack.setEditorModificationEnabled(true);
         	   }
            }
         });
	}
}