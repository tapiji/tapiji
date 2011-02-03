package at.ac.tuwien.inso.eclipse.i18n.ui.widgets.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleGroup;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTreeItem;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IValuedKeyTreeItem;

import com.essiembre.eclipse.rbe.api.ValuedKeyTreeItem;


public class ResKeyTreeContentProvider extends KeyTreeContentProvider {

	private IBundleGroup bundle;
	private List<Locale> locales;
	private ResourceBundleManager manager;
	private String bundleId;
	
	public ResKeyTreeContentProvider (IBundleGroup iBundleGroup, List<Locale> locales, ResourceBundleManager manager, String bundleId) {
		this.bundle = iBundleGroup;
		this.locales = locales;
		this.manager = manager;
		this.bundleId = bundleId;
	}
	
	public void setBundleGroup (IBundleGroup iBundleGroup) {
		this.bundle = iBundleGroup;
	}
		
	public ResKeyTreeContentProvider() {
		locales = new ArrayList<Locale>();
	}

	public void setLocales (List<Locale> locales) {
		this.locales = locales;
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IValuedKeyTreeItem) {
			IKeyTreeItem ki = (IKeyTreeItem)parentElement;
			return convertKTItoVKTI(super.getChildren(ki));
		}
		return convertKTItoVKTI(super.getChildren(parentElement));
	}
	
	protected Object[] convertKTItoVKTI (Object[] children) {
		Collection<ValuedKeyTreeItem> items = new ArrayList<ValuedKeyTreeItem>();
		
		for (Object o : children) {
			if (o instanceof ValuedKeyTreeItem)
				items.add((ValuedKeyTreeItem)o);
			else {
				IKeyTreeItem kti = (IKeyTreeItem) o;
				ValuedKeyTreeItem vkti = new ValuedKeyTreeItem(kti.getKeyTree(), kti.getId(), kti.getName());
				vkti.setParent(kti.getParent());
				for (IKeyTreeItem k : kti.getChildren()) {
					vkti.addChildren(k);
				}
				
				// init translations
				for (Locale l : locales) {
					try {
						vkti.addValue(l, bundle.getBundle(l).getEntry(kti.getId()).getValue());
					} catch (Exception e) {}
				}
				items.add(vkti);
			}
		}
		
		return items.toArray();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return convertKTItoVKTI(super.getElements(inputElement));
	}

	@Override
	public Object getParent(Object element) {
		Object[] parent = new Object[1];
		parent[0] = super.getParent(parent);
		
		if (parent[0] == null)
			return null;
		
	Object[] result = convertKTItoVKTI(parent);
	if (result.length > 0)
		return result[0];
	else
		return null;
	}
	
	public IBundleGroup getBundle() {
		return bundle;
	}

	public ResourceBundleManager getManager() {
		return manager;
	}
	
	public String getBundleId() {
		return bundleId;
	}
}
