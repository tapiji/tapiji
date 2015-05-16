package org.eclipselabs.e4.tapiji.translator;


import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.e4.ui.workbench.lifecycle.ProcessRemovals;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


@SuppressWarnings("restriction")
public class LifeCycleManager {

    private static final String TAG = LifeCycleManager.class.getSimpleName();
    public static final String PLUGIN_ID = "org.eclipselabs.e4.tapiji.translator"; //$NON-NLS-1$

    @PostContextCreate
    void postContextCreate(final IEventBroker eventBroker, final IEclipseContext context, IWorkbenchWindow work) {
        Log.d(TAG, "postContextCreate");

        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, AppStartupCompleteEventHandler.create(eventBroker, context, work));
    }

    @ProcessRemovals
    void processRemovals() {
        Log.d(TAG, "processRemovals");
    }

    private static final class AppStartupCompleteEventHandler implements EventHandler {

        private final IEventBroker eventBroker;
        private final IEclipseContext context;
        private Image trayImage;
        private TrayItem trayItem;
        private IWorkbenchWindow work;


        private AppStartupCompleteEventHandler(final IEventBroker eventBroker, final IEclipseContext context, IWorkbenchWindow work) {
            this.eventBroker = eventBroker;
            this.context = context;
            this.work = work;
        }

        @Override
        public void handleEvent(final Event event) {
            eventBroker.unsubscribe(this);
            final Shell shell = (Shell) context.get(IServiceConstants.ACTIVE_SHELL);
            createSystemTray(shell);

            minimizeBehavior(shell);
        }

        private void minimizeBehavior(Shell shell) {
            shell.addShellListener(new ShellAdapter() {

                // If the window is minimized hide the window
                @Override
                public void shellIconified(ShellEvent e) {
                    shell.setVisible(false);
                }
            });

            // If user double-clicks on the tray icons the application will be
            // visible again
            trayItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    // Shell shell = window.getShell();
                    if (!shell.isVisible()) {
                        work.getShell().setMinimized(false);
                        shell.setVisible(true);
                    }
                }
            });
        }

        private void createSystemTray(final Shell shell) {
            final Tray osTray = shell.getDisplay().getSystemTray();
            trayItem = new TrayItem(osTray, SWT.None);
            trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, "/tapiji.png").createImage();
            trayItem.setImage(trayImage);
            trayItem.setToolTipText("TapiJI - Translator");
        }

        private void createSystemTrayMenu() {

        }

        public static AppStartupCompleteEventHandler create(final IEventBroker eventBroker, final IEclipseContext context, IWorkbenchWindow work) {
            return new AppStartupCompleteEventHandler(eventBroker, context, work);
        }
    }
}
