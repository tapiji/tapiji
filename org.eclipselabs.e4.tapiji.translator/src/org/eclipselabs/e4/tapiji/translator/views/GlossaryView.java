/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.e4.tapiji.translator.views;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
import org.eclipselabs.e4.tapiji.logger.Log;
// import org.eclipse.ui.IActionBars;
// import org.eclipse.ui.IMemento;
// import org.eclipse.ui.IViewSite;
// import org.eclipse.ui.PartInitException;
// import org.eclipse.ui.dialogs.ListSelectionDialog;
// import org.eclipse.ui.part.ViewPart;

import org.eclipselabs.e4.tapiji.translator.model.Glossary;

import org.eclipselabs.e4.tapiji.translator.model.constants.GlossaryServiceConstants;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.menus.GlossaryEntryMenuContribution;
import org.eclipselabs.e4.tapiji.translator.views.widgets.GlossaryWidget;
import org.eclipselabs.e4.tapiji.translator.views.widgets.model.GlossaryViewState;
import org.eclipselabs.e4.tapiji.translator.views.widgets.provider.AbstractGlossaryLabelProvider;


public final class GlossaryView implements org.eclipse.swt.widgets.Listener {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "org.eclipselabs.tapiji.translator.views.GlossaryView";

  private static final String TAG = GlossaryView.class.getSimpleName();

  /*** Primary view controls ***/
  private GlossaryWidget treeViewer;
  private Scale fuzzyScaler;
  private Label lblScale;
  private Text inputFilter;

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
  // private IMemento memento;
  private GlossaryViewState viewState;

  private IGlossaryService glossaryService;

  @Inject
  private MPart part;

  @Inject
  private ESelectionService selectionService;
  
  @Inject
  private IEventBroker eventBroker;

  /**
   * The constructor.
   */
  public GlossaryView() {
    /**
     * Register the view for being informed each time a new glossary is loaded into the translator
     */
    //GlossaryManager.registerLoadGlossaryListener(this);

    Log.d("GlossaryView", "GlossaryView Constructor");
  }

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @PostConstruct
  public void createPartControl(final Composite parent, IGlossaryService glossaryService) {
    this.glossaryService = glossaryService;
    Log.i(TAG, "" + glossaryService.getGlossary());

    parent.setLayout(new GridLayout(1, false));
    initSearchBar(parent);


    /*
     * initMessagesTree(parent); makeActions(); hookContextMenu(); contributeToActionBars(); initListener(parent);
     */
  }

  
  @Inject
  @Optional
  private void getNotified(@UIEventTopic(GlossaryServiceConstants.TOPIC_GLOSSARY_NEW) String s) {
    Log.d(TAG, "GOT NOTIFICATION %s "+s);
    
  /*  final File glossaryFile = event.getGlossaryFile();
    try {
      
   //   this.glossaryService = new GlossaryManager(glossaryFile, event.isNewGlossary());
      viewState.setGlossaryFile(glossaryFile.getAbsolutePath());

      referenceActions = null;
      displayActions = null;
      viewState.setDisplayLangArr(glossaryService.getGlossary().info.getTranslations());
      this.redrawTreeViewer();
    } catch (final Exception e) {
      // MessageDialog.openError(getViewSite().getShell(), "Cannot open Glossary",
      //       "The choosen file does not represent a valid Glossary!");
    }*/
  }
  

