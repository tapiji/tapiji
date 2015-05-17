/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipselabs.tapiji.translator;


import java.lang.reflect.Constructor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public abstract class AbstractWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    /** Workbench state **/
    private IWorkbenchWindow window;

    /** System tray item / icon **/
    private TrayItem trayItem;
    private Image trayImage;

    /** Command ids **/
    private final static String COMMAND_ABOUT_ID = "org.eclipse.ui.help.aboutAction";
    private final static String COMMAND_EXIT_ID = "org.eclipse.ui.file.exit";

    public static final String INSTANCE_CLASS = "org.eclipselabs.tapiji.translator.ApplicationWorkbenchWindowAdvisor";


    public AbstractWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        AbstractApplicationActionBarAdvisor actionBar = null;
        try {
            Class<?> clazz = Class.forName(AbstractApplicationActionBarAdvisor.INSTANCE_CLASS);
            Constructor<?> constr = clazz.getConstructor(IActionBarConfigurer.class);
            actionBar = (AbstractApplicationActionBarAdvisor) constr.newInstance(configurer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return actionBar;
    }

    @Override
    abstract public void preWindowOpen();

    @Override
    public void postWindowOpen() {
        super.postWindowOpen();
        window = getWindowConfigurer().getWindow();

        /** Add the application into the system tray icon section **/
        trayItem = initTrayItem(window);

        // If tray items are not supported by the operating system
        if (trayItem != null) {

            // minimize / maximize action
            window.getShell().addShellListener(new ShellAdapter() {

                @Override
                public void shellIconified(ShellEvent e) {
                    window.getShell().setMinimized(true);
                    window.getShell().setVisible(false);
                }
            });

            trayItem.addListener(SWT.DefaultSelection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    Shell shell = window.getShell();
                    if (!shell.isVisible() || window.getShell().getMinimized()) {
                        window.getShell().setMinimized(false);
                        shell.setVisible(true);
                    }
                }
            });

            // Add actions menu
            hookActionsMenu();
        }
    }

    private void hookActionsMenu() {
        trayItem.addListener(SWT.MenuDetect, new Listener() {

            @Override
            public void handleEvent(Event event) {
                Menu menu = new Menu(window.getShell(), SWT.POP_UP);

                MenuItem about = new MenuItem(menu, SWT.None);
                about.setText("&�ber");
                about.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        try {
                            IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
                            handlerService.executeCommand(COMMAND_ABOUT_ID, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });

                MenuItem sep = new MenuItem(menu, SWT.SEPARATOR);

                // Add exit action
                MenuItem exit = new MenuItem(menu, SWT.NONE);
                exit.setText("Exit");
                exit.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        // Perform a call to the exit command
                        IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
                        try {
                            handlerService.executeCommand(COMMAND_EXIT_ID, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                menu.setVisible(true);
            }
        });
    }

    private TrayItem initTrayItem(IWorkbenchWindow window) {
        final Tray osTray = window.getShell().getDisplay().getSystemTray();

        // no support for system tray (RAP)
        if (osTray == null) {
            return null;
        }

        TrayItem item = new TrayItem(osTray, SWT.None);

        trayImage = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "/icons/TapiJI_32.png").createImage();
        item.setImage(trayImage);
        item.setToolTipText("TapiJI - Translator");

        return item;
    }

    @Override
    public void dispose() {
        try {
            super.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (trayImage != null) trayImage.dispose();
            if (trayItem != null) trayItem.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void postWindowClose() {
        super.postWindowClose();
    }
}
