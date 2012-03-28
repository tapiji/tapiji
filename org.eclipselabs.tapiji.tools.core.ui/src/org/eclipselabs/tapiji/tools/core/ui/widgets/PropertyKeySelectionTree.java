package org.eclipselabs.tapiji.tools.core.ui.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.babel.core.configuration.DirtyHack;
import org.eclipse.babel.core.message.manager.IMessagesEditorListener;
import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.editor.api.KeyTreeFactory;
import org.eclipse.babel.editor.api.MessagesBundleFactory;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.tools.core.Logger;
import org.eclipselabs.tapiji.tools.core.model.IResourceBundleChangedListener;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleChangedEvent;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.model.view.SortInfo;
import org.eclipselabs.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd.KeyTreeItemDropTarget;
import org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd.MessagesDragSource;
import org.eclipselabs.tapiji.tools.core.ui.views.messagesview.dnd.MessagesDropTarget;
import org.eclipselabs.tapiji.tools.core.ui.widgets.filter.ExactMatcher;
import org.eclipselabs.tapiji.tools.core.ui.widgets.filter.FuzzyMatcher;
import org.eclipselabs.tapiji.tools.core.ui.widgets.provider.ResKeyTreeContentProvider;
import org.eclipselabs.tapiji.tools.core.ui.widgets.provider.ResKeyTreeLabelProvider;
import org.eclipselabs.tapiji.tools.core.ui.widgets.sorter.ValuedKeyTreeItemSorter;
import org.eclipselabs.tapiji.tools.core.util.EditorUtils;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IAbstractKeyTreeModel;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundle;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.TreeType;

public class PropertyKeySelectionTree extends Composite implements IResourceBundleChangedListener {

    private final int KEY_COLUMN_WEIGHT = 1;
    private final int LOCALE_COLUMN_WEIGHT = 1;

    private ResourceBundleManager manager;
    private String resourceBundle;
    private List<Locale> visibleLocales = new ArrayList<Locale>();
    private boolean editable;

    private IWorkbenchPartSite site;
    private TreeColumnLayout basicLayout;
    private TreeViewer treeViewer;
    private TreeColumn keyColumn;
    private boolean grouped = true;
    private boolean fuzzyMatchingEnabled = false;
    private float matchingPrecision = .75f;
    private Locale uiLocale = new Locale("en");

    private SortInfo sortInfo;

    private ResKeyTreeContentProvider contentProvider;
    private ResKeyTreeLabelProvider labelProvider;
    private TreeType treeType = TreeType.Tree;
    
    private IMessagesEditorListener editorListener;

    /*** MATCHER ***/
    ExactMatcher matcher;

    /*** SORTER ***/
    ValuedKeyTreeItemSorter sorter;

    /*** ACTIONS ***/
    private Action doubleClickAction;

    /*** LISTENERS ***/
    private ISelectionChangedListener selectionChangedListener;

    public PropertyKeySelectionTree(IViewSite viewSite, IWorkbenchPartSite site, Composite parent, int style,
            String projectName, String resources, List<Locale> locales) {
        super(parent, style);
        this.site = site;
        resourceBundle = resources;

        if (resourceBundle != null && resourceBundle.trim().length() > 0) {
            manager = ResourceBundleManager.getManager(projectName);
            if (locales == null)
                initVisibleLocales();
            else
                this.visibleLocales = locales;
        }

        constructWidget();

        if (resourceBundle != null && resourceBundle.trim().length() > 0) {
            initTreeViewer();
            initMatchers();
            initSorters();
            treeViewer.expandAll();
        }

        hookDragAndDrop();
        registerListeners();
    }

    @Override
    public void dispose() {
        super.dispose();
        unregisterListeners();
    }

    protected void initSorters() {
        sorter = new ValuedKeyTreeItemSorter(treeViewer, sortInfo);
        treeViewer.setSorter(sorter);
    }

    public void enableFuzzyMatching(boolean enable) {
        String pattern = "";
        if (matcher != null) {
            pattern = matcher.getPattern();

            if (!fuzzyMatchingEnabled && enable) {
                if (matcher.getPattern().trim().length() > 1 && matcher.getPattern().startsWith("*")
                        && matcher.getPattern().endsWith("*"))
                    pattern = pattern.substring(1).substring(0, pattern.length() - 2);
                matcher.setPattern(null);
            }
        }
        fuzzyMatchingEnabled = enable;
        initMatchers();

        matcher.setPattern(pattern);
        treeViewer.refresh();
    }

    public boolean isFuzzyMatchingEnabled() {
        return fuzzyMatchingEnabled;
    }

    protected void initMatchers() {
        treeViewer.resetFilters();

        if (fuzzyMatchingEnabled) {
            matcher = new FuzzyMatcher(treeViewer);
            ((FuzzyMatcher) matcher).setMinimumSimilarity(matchingPrecision);
        } else
            matcher = new ExactMatcher(treeViewer);

    }

