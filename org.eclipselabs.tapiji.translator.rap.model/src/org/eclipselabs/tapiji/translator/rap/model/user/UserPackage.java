/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserFactory
 * @model kind="package"
 * @generated
 */
public interface UserPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "user";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://user/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "user";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	UserPackage eINSTANCE = org.eclipselabs.tapiji.translator.rap.model.user.impl.UserPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.UserImpl <em>User</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserImpl
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserPackageImpl#getUser()
	 * @generated
	 */
	int USER = 0;

	/**
	 * The feature id for the '<em><b>Username</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER__USERNAME = 0;

	/**
	 * The feature id for the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER__PASSWORD = 1;

	/**
	 * The feature id for the '<em><b>Stored RBs</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER__STORED_RBS = 2;

	/**
	 * The number of structural features of the '<em>User</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int USER_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.PropertiesFileImpl <em>Properties File</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.PropertiesFileImpl
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserPackageImpl#getPropertiesFile()
	 * @generated
	 */
	int PROPERTIES_FILE = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_FILE__ID = 0;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_FILE__PATH = 1;

	/**
	 * The number of structural features of the '<em>Properties File</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTIES_FILE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl <em>Resource Bundle</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserPackageImpl#getResourceBundle()
	 * @generated
	 */
	int RESOURCE_BUNDLE = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_BUNDLE__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_BUNDLE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Local Files</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_BUNDLE__LOCAL_FILES = 2;

	/**
	 * The feature id for the '<em><b>Shared Users</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_BUNDLE__SHARED_USERS = 3;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_BUNDLE__OWNER = 4;

	/**
	 * The number of structural features of the '<em>Resource Bundle</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_BUNDLE_FEATURE_COUNT = 5;


	/**
	 * Returns the meta object for class '{@link org.eclipselabs.tapiji.translator.rap.model.user.User <em>User</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>User</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.User
	 * @generated
	 */
	EClass getUser();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getUsername <em>Username</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Username</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.User#getUsername()
	 * @see #getUser()
	 * @generated
	 */
	EAttribute getUser_Username();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getPassword <em>Password</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Password</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.User#getPassword()
	 * @see #getUser()
	 * @generated
	 */
	EAttribute getUser_Password();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipselabs.tapiji.translator.rap.model.user.User#getStoredRBs <em>Stored RBs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Stored RBs</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.User#getStoredRBs()
	 * @see #getUser()
	 * @generated
	 */
	EReference getUser_StoredRBs();

	/**
	 * Returns the meta object for class '{@link org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile <em>Properties File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Properties File</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile
	 * @generated
	 */
	EClass getPropertiesFile();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile#getId()
	 * @see #getPropertiesFile()
	 * @generated
	 */
	EAttribute getPropertiesFile_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile#getPath()
	 * @see #getPropertiesFile()
	 * @generated
	 */
	EAttribute getPropertiesFile_Path();

	/**
	 * Returns the meta object for class '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle <em>Resource Bundle</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Resource Bundle</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle
	 * @generated
	 */
	EClass getResourceBundle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getId()
	 * @see #getResourceBundle()
	 * @generated
	 */
	EAttribute getResourceBundle_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getName()
	 * @see #getResourceBundle()
	 * @generated
	 */
	EAttribute getResourceBundle_Name();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getLocalFiles <em>Local Files</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Local Files</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getLocalFiles()
	 * @see #getResourceBundle()
	 * @generated
	 */
	EReference getResourceBundle_LocalFiles();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getSharedUsers <em>Shared Users</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Shared Users</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getSharedUsers()
	 * @see #getResourceBundle()
	 * @generated
	 */
	EReference getResourceBundle_SharedUsers();

	/**
	 * Returns the meta object for the reference '{@link org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getOwner <em>Owner</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Owner</em>'.
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle#getOwner()
	 * @see #getResourceBundle()
	 * @generated
	 */
	EReference getResourceBundle_Owner();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	UserFactory getUserFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.UserImpl <em>User</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserImpl
		 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserPackageImpl#getUser()
		 * @generated
		 */
		EClass USER = eINSTANCE.getUser();

		/**
		 * The meta object literal for the '<em><b>Username</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USER__USERNAME = eINSTANCE.getUser_Username();

		/**
		 * The meta object literal for the '<em><b>Password</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute USER__PASSWORD = eINSTANCE.getUser_Password();

		/**
		 * The meta object literal for the '<em><b>Stored RBs</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference USER__STORED_RBS = eINSTANCE.getUser_StoredRBs();

		/**
		 * The meta object literal for the '{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.PropertiesFileImpl <em>Properties File</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.PropertiesFileImpl
		 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserPackageImpl#getPropertiesFile()
		 * @generated
		 */
		EClass PROPERTIES_FILE = eINSTANCE.getPropertiesFile();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTIES_FILE__ID = eINSTANCE.getPropertiesFile_Id();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTIES_FILE__PATH = eINSTANCE.getPropertiesFile_Path();

		/**
		 * The meta object literal for the '{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl <em>Resource Bundle</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl
		 * @see org.eclipselabs.tapiji.translator.rap.model.user.impl.UserPackageImpl#getResourceBundle()
		 * @generated
		 */
		EClass RESOURCE_BUNDLE = eINSTANCE.getResourceBundle();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESOURCE_BUNDLE__ID = eINSTANCE.getResourceBundle_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESOURCE_BUNDLE__NAME = eINSTANCE.getResourceBundle_Name();

		/**
		 * The meta object literal for the '<em><b>Local Files</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_BUNDLE__LOCAL_FILES = eINSTANCE.getResourceBundle_LocalFiles();

		/**
		 * The meta object literal for the '<em><b>Shared Users</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_BUNDLE__SHARED_USERS = eINSTANCE.getResourceBundle_SharedUsers();

		/**
		 * The meta object literal for the '<em><b>Owner</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_BUNDLE__OWNER = eINSTANCE.getResourceBundle_Owner();

	}

} //UserPackage
