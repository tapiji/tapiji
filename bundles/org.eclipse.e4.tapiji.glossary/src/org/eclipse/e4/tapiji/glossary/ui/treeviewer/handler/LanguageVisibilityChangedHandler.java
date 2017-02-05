package org.eclipse.e4.tapiji.glossary.ui.treeviewer.handler;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.glossary.preference.StoreInstanceState;
import org.eclipse.e4.tapiji.glossary.ui.glossary.GlossaryContract;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;


public final class LanguageVisibilityChangedHandler {

    private static final String TAG = LanguageVisibilityChangedHandler.class.getSimpleName();

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState, final MPart part) {
        if (part.getObject() instanceof GlossaryContract.View) {
            final GlossaryContract.View glossaryView = (GlossaryContract.View) part.getObject();
            Log.d(TAG, String.format("Store reference language: %s", menuItem.isSelected()));
            if (menuItem.isSelected()) {
                storeInstanceState.showLocale(menuItem.getContainerData());
            } else {
                storeInstanceState.hideLocale(menuItem.getContainerData());
            }
            glossaryView.getTreeViewerView().showHideTranslationColumn(menuItem.getContainerData());
        }
    }
}