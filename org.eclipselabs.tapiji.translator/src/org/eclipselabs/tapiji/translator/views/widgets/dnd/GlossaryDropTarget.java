package org.eclipselabs.tapiji.translator.views.widgets.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipselabs.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.tapiji.translator.model.Glossary;
import org.eclipselabs.tapiji.translator.model.Term;
import org.eclipselabs.tapiji.translator.views.widgets.provider.GlossaryContentProvider;


public class GlossaryDropTarget extends DropTargetAdapter {
	private final TreeViewer target;
	private final GlossaryManager manager;
	
	public GlossaryDropTarget (TreeViewer viewer, GlossaryManager manager) {
		super();
		this.target = viewer;
		this.manager = manager;
	}
	
	public void dragEnter (DropTargetEvent event) {
		if (event.detail == DND.DROP_MOVE || event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_MOVE) != 0)
				event.detail = DND.DROP_MOVE;
			else
				event.detail = DND.DROP_NONE;
		}
	}
	
	public void drop (DropTargetEvent event) {
		if (TermTransfer.getInstance().isSupportedType (event.currentDataType)) {
			Term parentTerm = null;
			
			event.detail = DND.DROP_MOVE;
			event.feedback = DND.FEEDBACK_INSERT_AFTER;

			if (event.item instanceof TreeItem &&
				((TreeItem) event.item).getData() instanceof Term) {
				parentTerm = ((Term) ((TreeItem) event.item).getData());
			}
				
			Term[] moveTerm = (Term[]) event.data;
			Glossary glossary = ((GlossaryContentProvider) target.getContentProvider()).getGlossary();
			
			/* Remove the move term from its initial position 
			for (Term selectionObject : moveTerm)
				glossary.removeTerm(selectionObject);*/
			
			/* Insert the move term on its target position */
			if (parentTerm == null) {
				for (Term t : moveTerm)
					glossary.terms.add(t);
			} else {
				for (Term t : moveTerm)
					parentTerm.subTerms.add(t);
			}
			
			manager.setGlossary(glossary);
			try {
				manager.saveGlossary();
			} catch (Exception e) {
				e.printStackTrace();
			}
			target.refresh();
		} else
			event.detail = DND.DROP_NONE;
	}
}