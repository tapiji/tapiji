package org.eclipselabs.tapiji.translator.rap.helpers.managers;

import java.util.concurrent.Semaphore;

import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class RBLock {
	private long rbID;
	private User owner;
	private Semaphore sema = new Semaphore(1);
	private boolean locked = false;
	
	public RBLock(long rbID, User owner) {
		this.rbID = rbID;
		this.owner = owner;
	}

	public long getResourceBundleID() {
		return rbID;
	}

	public void setResourceBundleID(long rbID) {
		this.rbID = rbID;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public boolean isReleased() {
		return !locked;
	}
	
	public boolean isLocked() {
		return locked;
	}

	public void release() {
		if (locked == true) {
			sema.release();
			this.locked = false;
		}
	}
	
	public void lock() throws InterruptedException {
		sema.acquire();	
		this.locked = true;
	}

//	public Semaphore getSemaphore() {
//		return sema;
//	}
//
//	public void setSemaphore(Semaphore sema) {
//		this.sema = sema;
//	}
}
