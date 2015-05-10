package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import java.util.List;
import java.util.Locale;
import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class ReferenceTranslationHandler {

    private static final String CONTRIBUTION_URI = "bundleclass://org.eclipselabs.e4.tapiji.translator/org.eclipselabs.e4.tapiji.translator.handlers.treeviewer.ReferenceLanguageChangedHandler";
    private static final String TAG = ReferenceTranslationHandler.class.getSimpleName();

    @AboutToShow
    public void aboutToShow(final List<MMenuElement> items, final EModelService modelService,
                    final IGlossaryService glossaryService, final StoreInstanceState storeInstanceState) {


        final String[] translations = glossaryService.getTranslations();
        final String referenceLanguage = storeInstanceState.getReferenceLanguage();


        Log.d(TAG, String.format("Get reference language: %s", referenceLanguage));
        Log.d(TAG, String.format("Get reference language: %s", items));


        MDirectMenuItem dynamicItem;
        for (final String lang : translations) {
            Log.d(TAG, String.format("Language: %s", lang));
            dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
            dynamicItem.setLabel(getLocale(lang).getDisplayName());
            dynamicItem.setContainerData(lang);
            dynamicItem.setContributionURI(CONTRIBUTION_URI);
            if (referenceLanguage.equals(lang)) {
                dynamicItem.setSelected(true);
            } else {
                dynamicItem.setSelected(false);
            }
            dynamicItem.setType(ItemType.RADIO);
            items.add(dynamicItem);
        }

    }

    @AboutToHide
    public void aboutToHide(List<MMenuElement> items) {

    }

    public Locale getLocale(final String lang) {
        final String[] locDef = lang.split("_");
        final Locale l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0],
                        locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
        return l;
    }

}
