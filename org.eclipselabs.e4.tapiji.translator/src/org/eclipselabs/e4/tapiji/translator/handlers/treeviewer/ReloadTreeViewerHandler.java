package org.eclipselabs.e4.tapiji.translator.handlers.treeviewer;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;


public final class ReloadTreeViewerHandler {

    private static final String TAG = ReloadTreeViewerHandler.class.getSimpleName();

    @Execute
    public void execute(final IGlossaryService glossaryService) {
        Log.d(TAG, "Jsaddhdhsaudh");
        glossaryService.reloadGlossary();
    }
}
