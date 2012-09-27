package org.eclipselabs.tapiji.translator.rap.helpers.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class RBLockManager {

	private static Map<Long, RBLock> rbLockMap = new ConcurrentHashMap<Long, RBLock>();
	
	
	private static Map<Long, Set<IResourceBundleLockListener>> lockListenerMap = 
			new HashMap<Long, Set<IResourceBundleLockListener>>();
	
	public static final RBLockManager INSTANCE = new RBLockManager();
		
	public synchronized RBLock getRBLock(long rbID) {
		return rbLockMap.get(rbID);
	}
	
	public synchronized boolean isLocked(long rbID) {
		RBLock lock = rbLockMap.get(rbID);
		if (lock != null && lock.isLocked())			
			return true;
		return false;
	}
	
	public void lock(long rbID) {
		lock(rbID, UserUtils.getUser());
	}
	
	public void lock(long rbID, User currentUser) {
		RBLock lock = rbLockMap.get(rbID);
		// lazy init
		if (lock == null) {
			lock = new RBLock(rbID, currentUser);
			rbLockMap.put(rbID, lock);
		}
		// only lock RB if it isn't locked already by same user	
		if (lock.isLocked() && lock.getOwner().equals(currentUser))
			return;
		try {
			lock.lock();
			lock.setOwner(currentUser);
			fireRBLockAcquired(lock);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	public synchronized User tryLock(long rbID) {
		RBLock lock = rbLockMap.get(rbID);
		if (lock == null || lock.isReleased()) {
			lock(rbID);
			lock = rbLockMap.get(rbID);
		}
		return lock.getOwner();
	}
	
	public synchronized void release(long rbID) {
		release(rbID, UserUtils.getUser());
	}
	
	public synchronized void release(long rbID, User currentUser) {
		RBLock lock = rbLockMap.get(rbID);
		// only owner can release lock
		if (lock != null && lock.getOwner().equals(currentUser)) {
			lock.release();
			fireRBLockReleased(lock);
		}
	}
	
	public void addRBLockListener(long resourceBundleID, IResourceBundleLockListener listener) {
		Set<IResourceBundleLockListener> list = lockListenerMap.get(resourceBundleID);
		if (list == null) {
			list = new HashSet<IResourceBundleLockListener>();
			lockListenerMap.put(resourceBundleID, list);
		}
		list.add(listener);
	}
	
	public void removeRBLockListener(long resourceBundleID, IResourceBundleLockListener listener) {
		Set<IResourceBundleLockListener> list = lockListenerMap.get(resourceBundleID);
		if (list != null) {
			list.remove(listener);
		}
	}
	
	public synchronized void fireRBLockAcquired(RBLock lock) {
		Set<IResourceBundleLockListener> listeners = lockListenerMap.get(lock.getResourceBundleID());
		if (listeners != null) {
			for (IResourceBundleLockListener listener : listeners)
				listener.lockAcquired(lock);
		}
		
	}
	
	public synchronized void fireRBLockReleased(RBLock lock) {
		Set<IResourceBundleLockListener> listeners = lockListenerMap.get(lock.getResourceBundleID());
		if (listeners != null) {
			for (IResourceBundleLockListener listener : listeners)		
				listener.lockReleased(lock);
		}
	}
}
