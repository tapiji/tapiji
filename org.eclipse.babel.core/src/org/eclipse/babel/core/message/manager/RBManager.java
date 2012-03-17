package org.eclipse.babel.core.message.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.babel.core.util.PDEUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundle;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;

public class RBManager {

	private static Map<IProject, RBManager> managerMap = new HashMap<IProject, RBManager>();
	
	private Map<String, IMessagesBundleGroup> resourceBundles;

	private static RBManager INSTANCE;
	
	private List<IMessagesEditorListener> editorListeners;
	
	private IProject project;
	
	private RBManager() {
		resourceBundles = new HashMap<String, IMessagesBundleGroup>();
		editorListeners = new ArrayList<IMessagesEditorListener>(3);
	}
	
	public IMessagesBundleGroup getMessagesBundleGroup(String name) {
		if (!resourceBundles.containsKey(name)) {
			System.out.println("ohje");
			return null;
		} else {
			return resourceBundles.get(name);
		}
	}
	
	/**
	 * Hier darf nur BABEL rein
	 * @param bundleGroup
	 */
	public void notifyMessagesBundleCreated(IMessagesBundleGroup bundleGroup) {
		if (resourceBundles.containsKey(bundleGroup.getResourceBundleId())) {
			if (equalHash(resourceBundles.get(bundleGroup.getResourceBundleId()), bundleGroup)) {
				resourceBundles.put(bundleGroup.getResourceBundleId(), bundleGroup); // OK wenn Builder
//				System.out.println(bundleGroup.getResourceBundleId() + "overridden!");
			} else {
				// not same -> sync
				
				syncBundles(resourceBundles.get(bundleGroup.getResourceBundleId()), bundleGroup);
			}
		} else {
			resourceBundles.put(bundleGroup.getResourceBundleId(), bundleGroup);
		}
//		System.out.println("created: " + bundleGroup.hashCode());
	}
	
	/**
	 * Hier darf nur BABEL rein
	 * @param bundleGroup
	 */
	public void notifyMessagesBundleDeleted(IMessagesBundleGroup bundleGroup) {
		if (resourceBundles.containsKey(bundleGroup.getResourceBundleId())) {
			
			if (equalHash(resourceBundles.get(bundleGroup.getResourceBundleId()), bundleGroup)) {
				resourceBundles.remove(bundleGroup.getResourceBundleId());
				System.out.println(bundleGroup.getResourceBundleId() + "deleted!");
			} else {
				// not same
			}
		} else {
			System.out.println("ohje"); // OK wenn Builder
		}
//		System.out.println("removed: " + bundleGroup.hashCode());
	}
	
	/**
	 * Weil der BABEL-Builder nicht richtig funkt (added 1 x und removed 2 x das GLEICHE!)
	 * @param oldBundleGroup
	 * @param newBundleGroup
	 * @return
	 */
	private boolean equalHash(IMessagesBundleGroup oldBundleGroup, IMessagesBundleGroup newBundleGroup) {
		int oldHashCode = oldBundleGroup.hashCode();
		int newHashCode = newBundleGroup.hashCode();
		return oldHashCode == newHashCode;
	}
	
	private void syncBundles(IMessagesBundleGroup oldBundleGroup, IMessagesBundleGroup newBundleGroup) {
		for (IMessagesBundle newBundle : newBundleGroup.getMessagesBundles()) {
			IMessagesBundle oldBundle = oldBundleGroup.getMessagesBundle(newBundle.getLocale());
			if (oldBundle == null) { // it's a new one
				oldBundleGroup.addMessagesBundle(newBundle.getLocale(), newBundle);
			} else { // check keys
				for (IMessage newMsg : newBundle.getMessages()) {
					if (oldBundle.getMessage(newMsg.getKey()) == null) { // remove old entries
						oldBundle.removeMessage(newMsg.getKey());
					} else { // update old entries
						IMessage oldMsg = oldBundle.getMessage(newMsg.getKey());
						if (oldMsg == null) { // it's a new one
							oldBundle.addMessage(newMsg);
						} else { // check value
							oldMsg.setComment(newMsg.getComment(), true);
							oldMsg.setText(newMsg.getValue(), true); // because of illegal thread access
						}
					}
				}
			}
		}
	}
	
	/**
	 * Hier darf nur TAPIJI rein
	 * @param bundleGroup
	 */
	public void deleteMessagesBundle(String name) {
		if (resourceBundles.containsKey(name)) {
			resourceBundles.remove(name);
		} else {
			System.out.println("ohje");
		}
	}
	
	/**
	 * Hier darf nur TAPIJI rein
	 * @param bundleGroup
	 */
	public void addMessagesBundleGroup(IMessagesBundleGroup bundleGroup) {
		if (resourceBundles.containsKey(bundleGroup.getResourceBundleId())) {
			System.out.println("ohje");
		} else {
			resourceBundles.put(bundleGroup.getResourceBundleId(), bundleGroup);
		}
	}
	
	/**
	 * Hier darf nur TAPIJI rein
	 * @param bundleGroup
	 */
	public void addMessagesBundle(String resourceBundleId, IMessagesBundle bundle) {
		if (resourceBundles.containsKey(resourceBundleId)) {
			IMessagesBundleGroup messagesBundleGroup = resourceBundles.get(resourceBundleId);
			messagesBundleGroup.addMessagesBundle(bundle.getLocale(), bundle);
		} else {
			System.out.println("ohje");
		}
	}
	
	public boolean containsMessagesBundleGroup(String name) {
		return resourceBundles.containsKey(name);
	}
	
	public static RBManager getInstance(IProject project) {
		// set host-project
//		if (FragmentProjectUtils.isFragment(project))
//			project =  FragmentProjectUtils.getFragmentHost(project);
		// TODO
		
		INSTANCE = managerMap.get(project);
		if (INSTANCE == null) {
			INSTANCE = new RBManager();
			INSTANCE.project = project;
			INSTANCE.detectResourceBundles();
			managerMap.put(project, INSTANCE);
			
		}
		return INSTANCE;
	}
	
	public static RBManager getInstance(String projectName) {
		for (IProject project : getAllSupportedProjects()) {
			if (project.getName().equals(projectName)) {
				return getInstance(project);
			}
		}
		return null;
	}
	
	public static Set<IProject> getAllSupportedProjects () {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		Set<IProject> projs = new HashSet<IProject>();
		
		for (IProject p : projects) {
			try {
				if (p.hasNature("org.eclipselabs.tapiji.tools.core.nature")) {
					projs.add(p);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return projs;
	}
	
	public void addMessagesEditorListener(IMessagesEditorListener listener) {
		this.editorListeners.add(listener);
	}
	
	public void removeMessagesEditorListener(IMessagesEditorListener listener) {
		this.editorListeners.remove(listener);
	}
	
	public void fireEditorSaved() {
		for (IMessagesEditorListener listener : this.editorListeners) {
			listener.onSave();
		}
	}
	
	public void fireEditorChanged() {
		for (IMessagesEditorListener listener : this.editorListeners) {
			listener.onModify();
		}
	}
	
	
	protected void detectResourceBundles () {
		try {		
			project.accept(new ResourceBundleDetectionVisitor(this));
			
			IProject[] fragments = PDEUtils.lookupFragment(project);
			if (fragments != null){
				for (IProject p :  fragments){
					p.accept(new ResourceBundleDetectionVisitor(this));
				}
			}
		} catch (CoreException e) {
		}
	}
	
	// experimental 
	public void addBundleResource(IResource resource) {
		// create it with MessagesBundleFactory
		
	}
}
