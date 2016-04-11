package org.eclipselabs.tapiji.translator;

import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.core.message.manager.RBManager;
import org.eclipse.babel.editor.IMessagesEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public class ApplicationWorkbenchWindowAdvisor extends
		AbstractWorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// TODO [RAP] not yet supported (RAP 1.5)
		//configurer.setShowFastViewBars(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(false);
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setTitle("TapiJI Translator");
		// show only title (no minimize, maximize or close button)
		configurer.setShellStyle(SWT.TITLE);
	}
	
	@Override
	public void postWindowOpen() {		
		super.postWindowOpen();
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// maximize shell
		configurer.getWindow().getShell().setMaximized(true);
		
		// set user logged in context
		// user object is stored in http session that will not be destroyed when refresh will be pressed
		if (UserUtils.getUser() != null)
			UserUtils.setUserLoggedInContext(true);
		
		// add editor close listener
		configurer.getWindow().getActivePage().addPartListener(new IPartListener2() {			
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {				
			}
			
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {				
			}
			
			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {				
				IWorkbenchPart workbenchPart = partRef.getPart(false);
				IEditorPart editor = null;
				
				if (workbenchPart instanceof IEditorPart)
					editor = (IEditorPart) workbenchPart;
				
				if (editor != null && editor instanceof IMessagesEditor) {
					if (UserUtils.isUserLoggedIn()) {
						// release resource bundle locks
						IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
						String ifilePath = editorInput.getFile().getLocation().toOSString();
						
						PropertiesFile propsFile = DBUtils.getPropertiesFile(ifilePath);
						if (propsFile != null) {
							ResourceBundle rb = propsFile.getResourceBundle();
							// release only user locks
							RBLockManager.INSTANCE.releaseLocksHeldByUser(UserUtils.getUser(), rb);
						}
					}
					
					// dispose msg bundle group
					IMessagesBundleGroup msgBundleGroup = ((IMessagesEditor) editor).getBundleGroup();
					// dispose mbg with underlying eclipse properties editor resources
					msgBundleGroup.dispose();
					// dispose mbg with underlying properties file resources if it exists
					IMessagesBundleGroup mbgPfr = RBManager.getInstance(msgBundleGroup.getProjectName()).
							getMessagesBundleGroup("");
					if (mbgPfr != null)
						mbgPfr.dispose();
					
					
				}				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {				
			}
			
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
			}
		});
	}
	
}
