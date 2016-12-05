package org.eclipse.e4.tapiji.rap.glossary.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.service.ServiceHandler;

public class DownloadService implements ServiceHandler {
	 
		public static final String DOWNLOAD_HANDLER_ID = "downloadServiceHandler";
		public static final String FILEPATH_ID = "filepath";
		
		@Override
		public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
					// Which file to download?
					String filepath = request.getParameter( FILEPATH_ID );
					  
					// Get the file content    
					File file = new File(filepath);    
					if (! file.exists())
						return;
					
					FileInputStream fistream = new FileInputStream(file);
					byte[] download = new byte[(int) file.length()];
					fistream.read(download);
					fistream.close();
					
					// Send the file in the response	
					//response.setContentType( "application/" + FilenameUtils.getExtension(file.getName()));
					response.setContentLength( download.length );
					String contentDisposition = "attachment; filename=\"" + file.getName() + "\"";
					response.setHeader( "Content-Disposition", contentDisposition );
					    response.getOutputStream().write(download);
			
		}
}
