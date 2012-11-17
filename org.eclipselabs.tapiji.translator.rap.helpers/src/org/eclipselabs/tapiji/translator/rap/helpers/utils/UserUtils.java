package org.eclipselabs.tapiji.translator.rap.helpers.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.rwt.RWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;

/**
 * Utility methods for user management
 * @author Matthias Lettmayer
 *
 */
public class UserUtils {
	/** user session attribute id */
	public static final String SESSION_USER_ATT = "org.eclipselabs.tapiji.translator.rap.model.user.User";
	/** context id, which indicates if user is logged in or logout */
	public static final String CONTEXT_ID_USERLOGGEDIN = "org.eclipselabs.tapiji.translator.userLoggedIn";
	/** A map that stores the user login context information. 
	 * Contains all logged in usernames (as keys) and their context activations (as values) */
	public static final Map<String, IContextActivation> loginContextMap = 
			new HashMap<String, IContextActivation>();
		
	/**
	 * Checks if a user is logged in, in this session. 
	 * @return true if user is logged in, false if user is logged out
	 */
	public static boolean isUserLoggedIn() {
		return getUser() != null;
	}
	
	/**
	 * Sets the context variable to control menu items.
	 * @param activate true to activate context variable, otherwise deactivates context variable
	 */
	public static void setUserLoggedInContext(boolean activate) {
		IContextService contextService = (IContextService)PlatformUI.getWorkbench()
				.getService(IContextService.class);		
		String username = getUser().getUsername();		
		
		// user login -> activate context variable
		if (activate) {						
			IContextActivation contextActivation = contextService.activateContext(CONTEXT_ID_USERLOGGEDIN);
			loginContextMap.put(username, contextActivation);
		// user logout -> deactivate context variable
		} else {
			IContextActivation contextActivation = loginContextMap.get(username);
			if (contextActivation != null) {
				contextService.deactivateContext(contextActivation);
				loginContextMap.remove(username);
			}
		}
	}

	/** 
	 * Checks if user is allowed to log in. Returns user object, if username exists in database 
	 * and password is correct
	 * @param username name of the user
	 * @param password password of the user
	 * @return The existing user object if username and password correct, otherwise null.
	 */
	public static User verifyUser(String username, String password) {
		Resource resource = DBUtils.getPersistentData();
		EList<EObject> registeredObjects = resource.getContents();
		
		for (EObject obj : registeredObjects) {
			if (obj instanceof User) {
				User user = (User) obj;
				if (user.getUsername().equals(username)) {
					// password + username correct
					if (user.equalsPassword(password))
						return user;
					// password is incorrect
					return null;
				}
			}
		}
		// username not found
		return null;
	}

	/**
	 * The given user will be logged in. Session attribute and context variable will be set.
	 * @param username name of user
	 * @param password password of user
	 * @return The logged in user or null if username or passwor incorrect.
	 */
	public static User loginUser(String username, String password) {
		User user = verifyUser(username, password);
		if (user != null) {
			RWT.getSessionStore().getHttpSession().setAttribute(UserUtils.SESSION_USER_ATT, user);
			setUserLoggedInContext(true);
		}
		
		return user;
	}

	/**
	 * The user stored in the session will be logged out. No exception will be thrown if no user is logged in.
	 * @return The user who was logged in or null if no user was logged in.
	 */
	public static User logoutUser() {
		User user = getUser();
		setUserLoggedInContext(false);
		RWT.getSessionStore().getHttpSession().setAttribute(UserUtils.SESSION_USER_ATT, null);		
		return user;
	}

	/**
	 * Checks if the given username matches an existing user stored in database.
	 * @param username name of a user
	 * @return true if the user exists or false if not.
	 */
	public static boolean existsUser(String username) {
		Resource resource = DBUtils.getPersistentData();
		EList<EObject> registeredObjects = resource.getContents();
		
		for (EObject obj : registeredObjects) {
			if (obj instanceof User) {
				User user = (User) obj;
				// not case sensitive
				if (user.getUsername().toLowerCase().equals(username.toLowerCase())) {
					return true;
				}
			}
		}
		// username not found
		return false;
	}

	/**
	 * Stores a new user in database. Does nothing if username exists already.
	 * @param username name of user
	 * @param password password of user
	 * @return The stored user or null if username exists already.
	 * @throws IOException if database write error occurs
	 */
	public static User registerUser(String username, String password) throws IOException {
		if (existsUser(username))
			return null;
		
		User newUser = UserFactory.eINSTANCE.createUser();
		newUser.setUsername(username);
		newUser.setPasswordEncrypted(password);
		
		Resource resource = DBUtils.getPersistentData();
		resource.getContents().add(newUser);
		
		resource.save(null);
		
		return newUser;		
	}

	/**
	 * Deletes an existing user from database.
	 * @param user user that will be deleted
	 * @throws IOException if database delete error occurs
	 */
	public static void unregisterUser(User user) throws IOException {
		if (! existsUser(user.getUsername()))
			return;
		
		Resource resource = DBUtils.getPersistentData();
		resource.getContents().remove(user);
		
		resource.save(null);
	}

	/**
	 * Returns the user from the session attribute.
	 * @return the stored user from session or null if no user is stored.
	 */
	public static User getUser() {
		return (User) RWT.getSessionStore().getHttpSession().getAttribute(UserUtils.SESSION_USER_ATT);
	}
	
	public static User getUser(String username) {
		Resource resource = DBUtils.getPersistentData();
		EList<EObject> registeredObjects = resource.getContents();
		
		for (EObject obj : registeredObjects) {
			if (obj instanceof User) {
				User user = (User) obj;
				if (user.getUsername().equals(username)) {
					return user;
				}
			}
		}
		// username not found
		return null;
	}
}
