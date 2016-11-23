package org.eclipse.e4.tapiji.glossary.ui.treeviewer.handler;


import java.util.List;
import java.util.Locale;

import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.tapiji.glossary.preference.StoreInstanceState;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;


public class LanguageVisibilityHandler {

    private static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.e4.tapiji.glossary/org.eclipse.e4.tapiji.glossary.ui.treeviewer.handler.LanguageVisibilityChangedHandler";
    private static final String TAG = LanguageVisibilityHandler.class.getSimpleName();

    @AboutToShow
    public void aboutToShow(final List<MMenuElement> items, final EModelService modelService, final IGlossaryService glossaryService, final StoreInstanceState storeInstanceState) {
        final String[] translations = glossaryService.getTranslations();


        Log.d(TAG, "TRANSLATIONS:" + translations.toString());

        final List<String> locales = storeInstanceState.getHiddenLocales();
        final String referenceLanguage = storeInstanceState.getReferenceLanguage();
        MDirectMenuItem dynamicItem;
        for (final String lang : translations) {
            if (referenceLanguage.equals(lang)) {
                continue;
            }
            dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
            dynamicItem.setLabel(getLocale(lang).getDisplayName());
            dynamicItem.setContributionURI(CONTRIBUTION_URI);
            
            if (locales.contains(lang)) {
                dynamicItem.setSelected(false);
            } else {
                dynamicItem.setSelected(true);
            }
            dynamicItem.setContainerData(lang);
            dynamicItem.setType(ItemType.CHECK);
            items.add(dynamicItem);
        }
        Log.d(TAG, "TERM: " + storeInstanceState.getHiddenLocales().toString());
    }

    public Locale getLocale(final String lang) {
        final String[] locDef = lang.split("_");
        return locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
    }
}
