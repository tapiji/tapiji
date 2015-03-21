package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public class TranslationVisibilityHandler {

    private static final String TAG = TranslationVisibilityHandler.class.getSimpleName();

    @Execute
    public void execute(final MMenuItem menuItem, final IEventBroker eventBroker, final StoreInstanceState storeInstanceState) {
        Log.d(TAG, "" + menuItem.isSelected());
        Log.d(TAG, "" + menuItem.getLabel());

    }

    @CanExecute
    public boolean canExecute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {

        return true;
    }
}
