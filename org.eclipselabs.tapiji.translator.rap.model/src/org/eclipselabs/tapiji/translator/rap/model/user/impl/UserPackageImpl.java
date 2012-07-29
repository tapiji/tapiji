/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserFactory;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class UserPackageImpl extends EPackageImpl implements UserPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass userEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertiesFileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceBundleEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipselabs.tapiji.translator.rap.model.user.UserPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private UserPackageImpl() {
		super(eNS_URI, UserFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link UserPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static UserPackage init() {
		if (isInited) return (UserPackage)EPackage.Registry.INSTANCE.getEPackage(UserPackage.eNS_URI);

		// Obtain or create and register package
		UserPackageImpl theUserPackage = (UserPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof UserPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new UserPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theUserPackage.createPackageContents();

		// Initialize created meta-data
		theUserPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theUserPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(UserPackage.eNS_URI, theUserPackage);
		return theUserPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getUser() {
		return userEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUser_Username() {
		return (EAttribute)userEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getUser_Password() {
		return (EAttribute)userEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getUser_StoredRBs() {
		return (EReference)userEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPropertiesFile() {
		return propertiesFileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPropertiesFile_Path() {
		return (EAttribute)propertiesFileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getResourceBundle() {
		return resourceBundleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getResourceBundle_Name() {
		return (EAttribute)resourceBundleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getResourceBundle_LocalFiles() {
		return (EReference)resourceBundleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UserFactory getUserFactory() {
		return (UserFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		userEClass = createEClass(USER);
		createEAttribute(userEClass, USER__USERNAME);
		createEAttribute(userEClass, USER__PASSWORD);
		createEReference(userEClass, USER__STORED_RBS);

		propertiesFileEClass = createEClass(PROPERTIES_FILE);
		createEAttribute(propertiesFileEClass, PROPERTIES_FILE__PATH);

		resourceBundleEClass = createEClass(RESOURCE_BUNDLE);
		createEAttribute(resourceBundleEClass, RESOURCE_BUNDLE__NAME);
		createEReference(resourceBundleEClass, RESOURCE_BUNDLE__LOCAL_FILES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(userEClass, User.class, "User", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUser_Username(), ecorePackage.getEString(), "username", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUser_Password(), ecorePackage.getEString(), "password", null, 0, 1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUser_StoredRBs(), this.getResourceBundle(), null, "storedRBs", null, 0, -1, User.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(propertiesFileEClass, PropertiesFile.class, "PropertiesFile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPropertiesFile_Path(), ecorePackage.getEString(), "path", null, 0, 1, PropertiesFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(propertiesFileEClass, ecorePackage.getEString(), "getFilename", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(propertiesFileEClass, this.getResourceBundle(), "getResourceBundle", 0, 1, IS_UNIQUE, IS_ORDERED);

		EOperation op = addEOperation(propertiesFileEClass, null, "setFilename", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "filename", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(resourceBundleEClass, ResourceBundle.class, "ResourceBundle", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResourceBundle_Name(), ecorePackage.getEString(), "name", null, 0, 1, ResourceBundle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceBundle_LocalFiles(), this.getPropertiesFile(), null, "localFiles", null, 1, -1, ResourceBundle.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(resourceBundleEClass, ecorePackage.getEBoolean(), "isTemporary", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(resourceBundleEClass, this.getUser(), "getUser", 0, 1, IS_UNIQUE, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
		addAnnotation
		  (userEClass, 
		   source, 
		   new String[] {
			 "name", "User",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getUser_Username(), 
		   source, 
		   new String[] {
			 "name", "username",
			 "kind", "element"
		   });		
		addAnnotation
		  (getUser_Password(), 
		   source, 
		   new String[] {
			 "name", "password",
			 "kind", "element"
		   });		
		addAnnotation
		  (propertiesFileEClass, 
		   source, 
		   new String[] {
			 "name", "File",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getPropertiesFile_Path(), 
		   source, 
		   new String[] {
			 "name", "path",
			 "kind", "element"
		   });
	}

} //UserPackageImpl
