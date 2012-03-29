package org.eclipse.babel.core.message.manager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.babel.core.configuration.ConfigurationManager;
import org.eclipse.babel.core.configuration.DirtyHack;
import org.eclipse.babel.core.factory.MessagesBundleGroupFactory;
import org.eclipse.babel.core.message.Message;
import org.eclipse.babel.core.message.resource.PropertiesFileResource;
import org.eclipse.babel.core.message.resource.ser.PropertiesSerializer;
import org.eclipse.babel.core.message.strategy.PropertiesFileGroupStrategy;
import org.eclipse.babel.core.util.PDEUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
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

    public List<String> getMessagesBundleGroupNames() {
	Set<String> keySet = resourceBundles.keySet();
	List<String> bundleGroupNames = new ArrayList<String>();

	for (String key : keySet) {
	    bundleGroupNames.add(project.getName() + "/" + key);
	}
	return bundleGroupNames;
    }

    public static List<String> getAllMessagesBundleGroupNames() {
	Set<IProject> projects = getAllSupportedProjects();
	List<String> bundleGroupNames = new ArrayList<String>();

	for (IProject project : projects) {
	    RBManager manager = getInstance(project);
	    bundleGroupNames.addAll(manager.getMessagesBundleGroupNames());
	}
	return bundleGroupNames;
    }

    /**
     * Hier darf nur BABEL rein
     * 
     * @param bundleGroup
     */
    public void notifyMessagesBundleCreated(IMessagesBundleGroup bundleGroup) {
	if (resourceBundles.containsKey(bundleGroup.getResourceBundleId())) {
	    IMessagesBundleGroup oldbundleGroup = resourceBundles
		    .get(bundleGroup.getResourceBundleId());
	    if (!equalHash(oldbundleGroup, bundleGroup)) {
		boolean oldHasPropertiesStrategy = oldbundleGroup
			.hasPropertiesFileGroupStrategy();
		boolean newHasPropertiesStrategy = bundleGroup
			.hasPropertiesFileGroupStrategy();

		// in this case, the old one is only writing to the property
		// file, not the editor
		// we have to sync them and store the bundle with the editor as
		// resource
		if (oldHasPropertiesStrategy && !newHasPropertiesStrategy) {

		    syncBundles(bundleGroup, oldbundleGroup);
		    resourceBundles.put(bundleGroup.getResourceBundleId(),
			    bundleGroup);

		    oldbundleGroup.dispose();

		} else if ((oldHasPropertiesStrategy && newHasPropertiesStrategy)
			|| (!oldHasPropertiesStrategy && !newHasPropertiesStrategy)) {

		    // syncBundles(oldbundleGroup, bundleGroup); do not need
		    // that, because we take the new one
		    // and we do that, because otherwise we cache old
		    // Text-Editor instances, which we
		    // do not need -> read only phenomenon
		    resourceBundles.put(bundleGroup.getResourceBundleId(),
			    bundleGroup);

		    oldbundleGroup.dispose();
		} else {
		    // in this case our old resource has an EditorSite, but not
		    // the new one
		    bundleGroup.dispose();
		}
	    }
	} else {
	    resourceBundles.put(bundleGroup.getResourceBundleId(), bundleGroup);
	}
    }

    /**
     * Hier darf nur BABEL rein
     * 
     * @param bundleGroup
     */
    public void notifyMessagesBundleDeleted(IMessagesBundleGroup bundleGroup) {
	if (resourceBundles.containsKey(bundleGroup.getResourceBundleId())) {
	    if (equalHash(
		    resourceBundles.get(bundleGroup.getResourceBundleId()),
		    bundleGroup)) {
		resourceBundles.remove(bundleGroup.getResourceBundleId());
		System.out.println(bundleGroup.getResourceBundleId()
			+ " deleted!");
	    }
	}
    }

    public void notifyResourceRemoved(IResource resourceBundle) {
	// String parentName = resourceBundle.getParent().getName();
	// String resourceBundleId = parentName + "." +
	// getResourceBundleName(resourceBundle);
	String resourceBundleId = PropertiesFileGroupStrategy
		.getResourceBundleId(resourceBundle);
	IMessagesBundleGroup bundleGroup = resourceBundles
		.get(resourceBundleId);
	if (bundleGroup != null) {
	    Locale locale = getLocaleByName(
		    getResourceBundleName(resourceBundle),
		    resourceBundle.getName());
	    IMessagesBundle messagesBundle = bundleGroup
		    .getMessagesBundle(locale);
	    if (messagesBundle != null) {
		bundleGroup.removeMessagesBundle(messagesBundle);
	    }
	    if (bundleGroup.getMessagesBundleCount() == 0) {
		notifyMessagesBundleDeleted(bundleGroup);
	    }
	}

	// TODO: maybe save and reinit the editor?

    }

    /**
     * Weil der BABEL-Builder nicht richtig funkt (added 1 x und removed 2 x das
     * GLEICHE!)
     * 
     * @param oldBundleGroup
     * @param newBundleGroup
     * @return
     */
    private boolean equalHash(IMessagesBundleGroup oldBundleGroup,
	    IMessagesBundleGroup newBundleGroup) {
	int oldHashCode = oldBundleGroup.hashCode();
	int newHashCode = newBundleGroup.hashCode();
	return oldHashCode == newHashCode;
    }

    private void syncBundles(IMessagesBundleGroup oldBundleGroup,
	    IMessagesBundleGroup newBundleGroup) {
	List<IMessagesBundle> bundlesToRemove = new ArrayList<IMessagesBundle>();
	List<IMessage> keysToRemove = new ArrayList<IMessage>();

	DirtyHack.setFireEnabled(false); // hebelt AbstractMessageModel aus
	// sonst m�ssten wir in setText von EclipsePropertiesEditorResource ein
	// asyncExec zulassen

	for (IMessagesBundle newBundle : newBundleGroup.getMessagesBundles()) {
	    IMessagesBundle oldBundle = oldBundleGroup
		    .getMessagesBundle(newBundle.getLocale());
	    if (oldBundle == null) { // it's a new one
		oldBundleGroup.addMessagesBundle(newBundle.getLocale(),
			newBundle);
	    } else { // check keys
		for (IMessage newMsg : newBundle.getMessages()) {
		    if (oldBundle.getMessage(newMsg.getKey()) == null) { // new
									 // entry,
									 // create
									 // new
									 // message
			oldBundle.addMessage(new Message(newMsg.getKey(),
				newMsg.getLocale()));
		    } else { // update old entries
			IMessage oldMsg = oldBundle.getMessage(newMsg.getKey());
			if (oldMsg == null) { // it's a new one
			    oldBundle.addMessage(newMsg);
			} else { // check value
			    oldMsg.setComment(newMsg.getComment());
			    oldMsg.setText(newMsg.getValue());
			}
		    }
		}
	    }
	}

	// check keys
	for (IMessagesBundle oldBundle : oldBundleGroup.getMessagesBundles()) {
	    IMessagesBundle newBundle = newBundleGroup
		    .getMessagesBundle(oldBundle.getLocale());
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
	    oldBundleGroup.removeMessagesBundle(bundle);
	}

	for (IMessage msg : keysToRemove) {
	    IMessagesBundle mb = oldBundleGroup.getMessagesBundle(msg
		    .getLocale());
	    if (mb != null) {
		mb.removeMessage(msg.getKey());
	    }
	}

	DirtyHack.setFireEnabled(true);

    }

    /**
     * Hier darf nur TAPIJI rein
     * 
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
	if (PDEUtils.isFragment(project))
	    project = PDEUtils.getFragmentHost(project);

	INSTANCE = managerMap.get(project);

	if (INSTANCE == null) {
	    INSTANCE = new RBManager();
	    INSTANCE.project = project;
	    managerMap.put(project, INSTANCE);
	    INSTANCE.detectResourceBundles();
	}

	return INSTANCE;
    }

    public static RBManager getInstance(String projectName) {
	for (IProject project : getAllWorkspaceProjects(true)) {
	    if (project.getName().equals(projectName)) {
		// check if the projectName is a fragment and return the manager
		// for the host
		if (PDEUtils.isFragment(project)) {
		    return getInstance(PDEUtils.getFragmentHost(project));
		} else {
		    return getInstance(project);
		}
	    }
	}
	return null;
    }

    public static Set<IProject> getAllWorkspaceProjects(boolean ignoreNature) {
	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
		.getProjects();
	Set<IProject> projs = new HashSet<IProject>();

	for (IProject p : projects) {
	    try {
		if (ignoreNature
			|| p.hasNature("org.eclipselabs.tapiji.tools.core.nature")) {
		    projs.add(p);
		}
	    } catch (CoreException e) {
		e.printStackTrace();
	    }
	}
	return projs;
    }

    public static Set<IProject> getAllSupportedProjects() {
	return getAllWorkspaceProjects(false);
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

    public void fireResourceChanged(IMessagesBundle bundle) {
	for (IMessagesEditorListener listener : this.editorListeners) {
	    listener.onResourceChanged(bundle);
	}
    }

    protected void detectResourceBundles() {
	try {
	    project.accept(new ResourceBundleDetectionVisitor(this));

	    IProject[] fragments = PDEUtils.lookupFragment(project);
	    if (fragments != null) {
		for (IProject p : fragments) {
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

	String resourceBundleId = getResourceBundleId(resource);
	if (!resourceBundles.containsKey(resourceBundleId)) {
	    // if we do not have this condition, then you will be doomed with
	    // resource out of syncs, because here we instantiate
	    // PropertiesFileResources, which have an evil setText-Method
	    MessagesBundleGroupFactory.createBundleGroup(resource);
	}
    }

    public static String getResourceBundleId(IResource resource) {
	String packageFragment = "";

	IJavaElement propertyFile = JavaCore.create(resource.getParent());
	if (propertyFile != null && propertyFile instanceof IPackageFragment)
	    packageFragment = ((IPackageFragment) propertyFile)
		    .getElementName();

	return (packageFragment.length() > 0 ? packageFragment + "." : "")
		+ getResourceBundleName(resource);
    }

    public static String getResourceBundleName(IResource res) {
	String name = res.getName();
	String regex = "^(.*?)" //$NON-NLS-1$
		+ "((_[a-z]{2,3})|(_[a-z]{2,3}_[A-Z]{2})" //$NON-NLS-1$
		+ "|(_[a-z]{2,3}_[A-Z]{2}_\\w*))?(\\." //$NON-NLS-1$
		+ res.getFileExtension() + ")$"; //$NON-NLS-1$
	return name.replaceFirst(regex, "$1"); //$NON-NLS-1$
    }

    public void writeToFile(IMessagesBundleGroup bundleGroup) {
	for (IMessagesBundle bundle : bundleGroup.getMessagesBundles()) {
	    writeToFile(bundle);
	}
    }

    public void writeToFile(IMessagesBundle bundle) {
	DirtyHack.setEditorModificationEnabled(false);

	PropertiesSerializer ps = new PropertiesSerializer(ConfigurationManager
		.getInstance().getSerializerConfig());
	String editorContent = ps.serialize(bundle);
	IFile file = getFile(bundle);
	try {
	    file.refreshLocal(IResource.DEPTH_ZERO, null);
	    file.setContents(
		    new ByteArrayInputStream(editorContent.getBytes()), false,
		    true, null);
	    file.refreshLocal(IResource.DEPTH_ZERO, null);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    DirtyHack.setEditorModificationEnabled(true);
	}

	fireResourceChanged(bundle);

    }

    private IFile getFile(IMessagesBundle bundle) {
	if (bundle.getResource() instanceof PropertiesFileResource) { // different
								      // ResourceLocationLabel
	    String path = bundle.getResource().getResourceLocationLabel(); // P:\Allianz\Workspace\AST\TEST\src\messages\Messages_de.properties
	    int index = path.indexOf("src");
	    String pathBeforeSrc = path.substring(0, index - 1);
	    int lastIndexOf = pathBeforeSrc.lastIndexOf(File.separatorChar);
	    String projectName = path.substring(lastIndexOf + 1, index - 1);
	    String relativeFilePath = path.substring(index, path.length());

	    return ResourcesPlugin.getWorkspace().getRoot()
		    .getProject(projectName).getFile(relativeFilePath);
	} else {
	    String location = bundle.getResource().getResourceLocationLabel(); // /TEST/src/messages/Messages_en_IN.properties
	    location = location.substring(project.getName().length() + 1,
		    location.length());
	    return ResourcesPlugin.getWorkspace().getRoot()
		    .getProject(project.getName()).getFile(location);
	}
    }

    protected Locale getLocaleByName(String bundleName, String localeID) {
	// Check locale
	Locale locale = null;
	localeID = localeID.substring(0,
		localeID.length() - "properties".length() - 1);
	if (localeID.length() == bundleName.length()) {
	    // default locale
	    return null;
	} else {
	    localeID = localeID.substring(bundleName.length() + 1);
	    String[] localeTokens = localeID.split("_");

	    switch (localeTokens.length) {
	    case 1:
		locale = new Locale(localeTokens[0]);
		break;
	    case 2:
		locale = new Locale(localeTokens[0], localeTokens[1]);
		break;
	    case 3:
		locale = new Locale(localeTokens[0], localeTokens[1],
			localeTokens[2]);
		break;
	    default:
		locale = null;
		break;
	    }
	}

	return locale;
    }
}
