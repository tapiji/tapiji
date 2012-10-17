package org.eclipselabs.tapiji.translator.handler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.IServiceHandler;

public class DownloadServiceHandler implements IServiceHandler {
 
	public static final String DOWNLOAD_HANDLER_ID = "downloadServiceHandler";
	public static final String FILEPATH_ID = "filepath";
	
	public void service() throws IOException, ServletException {
	    // Which file to download?
		String filepath = RWT.getRequest().getParameter( FILEPATH_ID );
		  
		// Get the file content    
		File file = new File(filepath);    
		if (! file.exists())
			return;
		
		FileInputStream fistream = new FileInputStream(file);
		byte[] download = new byte[(int) file.length()];
		fistream.read(download);
		fistream.close();
		// Send the file in the response
		HttpServletResponse response = RWT.getResponse();	
		response.setContentType( "application/" + FilenameUtils.getExtension(file.getName()));
		response.setContentLength( download.length );
		String contentDisposition = "attachment; filename=\"" + file.getName() + "\"";
		response.setHeader( "Content-Disposition", contentDisposition );
		    response.getOutputStream().write(download);
	}
	
}