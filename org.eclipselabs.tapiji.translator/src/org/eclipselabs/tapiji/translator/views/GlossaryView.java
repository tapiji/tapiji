/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipselabs.tapiji.translator.views;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.tapiji.translator.core.GlossaryManager;
import org.eclipselabs.tapiji.translator.core.ILoadGlossaryListener;
import org.eclipselabs.tapiji.translator.core.LoadGlossaryEvent;
import org.eclipselabs.tapiji.translator.model.Glossary;
import org.eclipselabs.tapiji.translator.views.dialog.LocaleContentProvider;
import org.eclipselabs.tapiji.translator.views.dialog.LocaleLabelProvider;
import org.eclipselabs.tapiji.translator.views.menus.GlossaryEntryMenuContribution;
import org.eclipselabs.tapiji.translator.views.widgets.GlossaryWidget;
import org.eclipselabs.tapiji.translator.views.widgets.model.GlossaryViewState;
import org.eclipselabs.tapiji.translator.views.widgets.provider.GlossaryLabelProvider;


public class GlossaryView extends ViewPart implements ILoadGlossaryListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.eclipselabs.tapiji.translator.views.GlossaryView";
	
	/*** Primary view controls ***/
	private GlossaryWidget treeViewer;
	private Scale fuzzyScaler;
	private Label lblScale;
	private Text filter;
	
	/*** ACTIONS ***/
	private GlossaryEntryMenuContribution glossaryEditContribution;
	private MenuManager referenceMenu;
	private MenuManager displayMenu;
	private MenuManager showMenu;
	private Action newEntry;
	private Action enableFuzzyMatching;
	private Action editable;
	private Action newTranslation;
	private Action deleteTranslation;
	private Action showAll;
	private Action showSelectiveContent;
	private List<Action> referenceActions;
	private List<Action> displayActions;
	
	/*** Parent component ***/
	private Composite parent;
	
	/*** View state ***/
	private IMemento memento;
	private GlossaryViewState viewState;
	private GlossaryManager glossary;
	
	/**
	 * The constructor.
	 */
	public GlossaryView () {
		/** Register the view for being informed each time a new glossary is loaded into the translator */
		GlossaryManager.registerLoadGlossaryListener(this);
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		initLayout (parent);
		initSearchBar (parent);
		initMessagesTree (parent);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		initListener (parent);
	}
	
	protected void initListener (Composite parent) {
		filter.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if (glossary != null && glossary.getGlossary() != null)
					treeViewer.setSearchString(filter.getText());
			}
		});
	}
	
	protected void initLayout (Composite parent) {
		GridLayout mainLayout = new GridLayout ();
		mainLayout.numColumns = 1;
		parent.setLayout(mainLayout);
		
	}
	
	protected void initSearchBar (Composite parent) {
		// Construct a new parent container
		Composite parentComp = new Composite(parent, SWT.BORDER);
		parentComp.setLayout(new GridLayout(4, false));
		parentComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblSearchText = new Label (parentComp, SWT.NONE);
		lblSearchText.setText("Search expression:");
		
		// define the grid data for the layout
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalSpan = 1;
		lblSearchText.setLayoutData(gridData);
		
		filter = new Text (parentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		if (viewState != null && viewState.getSearchString() != null) {
			if (viewState.getSearchString().length() > 1 && 
				viewState.getSearchString().startsWith("*") && viewState.getSearchString().endsWith("*"))
				filter.setText(viewState.getSearchString().substring(1).substring(0, viewState.getSearchString().length()-2));
			else
				filter.setText(viewState.getSearchString());
			
		}
		GridData gridDatas = new GridData();
		gridDatas.horizontalAlignment = SWT.FILL;
		gridDatas.grabExcessHorizontalSpace = true;
		gridDatas.horizontalSpan = 3;
		filter.setLayoutData(gridDatas);
		
		lblScale = new Label (parentComp, SWT.None);
		lblScale.setText("\nPrecision:");
		GridData gdScaler = new GridData();
		gdScaler.verticalAlignment = SWT.CENTER;
		gdScaler.grabExcessVerticalSpace = true;
		gdScaler.horizontalSpan = 1;
		lblScale.setLayoutData(gdScaler);
		
		// Add a scale for specification of fuzzy Matching precision
		fuzzyScaler = new Scale (parentComp, SWT.None);
		fuzzyScaler.setMaximum(100);
		fuzzyScaler.setMinimum(0);
		fuzzyScaler.setIncrement(1);
		fuzzyScaler.setPageIncrement(5);
		fuzzyScaler.setSelection(Math.round((treeViewer != null ? treeViewer.getMatchingPrecision() : viewState.getMatchingPrecision())*100.f));
		fuzzyScaler.addListener (SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				float val = 1f-(Float.parseFloat(
								(fuzzyScaler.getMaximum() - 
								 fuzzyScaler.getSelection() + 
								 fuzzyScaler.getMinimum()) + "") / 100.f);
				treeViewer.setMatchingPrecision (val);
			}
		});
		fuzzyScaler.setSize(100, 10);
		
		GridData gdScalers = new GridData();
		gdScalers.verticalAlignment = SWT.BEGINNING;
		gdScalers.horizontalAlignment = SWT.FILL;
		gdScalers.horizontalSpan = 3;
		fuzzyScaler.setLayoutData(gdScalers);
		refreshSearchbarState();
	}
	
	protected void refreshSearchbarState () {
		lblScale.setVisible(treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled());
		fuzzyScaler.setVisible(treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled());
		if (treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled()) {
			((GridData)lblScale.getLayoutData()).heightHint = 40;
			((GridData)fuzzyScaler.getLayoutData()).heightHint = 40;
		} else {
			((GridData)lblScale.getLayoutData()).heightHint = 0;
			((GridData)fuzzyScaler.getLayoutData()).heightHint = 0;
		}

		lblScale.getParent().layout();
		lblScale.getParent().getParent().layout();
	}
	
	protected void initMessagesTree(Composite parent) {
		// Unregister the label provider as selection listener
		if (treeViewer != null && 
			treeViewer.getViewer() != null && 
			treeViewer.getViewer().getLabelProvider() != null && 
			treeViewer.getViewer().getLabelProvider() instanceof GlossaryLabelProvider)
			getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(((GlossaryLabelProvider)treeViewer.getViewer().getLabelProvider()));
		
		
		treeViewer = new GlossaryWidget (getSite(), parent, SWT.NONE, glossary != null ? glossary : null, viewState != null ? viewState.getReferenceLanguage() : null, 
				viewState != null ? viewState.getDisplayLanguages() : null);
		
		// Register the label provider as selection listener
		if (treeViewer.getViewer() != null && 
			treeViewer.getViewer().getLabelProvider() != null && 
			treeViewer.getViewer().getLabelProvider() instanceof GlossaryLabelProvider)
			getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(((GlossaryLabelProvider)treeViewer.getViewer().getLabelProvider()));
		if (treeViewer != null && this.glossary != null && this.glossary.getGlossary() != null) {	
			if (viewState != null && viewState.getSortings() != null)
				treeViewer.setSortInfo(viewState.getSortings());
				
			treeViewer.enableFuzzyMatching(viewState.isFuzzyMatchingEnabled());
			treeViewer.bindContentToSelection(viewState.isSelectiveViewEnabled());
			treeViewer.setMatchingPrecision(viewState.getMatchingPrecision());
			treeViewer.setEditable(viewState.isEditable());
			
			if (viewState.getSearchString() != null)
				treeViewer.setSearchString(viewState.getSearchString());
		}
				
		// define the grid data for the layout
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		treeViewer.setLayoutData(gridData);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		treeViewer.setFocus();
	}
	
	protected void redrawTreeViewer () {
		parent.setRedraw(false);
		treeViewer.dispose();
		try {
			initMessagesTree(parent);
			makeActions();
			contributeToActionBars();
			hookContextMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parent.setRedraw(true);
		parent.layout(true);
		treeViewer.layout(true);
		refreshSearchbarState();
	}
	
	/*** ACTIONS ***/
	private void makeActions() {
		newEntry = new Action () {
			@Override
			public void run() {
				super.run();
			}
		};
		newEntry.setText ("New term ...");
		newEntry.setDescription("Creates a new glossary entry");
		newEntry.setToolTipText("Creates a new glossary entry");
		
		enableFuzzyMatching = new Action () {
			public void run () {
				super.run();
				treeViewer.enableFuzzyMatching(!treeViewer.isFuzzyMatchingEnabled());
				viewState.setFuzzyMatchingEnabled(treeViewer.isFuzzyMatchingEnabled());
				refreshSearchbarState();
			}
		};
		enableFuzzyMatching.setText("Fuzzy-Matching");
		enableFuzzyMatching.setDescription("Enables Fuzzy matching for searching Resource-Bundle entries.");
		enableFuzzyMatching.setChecked(viewState.isFuzzyMatchingEnabled());
		enableFuzzyMatching.setToolTipText(enableFuzzyMatching.getDescription());
		
		editable = new Action () {
			public void run () {
				super.run();
				treeViewer.setEditable(!treeViewer.isEditable());
			}
		};
		editable.setText("Editable");
		editable.setDescription("Allows you to edit Resource-Bundle entries.");
		editable.setChecked(viewState.isEditable());
		editable.setToolTipText(editable.getDescription());
		
		/** New Translation */
		newTranslation = new Action ("New Translation ...") {
			public void run() {
				/* Construct a list of all Locales except Locales that are already part of the translation glossary */
				if (glossary == null || glossary.getGlossary() == null)
					return;

				List<Locale> allLocales = new ArrayList<Locale>();
				List<Locale> locales = new ArrayList<Locale>();
				for (String l : glossary.getGlossary().info.getTranslations()) {
					String [] locDef = l.split("_");
					Locale locale = locDef.length < 3 ? (locDef.length < 2 ? new Locale (locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale (locDef[0], locDef[1], locDef[2]);
					locales.add(locale);
				}
				
				for (Locale l : Locale.getAvailableLocales()) {
					if (!locales.contains(l))
						allLocales.add(l);
				}
				
				/* Ask the user for the set of locales that need to be added to the translation glossary */
				Collections.sort(allLocales, new Comparator<Locale>() {
					@Override
					public int compare(Locale o1, Locale o2) {
						return o1.getDisplayName().compareTo(o2.getDisplayName());
					}
				});
				ListSelectionDialog dlg = new ListSelectionDialog(getSite().getShell(), 
						allLocales, 
						new LocaleContentProvider(), 
						new LocaleLabelProvider(), 
						"Select the Translation:");
				dlg.setTitle("Translation Selection");
				if (dlg.open() == dlg.OK) {
					Object[] addLocales = (Object[]) dlg.getResult();
					for (Object addLoc : addLocales) {
						Locale locale = (Locale) addLoc;
						String strLocale = locale.toString();
						glossary.getGlossary().info.translations.add(strLocale);
					}

					try {
						glossary.saveGlossary();
						displayActions = null;
						referenceActions = null;
						viewState.setDisplayLanguages(null);
						redrawTreeViewer();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		};
		newTranslation.setDescription("Adds a new Locale for translation.");
		newTranslation.setToolTipText(newTranslation.getDescription());
		
		/** Delete Translation */
		deleteTranslation = new Action ("Delete Translation ...") {
			public void run() {
				/* Construct a list of type locale from all existing translations */
				if (glossary == null || glossary.getGlossary() == null)
					return;
				
				String referenceLang = glossary.getGlossary().info.getTranslations()[0];
				if (viewState != null && viewState.getReferenceLanguage() != null)
					referenceLang = viewState.getReferenceLanguage();
				
				List<Locale> locales = new ArrayList<Locale>();
				List<String> strLoc = new ArrayList<String>();
				for (String l : glossary.getGlossary().info.getTranslations()) {
					if (l.equalsIgnoreCase(referenceLang))
						continue;
					String [] locDef = l.split("_");
					Locale locale = locDef.length < 3 ? (locDef.length < 2 ? new Locale (locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale (locDef[0], locDef[1], locDef[2]);
					locales.add(locale);
					strLoc.add(l);
				}
				
				/* Ask the user for the set of locales that need to be removed from the translation glossary */
				ListSelectionDialog dlg = new ListSelectionDialog(getSite().getShell(), 
						locales, 
						new LocaleContentProvider(), 
						new LocaleLabelProvider(), 
						"Select the Translation:");
				dlg.setTitle("Translation Selection");
				if (dlg.open() == ListSelectionDialog.OK) {
					Object[] delLocales = (Object[]) dlg.getResult();
					List<String> toRemove = new ArrayList<String>();
					for (Object delLoc : delLocales) {
						toRemove.add(strLoc.get(locales.indexOf(delLoc)));
					}
					glossary.getGlossary().info.translations.removeAll(toRemove);
					try {
						glossary.saveGlossary();
						displayActions = null;
						referenceActions = null;
						viewState.setDisplayLanguages(null);
						redrawTreeViewer();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
		};
		deleteTranslation.setDescription("Deletes a specific Locale from the translation glossary.");
		deleteTranslation.setToolTipText(deleteTranslation.getDescription());
	}
	
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private void fillLocalPullDown(IMenuManager manager) {
		manager.removeAll();
		
		if (this.glossary != null && this.glossary.getGlossary() != null) {
			glossaryEditContribution = new GlossaryEntryMenuContribution(treeViewer, !treeViewer.getViewer().getSelection().isEmpty());
			manager.add(this.glossaryEditContribution);
			manager.add(new Separator());
		}
	
		manager.add(enableFuzzyMatching);
		manager.add(editable);
		
		if (this.glossary != null && this.glossary.getGlossary() != null) {
			manager.add(new Separator());
			manager.add(newTranslation);
			manager.add(deleteTranslation);
			createMenuAdditions (manager);
		}
	}

	/*** CONTEXT MENU ***/
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(treeViewer.getViewer().getControl());
		treeViewer.getViewer().getControl().setMenu(menu);
		getViewSite().registerContextMenu(menuMgr, treeViewer.getViewer());
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.removeAll();
		
		if (this.glossary != null && this.glossary.getGlossary() != null) {
			glossaryEditContribution = new GlossaryEntryMenuContribution(treeViewer, !treeViewer.getViewer().getSelection().isEmpty());
			manager.add(this.glossaryEditContribution);
			manager.add(new Separator());
			
			createShowContentMenu(manager);
			manager.add(new Separator());
		}
		manager.add(editable);
		manager.add(enableFuzzyMatching);
		
		/** Locale management section */
		if (this.glossary != null && this.glossary.getGlossary() != null) {
			manager.add(new Separator());
			manager.add(newTranslation);
			manager.add(deleteTranslation);
		}
		
		createMenuAdditions (manager);
	}
	
	private void createShowContentMenu (IMenuManager manager) {
		showMenu  = new MenuManager ("&Show", "show");
		
		if (showAll == null || showSelectiveContent == null) {			
			showAll = new Action ("All terms", Action.AS_RADIO_BUTTON) {
				@Override
				public void run() {
					super.run();
					treeViewer.bindContentToSelection(false);
				}
			};
			showAll.setDescription("Display all glossary entries");
			showAll.setToolTipText(showAll.getDescription());
			showAll.setChecked(!viewState.isSelectiveViewEnabled());
			
			showSelectiveContent = new Action ("Relevant terms", Action.AS_RADIO_BUTTON) {
				@Override
				public void run() {
					super.run();
					treeViewer.bindContentToSelection(true);
				}
			};
			showSelectiveContent.setDescription("Displays only terms that are relevant for the currently selected Resource-Bundle entry");
			showSelectiveContent.setToolTipText(showSelectiveContent.getDescription());
			showSelectiveContent.setChecked(viewState.isSelectiveViewEnabled());
		}
		
		showMenu.add(showAll);
		showMenu.add(showSelectiveContent);
		
		manager.add(showMenu);
	}
	
	private void createMenuAdditions(IMenuManager manager) {
		// Make reference language actions
		if (glossary != null && glossary.getGlossary() != null) { 
			Glossary g = glossary.getGlossary();
			final String[] translations = g.info.getTranslations();
			
			if (translations == null || translations.length == 0)
				return;
			
			referenceMenu = new MenuManager ("&Reference Translation", "reflang");
			if (referenceActions == null) {
				referenceActions = new ArrayList<Action>();
				
				for (final String lang : translations) {
					String[] locDef = lang.split("_");
					Locale l = locDef.length < 3 ? (locDef.length < 2 ? new Locale (locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale (locDef[0], locDef[1], locDef[2]);
					Action refLangAction = new Action (lang, Action.AS_RADIO_BUTTON) {
						@Override
						public void run() {
							super.run();
							// init reference language specification
							String referenceLanguage = translations[0];
							if (viewState.getReferenceLanguage() != null)
								referenceLanguage = viewState.getReferenceLanguage();

							if (!lang.equalsIgnoreCase(referenceLanguage)) {
								viewState.setReferenceLanguage(lang);
								
								// trigger redraw of displayed translations menu
								displayActions = null;
								
								redrawTreeViewer();
							}
						}
					};
					// init reference language specification
					String referenceLanguage = translations[0];
					if (viewState.getReferenceLanguage() != null)
						referenceLanguage = viewState.getReferenceLanguage();
					else 
						viewState.setReferenceLanguage(referenceLanguage);
					
					refLangAction.setChecked(lang.equalsIgnoreCase(referenceLanguage));
					refLangAction.setText(l.getDisplayName());
					referenceActions.add(refLangAction);
				}
			}
			
			for (Action a : referenceActions) {
				referenceMenu.add(a);
			}
			
			// Make display language actions
			displayMenu = new MenuManager ("&Displayed Translations", "displaylang");
			
			if (displayActions == null) {
				List<String> displayLanguages = viewState.getDisplayLanguages();
				
				if (displayLanguages == null) {
					viewState.setDisplayLangArr(translations);
					displayLanguages = viewState.getDisplayLanguages();
				}
				
				displayActions = new ArrayList<Action>();
				for (final String lang : translations) {
					String referenceLanguage = translations[0];
					if (viewState.getReferenceLanguage() != null)
						referenceLanguage = viewState.getReferenceLanguage();
					
					if (lang.equalsIgnoreCase(referenceLanguage))
						continue;
					
					String[] locDef = lang.split("_");
					Locale l = locDef.length < 3 ? (locDef.length < 2 ? new Locale (locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale (locDef[0], locDef[1], locDef[2]);
					Action refLangAction = new Action (lang, Action.AS_CHECK_BOX) {
						@Override
						public void run() {
							super.run();
							List<String> dls = viewState.getDisplayLanguages();
							if (this.isChecked()) {
								if (!dls.contains(lang))
									dls.add(lang);
							} else {
								if (dls.contains(lang))
									dls.remove(lang);
							}
							viewState.setDisplayLanguages(dls);
							redrawTreeViewer();
						}
					};
					// init reference language specification
					refLangAction.setChecked(displayLanguages.contains(lang));
					refLangAction.setText(l.getDisplayName());
					displayActions.add(refLangAction);
				}
			}
			
			for (Action a : displayActions) {
				displayMenu.add(a);
			}
			
			manager.add(new Separator());
			manager.add(referenceMenu);
			manager.add(displayMenu);
		}
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}
	
	@Override
	public void saveState (IMemento memento) {
		super.saveState(memento);
		try {
			viewState.setEditable (treeViewer.isEditable());
			viewState.setSortings(treeViewer.getSortInfo());
			viewState.setSearchString(treeViewer.getSearchString());
			viewState.setFuzzyMatchingEnabled(treeViewer.isFuzzyMatchingEnabled());
			viewState.setMatchingPrecision (treeViewer.getMatchingPrecision());
			viewState.setSelectiveViewEnabled(treeViewer.isSelectiveViewEnabled());
			viewState.saveState(memento);
		} catch (Exception e) {}
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
		
		// init Viewstate
		viewState = new GlossaryViewState(null, null, false, null);
		viewState.init(memento);
		if (viewState.getGlossaryFile() != null) {
			try {
				glossary = new GlossaryManager(new File (viewState.getGlossaryFile ()), false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void glossaryLoaded(LoadGlossaryEvent event) {
		File glossaryFile = event.getGlossaryFile();
		try {
			this.glossary = new GlossaryManager (glossaryFile, event.isNewGlossary());
			viewState.setGlossaryFile (glossaryFile.getAbsolutePath());
			
			referenceActions = null;
			displayActions = null;
			viewState.setDisplayLangArr(glossary.getGlossary().info.getTranslations());
			this.redrawTreeViewer();
		} catch (Exception e) {
			MessageDialog.openError(getViewSite().getShell(), 
					"Cannot open Glossary", "The choosen file does not represent a valid Glossary!");
		}
	}
}
