/**
 */
package org.eclipselabs.tapiji.translator.rap.model.user.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;
import org.eclipselabs.tapiji.translator.rap.model.user.UserPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Resource Bundle</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl#getPropertiesFiles <em>Properties Files</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl#getSharedUsers <em>Shared Users</em>}</li>
 *   <li>{@link org.eclipselabs.tapiji.translator.rap.model.user.impl.ResourceBundleImpl#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ResourceBundleImpl extends EObjectImpl implements ResourceBundle {
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
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPropertiesFiles() <em>Properties Files</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPropertiesFiles()
	 * @generated
	 * @ordered
	 */
	protected EList<PropertiesFile> propertiesFiles;

	/**
	 * The cached value of the '{@link #getSharedUsers() <em>Shared Users</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSharedUsers()
	 * @generated
	 * @ordered
	 */
	protected EList<User> sharedUsers;

	/**
	 * The cached value of the '{@link #getOwner() <em>Owner</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOwner()
	 * @generated
	 * @ordered
	 */
	protected User owner;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ResourceBundleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return UserPackage.Literals.RESOURCE_BUNDLE;
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
			eNotify(new ENotificationImpl(this, Notification.SET, UserPackage.RESOURCE_BUNDLE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UserPackage.RESOURCE_BUNDLE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PropertiesFile> getPropertiesFiles() {
		if (propertiesFiles == null) {
			propertiesFiles = new EObjectContainmentEList.Resolving<PropertiesFile>(PropertiesFile.class, this, UserPackage.RESOURCE_BUNDLE__PROPERTIES_FILES);
		}
		return propertiesFiles;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<User> getSharedUsers() {
		if (sharedUsers == null) {
			sharedUsers = new EObjectResolvingEList<User>(User.class, this, UserPackage.RESOURCE_BUNDLE__SHARED_USERS);
		}
		return sharedUsers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public User getOwner() {
		if (owner != null && owner.eIsProxy()) {
			InternalEObject oldOwner = (InternalEObject)owner;
			owner = (User)eResolveProxy(oldOwner);
			if (owner != oldOwner) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, UserPackage.RESOURCE_BUNDLE__OWNER, oldOwner, owner));
			}
		}
		return owner;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public User basicGetOwner() {
		return owner;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOwner(User newOwner) {
		User oldOwner = owner;
		owner = newOwner;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, UserPackage.RESOURCE_BUNDLE__OWNER, oldOwner, owner));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean isTemporary() {
		if (getOwner() != null)
			return false;
		
		return true;
	}

//	/**
//	 * <!-- begin-user-doc -->
//	 * <!-- end-user-doc -->
//	 * @generated NOT
//	 */
//	public User getUser() {
//		if (eContainer() instanceof User) {
//	        return (User) eContainer();
//	    }
//		return null;
//	}
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case UserPackage.RESOURCE_BUNDLE__PROPERTIES_FILES:
				return ((InternalEList<?>)getPropertiesFiles()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case UserPackage.RESOURCE_BUNDLE__ID:
				return getId();
			case UserPackage.RESOURCE_BUNDLE__NAME:
				return getName();
			case UserPackage.RESOURCE_BUNDLE__PROPERTIES_FILES:
				return getPropertiesFiles();
			case UserPackage.RESOURCE_BUNDLE__SHARED_USERS:
				return getSharedUsers();
			case UserPackage.RESOURCE_BUNDLE__OWNER:
				if (resolve) return getOwner();
				return basicGetOwner();
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
			case UserPackage.RESOURCE_BUNDLE__ID:
				setId((Long)newValue);
				return;
			case UserPackage.RESOURCE_BUNDLE__NAME:
				setName((String)newValue);
				return;
			case UserPackage.RESOURCE_BUNDLE__PROPERTIES_FILES:
				getPropertiesFiles().clear();
				getPropertiesFiles().addAll((Collection<? extends PropertiesFile>)newValue);
				return;
			case UserPackage.RESOURCE_BUNDLE__SHARED_USERS:
				getSharedUsers().clear();
				getSharedUsers().addAll((Collection<? extends User>)newValue);
				return;
			case UserPackage.RESOURCE_BUNDLE__OWNER:
				setOwner((User)newValue);
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
			case UserPackage.RESOURCE_BUNDLE__ID:
				setId(ID_EDEFAULT);
				return;
			case UserPackage.RESOURCE_BUNDLE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case UserPackage.RESOURCE_BUNDLE__PROPERTIES_FILES:
				getPropertiesFiles().clear();
				return;
			case UserPackage.RESOURCE_BUNDLE__SHARED_USERS:
				getSharedUsers().clear();
				return;
			case UserPackage.RESOURCE_BUNDLE__OWNER:
				setOwner((User)null);
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
			case UserPackage.RESOURCE_BUNDLE__ID:
				return id != ID_EDEFAULT;
			case UserPackage.RESOURCE_BUNDLE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case UserPackage.RESOURCE_BUNDLE__PROPERTIES_FILES:
				return propertiesFiles != null && !propertiesFiles.isEmpty();
			case UserPackage.RESOURCE_BUNDLE__SHARED_USERS:
				return sharedUsers != null && !sharedUsers.isEmpty();
			case UserPackage.RESOURCE_BUNDLE__OWNER:
				return owner != null;
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
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		
		if (obj instanceof ResourceBundle) {
			ResourceBundle otherRB = (ResourceBundle) obj;
			
			if (getName().equals(otherRB.getName()) && equalsLocalFiles(otherRB.getPropertiesFiles()) ) {
				if (getOwner() == null) {
					if (otherRB.getOwner() == null)
						return true;
				} else if (getOwner().equals(otherRB.getOwner())) {
					return true;
				}				
			}			
			return false;
		}
		
		return super.equals(obj);
	}
	
	private boolean equalsLocalFiles(EList<PropertiesFile> otherFiles) {
		if (getPropertiesFiles().size() != otherFiles.size())
			return false;
		
		for (int i=0; i < getPropertiesFiles().size(); i++) {
			PropertiesFile file = getPropertiesFiles().get(i);
			if (! file.equals(otherFiles.get(i)))
				return false;
		}
		return true;
	}

	// don't override (Teneo/EMF restriction) see http://www.eclipse.org/forums/index.php?t=msg&goto=71909&
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
} //ResourceBundleImpl
