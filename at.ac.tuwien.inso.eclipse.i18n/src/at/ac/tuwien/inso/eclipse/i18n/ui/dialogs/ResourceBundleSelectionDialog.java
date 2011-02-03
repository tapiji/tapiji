package at.ac.tuwien.inso.eclipse.i18n.ui.dialogs;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.i18n.util.ImageUtils;

public class ResourceBundleSelectionDialog extends ListDialog {

	private IProject project;
	
	public ResourceBundleSelectionDialog(Shell parent, IProject project) {
		super(parent);
		this.project = project;
		
		initDialog ();
	}
	
	protected void initDialog () {
		this.setAddCancelButton(true);
		this.setMessage("Select one of the following Resource-Bundle to open:");
		this.setTitle("Resource-Bundle Selector");
		this.setContentProvider(new RBContentProvider());
		this.setLabelProvider(new RBLabelProvider());
		this.setBlockOnOpen(true);
		
		if (project != null)
			this.setInput(ResourceBundleManager.getManager(project).getResourceBundleNames());
		else
			this.setInput(ResourceBundleManager.getAllResourceBundleNames());
	}
	
	public String getSelectedBundleId () {
		Object[] selection = this.getResult();
		if (selection != null && selection.length > 0)
			return (String) selection[0];
		return null;
	}
	
	class RBContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			List<String> resources = (List<String>) inputElement;
			return resources.toArray();
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class RBLabelProvider implements ILabelProvider {

		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return ImageUtils.getImage(ImageUtils.IMAGE_RESOURCE_BUNDLE);
		}

		@Override
		public String getText(Object element) {
			// TODO Auto-generated method stub
			return ((String) element);
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
