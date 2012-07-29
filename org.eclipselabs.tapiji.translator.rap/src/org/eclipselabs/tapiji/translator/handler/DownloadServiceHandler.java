package org.eclipselabs.tapiji.translator.handler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;

public class DownloadServiceHandler implements IServiceHandler {
 
	public static final String DOWNLOAD_HANDLER_ID = "downloadServiceHandler";
	
  public void service() throws IOException, ServletException {
    // Which file to download?
    String fileName = RWT.getRequest().getParameter( "filename" );
  
    // Get the file content    
    File file = new File(fileName);    
    if (! file.exists())
    	return;
    
    FileInputStream fistream = new FileInputStream(file);
    byte[] download = new byte[(int) file.length()];
    fistream.read(download);
    fistream.close();
    // Send the file in the response
    HttpServletResponse response = RWT.getResponse();
    response.setContentType( "application/octet-stream" );
    response.setContentLength( download.length );
    String contentDisposition = "attachment; filename=\"" + fileName + "\"";
    response.setHeader( "Content-Disposition", contentDisposition );
    response.getOutputStream().write(download);
  }
}