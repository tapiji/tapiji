package org.eclipse.e4.tapiji.glossary;


import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.tapiji.glossary.ui.window.AboutHandler;
import org.eclipse.e4.tapiji.glossary.ui.window.ExitHandler;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.osgi.service.event.EventHandler;


@SuppressWarnings("restriction")
public final class LifeCycleManager {

    private static final String TAG = LifeCycleManager.class.getSimpleName();
    private static final String COMMAND_EXIT = "org.eclipse.ui.file.exit";
    private static final String COMMAND_ABOUT = "org.eclipse.ui.help.aboutAction";


    @PostContextCreate
    void postContextCreate(final IEventBroker eventBroker, final IEclipseContext context, final ITapijiResourceProvider resourceProvider) {
        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, AppStartupCompleteEventHandler.create(eventBroker, context, resourceProvider));
    }

    private static final class AppStartupCompleteEventHandler implements EventHandler {

        private final IEventBroker eventBroker;
        private final IEclipseContext context;
        private TrayItem trayItem;
        private final ITapijiResourceProvider resourceProvider;

        private AppStartupCompleteEventHandler(final IEventBroker eventBroker, final IEclipseContext context, final ITapijiResourceProvider resourceProvider) {
            this.eventBroker = eventBroker;
            this.context = context;
            this.resourceProvider = resourceProvider;
        }

        @Override
        public void handleEvent(final org.osgi.service.event.Event event) {
            final Shell shell = (Shell) context.get(IServiceConstants.ACTIVE_SHELL);
            final Tray systemTray = shell.getDisplay().getSystemTray();
            if (null != systemTray) {
                trayItem = new TrayItem(systemTray, SWT.None);
                trayItem.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_TAPIJI_64x64));
                trayItem.setToolTipText("TapiJI - Translator");

                if (null != trayItem) {
                    minimizeBehavior(shell);
                    popupMenu(shell);
                }
            }
            eventBroker.unsubscribe(this);
        }

        private void popupMenu(final Shell shell) {

            final ECommandService commandService = context.get(ECommandService.class);
            final EHandlerService handlerService = context.get(EHandlerService.class);
            trayItem.addListener(SWT.MenuDetect, new Listener() {

                @Override
                public void handleEvent(final Event event) {
                    final Menu menu = new Menu(shell, SWT.POP_UP);
                    final MenuItem menuItemAbout = new MenuItem(menu, SWT.None);
                    menuItemAbout.setText("&Über");
                    menuItemAbout.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(final Event event) {
                            executeCommand(commandService, handlerService, COMMAND_ABOUT, new AboutHandler());
                        }
                    });

                    new MenuItem(menu, SWT.SEPARATOR);

                    final MenuItem menuItemExit = new MenuItem(menu, SWT.NONE);
                    menuItemExit.setText("&Exit");
                    menuItemExit.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(final Event event) {
                            executeCommand(commandService, handlerService, COMMAND_EXIT, new ExitHandler());
                        }
                    });
                    menu.setVisible(true);
                }
            });
        }

        private void executeCommand(final ECommandService commandService, final EHandlerService handlerService, final String commandId, final Object handler) {
            final Command command = commandService.getCommand(commandId);
            if (command.isDefined()) {
                handlerService.activateHandler(commandId, handler);
                final ParameterizedCommand cmd = commandService.createCommand(commandId, null);
                if (handlerService.canExecute(cmd)) {
                    handlerService.executeHandler(cmd);
                }
            }
        }

        private void minimizeBehavior(final Shell shell) {
            shell.addShellListener(new ShellAdapter() {

                @Override
                public void shellIconified(final ShellEvent e) {
                    shell.setMinimized(true);
                }
            });
            trayItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    shell.setMinimized(false);
                }
            });
        }

        public static AppStartupCompleteEventHandler create(final IEventBroker eventBroker, final IEclipseContext context, final ITapijiResourceProvider resourceProvider) {
            return new AppStartupCompleteEventHandler(eventBroker, context, resourceProvider);
        }
    }
}
