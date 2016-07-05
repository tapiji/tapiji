package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.translator.constant.TranslatorConstant;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;


public final class FuzzyMatchingModeHandler {

    @Execute
    public void execute(final MMenuItem menuItem, final IEventBroker eventBroker, final StoreInstanceState storeInstanceState) {
        storeInstanceState.setFuzzyMode(!menuItem.isSelected());
        eventBroker.post(TranslatorConstant.TOPIC_SHOW_FUZZY_MATCHING, !menuItem.isSelected());
    }

    @CanExecute
    public boolean canExecute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {
        if ((menuItem == null) && (storeInstanceState == null)) {
            return false;
        } else {
            menuItem.setSelected(storeInstanceState.isFuzzyMode());
            return true;
        }
    }
}