  protected void initListener(final Composite parent) {
    inputFilter.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent e) {
        if ((glossaryService != null) && (glossaryService.getGlossary() != null)) {
          treeViewer.setSearchString(inputFilter.getText());
        }
      }
    });
  }

  private Label createLabel(final Composite parent, final String text) {
    final Label label = new Label(parent, SWT.None);
    label.setText(text);
    return label;
  }

  private GridData createGrid(final int horizontalAlignment, final int verticalAlignment,
          final boolean hasHorizontalSpace, final int horizontalSpan) {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = horizontalAlignment;
    gridData.verticalAlignment = verticalAlignment;
    gridData.grabExcessHorizontalSpace = hasHorizontalSpace;
    gridData.horizontalSpan = horizontalSpan;
    return gridData;
  }

  protected void initSearchBar(final Composite parent) {
    // Construct a new parent container
    final Composite parentComp = new Composite(parent, SWT.BORDER);
    parentComp.setLayout(new GridLayout(4, false));
    parentComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));


    final Label label = new Label(parentComp, SWT.None);
    label.setText("Search expression:");
    label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));


    inputFilter = new Text(parentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    // inputFilter.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 1, 1));

    if ((viewState != null) && (viewState.getSearchString() != null)) {
      if ((viewState.getSearchString().length() > 1) && viewState.getSearchString().startsWith("*")
              && viewState.getSearchString().endsWith("*")) {
        inputFilter.setText(viewState.getSearchString().substring(1)
                .substring(0, viewState.getSearchString().length() - 2));
      } else {
        inputFilter.setText(viewState.getSearchString());
      }

    }
    inputFilter.setLayoutData(createGrid(SWT.FILL, SWT.LEFT, true, 3));

    final GridData gdScaler = new GridData();
    gdScaler.verticalAlignment = SWT.CENTER;
    gdScaler.grabExcessVerticalSpace = true;
    gdScaler.horizontalSpan = 1;

    lblScale = createLabel(parentComp, "\nPrecision:");
    // lblScale.setLayoutData(createGrid(SWT.BEGINNING, SWT.CENTER, true, 1));

    // Add a scale for specification of fuzzy Matching precision
    fuzzyScaler = new Scale(parentComp, SWT.FILL);
    fuzzyScaler.setMaximum(100);
    fuzzyScaler.setMinimum(0);
    fuzzyScaler.setIncrement(1);
    fuzzyScaler.setPageIncrement(5);
    fuzzyScaler.setSelection(Math.round((treeViewer != null ? treeViewer.getMatchingPrecision() : viewState
            .getMatchingPrecision()) * 100.f));
    fuzzyScaler.addListener(SWT.Selection, new Listener() {

      @Override
      public void handleEvent(final Event event) {
        final float val = 1f - (Float.parseFloat(((fuzzyScaler.getMaximum() - fuzzyScaler.getSelection()) + fuzzyScaler
                .getMinimum()) + "") / 100.f);
        treeViewer.setMatchingPrecision(val);
        System.out.println("FUZZYSCALER");
      }
    });
    fuzzyScaler.setSize(100, 10);

    final GridData gdScalers = new GridData();
    gdScalers.verticalAlignment = SWT.BEGINNING;
    gdScalers.horizontalAlignment = SWT.FILL;
    gdScalers.horizontalSpan = 3;
    fuzzyScaler.setLayoutData(gdScalers);
    refreshSearchbarState();
  }

  protected void refreshSearchbarState() {
    lblScale.setVisible(treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled());
    fuzzyScaler.setVisible(treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState
            .isFuzzyMatchingEnabled());
    if (treeViewer != null ? treeViewer.isFuzzyMatchingEnabled() : viewState.isFuzzyMatchingEnabled()) {
      ((GridData) lblScale.getLayoutData()).heightHint = 40;
      ((GridData) fuzzyScaler.getLayoutData()).heightHint = 40;
    } else {
      ((GridData) lblScale.getLayoutData()).heightHint = 0;
      ((GridData) fuzzyScaler.getLayoutData()).heightHint = 0;
    }

    lblScale.getParent().layout();
    lblScale.getParent().getParent().layout();
  }

  protected void initMessagesTree(final Composite parent) {
    // Unregister the label provider as selection listener
    if ((treeViewer != null) && (treeViewer.getViewer() != null) && (treeViewer.getViewer().getLabelProvider() != null)
            && (treeViewer.getViewer().getLabelProvider() instanceof AbstractGlossaryLabelProvider)) {
      selectionService.removeSelectionListener((ISelectionListener) treeViewer.getViewer().getLabelProvider());
    }
    treeViewer = new GlossaryWidget(parent, SWT.NONE, glossaryService != null ? glossaryService : null,
            viewState != null ? viewState.getReferenceLanguage() : null,
            viewState != null ? viewState.getDisplayLanguages() : null);

    // Register the label provider as selection listener
    if ((treeViewer.getViewer() != null) && (treeViewer.getViewer().getLabelProvider() != null)
            && (treeViewer.getViewer().getLabelProvider() instanceof AbstractGlossaryLabelProvider)) {
      selectionService.addSelectionListener((ISelectionListener) treeViewer.getViewer().getLabelProvider());
    }

    if ((treeViewer != null) && (this.glossaryService != null) && (this.glossaryService.getGlossary() != null)) {
      if ((viewState != null) && (viewState.getSortings() != null)) {
        treeViewer.setSortInfo(viewState.getSortings());
      }

      treeViewer.enableFuzzyMatching(viewState.isFuzzyMatchingEnabled());
      treeViewer.bindContentToSelection(viewState.isSelectiveViewEnabled());
      treeViewer.setMatchingPrecision(viewState.getMatchingPrecision());
      treeViewer.setEditable(viewState.isEditable());

      if (viewState.getSearchString() != null) {
        treeViewer.setSearchString(viewState.getSearchString());
      }
    }

    // define the grid data for the layout
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.FILL;
    gridData.verticalAlignment = SWT.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    treeViewer.setLayoutData(gridData);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Focus
  public void setFocus() {
    if (treeViewer != null) {
      treeViewer.setFocus();
    }
  }

  protected void redrawTreeViewer() {
    parent.setRedraw(false);
    treeViewer.dispose();
    try {
      initMessagesTree(parent);
      makeActions();
      contributeToActionBars();
      hookContextMenu();
    } catch (final Exception e) {
      e.printStackTrace();
    }
    parent.setRedraw(true);
    parent.layout(true);
    treeViewer.layout(true);
    refreshSearchbarState();
  }

  /*** ACTIONS ***/
  private void makeActions() {
    newEntry = new Action() {

      @Override
      public void run() {
        super.run();
      }
    };
    newEntry.setText("New term ...");
    newEntry.setDescription("Creates a new glossary entry");
    newEntry.setToolTipText("Creates a new glossary entry");

    enableFuzzyMatching = new Action() {

      @Override
      public void run() {
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

    editable = new Action() {

      @Override
      public void run() {
        super.run();
        treeViewer.setEditable(!treeViewer.isEditable());
      }
    };
    editable.setText("Editable");
    editable.setDescription("Allows you to edit Resource-Bundle entries.");
    editable.setChecked(viewState.isEditable());
    editable.setToolTipText(editable.getDescription());

    /** New Translation */
    newTranslation = new Action("New Translation ...") {

      @Override
      public void run() {
        /*
         * Construct a list of all Locales except Locales that are already part of the translation glossary
         */
        if ((glossaryService == null) || (glossaryService.getGlossary() == null)) {
          return;
        }

        final List<Locale> allLocales = new ArrayList<Locale>();
        final List<Locale> locales = new ArrayList<Locale>();
        for (final String l : glossaryService.getGlossary().info.getTranslations()) {
          final String[] locDef = l.split("_");
          final Locale locale = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0],
                  locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
          locales.add(locale);
        }

        for (final Locale l : Locale.getAvailableLocales()) {
          if (!locales.contains(l)) {
            allLocales.add(l);
          }
        }

        /*
         * Ask the user for the set of locales that need to be added to the translation glossary
         */
        Collections.sort(allLocales, new Comparator<Locale>() {

          @Override
          public int compare(final Locale o1, final Locale o2) {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
          }
        });
        /*
         * ListSelectionDialog dlg = new ListSelectionDialog(getSite() .getShell(), allLocales, new
         * LocaleContentProvider(), new LocaleLabelProvider(), "Select the Translation:");
         * dlg.setTitle("Translation Selection"); if (dlg.open() == dlg.OK) { Object[] addLocales = (Object[])
         * dlg.getResult(); for (Object addLoc : addLocales) { Locale locale = (Locale) addLoc; String strLocale =
         * locale.toString(); glossary.getGlossary().info.translations.add(strLocale); } try { glossary.saveGlossary();
         * displayActions = null; referenceActions = null; viewState.setDisplayLanguages(null); redrawTreeViewer(); }
         * catch (Exception e) { e.printStackTrace(); } }
         */
      };
    };
    newTranslation.setDescription("Adds a new Locale for translation.");
    newTranslation.setToolTipText(newTranslation.getDescription());

    /** Delete Translation */
    deleteTranslation = new Action("Delete Translation ...") {

      @Override
      public void run() {
        /*
         * Construct a list of type locale from all existing translations
         */
        if ((glossaryService == null) || (glossaryService.getGlossary() == null)) {
          return;
        }

        String referenceLang = glossaryService.getGlossary().info.getTranslations()[0];
        if ((viewState != null) && (viewState.getReferenceLanguage() != null)) {
          referenceLang = viewState.getReferenceLanguage();
        }

        final List<Locale> locales = new ArrayList<Locale>();
        final List<String> strLoc = new ArrayList<String>();
        for (final String l : glossaryService.getGlossary().info.getTranslations()) {
          if (l.equalsIgnoreCase(referenceLang)) {
            continue;
          }
          final String[] locDef = l.split("_");
          final Locale locale = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0],
                  locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
          locales.add(locale);
          strLoc.add(l);
        }

        /*
         * Ask the user for the set of locales that need to be removed from the translation glossary
         */
        // ListSelectionDialog dlg = new ListSelectionDialog(getSite()
        // .getShell(), locales, new LocaleContentProvider(),
        // new LocaleLabelProvider(), "Select the Translation:");
        // dlg.setTitle("Translation Selection");
        // if (dlg.open() == ListSelectionDialog.OK) {
        // Object[] delLocales = (Object[]) dlg.getResult();
        // List<String> toRemove = new ArrayList<String>();
        // for (Object delLoc : delLocales) {
        // toRemove.add(strLoc.get(locales.indexOf(delLoc)));
        // }
        // glossary.getGlossary().info.translations
        // .removeAll(toRemove);
        // try {
        // glossary.saveGlossary();
        // displayActions = null;
        // referenceActions = null;
        // viewState.setDisplayLanguages(null);
        // redrawTreeViewer();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
      };
    };
    deleteTranslation.setDescription("Deletes a specific Locale from the translation glossary.");
    deleteTranslation.setToolTipText(deleteTranslation.getDescription());
  }

  private void contributeToActionBars() {
    MToolBar bars = part.getToolbar();

    // fillLocalPullDown(bars.getMenuManager());
    // fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(final IMenuManager manager) {
    manager.removeAll();

    if ((this.glossaryService != null) && (this.glossaryService.getGlossary() != null)) {
      glossaryEditContribution = new GlossaryEntryMenuContribution(treeViewer, !treeViewer.getViewer().getSelection()
              .isEmpty());
      manager.add(this.glossaryEditContribution);
      manager.add(new Separator());
    }

    manager.add(enableFuzzyMatching);
    manager.add(editable);

    if ((this.glossaryService != null) && (this.glossaryService.getGlossary() != null)) {
      manager.add(new Separator());
      manager.add(newTranslation);
      manager.add(deleteTranslation);
      createMenuAdditions(manager);
    }
  }

  /*** CONTEXT MENU ***/
  private void hookContextMenu() {
    final MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      @Override
      public void menuAboutToShow(final IMenuManager manager) {
        fillContextMenu(manager);
      }
    });
    final Menu menu = menuMgr.createContextMenu(treeViewer.getViewer().getControl());
    treeViewer.getViewer().getControl().setMenu(menu);
    // getViewSite().registerContextMenu(menuMgr, treeViewer.getViewer());
  }

  private void fillContextMenu(final IMenuManager manager) {
    manager.removeAll();

    if ((this.glossaryService != null) && (this.glossaryService.getGlossary() != null)) {
      glossaryEditContribution = new GlossaryEntryMenuContribution(treeViewer, !treeViewer.getViewer().getSelection()
              .isEmpty());
      manager.add(this.glossaryEditContribution);
      manager.add(new Separator());

      createShowContentMenu(manager);
      manager.add(new Separator());
    }
    manager.add(editable);
    manager.add(enableFuzzyMatching);

    /** Locale management section */
    if ((this.glossaryService != null) && (this.glossaryService.getGlossary() != null)) {
      manager.add(new Separator());
      manager.add(newTranslation);
      manager.add(deleteTranslation);
    }

    createMenuAdditions(manager);
  }

  private void createShowContentMenu(final IMenuManager manager) {
    showMenu = new MenuManager("&Show", "show");

    if ((showAll == null) || (showSelectiveContent == null)) {
      showAll = new Action("All terms", IAction.AS_RADIO_BUTTON) {

        @Override
        public void run() {
          super.run();
          treeViewer.bindContentToSelection(false);
        }
      };
      showAll.setDescription("Display all glossary entries");
      showAll.setToolTipText(showAll.getDescription());
      showAll.setChecked(!viewState.isSelectiveViewEnabled());

      showSelectiveContent = new Action("Relevant terms", IAction.AS_RADIO_BUTTON) {

        @Override
        public void run() {
          super.run();
          treeViewer.bindContentToSelection(true);
        }
      };
      showSelectiveContent
              .setDescription("Displays only terms that are relevant for the currently selected Resource-Bundle entry");
      showSelectiveContent.setToolTipText(showSelectiveContent.getDescription());
      showSelectiveContent.setChecked(viewState.isSelectiveViewEnabled());
    }

    showMenu.add(showAll);
    showMenu.add(showSelectiveContent);

    manager.add(showMenu);
  }

  private void createMenuAdditions(final IMenuManager manager) {
    // Make reference language actions
    if ((glossaryService != null) && (glossaryService.getGlossary() != null)) {
      final Glossary g = glossaryService.getGlossary();
      final String[] translations = g.info.getTranslations();

      if ((translations == null) || (translations.length == 0)) {
        return;
      }

      referenceMenu = new MenuManager("&Reference Translation", "reflang");
      if (referenceActions == null) {
        referenceActions = new ArrayList<Action>();

        for (final String lang : translations) {
          final String[] locDef = lang.split("_");
          final Locale l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0],
                  locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
          final Action refLangAction = new Action(lang, IAction.AS_RADIO_BUTTON) {

            @Override
            public void run() {
              super.run();
              // init reference language specification
              String referenceLanguage = translations[0];
              if (viewState.getReferenceLanguage() != null) {
                referenceLanguage = viewState.getReferenceLanguage();
              }

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
          if (viewState.getReferenceLanguage() != null) {
            referenceLanguage = viewState.getReferenceLanguage();
          } else {
            viewState.setReferenceLanguage(referenceLanguage);
          }

          refLangAction.setChecked(lang.equalsIgnoreCase(referenceLanguage));
          refLangAction.setText(l.getDisplayName());
          referenceActions.add(refLangAction);
        }
      }

      for (final Action a : referenceActions) {
        referenceMenu.add(a);
      }

      // Make display language actions
      displayMenu = new MenuManager("&Displayed Translations", "displaylang");

      if (displayActions == null) {
        List<String> displayLanguages = viewState.getDisplayLanguages();

        if (displayLanguages == null) {
          viewState.setDisplayLangArr(translations);
          displayLanguages = viewState.getDisplayLanguages();
        }

        displayActions = new ArrayList<Action>();
        for (final String lang : translations) {
          String referenceLanguage = translations[0];
          if (viewState.getReferenceLanguage() != null) {
            referenceLanguage = viewState.getReferenceLanguage();
          }

          if (lang.equalsIgnoreCase(referenceLanguage)) {
            continue;
          }

          final String[] locDef = lang.split("_");
          final Locale l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0],
                  locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
          final Action refLangAction = new Action(lang, IAction.AS_CHECK_BOX) {

            @Override
            public void run() {
              super.run();
              final List<String> dls = viewState.getDisplayLanguages();
              if (this.isChecked()) {
                if (!dls.contains(lang)) {
                  dls.add(lang);
                }
              } else {
                if (dls.contains(lang)) {
                  dls.remove(lang);
                }
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

      for (final Action a : displayActions) {
        displayMenu.add(a);
      }

      manager.add(new Separator());
      manager.add(referenceMenu);
      manager.add(displayMenu);
    }
  }

  private void fillLocalToolBar(final IToolBarManager manager) {
  }

  // @Override
  // public void saveState(IMemento memento) {
  // super.saveState(memento);
  // try {
  // viewState.setEditable(treeViewer.isEditable());
  // viewState.setSortings(treeViewer.getSortInfo());
  // viewState.setSearchString(treeViewer.getSearchString());
  // viewState.setFuzzyMatchingEnabled(treeViewer
  // .isFuzzyMatchingEnabled());
  // viewState.setMatchingPrecision(treeViewer.getMatchingPrecision());
  // viewState.setSelectiveViewEnabled(treeViewer
  // .isSelectiveViewEnabled());
  // viewState.saveState(memento);
  // } catch (Exception e) {
  // }
  // }
  //
  // @Override
  // public void init(IViewSite site, IMemento memento) throws PartInitException {
  // super.init(site, memento);
  // this.memento = memento;
  //
  // // init Viewstate
  // viewState = new GlossaryViewState(null, null, false, null);
  // viewState.init(memento);
  // if (viewState.getGlossaryFile() != null) {
  // try {
  // glossary = new GlossaryManager(new File(
  // viewState.getGlossaryFile()), false);
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // }
  // }


  @Override
  public void handleEvent(Event event) {
    // TODO Auto-generated method stub

  }


}
