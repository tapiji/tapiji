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
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getLocalFiles <em>Local Files</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle()
 * @model
 * @generated
 */
public interface ResourceBundle extends EObject {
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
	 * Returns the value of the '<em><b>Local Files</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Local Files</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Local Files</em>' containment reference list.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getResourceBundle_LocalFiles()
	 * @model containment="true" required="true"
	 * @generated
	 */
	EList<PropertiesFile> getLocalFiles();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	boolean isTemporary();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	User getUser();

} // ResourceBundle
