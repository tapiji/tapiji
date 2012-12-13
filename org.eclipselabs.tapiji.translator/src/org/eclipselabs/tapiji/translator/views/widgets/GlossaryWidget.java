/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.views.widgets;

import java.awt.ComponentOrientation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipselabs.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.tapiji.translator.model.Glossary;
import org.eclipselabs.tapiji.translator.model.Term;
import org.eclipselabs.tapiji.translator.model.Translation;
import org.eclipselabs.tapiji.translator.compat.SwtRapCompatibilitySWT;
import org.eclipselabs.tapiji.translator.views.widgets.dnd.GlossaryDragSource;
import org.eclipselabs.tapiji.translator.views.widgets.dnd.GlossaryDropTarget;
import org.eclipselabs.tapiji.translator.views.widgets.dnd.TermTransfer;
import org.eclipselabs.tapiji.translator.views.widgets.filter.ExactMatcher;
import org.eclipselabs.tapiji.translator.views.widgets.filter.FuzzyMatcher;
import org.eclipselabs.tapiji.translator.views.widgets.filter.SelectiveMatcher;
import org.eclipselabs.tapiji.translator.views.widgets.provider.GlossaryContentProvider;
import org.eclipselabs.tapiji.translator.views.widgets.provider.AbstractGlossaryLabelProvider;
import org.eclipselabs.tapiji.translator.views.widgets.sorter.GlossaryEntrySorter;
import org.eclipselabs.tapiji.translator.views.widgets.sorter.SortInfo;

