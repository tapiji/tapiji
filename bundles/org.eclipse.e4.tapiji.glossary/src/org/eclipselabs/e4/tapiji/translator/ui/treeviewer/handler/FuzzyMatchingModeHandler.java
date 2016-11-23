package org.eclipselabs.e4.tapiji.translator.ui.treeviewer.handler;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipselabs.e4.tapiji.translator.preference.StoreInstanceState;
import org.eclipselabs.e4.tapiji.translator.ui.glossary.GlossaryContract;


public final class FuzzyMatchingModeHandler {

    @Execute
    public void execute(final MMenuItem menuItem, final StoreInstanceState storeInstanceState, final MPart part) {
        if (part.getObject() instanceof GlossaryContract.View) {
            final GlossaryContract.View glossaryView = (GlossaryContract.View) part.getObject();
            storeInstanceState.setFuzzyMode(!menuItem.isSelected());
            glossaryView.showHideFuzzyMatching(!menuItem.isSelected());
        }
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
