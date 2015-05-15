package org.eclipselabs.e4.tapiji.editor.ui.handler.toolbar;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipselabs.e4.tapiji.logger.Log;


public final class KeyMissingUnusedHandler {

    private static final String TAG = KeyMissingUnusedHandler.class.getSimpleName();

    @Execute
    public void execute() {
        Log.d(TAG, "execute");
    }
}
