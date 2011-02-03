package at.ac.tuwien.inso.eclipse.tapiji.views.widgets.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import at.ac.tuwien.inso.eclipse.tapiji.core.GlossaryManager;
import at.ac.tuwien.inso.eclipse.tapiji.model.Glossary;
import at.ac.tuwien.inso.eclipse.tapiji.model.Term;
import at.ac.tuwien.inso.eclipse.tapiji.views.widgets.provider.GlossaryContentProvider;

public class GlossaryDragSource implements DragSourceListener {

	private final TreeViewer source;
	private final GlossaryManager manager;
	private List<Term> selectionList;
	
	public GlossaryDragSource (TreeViewer sourceView, GlossaryManager manager) {
		source = sourceView;
		this.manager = manager;
		this.selectionList = new ArrayList<Term>();
	}
	
	@Override
	public void dragFinished(DragSourceEvent event) {
		GlossaryContentProvider contentProvider = ((GlossaryContentProvider) source.getContentProvider());
		Glossary glossary = contentProvider.getGlossary();
		for (Term selectionObject : selectionList)
			glossary.removeTerm(selectionObject);
		manager.setGlossary(glossary);
		this.source.refresh();
		try {
			manager.saveGlossary();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		selectionList = new ArrayList<Term> ();
		for (Object selectionObject : ((IStructuredSelection)source.getSelection()).toList())
			selectionList.add((Term) selectionObject);
		
		event.data = selectionList.toArray(new Term[selectionList.size()]);
	}

	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !source.getSelection().isEmpty();
	}

}