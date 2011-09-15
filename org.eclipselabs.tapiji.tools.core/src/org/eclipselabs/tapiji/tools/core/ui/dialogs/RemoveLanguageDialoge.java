package org.eclipselabs.tapiji.tools.core.ui.dialogs;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.ImageUtils;


public class RemoveLanguageDialoge extends ListDialog{
	private IProject project;

	
	public RemoveLanguageDialoge(IProject project, Shell shell) {
		super(shell);
		this.project=project;
		
		initDialog();
	}

	protected void initDialog () {
		this.setAddCancelButton(true);
		this.setMessage("Select one of the following languages to delete:");
		this.setTitle("Language Selector");
		this.setContentProvider(new RBContentProvider());
		this.setLabelProvider(new RBLabelProvider());
		
		this.setInput(ResourceBundleManager.getManager(project).getProjectProvidedLocales());
	}
	
	public Locale getSelectedLanguage() {
		Object[] selection = this.getResult();
		if (selection != null && selection.length > 0)
			return (Locale) selection[0];
		return null;
	}
	
	
	//private classes-------------------------------------------------------------------------------------
	class RBContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			Set<Locale> resources = (Set<Locale>) inputElement;
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
			return ImageUtils.getImage(ImageUtils.IMAGE_RESOURCE_BUNDLE);
		}

		@Override
		public String getText(Object element) {
			Locale l = ((Locale) element);
			String text = l.getDisplayName();
			if (text==null || text.equals("")) text="default";
			else text += " - "+l.getLanguage()+" "+l.getCountry()+" "+l.getVariant();
			return text;
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
