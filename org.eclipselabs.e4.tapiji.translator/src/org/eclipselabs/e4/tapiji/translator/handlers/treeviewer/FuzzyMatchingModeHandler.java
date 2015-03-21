package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.translator.constants.TranslatorConstants;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class FuzzyMatchingModeHandler {

    @Execute
    public void execute(final MMenuItem menuItem, final IEventBroker eventBroker, final StoreInstanceState storeInstanceState) {
        storeInstanceState.setFuzzyMode(!menuItem.isSelected());
        eventBroker.post(TranslatorConstants.TOPIC_GUI, menuItem.isSelected());
    }

    @CanExecute
    public boolean canExecute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {
        if (storeInstanceState.isFuzzyMode()) {
            menuItem.setSelected(storeInstanceState.isFuzzyMode());
        } else {
            storeInstanceState.setFuzzyMode(false);
        }
        return true;
    }
}
