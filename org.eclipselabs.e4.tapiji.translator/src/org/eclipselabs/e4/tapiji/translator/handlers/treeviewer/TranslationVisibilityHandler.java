package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class TranslationVisibilityHandler {

    private static final String TAG = TranslationVisibilityHandler.class.getSimpleName();

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {
        if (menuItem.isSelected()) {
            storeInstanceState.showLocale(menuItem.getContainerData());
        } else {
            storeInstanceState.hideLocale(menuItem.getContainerData());
        }
        Log.d(TAG, String.format("Store reference language: %s", menuItem.isSelected()));
    }


}
