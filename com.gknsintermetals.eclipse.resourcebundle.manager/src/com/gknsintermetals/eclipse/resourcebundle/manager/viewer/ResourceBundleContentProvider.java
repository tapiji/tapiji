package com.gknsintermetals.eclipse.resourcebundle.manager.viewer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.UIJob;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.util.RBFileUtils;

import com.gknsintermetals.eclipse.resourcebundle.manager.model.VirtualContainer;
import com.gknsintermetals.eclipse.resourcebundle.manager.model.VirtualContentManager;
import com.gknsintermetals.eclipse.resourcebundle.manager.model.VirtualProject;
import com.gknsintermetals.eclipse.resourcebundle.manager.model.VirtualResourceBundle;


/**
 * 
 * 
 */
public class ResourceBundleContentProvider implements ITreeContentProvider, IResourceChangeListener {
	private StructuredViewer viewer;
	private VirtualContentManager vcManager;
	private UIJob refresh;
	
	/**
	 * 
	 */
	public ResourceBundleContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		vcManager = VirtualContentManager.getVirtualContentManager();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		Object[] children = null;
		
		if (parentElement instanceof IWorkspaceRoot) {
			//Open all projects asynchronous in the ResourceBundleManager
//			Display.getDefault().asyncExec(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						for(IResource r : ((IWorkspaceRoot)parentElement).members()){
//							if (r instanceof IProject){
//								IProject iproject = (IProject) r;
//								if (!projects.containsKey(iproject)) {
//									Project project = new Project(iproject);
//									projects.put(iproject , project);
//								}
//							}
//						}
//					} catch (CoreException e) {/*...*/}
//				}
//			});
			
			// Return all IProjects
			try {
				return children = ((IWorkspaceRoot)parentElement).members();
			} catch (CoreException e) {	}
		}
		
		if (parentElement instanceof IProject) {
			final IProject iproject = (IProject) parentElement;
			VirtualContainer vproject = vcManager.getContainer(iproject);
			if (vproject == null){
				vproject = new VirtualProject(iproject, true);
				vcManager.addVContainer(iproject, vproject);
			}
		}
		
		if (parentElement instanceof IContainer) {
			IContainer container = (IContainer) parentElement;
			try {
				children = addChildren(container);
			} catch (CoreException e) {/**/}
		}
		
		if (parentElement instanceof VirtualResourceBundle) {
			VirtualResourceBundle virtualrb = (VirtualResourceBundle) parentElement;
			ResourceBundleManager rbmanager = virtualrb.getResourceBundleManager();
			children = rbmanager.getResourceBundles(virtualrb.getResourceBundleId()).toArray();
		}
		
		return children != null ? children : new Object[0];
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	@Override
	public Object getParent(Object element) {
		if (element instanceof IContainer) {
			return ((IContainer) element).getParent();
		}
		if (element instanceof IFile) {
			String rbId = RBFileUtils.getCorrespondingResourceBundleId((IFile) element);
			return vcManager.getVResourceBundles(rbId);
		}
		if (element instanceof VirtualResourceBundle) {
			Iterator<IResource> i = new HashSet<IResource>(((VirtualResourceBundle) element).getFiles()).iterator();
			if (i.hasNext()) return i.next().getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IWorkspaceRoot) {
			try {
				if (((IWorkspaceRoot) element).members().length > 0) return true;
			} catch (CoreException e) {}
		}
		if (element instanceof IContainer) {
			try {
				VirtualContainer vContainer = vcManager.getContainer((IContainer) element);
				if (vContainer != null) 
					return vContainer.getRbCount() > 0 ? true : false;
				else if (((IContainer) element).members().length > 0) return true;
			} catch (CoreException e) {}
		}
		if (element instanceof VirtualResourceBundle){
			return true;
		}
		return false;
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (StructuredViewer) viewer;
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		
		final IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			@Override
			public boolean visit(IResourceDelta delta) throws CoreException {
				final IResource res = delta.getResource();
				
				switch (delta.getKind()) {
					case IResourceDelta.ADDED:
					case IResourceDelta.REMOVED:
						if (res instanceof IResource){
							IResourceDelta[] affectChildren = delta.getAffectedChildren();
							for (IResourceDelta child : affectChildren){
								//TODO this doesn't work
								if (child.getResource() instanceof IContainer){
									VirtualContainer vContainer = vcManager.getContainer((IContainer) child.getResource());
									if (vContainer != null) vContainer.recount();
								}
							}
						}
							
						//TODO remove unused VirtualResourceBundles and VirtualContainer
						break;
					case IResourceDelta.CHANGED:
						if (delta.getFlags() == IResourceDelta.MARKERS){
							break;
						} else return true;
				}
				
				if (refresh == null || refresh.getResult() != null) {
					refresh = new UIJob("refresh viewer") {
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor) {
							if (viewer != null
									&& !viewer.getControl().isDisposed())
								viewer.refresh(res.getProject(),true); // refresh(res);
							return Status.OK_STATUS;
						}
					};
					refresh.schedule();
				}
				
				return true;
			}
		};
		
		try {
			event.getDelta().accept(visitor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return all Resource Bundles and subcontainers with Resource Bundles in the subtree (except the 'bin' folder) of a Container
	 */
	private Object[] addChildren(IContainer container) throws CoreException{
		Map<String, Object> children = new HashMap<String,Object>();
		
		IResource[] members = container.members();
		for(IResource r : members){
				
				if (r instanceof IFile) {
					String resourcebundleId =  RBFileUtils.getCorrespondingResourceBundleId((IFile)r);
					if( resourcebundleId != null && (!children.containsKey(resourcebundleId))) {
						VirtualResourceBundle vrb;
						VirtualProject p = (VirtualProject) vcManager.getContainer(r.getProject());
						String vRBId;
						
						if (!p.isFragment()) vRBId = r.getProject()+"."+resourcebundleId;
						else vRBId = p.getHostProject() + "." + resourcebundleId;
						
						VirtualResourceBundle vResourceBundle = vcManager.getVResourceBundles(vRBId);
						if (vResourceBundle == null){
							String resourcebundleName = ResourceBundleManager.getResourceBundleName(r);
							vrb = new VirtualResourceBundle(resourcebundleName, resourcebundleId, ResourceBundleManager.getManager(r.getProject()));
							vcManager.addVResourceBundle(vRBId, vrb);
						} else vrb = vcManager.getVResourceBundles(vRBId);
						
						children.put(resourcebundleId, vrb);
					}
				} 
				if (r instanceof IContainer) 
					if (!r.getClass().getSimpleName().equals("CompilationUnit")){			//Don't show the 'bin' folder
						VirtualContainer vContainer = vcManager.getContainer((IContainer) r);
						
						if (vContainer == null){
							int count = RBFileUtils.countRecursiveResourceBundle((IContainer)r);
							vContainer = new VirtualContainer(container, count);
							vcManager.addVContainer((IContainer) r, vContainer);
						}
					
						if (vContainer.getRbCount() != 0)									//Don't show folder with no resourcebundles
							children.put(""+children.size(), r);
				}
		}
		return children.values().toArray();
	}
}
