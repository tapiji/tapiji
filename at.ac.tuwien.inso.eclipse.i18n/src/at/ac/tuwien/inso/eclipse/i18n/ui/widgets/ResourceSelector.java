package at.ac.tuwien.inso.eclipse.i18n.ui.widgets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import at.ac.tuwien.inso.eclipse.i18n.ui.widgets.event.ResourceSelectionEvent;
import at.ac.tuwien.inso.eclipse.i18n.ui.widgets.listener.IResourceSelectionListener;
import at.ac.tuwien.inso.eclipse.i18n.ui.widgets.provider.ResKeyTreeContentProvider;
import at.ac.tuwien.inso.eclipse.i18n.ui.widgets.provider.ResKeyTreeLabelProvider;
import at.ac.tuwien.inso.eclipse.i18n.ui.widgets.provider.ValueKeyTreeLabelProvider;
import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleGroup;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTree;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTreeItem;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.updater.IKeyTreeUpdater;

import com.essiembre.eclipse.rbe.api.KeyTreeFactory;

public class ResourceSelector  extends Composite {

	public static final int DISPLAY_KEYS = 0;
	public static final int DISPLAY_TEXT = 1;
	
	private Locale displayLocale;
	private int displayMode;
	private String resourceBundle;
	private ResourceBundleManager manager;
	private boolean showTree;
	
	private TreeViewer viewer;
	private TreeColumnLayout basicLayout;
	private TreeColumn entries;
	private Set<IResourceSelectionListener> listeners = new HashSet<IResourceSelectionListener>();
	
	// Viewer model
	private IContentProvider contentProvider;
	private StyledCellLabelProvider labelProvider;
	
	public ResourceSelector(Composite parent, 
						    int style,
						    ResourceBundleManager manager,
						    String resourceBundle,
						    int displayMode,
						 	Locale displayLocale,
						 	boolean showTree) {
		super(parent, style);		
		this.manager = manager;
		this.resourceBundle = resourceBundle;
		this.displayMode = displayMode;
		this.displayLocale = displayLocale;
		this.showTree = showTree;
		
		initLayout (this);
		initViewer (this);
		
		updateViewer (true);
	}

	protected void updateContentProvider (IBundleGroup group) {
		// define input of treeviewer
		IKeyTreeUpdater updater = null;
		if (!showTree || displayMode == DISPLAY_TEXT)
			updater = KeyTreeFactory.createFlatKeyTreeUpdater();
		else
			updater = KeyTreeFactory.createGroupedKeyTreeUpdater();
		IKeyTree keyTree = KeyTreeFactory.createKeyTree(group, updater);
		viewer.setInput(keyTree);
	}
	
	protected void updateViewer (boolean updateContent) {
		IBundleGroup group = manager.getResourceBundle(resourceBundle);
		
		if (group == null)
			return;
		
		if (displayMode == DISPLAY_TEXT) {
			labelProvider = new ValueKeyTreeLabelProvider(group.getBundle(displayLocale));
		} else {
			labelProvider = new ResKeyTreeLabelProvider(null);
		}
		
		viewer.setLabelProvider(labelProvider);
		if (updateContent)
			updateContentProvider(group);
	}
	
	protected void initLayout (Composite parent) {
		basicLayout = new TreeColumnLayout();
		parent.setLayout(basicLayout);
	}
	
	protected void initViewer (Composite parent) {
		viewer = new TreeViewer (parent, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		Tree table = viewer.getTree();
		
		// Init table-columns
		entries = new TreeColumn (table, SWT.NONE);
		basicLayout.setColumnData(entries, new ColumnWeightData(1));
		
		viewer.setContentProvider(new ResKeyTreeContentProvider());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				String selectionSummary = "";
				String selectedKey = "";
				
				if (selection instanceof IStructuredSelection) {
					Iterator<IKeyTreeItem> itSel = ((IStructuredSelection) selection).iterator();
					if (itSel.hasNext()) {
						IKeyTreeItem selItem = itSel.next();
						IBundleGroup group = manager.getResourceBundle(resourceBundle);
						selectedKey = selItem.getId();
						
						if (group == null)
							return;
						Iterator<Locale> itLocales = manager.getProvidedLocales(resourceBundle).iterator();
						while (itLocales.hasNext()) {
							Locale l = itLocales.next();
							try {
								selectionSummary += (l.getDisplayLanguage().equals("") ? "[default]" : l.getDisplayLanguage()) + ":\n";
								selectionSummary += "\t" + group.getBundle(l).getEntry(selItem.getId()).getValue() + "\n";
							} catch (Exception e) {}
						}
					}
				}
			
				// construct ResourceSelectionEvent
				ResourceSelectionEvent e = new ResourceSelectionEvent(selectedKey, selectionSummary);
				fireSelectionChanged(e);
			}
		});
	}
	
	public Locale getDisplayLocale() {
		return displayLocale;
	}

	public void setDisplayLocale(Locale displayLocale) {
		this.displayLocale = displayLocale;
		updateViewer(false);
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
		updateViewer(true);
	}

	public void setResourceBundle(String resourceBundle) {
		this.resourceBundle = resourceBundle;
		updateViewer(true);
	}

	public String getResourceBundle() {
		return resourceBundle;
	}
	
	public void addSelectionChangedListener (IResourceSelectionListener l) {
		listeners.add(l);
	}
	
	public void removeSelectionChangedListener (IResourceSelectionListener l) {
		listeners.remove(l);
	}
	
	private void fireSelectionChanged (ResourceSelectionEvent event) {
		Iterator<IResourceSelectionListener> itResList = listeners.iterator();
		while (itResList.hasNext()) {
			itResList.next().selectionChanged(event);
		}
	}

}
