package org.eclipse.tapiji.rap.translator.views.widgets.filter;

//TODO: import org.eclipse.babel.rap.editor.api.EditorUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.tapiji.rap.translator.model.Term;
import org.eclipse.tapiji.rap.translator.model.Translation;
import org.eclipse.tapiji.rap.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.tapiji.rap.translator.rbe.babel.bundle.IMessage;

public class SelectiveMatcher extends ViewerFilter 
						      implements ISelectionListener, ISelectionChangedListener {

	protected final StructuredViewer viewer;
	protected String pattern = "";
	protected StringMatcher matcher;
	protected IKeyTreeNode selectedItem;
	protected IWorkbenchPage page;
	
	public SelectiveMatcher (StructuredViewer viewer, IWorkbenchPage page) {
		this.viewer = viewer;
		if (page.getActiveEditor() != null) {
			//TODO: this.selectedItem = EditorUtil.getSelectedKeyTreeNode(page);
		}
		
		this.page = page;
		page.getWorkbenchWindow().getSelectionService().addSelectionListener(this);
			
		viewer.addFilter(this);
		viewer.refresh();
	}
		
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (selectedItem == null)
			return false;
			
		Term term = (Term) element;
		FilterInfo filterInfo = new FilterInfo();
		boolean selected = false;
		
		// Iterate translations
		for (Translation translation : term.getAllTranslations()) {
			String value = translation.value;
			
			if (value.trim().length() == 0)
				continue;
			
			String locale = translation.id;
			
			for (IMessage entry : selectedItem.getMessagesBundleGroup().getMessages(selectedItem.getMessageKey())) {
				String ev = entry.getValue();
				String[] subValues = ev.split("[\\s\\p{Punct}]+");
				for (String v : subValues) {
					if (v.trim().equalsIgnoreCase(value.trim()))
						return true;
				}
			}
		} 
		
		return false;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		try {
			if (selection.isEmpty())
				return;
			
			if (!(selection instanceof IStructuredSelection))
				return;
			
			IStructuredSelection sel = (IStructuredSelection) selection;
			selectedItem = (IKeyTreeNode) sel.iterator().next();
			viewer.refresh();
		} catch (Exception e) {	}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		event.getSelection();
	}

	public void dispose () {
		page.getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
	}
}
