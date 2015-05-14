package org.eclipselabs.e4.tapiji.translator.ui.handler.treeviewer;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.storage.StoreInstanceState;


public final class ReferenceLanguageChangedHandler {

    private static final String TAG = ReferenceLanguageChangedHandler.class.getSimpleName();

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {
        if (menuItem.getContainerData() != null) {
            storeInstanceState.setReferenceLanguage(menuItem.getContainerData());
        }
        Log.d(TAG, String.format("Store reference language: %s", menuItem.getContainerData()));
    }

    @CanExecute
    public boolean canExecute() {
        return true;
    }
}
