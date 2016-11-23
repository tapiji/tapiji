package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;
import org.eclipselabs.e4.tapiji.translator.ui.glossary.GlossaryContract;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.TreeViewerContract.View;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.provider.TreeViewerContentProvider;


public final class ReferenceLanguageChangedHandler {

    private static final String TAG = ReferenceLanguageChangedHandler.class.getSimpleName();

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState, MPart part) {

        if (part.getObject() instanceof GlossaryContract.View) {
            final GlossaryContract.View glossaryView = (GlossaryContract.View) part.getObject();

            if ((menuItem.getContainerData() != null) && menuItem.isSelected()) {
                storeInstanceState.setReferenceLanguage(menuItem.getContainerData());

                final View view = glossaryView.getTreeViewerView();
                view.setReferenceLanguage(menuItem.getContainerData());
                view.updateView(((TreeViewerContentProvider) view.getTreeViewer().getContentProvider()).getGlossary());
            }
            Log.d(TAG, String.format("Store reference language: %s isSelected: %s", menuItem.getContainerData(), menuItem.isSelected()));
        }
    }
}
