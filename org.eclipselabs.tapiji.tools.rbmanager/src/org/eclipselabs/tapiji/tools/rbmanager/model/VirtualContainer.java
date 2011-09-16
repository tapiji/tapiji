package org.eclipselabs.tapiji.tools.rbmanager.model;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.RBFileUtils;

public class VirtualContainer {
	protected ResourceBundleManager rbmanager;
	protected IContainer container;
	protected int rbCount;
	
	public VirtualContainer(IContainer container1, boolean countResourceBundles){
		this.container = container1;
		rbmanager = ResourceBundleManager.getManager(container.getProject());
		if (countResourceBundles){
			rbCount=1;
			new Job("count ResourceBundles"){
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					recount();
					return Status.OK_STATUS;
				}
			}.schedule();	
		}else rbCount = 0;
	}
	
	protected VirtualContainer(IContainer container){
		this.container = container;
	}

	public VirtualContainer(IContainer container, int rbCount) {
		this(container, false);
		this.rbCount = rbCount;
	}

	public ResourceBundleManager getResourceBundleManager(){
		if (rbmanager == null) 
			rbmanager = ResourceBundleManager.getManager(container.getProject());
		return rbmanager;
	}
	
	public IContainer getContainer() {
		return container;
	}
	
	public void setRbCounter(int rbCount){
		this.rbCount = rbCount;
	}

	public int getRbCount() {
		return rbCount;
	}
	
	public void recount(){
		rbCount = RBFileUtils.countRecursiveResourceBundle(container);	
	}
}
