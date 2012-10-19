/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Resource Bundle</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getPropertiesFiles <em>Properties Files</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getSharedUsers <em>Shared Users</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle()
 * @model
 * @generated
 */
public interface ResourceBundle extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(long)
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle_Id()
	 * @model id="true"
	 * @generated
	 */
	long getId();

	/**
	 * Sets the value of the '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(long value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Properties Files</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Local Files</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties Files</em>' containment reference list.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle_PropertiesFiles()
	 * @model containment="true" resolveProxies="true" required="true"
	 * @generated
	 */
	EList<PropertiesFile> getPropertiesFiles();

	/**
	 * Returns the value of the '<em><b>Shared Users</b></em>' reference list.
	 * The list contents are of type {@link org.eclipselabs.tapiji.translator.rap.model.user.User}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Shared Users</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shared Users</em>' reference list.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle_SharedUsers()
	 * @model
	 * @generated
	 */
	EList<User> getSharedUsers();

	/**
	 * Returns the value of the '<em><b>Owner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owner</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Owner</em>' reference.
	 * @see #setOwner(User)
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle_Owner()
	 * @model
	 * @generated
	 */
	User getOwner();

	/**
	 * Sets the value of the '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getOwner <em>Owner</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Owner</em>' reference.
	 * @see #getOwner()
	 * @generated
	 */
	void setOwner(User value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isTemporary();

} // ResourceBundle
