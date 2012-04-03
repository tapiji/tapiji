package org.eclipse.babel.tapiji.tools.rbmanager.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;


public class VirtualContentManager {
	private Map<IContainer, VirtualContainer> containers = new HashMap<IContainer, VirtualContainer>();
	private Map<String, VirtualResourceBundle> vResourceBundles = new HashMap<String, VirtualResourceBundle>();
	
	static private VirtualContentManager singelton = null;
	
	private VirtualContentManager() {
	}

	public static VirtualContentManager getVirtualContentManager(){
		if (singelton == null){
			singelton = new VirtualContentManager(); 
		}
		return singelton;
	}

	public VirtualContainer getContainer(IContainer container) {
		return containers.get(container);
	}


	public void addVContainer(IContainer container, VirtualContainer vContainer) {
		containers.put(container, vContainer);
	}

	public void removeVContainer(IContainer container){
		vResourceBundles.remove(container);
	}
	
	public VirtualResourceBundle getVResourceBundles(String vRbId) {
		return vResourceBundles.get(vRbId);
	}


	public void addVResourceBundle(String vRbId, VirtualResourceBundle vResourceBundle) {
		vResourceBundles.put(vRbId, vResourceBundle);
	}
	
	public void removeVResourceBundle(String vRbId){
		vResourceBundles.remove(vRbId);
	}


	public boolean containsVResourceBundles(String vRbId) {
		return vResourceBundles.containsKey(vRbId);
	}

	public void reset() {
		vResourceBundles.clear();
		containers.clear();
	}
}