    protected void initTreeViewer() {
        this.setRedraw(false);
        // init content provider
        contentProvider = new ResKeyTreeContentProvider(manager.getResourceBundle(resourceBundle), visibleLocales,
                manager, resourceBundle, treeType);
        treeViewer.setContentProvider(contentProvider);

        // init label provider
        labelProvider = new ResKeyTreeLabelProvider(visibleLocales);
        treeViewer.setLabelProvider(labelProvider);

        // we need this to keep the tree expanded
        treeViewer.setComparer(new IElementComparer() {
			
			@Override
			public int hashCode(Object element) {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((toString() == null) ? 0 : toString().hashCode());
				return result;
			}
			
			@Override
			public boolean equals(Object a, Object b) {
				if (a == b) {
					return true;
				} 
				if (a instanceof IKeyTreeNode && b instanceof IKeyTreeNode) {
					IKeyTreeNode nodeA = (IKeyTreeNode) a;
					IKeyTreeNode nodeB = (IKeyTreeNode) b;
					return nodeA.equals(nodeB);
				}
				return false;
			}
		});
        
        setTreeStructure();
        this.setRedraw(true);
    }

    public void setTreeStructure() {
        IAbstractKeyTreeModel model = KeyTreeFactory.createModel(manager.getResourceBundle(resourceBundle));
        if (treeViewer.getInput() == null) {
            treeViewer.setUseHashlookup(true);
        }
        org.eclipse.jface.viewers.TreePath[] expandedTreePaths = treeViewer.getExpandedTreePaths();
        treeViewer.setInput(model);
        treeViewer.refresh();
        treeViewer.setExpandedTreePaths(expandedTreePaths);
    }

    protected void refreshContent(ResourceBundleChangedEvent event) {
        if (visibleLocales == null)
            initVisibleLocales();

        // update content provider
        contentProvider.setLocales(visibleLocales);
        contentProvider.setBundleGroup(manager.getResourceBundle(resourceBundle));

        // init label provider
        IMessagesBundleGroup group = manager.getResourceBundle(resourceBundle);
        labelProvider.setLocales(visibleLocales);
        if (treeViewer.getLabelProvider() != labelProvider)
            treeViewer.setLabelProvider(labelProvider);

        // define input of treeviewer
        setTreeStructure();
    }

    protected void initVisibleLocales() {
        SortedMap<String, Locale> locSorted = new TreeMap<String, Locale>();
        sortInfo = new SortInfo();
        visibleLocales.clear();
        if (resourceBundle != null) {
            for (Locale l : manager.getProvidedLocales(resourceBundle)) {
                if (l == null) {
                    locSorted.put("Default", null);
                } else {
                    locSorted.put(l.getDisplayName(uiLocale), l);
                }
            }
        }

        for (String lString : locSorted.keySet()) {
            visibleLocales.add(locSorted.get(lString));
        }
        sortInfo.setVisibleLocales(visibleLocales);
    }

    protected void constructWidget() {
        basicLayout = new TreeColumnLayout();
        this.setLayout(basicLayout);

        treeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER);
        Tree tree = treeViewer.getTree();

        if (resourceBundle != null) {
            tree.setHeaderVisible(true);
            tree.setLinesVisible(true);

            // create tree-columns
            constructTreeColumns(tree);
        } else {
            tree.setHeaderVisible(false);
            tree.setLinesVisible(false);
        }

        makeActions();
        hookDoubleClickAction();

