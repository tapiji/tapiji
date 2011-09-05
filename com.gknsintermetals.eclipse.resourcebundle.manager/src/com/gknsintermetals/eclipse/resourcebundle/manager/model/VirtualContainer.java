package com.gknsintermetals.eclipse.resourcebundle.manager.model;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.widgets.Display;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.RBFileUtils;

public class VirtualContainer {
	protected ResourceBundleManager rbmanager;
	protected IContainer container;
	protected int rbCount;
	
	public VirtualContainer(IContainer container1, boolean countResourceBundles){
		this.container = container1;
		rbmanager = ResourceBundleManager.getManager(container.getProject());
		if (countResourceBundles)
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					rbCount = RBFileUtils.countRecursiveResourceBundle(container);
				}
			});
		else rbCount = 0;
	}

	public VirtualContainer(IContainer container, int rbCount) {
		this(container, false);
		this.rbCount = rbCount;
	}

	public ResourceBundleManager getResourceBundleManager(){ 
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
