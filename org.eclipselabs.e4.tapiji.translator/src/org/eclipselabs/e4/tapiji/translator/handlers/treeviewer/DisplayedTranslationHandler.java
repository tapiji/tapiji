package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import java.util.List;
import java.util.Locale;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class DisplayedTranslationHandler {

    private static final String CONTRIBUTION_URI = "bundleclass://org.eclipselabs.e4.tapiji.translator/org.eclipselabs.e4.tapiji.translator.handlers.treeviewer.TranslationVisibilityHandler";
    
    @AboutToShow
    public void aboutToShow(List<MMenuElement> items, EModelService modelService, IGlossaryService glossaryService, StoreInstanceState storeInstanceState) {
        final String[] translations = glossaryService.getTranslations();
        // storeInstanceState.getReferenceLanguage();
        MDirectMenuItem dynamicItem;
        for (final String lang : translations) {
            dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
            dynamicItem.setLabel(getLocale(lang).getDisplayName());
            dynamicItem.setContributionURI(CONTRIBUTION_URI);
            dynamicItem.setSelected(true);
            dynamicItem.setType(ItemType.CHECK);
            items.add(dynamicItem);
        }
    }

    public Locale getLocale(final String lang) {
        String[] locDef = lang.split("_");
        Locale l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
        return l;
    }

    @CanExecute
    public boolean canExecute(IGlossaryService glossaryService) {
        if (glossaryService.getGlossary() == null) {
            return false;
        }
        return true;
    }
}
