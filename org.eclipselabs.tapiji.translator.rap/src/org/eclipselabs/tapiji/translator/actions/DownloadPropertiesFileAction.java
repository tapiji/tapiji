package org.eclipselabs.tapiji.translator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipselabs.tapiji.translator.handler.DownloadServiceHandler;

public class DownloadPropertiesFileAction extends Action implements IWorkbenchWindowActionDelegate {
	
	private static final long serialVersionUID = 1854301925848519466L;
	private IWorkbenchWindow window;
	private IServiceHandler handler;
	
	
	@Override
	public void run(IAction action) {
		// register service handler
		if (handler == null) {
			IServiceManager manager = RWT.getServiceManager();
			handler = new DownloadServiceHandler();
			manager.registerServiceHandler( DownloadServiceHandler.DOWNLOAD_HANDLER_ID, handler );
		}
		
		
		IEditorPart activeEditor = window.getActivePage().getActiveEditor();
		// no file editor active
		if (activeEditor == null || ! (activeEditor.getEditorInput() instanceof IFileEditorInput))
			return;
		
		IFileEditorInput activeFei = (IFileEditorInput) activeEditor.getEditorInput();
		
		String fileName = activeFei.getFile().getLocation().toString();
		Browser browser = new Browser(window.getShell(), SWT.NONE);
		browser.setUrl(createDownloadUrl(fileName));
			
	}
	
	@Override
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		window = null;
	}
	
	private String createDownloadUrl( String filename ) {
		  StringBuilder url = new StringBuilder();
		  url.append( RWT.getRequest().getRequestURL() );
		  url.append( "?" );
		  url.append( IServiceHandler.REQUEST_PARAM );
		  url.append( "=" + DownloadServiceHandler.DOWNLOAD_HANDLER_ID );
		  url.append( "&filename=" );
		  url.append( filename );
		  return RWT.getResponse().encodeURL( url.toString() );
	}
}
