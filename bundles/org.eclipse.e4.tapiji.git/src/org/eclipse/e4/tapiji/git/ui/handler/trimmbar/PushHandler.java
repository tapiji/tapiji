package org.eclipse.e4.tapiji.git.ui.handler.trimmbar;


import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.ui.dialog.LoginDialog;
import org.eclipse.e4.tapiji.git.ui.preferences.Preferences;
import org.eclipse.swt.widgets.Shell;


public class PushHandler {

    @Execute
    public void exec(final IEclipseContext context, Shell shell, final IGitService service, Preferences prefs) {
        LoginDialog.show(context, shell);

        //        service.pushChanges(prefs.getSelectedRepository(), new IGitServiceCallback<Void>() {
        //
        //            @Override
        //            public void onSuccess(GitServiceResult<Void> response) {
        //                // TODO Auto-generated method stub
        //
        //            }
        //
        //            @Override
        //            public void onError(GitServiceException exception) {
        //                Log.d("asas", exception.toString());
        //                if (exception.getCause() instanceof TransportException) {
        //                    Log.d("asas", "dsadasadadadasd");
        //                }
        //            }
        //        });
    }
}
