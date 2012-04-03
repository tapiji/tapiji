package org.eclipse.babel.tapiji.tools.core.ui.views.messagesview.dnd;

import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class MessagesDragSource implements DragSourceListener {

	private final TreeViewer source;
	private String bundleId;
	
	public MessagesDragSource (TreeViewer sourceView, String bundleId) {
		source = sourceView;
		this.bundleId = bundleId;
	}
	
	@Override
	public void dragFinished(DragSourceEvent event) {
		
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
	    IKeyTreeNode selectionObject = (IKeyTreeNode) 
			((IStructuredSelection)source.getSelection()).toList().get(0);
		
		String key = selectionObject.getMessageKey();
		
		// TODO Solve the problem that its not possible to retrieve the editor position of the drop event
		
//		event.data = "(new ResourceBundle(\"" + bundleId + "\")).getString(\"" + key + "\")";
		event.data = "\"" + key + "\"";
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !source.getSelection().isEmpty();
	}

}
