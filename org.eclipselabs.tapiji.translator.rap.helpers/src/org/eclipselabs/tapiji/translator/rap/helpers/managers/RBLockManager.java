package org.eclipselabs.tapiji.translator.rap.helpers.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class RBLockManager {

	private static Map<Long, PFLock> pfLockMap = new ConcurrentHashMap<Long, PFLock>();
	
	
	private static Map<Long, Set<IPropertiesFileLockListener>> lockListenerMap = 
			new HashMap<Long, Set<IPropertiesFileLockListener>>();
	
	private static List<IPropertiesFileLockListener> globalLockListeners = 
			new ArrayList<IPropertiesFileLockListener>();
	
	public static final RBLockManager INSTANCE = new RBLockManager();
		
	public synchronized PFLock getPFLock(long propsID) {
		return pfLockMap.get(propsID);
	}
	
	public boolean isRBLockedCompletely(ResourceBundle rb) {
		for (PropertiesFile pf : rb.getPropertiesFiles()) {
			if (! isPFLocked(pf.getId()))
				return false;
		}
		return true;
	}
	
	public boolean isRBLocked(ResourceBundle rb) {
		for (PropertiesFile pf : rb.getPropertiesFiles()) {
			if (isPFLocked(pf.getId()))
				return true;
		}		
		return false;
	}
	
	public boolean isOwnerOfRBLock(User user, ResourceBundle rb) {
		for (PropertiesFile pf : rb.getPropertiesFiles()) {
			PFLock lock = getPFLock(pf.getId());
			if (lock != null && lock.isLocked() && ! user.equals(lock.getOwner()))
				return false;
		}
		return true;
	}
	
	public void releaseLocksOfUser(User user, ResourceBundle rb) {
		for (PropertiesFile pf : rb.getPropertiesFiles()) {							
			PFLock lock = getPFLock(pf.getId());
			// is properties file locked by this user --> release lock
			if (lock != null && lock.isLocked() &&
					lock.getOwner().equals(user)) {
				release(pf.getId());
			}
		}
	}
	
	public synchronized boolean isPFLocked(long propsID) {
		PFLock lock = pfLockMap.get(propsID);
		if (lock != null && lock.isLocked())			
			return true;
		return false;
	}
	
	public void lock(long propsID) {
		lock(propsID, UserUtils.getUser());
	}
	
	public void lock(long propsID, User currentUser) {
		PFLock lock = pfLockMap.get(propsID);
		// lazy init
		if (lock == null) {
			lock = new PFLock(propsID, currentUser);
			pfLockMap.put(propsID, lock);
		}
		// only lock RB if it isn't locked already by same user	
		if (lock.isLocked() && lock.getOwner().equals(currentUser))
			return;
		try {
			lock.lock();
			lock.setOwner(currentUser);
			firePFLockAcquired(lock);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	public synchronized User tryLock(long propsID) {
		PFLock lock = pfLockMap.get(propsID);
		if (lock == null || lock.isReleased()) {
			lock(propsID);
			lock = pfLockMap.get(propsID);
		}
		return lock.getOwner();
	}
	
	public synchronized void release(long propsID) {
		release(propsID, UserUtils.getUser());
	}
	
	public synchronized void release(long propsID, User currentUser) {
		PFLock lock = pfLockMap.get(propsID);
		// only owner can release lock
		if (lock != null && lock.getOwner().equals(currentUser)) {
			lock.release();
			firePFLockReleased(lock);
		}
	}
	
	public void addPFLockListener(long propsID, IPropertiesFileLockListener listener) {
		Set<IPropertiesFileLockListener> list = lockListenerMap.get(propsID);
		if (list == null) {
			list = new HashSet<IPropertiesFileLockListener>();
			lockListenerMap.put(propsID, list);
		}
		list.add(listener);
	}
	
	public void removePFLockListener(long propsID, IPropertiesFileLockListener listener) {
		Set<IPropertiesFileLockListener> list = lockListenerMap.get(propsID);
		if (list != null) {
			list.remove(listener);
		}
	}
	
	public void addGlobalLockListener(IPropertiesFileLockListener listener) {
		globalLockListeners.add(listener);
	}
	
	public void removeGlobalLockListener(IPropertiesFileLockListener listener) {
		globalLockListeners.remove(listener);
	}
	
	public synchronized void firePFLockAcquired(PFLock lock) {
		Set<IPropertiesFileLockListener> listeners = lockListenerMap.get(lock.getPropertiesFileID());
		if (listeners != null) {
			for (IPropertiesFileLockListener listener : listeners)
				listener.lockAcquired(lock);
		}
		
		// execute global listeners
		for (IPropertiesFileLockListener listener : globalLockListeners)
			listener.lockAcquired(lock);
	}
	
	public synchronized void firePFLockReleased(PFLock lock) {
		Set<IPropertiesFileLockListener> listeners = lockListenerMap.get(lock.getPropertiesFileID());
		if (listeners != null) {
			for (IPropertiesFileLockListener listener : listeners)		
				listener.lockReleased(lock);
		}
		
		// execute global listeners
		for (IPropertiesFileLockListener listener : globalLockListeners)
			listener.lockReleased(lock);
	}
}