public class GlossaryWidget extends Composite implements
        IResourceChangeListener {

	private final int TERM_COLUMN_WEIGHT = 1;
	private final int DESCRIPTION_COLUMN_WEIGHT = 1;

	private boolean editable;

	private IWorkbenchPartSite site;
	private TreeColumnLayout basicLayout;
	private TreeViewer treeViewer;
	private TreeColumn termColumn;
	private boolean grouped = true;
	private boolean fuzzyMatchingEnabled = false;
	private boolean selectiveViewEnabled = false;
	private float matchingPrecision = .75f;
	private String referenceLocale;
	private List<String> displayedTranslations;
	private String[] translationsToDisplay;

	private SortInfo sortInfo;
	private Glossary glossary;
	private GlossaryManager manager;

	private GlossaryContentProvider contentProvider;
	private AbstractGlossaryLabelProvider labelProvider;

	/*** MATCHER ***/
	ExactMatcher matcher;

	/*** SORTER ***/
	GlossaryEntrySorter sorter;

	/*** ACTIONS ***/
	private Action doubleClickAction;

	public GlossaryWidget(IWorkbenchPartSite site, Composite parent, int style,
	        GlossaryManager manager, String refLang, List<String> dls) {
		super(parent, style);
		this.site = site;

		if (manager != null) {
			this.manager = manager;
			this.glossary = manager.getGlossary();

			if (refLang != null)
				this.referenceLocale = refLang;
			else
				this.referenceLocale = glossary.info.getTranslations()[0];

			if (dls != null)
				this.translationsToDisplay = dls
				        .toArray(new String[dls.size()]);
			else
				this.translationsToDisplay = glossary.info.getTranslations();
		}

		constructWidget();

		if (this.glossary != null) {
			initTreeViewer();
			initMatchers();
			initSorters();
		}

		hookDragAndDrop();
		registerListeners();
	}

	protected void registerListeners() {
		treeViewer.getControl().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.character == SWT.DEL && event.stateMask == 0) {
					deleteSelectedItems();
				}
			}
		});

		// Listen resource changes
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	protected void initSorters() {
		sorter = new GlossaryEntrySorter(treeViewer, sortInfo,
		        glossary.getIndexOfLocale(referenceLocale),
		        glossary.info.translations);
		treeViewer.setSorter(sorter);
	}

	public void enableFuzzyMatching(boolean enable) {
		String pattern = "";
		if (matcher != null) {
			pattern = matcher.getPattern();

			if (!fuzzyMatchingEnabled && enable) {
				if (matcher.getPattern().trim().length() > 1
				        && matcher.getPattern().startsWith("*")
				        && matcher.getPattern().endsWith("*"))
					pattern = pattern.substring(1).substring(0,
					        pattern.length() - 2);
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

		String patternBefore = matcher != null ? matcher.getPattern() : "";

		if (fuzzyMatchingEnabled) {
			matcher = new FuzzyMatcher(treeViewer);
			((FuzzyMatcher) matcher).setMinimumSimilarity(matchingPrecision);
		} else
			matcher = new ExactMatcher(treeViewer);

		matcher.setPattern(patternBefore);

		if (this.selectiveViewEnabled)
			new SelectiveMatcher(treeViewer, site.getPage());
	}

	protected void initTreeViewer() {
		// init content provider
		contentProvider = new GlossaryContentProvider(this.glossary);
		treeViewer.setContentProvider(contentProvider);
		
		// init label provider
		try {
			Class<?> clazz = Class.forName(AbstractGlossaryLabelProvider.INSTANCE_CLASS);
			Constructor<?> constr = clazz.getConstructor(int.class, List.class, IWorkbenchPage.class);
			labelProvider = (AbstractGlossaryLabelProvider) constr.newInstance(
					this.displayedTranslations.indexOf(referenceLocale),
					this.displayedTranslations, site.getPage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		treeViewer.setLabelProvider(labelProvider);

		setTreeStructure(grouped);
	}

	public void setTreeStructure(boolean grouped) {
		this.grouped = grouped;
		((GlossaryContentProvider) treeViewer.getContentProvider())
		        .setGrouped(this.grouped);
		if (treeViewer.getInput() == null)
			treeViewer.setUseHashlookup(false);
		treeViewer.setInput(this.glossary);
		treeViewer.refresh();
	}

	protected void constructWidget() {
		basicLayout = new TreeColumnLayout();
		this.setLayout(basicLayout);

		treeViewer = new TreeViewer(this, SWT.FULL_SELECTION | SWT.SINGLE
		        | SWT.BORDER);
		Tree tree = treeViewer.getTree();

		if (glossary != null) {
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

	/**
	 * Gets the orientation suited for a given locale.
	 * 
	 * @param locale
	 *            the locale
	 * @return <code>SWT.RIGHT_TO_LEFT</code> or <code>SWT.LEFT_TO_RIGHT</code>
	 */
	private int getOrientation(Locale locale) {
		if (locale != null) {
			ComponentOrientation orientation = ComponentOrientation
			        .getOrientation(locale);
			if (orientation == ComponentOrientation.RIGHT_TO_LEFT) {
				return SwtRapCompatibilitySWT.RIGHT_TO_LEFT;
			}
		}
		return SWT.LEFT_TO_RIGHT;
	}

	protected void constructTreeColumns(Tree tree) {
		tree.removeAll();
		if (this.displayedTranslations == null)
			this.displayedTranslations = new ArrayList<String>();

		this.displayedTranslations.clear();

		/** Reference term */
		String[] refDef = referenceLocale.split("_");
		Locale l = refDef.length < 3 ? (refDef.length < 2 ? new Locale(
		        refDef[0]) : new Locale(refDef[0], refDef[1])) : new Locale(
		        refDef[0], refDef[1], refDef[2]);

		this.displayedTranslations.add(referenceLocale);
		termColumn = new TreeColumn(tree, SwtRapCompatibilitySWT.RIGHT_TO_LEFT/* getOrientation(l) */);

		termColumn.setText(l.getDisplayName());
		TreeViewerColumn termCol = new TreeViewerColumn(treeViewer, termColumn);
		termCol.setEditingSupport(new EditingSupport(treeViewer) {
			TextCellEditor editor = null;

			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof Term) {
					Term term = (Term) element;
					Translation translation = (Translation) term
					        .getTranslation(referenceLocale);

					if (translation != null) {
						translation.value = (String) value;
						Glossary gl = ((GlossaryContentProvider) treeViewer
						        .getContentProvider()).getGlossary();
						manager.setGlossary(gl);
						try {
							manager.saveGlossary();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					treeViewer.refresh();
				}
			}

			@Override
			protected Object getValue(Object element) {
				return labelProvider.getColumnText(element, 0);
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
		termColumn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSorter(0);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateSorter(0);
			}
		});
		basicLayout.setColumnData(termColumn, new ColumnWeightData(
		        TERM_COLUMN_WEIGHT));

		/** Translations */
		String[] allLocales = this.translationsToDisplay;

		int iCol = 1;
		for (String locale : allLocales) {
			final int ifCall = iCol;
			final String sfLocale = locale;
			if (locale.equalsIgnoreCase(this.referenceLocale))
				continue;

			// trac the rendered translation
			this.displayedTranslations.add(locale);

			String[] locDef = locale.split("_");
			l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0])
			        : new Locale(locDef[0], locDef[1])) : new Locale(locDef[0],
			        locDef[1], locDef[2]);

			// Add editing support to this table column
			TreeColumn descriptionColumn = new TreeColumn(tree, SWT.NONE);
			TreeViewerColumn tCol = new TreeViewerColumn(treeViewer,
			        descriptionColumn);
			tCol.setEditingSupport(new EditingSupport(treeViewer) {
				TextCellEditor editor = null;

				@Override
				protected void setValue(Object element, Object value) {
					if (element instanceof Term) {
						Term term = (Term) element;
						Translation translation = (Translation) term
						        .getTranslation(sfLocale);

						if (translation != null) {
							translation.value = (String) value;
							Glossary gl = ((GlossaryContentProvider) treeViewer
							        .getContentProvider()).getGlossary();
							manager.setGlossary(gl);
							try {
								manager.saveGlossary();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						treeViewer.refresh();
					}
				}

				@Override
				protected Object getValue(Object element) {
					return labelProvider.getColumnText(element, ifCall);
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

			descriptionColumn.setText(l.getDisplayName());
			descriptionColumn.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateSorter(ifCall);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					updateSorter(ifCall);
				}
			});
			basicLayout.setColumnData(descriptionColumn, new ColumnWeightData(
			        DESCRIPTION_COLUMN_WEIGHT));
			iCol++;
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
		sorter.setSortInfo(sortInfo);
		setTreeStructure(idx == 0);
		treeViewer.refresh();
	}

	@Override
	public boolean setFocus() {
		return treeViewer.getControl().setFocus();
	}

	/*** DRAG AND DROP ***/
	protected void hookDragAndDrop() {
		GlossaryDragSource source = new GlossaryDragSource(treeViewer, manager);
		GlossaryDropTarget target = new GlossaryDropTarget(treeViewer, manager);

		// Initialize drag source for copy event
		DragSource dragSource = new DragSource(treeViewer.getControl(),
		        DND.DROP_MOVE);
		dragSource.setTransfer(new Transfer[] { TermTransfer.getInstance() });
		dragSource.addDragListener(source);

		// Initialize drop target for copy event
		DropTarget dropTarget = new DropTarget(treeViewer.getControl(),
		        DND.DROP_MOVE);
		dropTarget.setTransfer(new Transfer[] { TermTransfer.getInstance() });
		dropTarget.addDropListener(target);
	}

	/*** ACTIONS ***/

	private void makeActions() {
		doubleClickAction = new Action() {

			@Override
			public void run() {
				// implement the cell edit event
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

	private void refreshViewer() {
		treeViewer.refresh();
	}

	public StructuredViewer getViewer() {
		return this.treeViewer;
	}

	public void setSearchString(String pattern) {
		matcher.setPattern(pattern);
		if (matcher.getPattern().trim().length() > 0)
			grouped = false;
		else
			grouped = true;
		labelProvider.setSearchEnabled(!grouped);
		this.setTreeStructure(grouped && sorter != null
		        && sorter.getSortInfo().getColIdx() == 0);
		treeViewer.refresh();
	}

	public SortInfo getSortInfo() {
		if (this.sorter != null)
			return this.sorter.getSortInfo();
		else
			return null;
	}

	public void setSortInfo(SortInfo sortInfo) {
		if (sorter != null) {
			sorter.setSortInfo(sortInfo);
			setTreeStructure(sortInfo.getColIdx() == 0);
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

	public void deleteSelectedItems() {
		List<String> ids = new ArrayList<String>();
		this.glossary = ((GlossaryContentProvider) treeViewer
		        .getContentProvider()).getGlossary();

		ISelection selection = site.getSelectionProvider().getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) selection)
			        .iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (elem instanceof Term) {
					this.glossary.removeTerm((Term) elem);
					this.manager.setGlossary(this.glossary);
					try {
						this.manager.saveGlossary();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		this.refreshViewer();
	}

	public void addNewItem() {
		// event.feedback = DND.FEEDBACK_INSERT_BEFORE;
		Term parentTerm = null;

		ISelection selection = site.getSelectionProvider().getSelection();
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> iter = ((IStructuredSelection) selection)
			        .iterator(); iter.hasNext();) {
				Object elem = iter.next();
				if (elem instanceof Term) {
					parentTerm = ((Term) elem);
					break;
				}
			}
		}

		InputDialog dialog = new InputDialog(this.getShell(), "Neuer Begriff",
		        "Please, define the new term:", "", null);

		if (dialog.open() == InputDialog.OK) {
			if (dialog.getValue() != null
			        && dialog.getValue().trim().length() > 0) {
				this.glossary = ((GlossaryContentProvider) treeViewer
				        .getContentProvider()).getGlossary();

				// Construct a new term
				Term newTerm = new Term();
				Translation defaultTranslation = new Translation();
				defaultTranslation.id = referenceLocale;
				defaultTranslation.value = dialog.getValue();
				newTerm.translations.add(defaultTranslation);

				this.glossary.addTerm(parentTerm, newTerm);

				this.manager.setGlossary(this.glossary);
				try {
					this.manager.saveGlossary();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		this.refreshViewer();
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

	public Control getControl() {
		return treeViewer.getControl();
	}

	public Glossary getGlossary() {
		return this.glossary;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		treeViewer.addSelectionChangedListener(listener);
	}

	public String getReferenceLanguage() {
		return referenceLocale;
	}

	public void setReferenceLanguage(String lang) {
		this.referenceLocale = lang;
	}

	public void bindContentToSelection(boolean enable) {
		this.selectiveViewEnabled = enable;
		initMatchers();
	}

	public boolean isSelectiveViewEnabled() {
		return selectiveViewEnabled;
	}

	@Override
	public void dispose() {
		super.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		initMatchers();
		this.refreshViewer();
	}

}
