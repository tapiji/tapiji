/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getUsername <em>Username</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getStoredRBs <em>Stored RBs</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getUser()
 * @model extendedMetaData="name='User' kind='elementOnly'"
 * @generated
 */
public interface User extends EObject {
	/**
	 * Returns the value of the '<em><b>Username</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Username</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Username</em>' attribute.
	 * @see #setUsername(String)
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getUser_Username()
	 * @model extendedMetaData="name='username' kind='element'"
	 * @generated
	 */
	String getUsername();

	/**
	 * Sets the value of the '{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getUsername <em>Username</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Username</em>' attribute.
	 * @see #getUsername()
	 * @generated
	 */
	void setUsername(String value);

	/**
	 * Returns the value of the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Password</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Password</em>' attribute.
	 * @see #setPassword(String)
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getUser_Password()
	 * @model unique="false"
	 *        extendedMetaData="name='password' kind='element'"
	 * @generated
	 */
	String getPassword();

	String getPasswordDecrypted();
	
	/**
	 * Sets the value of the '{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getPassword <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Password</em>' attribute.
	 * @see #getPassword()
	 * @generated
	 */
	void setPassword(String value);

	void setPasswordEncrypted(String password);
	
	/**
	 * Returns the value of the '<em><b>Stored RBs</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Stored RBs</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Stored RBs</em>' containment reference list.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getUser_StoredRBs()
	 * @model containment="true"
	 * @generated
	 */
	EList<ResourceBundle> getStoredRBs();

} // User
