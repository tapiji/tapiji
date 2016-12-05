package org.eclipse.e4.tapiji.rap.glossary.ui.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.e4.tapiji.rap.glossary.service.DownloadService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
public class DownloadGlossaryDialog extends Dialog {

	private DownloadService downloadHandler;
	private Shell shell;

	public DownloadGlossaryDialog(Shell parentShell) {
		super(parentShell);
		this.shell = shell;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);	
		newShell.setText("Download properties files");
		registerDownloadServiceHandler();

	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);

		
		GridLayout layout = (GridLayout) comp.getLayout();
	    layout.numColumns = 1;
			    	    
	    Group group = new Group(comp, SWT.NONE);
        layout = new GridLayout(5, false);
        layout.numColumns = 2;
        layout.horizontalSpacing = 20;
        group.setLayout(layout);
        group.setText("Properties files of resource bundle TEST: ");
        
        Label propertiesFile = new Label(group, SWT.NONE);
    	propertiesFile.setText("blub");
    	
		Label downloadLink = new Label(group, SWT.NONE);
		downloadLink.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		downloadLink.setData("file", "blub");
		downloadLink.setText("<a href=\"# hui\">Download ...</a>");
		downloadLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				Label link = (Label) e.getSource();
				//PropertiesFile file = (PropertiesFile) link.getData("file");

				//String filepath = file.getPath();
				Browser browser = new Browser(shell, SWT.NONE);
				browser.setUrl(createDownloadUrl("123456"));
			}
		});
		
		return comp;
	}
	
	private String createDownloadUrl( String filepath ) {
		ServiceManager manager = RWT.getServiceManager();
		String url = manager.getServiceHandlerUrl(
				DownloadService.DOWNLOAD_HANDLER_ID);
		// add file path parameter to service url
		url += "&"+ DownloadService.FILEPATH_ID + "=" + filepath;
		return RWT.getResponse().encodeURL(url);
	}
	
	
	@Override
	   protected void createButtonsForButtonBar(Composite parent) {
	    super.createButtonsForButtonBar(parent);
	    // rename ok button to close
	    Button ok = getButton(IDialogConstants.OK_ID);
	    ok.setText("Close");
	    
	    GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		Point minSize = ok.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		data.horizontalSpan = 2;
		ok.setLayoutData(data);
		
	    //setButtonLayoutData(ok);
	    // dispose cancel button
	    Button cancel = getButton(IDialogConstants.CANCEL_ID);
	    cancel.dispose();
	    //setButtonLayoutData(cancel);
	 }
	
	private void registerDownloadServiceHandler() {
		// register service handler
		if (downloadHandler == null) {
			ServiceManager manager = RWT.getServiceManager();
			downloadHandler = new DownloadService();
			manager.registerServiceHandler( DownloadService.DOWNLOAD_HANDLER_ID, downloadHandler );
		}
	}
}
