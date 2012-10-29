/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Properties File</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.PropertiesFileImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.PropertiesFileImpl#getPath <em>Path</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PropertiesFileImpl extends EObjectImpl implements PropertiesFile {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final long ID_EDEFAULT = 0L;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected long id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getPath() <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected static final String PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPath() <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected String path = PATH_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PropertiesFileImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UserPackage.Literals.PROPERTIES_FILE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(long newId) {
		long oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UserPackage.PROPERTIES_FILE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPath() {
		return path;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPath(String newPath) {
		String oldPath = path;
		path = newPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UserPackage.PROPERTIES_FILE__PATH, oldPath, path));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getFilename() {
		if (path != null) {
			java.io.File file = new java.io.File(path);
			return file.getName();
		}
		
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public ResourceBundle getResourceBundle() {
		 if (eContainer() instanceof ResourceBundle) {
		        return (ResourceBundle) eContainer();
		    }
		 return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setFilename(String filename) {
		if (path != null) {
			int index = path.lastIndexOf(java.io.File.separatorChar);
			if (index != -1) {
				String prePath = path.substring(0, index+1);
				setPath(prePath + filename);
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getLocale() {
		String filename = getFilename();
		String locale = null;
		if (filename != null) {
			// filename -> BUNDLENAME_LOCAL.EXT			
			int indexOfPoint = filename.lastIndexOf('.');			
			int indexOfUnderscore = filename.lastIndexOf('_');
			if (indexOfPoint != -1 && indexOfUnderscore != -1)			
				locale = filename.substring(indexOfUnderscore+1,indexOfPoint);
		}
		return locale;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case UserPackage.PROPERTIES_FILE__ID:
				return getId();
			case UserPackage.PROPERTIES_FILE__PATH:
				return getPath();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case UserPackage.PROPERTIES_FILE__ID:
				setId((Long)newValue);
				return;
			case UserPackage.PROPERTIES_FILE__PATH:
				setPath((String)newValue);
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
			case UserPackage.PROPERTIES_FILE__ID:
				setId(ID_EDEFAULT);
				return;
			case UserPackage.PROPERTIES_FILE__PATH:
				setPath(PATH_EDEFAULT);
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
			case UserPackage.PROPERTIES_FILE__ID:
				return id != ID_EDEFAULT;
			case UserPackage.PROPERTIES_FILE__PATH:
				return PATH_EDEFAULT == null ? path != null : !PATH_EDEFAULT.equals(path);
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
		result.append(" (id: ");
		result.append(id);
		result.append(", path: ");
		result.append(path);
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
		
		if (obj instanceof PropertiesFile) {
			PropertiesFile otherFile = (PropertiesFile) obj;
			
			if (getPath().equals(otherFile.getPath()))
				return true;
			
			return false;
		}
		
		return super.equals(obj);
	}
	
} //PropertiesFileImpl
