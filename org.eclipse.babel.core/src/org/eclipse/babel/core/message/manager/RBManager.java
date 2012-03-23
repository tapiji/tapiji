package org.eclipse.babel.core.message.manager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.babel.core.configuration.ConfigurationManager;
import org.eclipse.babel.core.configuration.DirtyHack;
import org.eclipse.babel.core.factory.MessagesBundleGroupFactory;
import org.eclipse.babel.core.message.Message;
import org.eclipse.babel.core.message.resource.ser.PropertiesSerializer;
import org.eclipse.babel.core.util.PDEUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessage;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundle;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;

/**
 * 
 * @author Alexej Strelzow
 */
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
			System.out.println("ohje"); // TODO log
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
			IMessagesBundleGroup oldbundleGroup = resourceBundles.get(bundleGroup.getResourceBundleId());
			if (!equalHash(oldbundleGroup, bundleGroup)) {
				// not same -> sync
				if (oldbundleGroup.hasPropertiesFileGroupStrategy()) {
					syncBundles(bundleGroup, oldbundleGroup); // rethink that line
					resourceBundles.put(bundleGroup.getResourceBundleId(), bundleGroup);
				} else {
					syncBundles(oldbundleGroup, bundleGroup);
				}
			}
		} else {
			resourceBundles.put(bundleGroup.getResourceBundleId(), bundleGroup);
		}
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
			}
		} 
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
		List<IMessagesBundle> bundlesToRemove = new ArrayList<IMessagesBundle>();
		List<IMessage> keysToRemove = new ArrayList<IMessage>();
		
		DirtyHack.setFireEnabled(false); // hebelt AbstractMessageModel aus 
		// sonst müssten wir in setText von EclipsePropertiesEditorResource ein asyncExec zulassen
		
		for (IMessagesBundle newBundle : newBundleGroup.getMessagesBundles()) {
			IMessagesBundle oldBundle = oldBundleGroup.getMessagesBundle(newBundle.getLocale());
			if (oldBundle == null) { // it's a new one
				oldBundleGroup.addMessagesBundle(newBundle.getLocale(), newBundle);
			} else { // check keys
				for (IMessage newMsg : newBundle.getMessages()) {
					if (oldBundle.getMessage(newMsg.getKey()) == null) { // new entry, create new message
						oldBundle.addMessage(new Message(newMsg.getKey(), newMsg.getLocale()));
					} else { // update old entries
						IMessage oldMsg = oldBundle.getMessage(newMsg.getKey());
						if (oldMsg == null) { // it's a new one
							oldBundle.addMessage(newMsg);
						} else { // check value
							oldMsg.setComment(newMsg.getComment());
							oldMsg.setText(newMsg.getValue()); // ?silent because of illegal thread access (firePropChanged)
						}
					}
				}
			}
		}
		
		// check keys
		for (IMessagesBundle oldBundle : oldBundleGroup.getMessagesBundles()) {
			IMessagesBundle newBundle = newBundleGroup.getMessagesBundle(oldBundle.getLocale());
			if (newBundle == null) { // we have an old one
				bundlesToRemove.add(oldBundle);
			} else {
				for (IMessage oldMsg : oldBundle.getMessages()) {
					if (newBundle.getMessage(oldMsg.getKey()) == null) {
						keysToRemove.add(oldMsg);
					}
				}
			}
		}
		
		for (IMessagesBundle bundle : bundlesToRemove) {
			// TODO remove old Bundles
		}
		
		for (IMessage msg : keysToRemove) {
			oldBundleGroup.getMessagesBundle(msg.getLocale()).removeMessage(msg.getKey());
		}
		
		DirtyHack.setFireEnabled(true);
		
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
	
	public boolean containsMessagesBundleGroup(String name) {
		return resourceBundles.containsKey(name);
	}
	
	public static RBManager getInstance(IProject project) {
		// set host-project
//		if (FragmentProjectUtils.isFragment(project))
//			project =  FragmentProjectUtils.getFragmentHost(project);
		// TODO fragments
		
		INSTANCE = managerMap.get(project);
		if (INSTANCE == null) {
			INSTANCE = new RBManager();
			INSTANCE.project = project;
			managerMap.put(project, INSTANCE);
//			INSTANCE.detectResourceBundles(); // almost there
			
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
	
	// passive loading -> see detectResourceBundles
	public void addBundleResource(IResource resource) {
		// create it with MessagesBundleFactory or read from resource!
		// we can optimize that, now we create a bundle group for each bundle
		// we should create a bundle group only once!
		MessagesBundleGroupFactory.createBundleGroup(resource);
	}
	
	public void writeToFile(IMessagesBundleGroup bundleGroup) {
		// TODO: add Key Funktion
		// TODO: remove Key Funktion
		for (IMessagesBundle bundle : bundleGroup.getMessagesBundles()) {
			writeToFile(bundle);
		}
	}
	
	public void writeToFile(IMessagesBundle bundle) {
		DirtyHack.setEditorModificationEnabled(false);
		
		PropertiesSerializer ps = new PropertiesSerializer(ConfigurationManager.getInstance().getSerializerConfig());
		String editorContent = ps.serialize(bundle);
		IFile file = getFile(bundle);
		try {
			file.setContents(new ByteArrayInputStream(editorContent.getBytes()), 
					false, true, null);
			file.refreshLocal(IResource.DEPTH_ZERO, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DirtyHack.setEditorModificationEnabled(true);
		}
	}

	private IFile getFile(IMessagesBundle bundle) {
		String location = bundle.getResource().getResourceLocationLabel(); ///TEST/src/messages/Messages_en_IN.properties
		location = location.substring(project.getName().length() + 1, location.length());
		return ResourcesPlugin.getWorkspace().getRoot().getProject(project.getName()).getFile(location);
	}
}
