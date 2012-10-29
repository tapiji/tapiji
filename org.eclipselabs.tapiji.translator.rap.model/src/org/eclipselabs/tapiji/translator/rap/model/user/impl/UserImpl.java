/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.persistence.internal.security.SecurableObjectHolder;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.UserImpl#getUsername <em>Username</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.UserImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.UserImpl#getStoredRBs <em>Stored RBs</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UserImpl extends EObjectImpl implements User {
	/**
	 * The default value of the '{@link #getUsername() <em>Username</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUsername()
	 * @generated
	 * @ordered
	 */
	protected static final String USERNAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUsername() <em>Username</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUsername()
	 * @generated
	 * @ordered
	 */
	protected String username = USERNAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getPassword() <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPassword()
	 * @generated
	 * @ordered
	 */
	protected static final String PASSWORD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPassword()
	 * @generated
	 * @ordered
	 */
	protected String password = PASSWORD_EDEFAULT;

	/**
	 * The cached value of the '{@link #getStoredRBs() <em>Stored RBs</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStoredRBs()
	 * @generated
	 * @ordered
	 */
	protected EList<ResourceBundle> storedRBs;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UserImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UserPackage.Literals.USER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUsername(String newUsername) {
		String oldUsername = username;
		username = newUsername;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UserPackage.USER__USERNAME, oldUsername, username));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPassword() {
		return password;
	}

	public String getPasswordDecrypted() {
		// decrypt password from db
		return new SecurableObjectHolder().getSecurableObject().decryptPassword(password);
	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPassword(String newPassword) {
		String oldPassword = password;
		password = newPassword;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UserPackage.USER__PASSWORD, oldPassword, password));
	}
	
	public void setPasswordEncrypted(String newPassword) {
		// encrypt password before persist
		newPassword =  new SecurableObjectHolder().getSecurableObject().encryptPassword(newPassword);		
		setPassword(newPassword);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ResourceBundle> getStoredRBs() {
		if (storedRBs == null) {
			storedRBs = new EObjectResolvingEList<ResourceBundle>(ResourceBundle.class, this, UserPackage.USER__STORED_RBS);
		}
		return storedRBs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case UserPackage.USER__USERNAME:
				return getUsername();
			case UserPackage.USER__PASSWORD:
				return getPassword();
			case UserPackage.USER__STORED_RBS:
				return getStoredRBs();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case UserPackage.USER__USERNAME:
				setUsername((String)newValue);
				return;
			case UserPackage.USER__PASSWORD:
				setPassword((String)newValue);
				return;
			case UserPackage.USER__STORED_RBS:
				getStoredRBs().clear();
				getStoredRBs().addAll((Collection<? extends ResourceBundle>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case UserPackage.USER__USERNAME:
				setUsername(USERNAME_EDEFAULT);
				return;
			case UserPackage.USER__PASSWORD:
				setPassword(PASSWORD_EDEFAULT);
				return;
			case UserPackage.USER__STORED_RBS:
				getStoredRBs().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case UserPackage.USER__USERNAME:
				return USERNAME_EDEFAULT == null ? username != null : !USERNAME_EDEFAULT.equals(username);
			case UserPackage.USER__PASSWORD:
				return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
			case UserPackage.USER__STORED_RBS:
				return storedRBs != null && !storedRBs.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (username: ");
		result.append(username);
		result.append(", password: ");
		result.append(password);
		result.append(')');
		return result.toString();
	}

	// don't override (Teneo/EMF restriction) see http://www.eclipse.org/forums/index.php?t=msg&goto=71909&
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		
		if (obj instanceof User) {
			User otherUser = (User) obj;
			
			if (getUsername().equals(otherUser.getUsername()))
					return true;
			
			return false;
		}
		
		return super.equals(obj);
	}
	
} //UserImpl
