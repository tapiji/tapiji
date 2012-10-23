package org.eclipselabs.tapiji.translator.rap.session;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.io.FileUtils;

public class TranslatorHTTPSessionListener implements HttpSessionListener {

    private static final String workspaceRelativePath = "eclipse" + File.separator + "workspace";
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
//		 HttpSession session = se.getSession();
//	        System.out.print(new Date(System.currentTimeMillis()).toString() + " (session) Created:");
//	        System.out.println("ID=" + session.getId() + " MaxInactiveInterval="
//	 + session.getMaxInactiveInterval());	        
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
        // session has been invalidated and all session data 
		// (except Id) is no longer available
        System.out.println(new Date(System.currentTimeMillis()).toString() + " (session) Destroyed:ID=" 
        		+ session.getId());
        
        // delete temp project (name=session id)
		try {
        	File tomcatWorkPath = (File) session.getServletContext().
        			getAttribute("javax.servlet.context.tempdir");
        	File tempProject = new File(tomcatWorkPath + File.separator + workspaceRelativePath 
        			+ File.separator + session.getId());
        	System.out.println("deleting " + tempProject.getAbsolutePath() + "...");
        	FileUtils.deleteDirectory(tempProject);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
