package org.eclipselabs.tapiji.translator.rap.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;
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
import org.eclipselabs.tapiji.translator.handler.DownloadServiceHandler;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;

public class DownloadDialog extends Dialog {
	private ResourceBundle resourceBundle;
	private DownloadServiceHandler downloadHandler;
	
	public DownloadDialog(Shell parentShell) {
		super(parentShell);		
	}
	
	public DownloadDialog(Shell parentShell, ResourceBundle rb) {
		this(parentShell);
		resourceBundle = rb;
	}

	public void setRB(ResourceBundle rb) {
		resourceBundle = rb;
	}
	
	public ResourceBundle getRB() {
		return resourceBundle;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Download properties files");
		registerDownloadServiceHandler();
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
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		List<PropertiesFile> files = resourceBundle.getLocalFiles();
		
		GridLayout layout = (GridLayout) comp.getLayout();
	    layout.numColumns = 1;
			    	    
	    Group group = new Group(comp, SWT.NONE);
        layout = new GridLayout(files.size(), false);
        layout.numColumns = 2;
        layout.horizontalSpacing = 20;
        group.setLayout(layout);
        group.setText("Properties files of resource bundle \""+ resourceBundle.getName() + "\": ");
    
	    for (PropertiesFile file : files) {
	    	Label propertiesFile = new Label(group, SWT.NONE);
	    	propertiesFile.setText(file.getFilename());
	    	
			Label downloadLink = new Label(group, SWT.NONE);
			downloadLink.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
			downloadLink.setData("file", file);
			downloadLink.setText("<a href=\"#"+ file.getFilename() +"\">Download ...</a>");
			downloadLink.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					Label link = (Label) e.getSource();
					PropertiesFile file = (PropertiesFile) link.getData("file");

					String filepath = file.getPath();
					Browser browser = new Browser(getShell(), SWT.NONE);
					browser.setUrl(createDownloadUrl(filepath));
				}
			});
	    }
		
	    if (files.size() > 1) {
	    	GridData data = new GridData();
	    	data.verticalAlignment = SWT.BOTTOM;
	    	data.verticalIndent = 10;
			
	    	Label downloadAllFiles = new Label(group, SWT.NONE);
		    downloadAllFiles.setText("All "+ files.size() + " files");
	    	downloadAllFiles.setLayoutData(data);
	    	
		    Label downloadAllLink = new Label(group, SWT.NONE);
			downloadAllLink.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
			downloadAllLink.setText("<a href=\"#download_zip\">Download (zip) ...</a>");
			downloadAllLink.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					File zipFile = createZipFile();
					
					if (zipFile != null) {
						Browser browser = new Browser(getShell(), SWT.NONE);
						browser.setUrl(createDownloadUrl(zipFile.getAbsolutePath()));
					}
				}
			});		 
			
			data = new GridData();
			//data.verticalAlignment = SWT.BOTTOM;
			data.verticalIndent = 10;
			downloadAllLink.setLayoutData(data);
	    }
		
		return comp;
	}
	
	private String createDownloadUrl( String filepath ) {
		  StringBuilder url = new StringBuilder();
		  url.append( RWT.getRequest().getRequestURL() );
		  url.append( "?" );
		  url.append( IServiceHandler.REQUEST_PARAM );
		  url.append( "=" + DownloadServiceHandler.DOWNLOAD_HANDLER_ID );
		  url.append( "&"+ DownloadServiceHandler.FILEPATH_ID + "=" );
		  url.append( filepath );
		  return RWT.getResponse().encodeURL( url.toString() );
	}
	
	private File createZipFile() {		
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		String tempDir = System.getProperty("java.io.tmpdir");
	    String zipFilename = tempDir + File.separator + resourceBundle.getName() + ".zip";
	    
		try {
		    // Create the ZIP file in OS temp directory
			
		    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilename));

		    // Compress the files
		    for (PropertiesFile file : resourceBundle.getLocalFiles()) {
		        FileInputStream in = new FileInputStream(file.getPath());

		        // Add ZIP entry to output stream.
		        out.putNextEntry(new ZipEntry(file.getFilename()));

		        // Transfer bytes from the file to the ZIP file
		        int len;
		        while ((len = in.read(buf)) > 0) {
		            out.write(buf, 0, len);
		        }

		        // Complete the entry
		        out.closeEntry();
		        in.close();
		    }

		    // Complete the ZIP file
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File zipFile = new File(zipFilename);
		if (zipFile.exists())
			return zipFile;
		
		return null;
	}
	
	private void registerDownloadServiceHandler() {
		// register service handler
		if (downloadHandler == null) {
			IServiceManager manager = RWT.getServiceManager();
			downloadHandler = new DownloadServiceHandler();
			manager.registerServiceHandler( DownloadServiceHandler.DOWNLOAD_HANDLER_ID, downloadHandler );
		}
	}
}
