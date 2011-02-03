package at.ac.tuwien.inso.eclipse.tapiji.views.widgets.filter;

import java.util.Collection;

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

import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleEntry;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTreeItem;
import at.ac.tuwien.inso.eclipse.tapiji.model.Term;
import at.ac.tuwien.inso.eclipse.tapiji.model.Translation;

import com.essiembre.eclipse.rbe.api.EditorUtil;

public class SelectiveMatcher extends ViewerFilter 
						      implements ISelectionListener, ISelectionChangedListener {

	protected final StructuredViewer viewer;
	protected String pattern = "";
	protected StringMatcher matcher;
	protected IKeyTreeItem selectedItem;
	protected IWorkbenchPage page;
	
	public SelectiveMatcher (StructuredViewer viewer, IWorkbenchPage page) {
		this.viewer = viewer;
		if (page.getActiveEditor() != null) {
			this.selectedItem = EditorUtil.getSelectedKeyTreeItem(page);
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
			
			Collection<IBundleEntry> selection = selectedItem.getKeyTree().getBundleGroup().getBundleEntries(selectedItem.getId());
			for (IBundleEntry entry : selection) {
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
			selectedItem = (IKeyTreeItem) sel.iterator().next();
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
