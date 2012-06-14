package org.eclipse.tapiji.rap.translator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.tapiji.rap.translator.utils.FileUtils;


public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	/** Workbench state **/
	private IWorkbenchWindow window;
	
	/** System tray item / icon **/
	private TrayItem trayItem;
	private Image trayImage;
	
	/** Command ids **/
	private final static String COMMAND_ABOUT_ID	= "org.eclipse.ui.help.aboutAction";
	private final static String COMMAND_EXIT_ID 	= "org.eclipse.ui.file.exit";
	
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setShowFastViewBars(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setInitialSize(new Point (1024, 768));
		
		/** Init workspace and container project */
		/*TODO: try {
			FileUtils.getProject();
		} catch (CoreException e) {
		}*/
	}
	
	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		window = getWindowConfigurer().getWindow();
		
		/** Add the application into the system tray icon section **/
		trayItem = initTrayItem (window);
		
		// If tray items are not supported by the operating system
		if (trayItem != null) {
			
			// minimize / maximize action
			window.getShell().addShellListener(new ShellAdapter() {
				public void shellIconified (ShellEvent e) {
					window.getShell().setMinimized(true);
					window.getShell().setVisible(false);
				}
			});
			
			trayItem.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent (Event event) {
					Shell shell = window.getShell();
					if (!shell.isVisible() || window.getShell().getMinimized()) {
						window.getShell().setMinimized(false);
						shell.setVisible(true);
					}
				}
			});
			
			// Add actions menu
			hookActionsMenu ();
		}
	}
	
	private void hookActionsMenu () {
		trayItem.addListener (SWT.MenuDetect, new Listener () {
			@Override
			public void handleEvent(Event event) {
				Menu menu = new Menu (window.getShell(), SWT.POP_UP);
				
				MenuItem about = new MenuItem (menu, SWT.None);
				about.setText("&�ber");
				about.addListener(SWT.Selection, new Listener () {

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
				
				MenuItem sep = new MenuItem (menu, SWT.SEPARATOR);
				
				// Add exit action
				MenuItem exit = new MenuItem (menu, SWT.NONE);
				exit.setText("Exit");
				exit.addListener (SWT.Selection, new Listener() {
					@Override
					public void handleEvent(Event event) {
						// Perform a call to the exit command
						IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
						try {
							handlerService.executeCommand (COMMAND_EXIT_ID, null);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				
				menu.setVisible(true);
			}
		});
	}
	
	private TrayItem initTrayItem (IWorkbenchWindow window) {
		final Tray osTray = window.getShell().getDisplay().getSystemTray();
		TrayItem item = new TrayItem (osTray, SWT.None);
		
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
			if (trayImage != null)
				trayImage.dispose();
			if (trayItem != null)
				trayItem.dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void postWindowClose() {
		super.postWindowClose();
	}
}
