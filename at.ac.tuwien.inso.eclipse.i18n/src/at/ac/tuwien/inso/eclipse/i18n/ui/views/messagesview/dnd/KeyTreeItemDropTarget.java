package at.ac.tuwien.inso.eclipse.i18n.ui.views.messagesview.dnd;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import at.ac.tuwien.inso.eclipse.i18n.model.exception.ResourceBundleException;
import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.i18n.ui.widgets.provider.ResKeyTreeContentProvider;
import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleEntry;
import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleGroup;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTree;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTreeItem;

import com.essiembre.eclipse.rbe.api.BundleFactory;
import com.essiembre.eclipse.rbe.api.ValuedKeyTreeItem;

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
	
	private void addBundleEntry (final String keyPrefix, 
								 final String key, 
								 final IKeyTreeItem kti, 
								 final IBundleGroup bundleGroup, 
								 final boolean removeOld) {
		
 	   try {
 		  String nKey = keyPrefix + "." + key;
 			String newKey = nKey;
 			
 			int i = 1;
 			while (bundleGroup.containsKey(newKey)) {
 				newKey = nKey + i;
 				i++;
 			}
 			
 			for (IKeyTreeItem child : kti.getChildren()) {
 				addBundleEntry (newKey, child.getName(), child, bundleGroup, removeOld);
 			}
 			
 			bundleGroup.addKey(newKey);
 			for (Object o : bundleGroup.getBundleEntries(kti.getId())) {
 				IBundleEntry be = (IBundleEntry) o;
 				bundleGroup.addBundleEntry(be.getBundle().getLocale(), BundleFactory.createBundleEntry(newKey, be.getValue(), be.getComment()));
 			}
 			
 			if (removeOld) {
 				bundleGroup.removeKey(kti.getId());
 			}
 	   } catch (Exception e) { e.printStackTrace(); }

	}
	
	public void drop (final DropTargetEvent event) {
		Display.getDefault().asyncExec(new Runnable() {
            public void run() {
         	   try {
		
					if (TextTransfer.getInstance().isSupportedType (event.currentDataType)) {
						String newKeyPrefix = "";
						
						if (event.item instanceof TreeItem &&
							((TreeItem) event.item).getData() instanceof ValuedKeyTreeItem) {
							newKeyPrefix = ((ValuedKeyTreeItem) ((TreeItem) event.item).getData()).getId();
						}
							
						String message = (String)event.data;
						String oldKey = message.replaceAll("\"", "");
						
						String[] keyArr = (oldKey).split("\\."); 
						String key = keyArr[keyArr.length-1];
						
						ResKeyTreeContentProvider contentProvider = (ResKeyTreeContentProvider) target.getContentProvider();
						IKeyTree keyTree = (IKeyTree) target.getInput();
						
						IBundleGroup bundleGroup = contentProvider.getBundle();
						if (!bundleGroup.containsKey(oldKey)) {
							event.detail = DND.DROP_COPY;
							return;
						}
						
						Collection<IBundleEntry> entries = bundleGroup.getBundleEntries(key);
						
						// Adopt and add new bundle entries
						IKeyTreeItem okti = keyTree.getKeyTreeItem(oldKey);
						addBundleEntry (newKeyPrefix, key, okti, bundleGroup, event.detail == DND.DROP_MOVE);
						
						// Store changes
						ResourceBundleManager manager = contentProvider.getManager();
						try {
							manager.saveResourceBundle(contentProvider.getBundleId(), bundleGroup);
						} catch (ResourceBundleException e) {
							e.printStackTrace();
						}
						
						target.refresh();
					} else
						event.detail = DND.DROP_NONE;
		
         	   } catch (Exception e) { e.printStackTrace(); }
            }
         });
	}
}