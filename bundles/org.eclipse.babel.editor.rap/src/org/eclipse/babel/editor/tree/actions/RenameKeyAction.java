package org.eclipse.babel.editor.tree.actions;

import java.util.Collection;

import org.eclipse.babel.core.message.tree.IKeyTreeNode;
import org.eclipse.babel.core.message.tree.internal.KeyTreeNode;
import org.eclipse.babel.editor.internal.AbstractMessagesEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;

public class RenameKeyAction extends AbstractRenameKeyAction {
	
	public RenameKeyAction(AbstractMessagesEditor editor, TreeViewer treeViewer) {
		super(editor, treeViewer);
	}

	@Override
	public void run() {		
		final KeyTreeNode node = getNodeSelection();
		
		treeViewer.setCellModifier(new ICellModifier() {
			private boolean enableEditing = true;
			@Override
			public boolean canModify(Object element, String property) {
				// only selected element can be modified
				return element.equals(node) && enableEditing;
			}

			@Override
			public Object getValue(Object element, String property) {
				IKeyTreeNode node = (IKeyTreeNode) element;
				return node.getName();
			}

			@Override
			public void modify(Object element, String property, Object value) {
				TreeItem item = (TreeItem) element;
				KeyTreeNode node = (KeyTreeNode) item.getData();
				
				String oldKey = node.getMessageKey();
				String newKey = value.toString();
				// remove dots (= key separator)
				newKey = newKey.replaceAll("\\.", "");
								
				if (node.getParent().getParent() != null) {
					newKey = node.getParent().getMessageKey() + "." + newKey;
				}	
				
				// new key doesn't exist already -> renaming
				if ((! oldKey.equals(newKey)) && (! getBundleGroup().isMessageKey(newKey))) {
					// rename child branch
					Collection<IKeyTreeNode> branchNodes = node.getDescendants();
					if (! branchNodes.isEmpty()) {					
						for (IKeyTreeNode branchNode : branchNodes) {
							String oldBranchKey = branchNode.getMessageKey();
							String newBranchKey = newKey + oldBranchKey.substring(oldKey.length());
						    getBundleGroup().renameMessageKeys(oldBranchKey, newBranchKey);						
						}
					}
					// rename selected node					
					getBundleGroup().renameMessageKeys(oldKey, newKey);
				}
				
				// rename finished -> disable editing
				enableEditing = false;
			}
		});
		
		treeViewer.editElement(node, 0);		
	}
}
