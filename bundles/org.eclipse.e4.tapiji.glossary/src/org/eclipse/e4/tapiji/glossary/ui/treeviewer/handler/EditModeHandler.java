package org.eclipse.e4.tapiji.glossary.ui.treeviewer.handler;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.glossary.preference.StoreInstanceState;
import org.eclipse.e4.tapiji.glossary.ui.glossary.GlossaryContract;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;


public final class EditModeHandler {

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState, final MPart part) {
        if (part.getObject() instanceof GlossaryContract.View) {
            final GlossaryContract.View glossaryView = (GlossaryContract.View) part.getObject();
            storeInstanceState.setEditMode(!menuItem.isSelected());
            glossaryView.getTreeViewerView().setColumnEditable(!menuItem.isSelected());
        }
    }

    @CanExecute
    public boolean canExecute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {
        if ((menuItem == null) && (storeInstanceState == null)) {
            return false;
        } else {
            menuItem.setSelected(storeInstanceState.isEditMode());
            return true;
        }
    }
}
