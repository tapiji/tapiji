/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Properties File</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile#getPath <em>Path</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getPropertiesFile()
 * @model extendedMetaData="name='File' kind='elementOnly'"
 * @generated
 */
public interface PropertiesFile extends EObject {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' attribute.
	 * @see #setPath(String)
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#getPropertiesFile_Path()
	 * @model extendedMetaData="name='path' kind='element'"
	 * @generated
	 */
	String getPath();

	/**
	 * Sets the value of the '{@link org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile#getPath <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' attribute.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(String value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	String getFilename();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	ResourceBundle getResourceBundle();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void setFilename(String filename);

} // PropertiesFile
