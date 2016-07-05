package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.constant.TranslatorConstant;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;


public final class ReferenceLanguageChangedHandler {

    private static final String TAG = ReferenceLanguageChangedHandler.class.getSimpleName();

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState, final IEventBroker eventBroker) {
        if ((menuItem.getContainerData() != null) && menuItem.isSelected()) {
            storeInstanceState.setReferenceLanguage(menuItem.getContainerData());
            eventBroker.post(TranslatorConstant.TOPIC_REFERENCE_LANGUAGE, menuItem.getContainerData());
        }
        Log.d(TAG, String.format("Store reference language: %s isSelected: %s", menuItem.getContainerData(), menuItem.isSelected()));
    }

    @CanExecute
    public boolean canExecute() {
        return true;
    }
}
