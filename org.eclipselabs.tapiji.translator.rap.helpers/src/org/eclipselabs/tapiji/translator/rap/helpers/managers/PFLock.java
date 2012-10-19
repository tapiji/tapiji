package org.eclipselabs.tapiji.translator.rap.helpers.managers;

import java.util.concurrent.Semaphore;

import org.eclipselabs.tapiji.translator.rap.model.user.User;

public class PFLock {
	private long propsID;
	private User owner;
	private Semaphore sema = new Semaphore(1);
	private boolean locked = false;
	
	public PFLock(long rbID, User owner) {
		this.propsID = rbID;
		this.owner = owner;
	}

	public long getPropertiesFileID() {
		return propsID;
	}

	public void setPropertiesFileID(long rbID) {
		this.propsID = rbID;
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
