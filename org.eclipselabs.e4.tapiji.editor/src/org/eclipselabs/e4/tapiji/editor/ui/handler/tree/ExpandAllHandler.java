package org.eclipselabs.e4.tapiji.editor.ui.handler.tree;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipselabs.e4.tapiji.logger.Log;


public final class ExpandAllHandler {

    private static final String TAG = ExpandAllHandler.class.getSimpleName();

    @Execute
    public void execute() {
        Log.d(TAG, "execute");
    }
}
