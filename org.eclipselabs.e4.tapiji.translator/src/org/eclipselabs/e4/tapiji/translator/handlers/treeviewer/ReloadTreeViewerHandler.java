package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;


public final class ReloadTreeViewerHandler {

    private static final String TAG = ReloadTreeViewerHandler.class.getSimpleName();

    @Execute
    public void execute(final IGlossaryService glossaryService) {
        glossaryService.reloadGlossary();
    }

    @CanExecute
    public boolean canExecute(final IGlossaryService glossaryService) {
        if (glossaryService.getGlossary() == null) {
            return false;
        }
        return true;
    }
}
