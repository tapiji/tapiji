package org.eclipselabs.e4.tapiji.editor.ui.handler.toolbar;


import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipselabs.e4.tapiji.logger.Log;


public final class KeyTreeVisibleHandler {

    private static final String TAG = KeyTreeVisibleHandler.class.getSimpleName();

    @Execute
    public void execute() {
        Log.d(TAG, "execute");
    }
}