        // register messages table as selection provider
        site.setSelectionProvider(treeViewer);
    }

    protected void constructTreeColumns(Tree tree) {
        tree.removeAll();
        //tree.getColumns().length;

        // construct key-column
        keyColumn = new TreeColumn(tree, SWT.NONE);
        keyColumn.setText("Key");
        keyColumn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateSorter(0);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                updateSorter(0);
            }
        });
        basicLayout.setColumnData(keyColumn, new ColumnWeightData(KEY_COLUMN_WEIGHT));

        if (visibleLocales != null) {
            for (final Locale l : visibleLocales) {
                TreeColumn col = new TreeColumn(tree, SWT.NONE);

                // Add editing support to this table column
                TreeViewerColumn tCol = new TreeViewerColumn(treeViewer, col);
                tCol.setEditingSupport(new EditingSupport(treeViewer) {

                    TextCellEditor editor = null;

                    @Override
                    protected void setValue(Object element, Object value) {
                    	boolean writeToFile = true;
                    	
                        if (element instanceof IValuedKeyTreeNode) {
                            IValuedKeyTreeNode vkti = (IValuedKeyTreeNode) element;
                            String activeKey = vkti.getMessageKey();

                            if (activeKey != null) {
                                IMessagesBundleGroup bundleGroup = manager.getResourceBundle(resourceBundle);
                                IMessage entry = bundleGroup.getMessage(activeKey, l);

                                if (entry == null || !value.equals(entry.getValue())) {
                                    String comment = null;
                                    if (entry != null) {
                                        comment = entry.getComment();
                                    }

                                    IMessagesBundle messagesBundle = bundleGroup.getMessagesBundle(l);

                                    DirtyHack.setFireEnabled(false);
                                    
                                	IMessage message = messagesBundle.getMessage(activeKey);
                                	if (message == null) {
                                		IMessage newMessage = MessagesBundleFactory.createMessage(activeKey, l);
                                		newMessage.setText(String.valueOf(value));
                                		newMessage.setComment(comment);
                                		messagesBundle.addMessage(newMessage);
                                		
                                	} else {
                                    	message.setText(String.valueOf(value));
                                    	message.setComment(comment);
                                    }
                                	
                                	RBManager.getInstance(manager.getProject()).writeToFile(messagesBundle);
                                    
                                	setTreeStructure();
                                	
                                    DirtyHack.setFireEnabled(true);
                                    
                                }
                            }
                        }
                    }

                    @Override
                    protected Object getValue(Object element) {
                        return labelProvider.getColumnText(element, visibleLocales.indexOf(l) + 1);
                    }

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        if (editor == null) {
                            Composite tree = (Composite) treeViewer.getControl();
                            editor = new TextCellEditor(tree);
                        }
                        return editor;
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        return editable;
                    }
                });

                String displayName = l == null ? ResourceBundleManager.defaultLocaleTag : l.getDisplayName(uiLocale);

                col.setText(displayName);
                col.addSelectionListener(new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        updateSorter(visibleLocales.indexOf(l) + 1);
                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {
                        updateSorter(visibleLocales.indexOf(l) + 1);
                    }
                });
                basicLayout.setColumnData(col, new ColumnWeightData(LOCALE_COLUMN_WEIGHT));
            }
        }
    }

    protected void updateSorter(int idx) {
        SortInfo sortInfo = sorter.getSortInfo();
        if (idx == sortInfo.getColIdx())
            sortInfo.setDESC(!sortInfo.isDESC());
        else {
            sortInfo.setColIdx(idx);
            sortInfo.setDESC(false);
        }
        sortInfo.setVisibleLocales(visibleLocales);
        sorter.setSortInfo(sortInfo);
        treeType = idx == 0 ? TreeType.Tree : TreeType.Flat;
        setTreeStructure();
        treeViewer.refresh();
    }

    @Override
    public boolean setFocus() {
        return treeViewer.getControl().setFocus();
    }

    /*** DRAG AND DROP ***/
    protected void hookDragAndDrop() {
        //KeyTreeItemDragSource ktiSource = new KeyTreeItemDragSource (treeViewer);
        KeyTreeItemDropTarget ktiTarget = new KeyTreeItemDropTarget(treeViewer);
        MessagesDragSource source = new MessagesDragSource(treeViewer, this.resourceBundle);
        MessagesDropTarget target = new MessagesDropTarget(treeViewer, manager, resourceBundle);

        // Initialize drag source for copy event
        DragSource dragSource = new DragSource(treeViewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE);
        dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
        //dragSource.addDragListener(ktiSource);
        dragSource.addDragListener(source);

        // Initialize drop target for copy event
        DropTarget dropTarget = new DropTarget(treeViewer.getControl(), DND.DROP_MOVE | DND.DROP_COPY);
        dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance(), JavaUI.getJavaElementClipboardTransfer() });
        dropTarget.addDropListener(ktiTarget);
        dropTarget.addDropListener(target);
    }

    /*** ACTIONS ***/

    private void makeActions() {
        doubleClickAction = new Action() {

            @Override
            public void run() {
                editSelectedItem();
            }

        };
    }

    private void hookDoubleClickAction() {
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                doubleClickAction.run();
            }
        });
    }

    /*** SELECTION LISTENER ***/

    protected void registerListeners() {

    	this.editorListener = new MessagesEditorListener();
    	if (manager != null) {
    		RBManager.getInstance(manager.getProject()).addMessagesEditorListener(editorListener);
    	}
        
        treeViewer.getControl().addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent event) {
                if (event.character == SWT.DEL && event.stateMask == 0) {
                    deleteSelectedItems();
                }
            }
        });
    }

    protected void unregisterListeners() {
    	if (manager != null) {
    		RBManager.getInstance(manager.getProject()).removeMessagesEditorListener(editorListener);
    	}
        treeViewer.removeSelectionChangedListener(selectionChangedListener);
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        treeViewer.addSelectionChangedListener(listener);
        selectionChangedListener = listener;
    }

    @Override
    public void resourceBundleChanged(final ResourceBundleChangedEvent event) {
        if (event.getType() != ResourceBundleChangedEvent.MODIFIED
                || !event.getBundle().equals(this.getResourceBundle()))
            return;

        if (Display.getCurrent() != null) {
            refreshViewer(event, true);
            return;
        }

        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                refreshViewer(event, true);
            }
        });
    }

    private void refreshViewer(ResourceBundleChangedEvent event, boolean computeVisibleLocales) {
        //manager.loadResourceBundle(resourceBundle);
        if (computeVisibleLocales) {
            refreshContent(event);
        }

        //		Display.getDefault().asyncExec(new Runnable() {
        //            public void run() {
        treeViewer.refresh();
        //            }
        //		});
    }

    public StructuredViewer getViewer() {
        return this.treeViewer;
    }

    public void setSearchString(String pattern) {
        matcher.setPattern(pattern);
        treeType = matcher.getPattern().trim().length() > 0 ? TreeType.Flat : TreeType.Tree;
        labelProvider.setSearchEnabled(treeType.equals(TreeType.Flat));
        // WTF?
        treeType = treeType.equals(TreeType.Tree) && sorter.getSortInfo().getColIdx() == 0 ? TreeType.Tree : TreeType.Flat; 
        treeViewer.refresh();
    }

    public SortInfo getSortInfo() {
        if (this.sorter != null)
            return this.sorter.getSortInfo();
        else
            return null;
    }

    public void setSortInfo(SortInfo sortInfo) {
        sortInfo.setVisibleLocales(visibleLocales);
        if (sorter != null) {
            sorter.setSortInfo(sortInfo);
            treeType = sortInfo.getColIdx() == 0 ? TreeType.Tree : TreeType.Flat; 
            treeViewer.refresh();
        }
    }

    public String getSearchString() {
        return matcher.getPattern();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public List<Locale> getVisibleLocales() {
        return visibleLocales;
    }

    public ResourceBundleManager getManager() {
        return this.manager;
    }

    public String getResourceBundle() {
        return resourceBundle;
    }

    public void editSelectedItem() {
        EditorUtils.openEditor(site.getPage(), manager.getRandomFile(resourceBundle),
                EditorUtils.RESOURCE_BUNDLE_EDITOR);
    }

    public void deleteSelectedItems() {
        List<String> keys = new ArrayList<String>();

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ISelection selection = window.getActivePage().getSelection();
        if (selection instanceof IStructuredSelection) {
            for (Iterator<?> iter = ((IStructuredSelection) selection).iterator(); iter.hasNext();) {
                Object elem = iter.next();
                if (elem instanceof IKeyTreeNode) {
                	addKeysToRemove((IKeyTreeNode)elem, keys);
                }
            }
        }

        try {
            manager.removeResourceBundleEntry(getResourceBundle(), keys);
        } catch (Exception ex) {
            Logger.logError(ex);
        }
    }
    
    private void addKeysToRemove(IKeyTreeNode node, List<String> keys) {
    	keys.add(node.getMessageKey());
    	for (IKeyTreeNode ktn : node.getChildren()) {
    		addKeysToRemove(ktn, keys);
    	}
    }

    public void addNewItem() {
        //event.feedback = DND.FEEDBACK_INSERT_BEFORE;
        String newKeyPrefix = "";

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ISelection selection = window.getActivePage().getSelection();
        if (selection instanceof IStructuredSelection) {
            for (Iterator<?> iter = ((IStructuredSelection) selection).iterator(); iter.hasNext();) {
                Object elem = iter.next();
                if (elem instanceof IKeyTreeNode) {
                    newKeyPrefix = ((IKeyTreeNode) elem).getMessageKey();
                    break;
                }
            }
        }

        CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(Display.getDefault()
                .getActiveShell(), manager, newKeyPrefix.trim().length() > 0 ? newKeyPrefix + "." + "[Platzhalter]"
                : "", "", getResourceBundle(), "");
        if (dialog.open() != InputDialog.OK)
            return;
    }

    public void setMatchingPrecision(float value) {
        matchingPrecision = value;
        if (matcher instanceof FuzzyMatcher) {
            ((FuzzyMatcher) matcher).setMinimumSimilarity(value);
            treeViewer.refresh();
        }
    }

    public float getMatchingPrecision() {
        return matchingPrecision;
    }
    
    private class MessagesEditorListener implements IMessagesEditorListener {
    	@Override
    	public void onSave() {
    		if (resourceBundle != null) {
    			setTreeStructure();
    		}
    	}
    	
    	@Override
    	public void onModify() {
    		if (resourceBundle != null) {
    			setTreeStructure();
    		}
    	}
    	
    	@Override
    	public void onResourceChanged(IMessagesBundle bundle) {
    		// TODO Auto-generated method stub
    		
    	}
    }
}
