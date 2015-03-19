package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.translator.preferences.Preference;


public final class EditModeHandler {

    @Execute
    public void execute(final Preference preferences, @Optional final MMenuItem menuItem) {
        preferences.setEditMode(!menuItem.isSelected());
    }

    @CanExecute
    public boolean canExecute(final Preference preferences, @Optional final MMenuItem menuItem) {
        boolean canExecute = true;
        if (menuItem == null) {
            canExecute = false;
        } else {
            menuItem.setSelected(preferences.isEditMode());
        }
        return canExecute;
    }
}
