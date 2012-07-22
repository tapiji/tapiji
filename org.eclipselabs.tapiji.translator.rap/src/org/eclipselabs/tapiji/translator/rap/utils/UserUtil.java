package org.eclipselabs.tapiji.translator.rap.utils;

import java.io.IOException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.rwt.RWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;

public class UserUtil {
	public final static String CONTEXT_ID_USERLOGGEDIN = "org.eclipselabs.tapiji.translator.userLoggedIn";
	private static IContextActivation loggedIn; 
	
	public static void setUserLoggedInContext(boolean activate) {
		IContextService contextService = (IContextService)PlatformUI.getWorkbench()
				.getService(IContextService.class);
		if (activate) {
			loggedIn = contextService.activateContext(CONTEXT_ID_USERLOGGEDIN);
		} else {
			if (loggedIn != null) {
				contextService.deactivateContext(loggedIn);
				loggedIn = null;
			}
		}
	}

	public static User verifyUser(String username, String password) {
		Resource resource = DBUtils.getPersistentData();
		EList<EObject> registeredObjects = resource.getContents();
		
		for (EObject obj : registeredObjects) {
			if (obj instanceof User) {
				User user = (User) obj;
				if (user.getUsername().equals(username)) {
					if (user.getPassword().equals(password))
						return user;
					// password is incorrect
					return null;
				}
			}
		}
		// username not found
		return null;
	}

	public static User loginUser(String username, String password) {
		User user = verifyUser(username, password);
		if (user != null) {
			RWT.getSessionStore().setAttribute(DBUtils.SESSION_USER_ATT, user);
			setUserLoggedInContext(true);
		}
		
		return user;
	}

	public static User logoutUser() {
		User user = (User) RWT.getSessionStore().getAttribute(DBUtils.SESSION_USER_ATT);
		RWT.getSessionStore().setAttribute(DBUtils.SESSION_USER_ATT, null);
		setUserLoggedInContext(false);
		return user;
	}

	public static boolean existsUser(String username) {
		Resource resource = DBUtils.getPersistentData();
		EList<EObject> registeredObjects = resource.getContents();
		
		for (EObject obj : registeredObjects) {
			if (obj instanceof User) {
				User user = (User) obj;
				if (user.getUsername().equals(username)) {
					return true;
				}
			}
		}
		// username not found
		return false;
	}

	public static User registerUser(String username, String password) throws IOException {
		if (existsUser(username))
			return null;
		
		User newUser = UserFactory.eINSTANCE.createUser();
		newUser.setUsername(username);
		newUser.setPassword(password);
		
		Resource resource = DBUtils.getPersistentData();
		resource.getContents().add(newUser);
		
		resource.save(null);
		
		return newUser;		
	}

	public static void unregisterUser(User user) throws IOException {
		if (! existsUser(user.getUsername()))
			return;
		
		Resource resource = DBUtils.getPersistentData();
		resource.getContents().remove(user);
		
		resource.save(null);
	}
}
