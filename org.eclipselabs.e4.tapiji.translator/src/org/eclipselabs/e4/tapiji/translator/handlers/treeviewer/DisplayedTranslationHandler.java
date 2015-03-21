package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import java.util.List;
import java.util.Locale;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class DisplayedTranslationHandler {

    @AboutToShow
    public void aboutToShow(List<MMenuElement> items, EModelService modelService, IGlossaryService glossaryService, StoreInstanceState storeInstanceState) {
        final String[] translations = glossaryService.getGlossary().info.getTranslations();
        storeInstanceState.getReferenceLanguage();
        for (final String lang : translations) {
            String[] locDef = lang.split("_");
            Locale l = locDef.length < 3 ? (locDef.length < 2 ? new Locale(locDef[0]) : new Locale(locDef[0], locDef[1])) : new Locale(locDef[0], locDef[1], locDef[2]);
            MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
            dynamicItem.setLabel(l.getDisplayName());
            //  dynamicItem.setContributorURI("platform:/plugin/at.descher.test");
            dynamicItem.setContributionURI("bundleclass://org.eclipselabs.e4.tapiji.translator/org.eclipselabs.e4.tapiji.translator.handlers.treeviewer.TranslationVisibilityHandler");
            dynamicItem.setSelected(true);
            dynamicItem.setType(ItemType.CHECK);
            items.add(dynamicItem);
        }
        /* MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
          dynamicItem.setLabel("Dynamic Menu Item (" + new Date() + ")");
          dynamicItem.setContributorURI("platform:/plugin/at.descher.test");
          dynamicItem.setContributionURI("bundleclass://at.descher.test/at.descher.test.CommandHandler");
          dynamicItem.setSelected(true);
          dynamicItem.setType(ItemType.RADIO);

          MDirectMenuItem dynamicItem2 = modelService.createModelElement(MDirectMenuItem.class);
          dynamicItem2.setLabel("Dynamic Menu Item (" + new Date() + ")");
          dynamicItem2.setContributorURI("platform:/plugin/at.descher.test");
          dynamicItem2.setContributionURI("bundleclass://at.descher.test/at.descher.test.CommandHandler");
          
          items.add(dynamicItem);
         items.add(dynamicItem2);*/
    }

    @Execute
    public void execute() {
        System.err.println("Direct Menu Item selected");
    }
}
