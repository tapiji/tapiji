package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.views.widgets.storage.StoreInstanceState;


public final class EditModeHandler {

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {
        storeInstanceState.setEditMode(menuItem.isSelected());
    }

    @CanExecute
    public boolean canExecute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState) {
        if (storeInstanceState.isEditMode()) {
            menuItem.setSelected(storeInstanceState.isEditMode());
        } else {
            storeInstanceState.setEditMode(false);
        }
        return true;
    }

    @CanExecute
    public boolean canExecute(IGlossaryService glossaryService) {
        if (glossaryService.getGlossary() == null) {
            return false;
        }
        return true;
    }
}
