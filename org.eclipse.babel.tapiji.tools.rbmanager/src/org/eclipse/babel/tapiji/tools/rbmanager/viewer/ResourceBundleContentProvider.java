/*******************************************************************************
 * Copyright (c) 2012 Michael Gasser.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Gasser - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.rbmanager.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.babel.tapiji.tools.core.model.IResourceBundleChangedListener;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleChangedEvent;
import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.preferences.TapiJIPreferences;
import org.eclipse.babel.tapiji.tools.core.ui.utils.RBFileUtils;
import org.eclipse.babel.tapiji.tools.core.ui.utils.ResourceUtils;
import org.eclipse.babel.tapiji.tools.core.util.FragmentProjectUtils;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualContainer;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualContentManager;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualProject;
import org.eclipse.babel.tapiji.tools.rbmanager.model.VirtualResourceBundle;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.UIJob;

/**
 * 
 * 
 */
public class ResourceBundleContentProvider implements ITreeContentProvider,
        IResourceChangeListener, IPropertyChangeListener,
        IResourceBundleChangedListener {
    private static final boolean FRAGMENT_PROJECTS_IN_CONTENT = false;
    private static final boolean SHOW_ONLY_PROJECTS_WITH_RBS = true;
    private StructuredViewer viewer;
    private VirtualContentManager vcManager;
    private UIJob refresh;
    private IWorkspaceRoot root;

    private List<IProject> listenedProjects;

    /**
	 * 
	 */
    public ResourceBundleContentProvider() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
                IResourceChangeEvent.POST_CHANGE);
        TapiJIPreferences.addPropertyChangeListener(this);
        vcManager = VirtualContentManager.getVirtualContentManager();
        listenedProjects = new LinkedList<IProject>();
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(final Object parentElement) {
        Object[] children = null;

        if (parentElement instanceof IWorkspaceRoot) {
            root = (IWorkspaceRoot) parentElement;
            try {
                IResource[] members = ((IWorkspaceRoot) parentElement)
                        .members();

                List<Object> displayedProjects = new ArrayList<Object>();
                for (IResource r : members) {
                    if (r instanceof IProject) {
                        IProject p = (IProject) r;
                        if (FragmentProjectUtils.isFragment(r.getProject())) {
                            if (vcManager.getContainer(p) == null) {
                                vcManager.addVContainer(p, new VirtualProject(
                                        p, true, false));
                            }
                            if (FRAGMENT_PROJECTS_IN_CONTENT) {
                                displayedProjects.add(r);
                            }
                        } else {
                            if (SHOW_ONLY_PROJECTS_WITH_RBS) {
                                VirtualProject vP;
                                if ((vP = (VirtualProject) vcManager
                                        .getContainer(p)) == null) {
                                    vP = new VirtualProject(p, false, true);
                                    vcManager.addVContainer(p, vP);
                                    registerResourceBundleListner(p);
                                }

                                if (vP.getRbCount() > 0) {
                                    displayedProjects.add(p);
                                }
                            } else {
                                displayedProjects.add(p);
                            }
                        }
                    }
                }

                children = displayedProjects.toArray();
                return children;
            } catch (CoreException e) {
            }
        }

        // if (parentElement instanceof IProject) {
        // final IProject iproject = (IProject) parentElement;
        // VirtualContainer vproject = vcManager.getContainer(iproject);
        // if (vproject == null){
        // vproject = new VirtualProject(iproject, true);
        // vcManager.addVContainer(iproject, vproject);
        // }
        // }

        if (parentElement instanceof IContainer) {
            IContainer container = (IContainer) parentElement;
            if (!((VirtualProject) vcManager
                    .getContainer(((IResource) parentElement).getProject()))
                    .isFragment()) {
                try {
                    children = addChildren(container);
                } catch (CoreException e) {/**/
                }
            }
        }

        if (parentElement instanceof VirtualResourceBundle) {
            VirtualResourceBundle virtualrb = (VirtualResourceBundle) parentElement;
            ResourceBundleManager rbmanager = virtualrb
                    .getResourceBundleManager();
            children = rbmanager.getResourceBundles(
                    virtualrb.getResourceBundleId()).toArray();
        }

        return children != null ? children : new Object[0];
    }

    /*
     * Returns all ResourceBundles and sub-containers (with ResourceBundles in
     * their subtree) of a Container
     */
    private Object[] addChildren(IContainer container) throws CoreException {
        Map<String, Object> children = new HashMap<String, Object>();

        VirtualProject p = (VirtualProject) vcManager.getContainer(container
                .getProject());
        List<IResource> members = new ArrayList<IResource>(
                Arrays.asList(container.members()));

        // finds files in the corresponding fragment-projects folder
        if (p.hasFragments()) {
            List<IContainer> folders = ResourceUtils.getCorrespondingFolders(
                    container, p.getFragmets());
            for (IContainer f : folders) {
                for (IResource r : f.members()) {
                    if (r instanceof IFile) {
                        members.add(r);
                    }
                }
            }
        }

        for (IResource r : members) {

            if (r instanceof IFile) {
                String resourcebundleId = RBFileUtils
                        .getCorrespondingResourceBundleId((IFile) r);
                if (resourcebundleId != null
                        && (!children.containsKey(resourcebundleId))) {
                    VirtualResourceBundle vrb;

                    String vRBId;

                    if (!p.isFragment()) {
                        vRBId = r.getProject() + "." + resourcebundleId;
                    } else {
                        vRBId = p.getHostProject() + "." + resourcebundleId;
                    }

                    VirtualResourceBundle vResourceBundle = vcManager
                            .getVResourceBundles(vRBId);
                    if (vResourceBundle == null) {
                        String resourcebundleName = ResourceBundleManager
                                .getResourceBundleName(r);
                        vrb = new VirtualResourceBundle(
                                resourcebundleName,
                                resourcebundleId,
                                ResourceBundleManager.getManager(r.getProject()));
                        vcManager.addVResourceBundle(vRBId, vrb);

                    } else {
                        vrb = vcManager.getVResourceBundles(vRBId);
                    }

                    children.put(resourcebundleId, vrb);
                }
            }
            if (r instanceof IContainer) {
                if (!r.isDerived()) { // Don't show the 'bin' folder
                    VirtualContainer vContainer = vcManager
                            .getContainer((IContainer) r);

                    if (vContainer == null) {
                        int count = RBFileUtils
                                .countRecursiveResourceBundle((IContainer) r);
                        vContainer = new VirtualContainer(container, count);
                        vcManager.addVContainer((IContainer) r, vContainer);
                    }

                    if (vContainer.getRbCount() != 0) {
                        // without resourcebundles
                        children.put("" + children.size(), r);
                    }
                }
            }
        }
        return children.values().toArray();
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IContainer) {
            return ((IContainer) element).getParent();
        }
        if (element instanceof IFile) {
            String rbId = RBFileUtils
                    .getCorrespondingResourceBundleId((IFile) element);
            return vcManager.getVResourceBundles(rbId);
        }
        if (element instanceof VirtualResourceBundle) {
            Iterator<IResource> i = new HashSet<IResource>(
                    ((VirtualResourceBundle) element).getFiles()).iterator();
            if (i.hasNext()) {
                return i.next().getParent();
            }
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof IWorkspaceRoot) {
            try {
                if (((IWorkspaceRoot) element).members().length > 0) {
                    return true;
                }
            } catch (CoreException e) {
            }
        }
        if (element instanceof IProject) {
            VirtualProject vProject = (VirtualProject) vcManager
                    .getContainer((IProject) element);
            if (vProject != null && vProject.isFragment()) {
                return false;
            }
        }
        if (element instanceof IContainer) {
            try {
                VirtualContainer vContainer = vcManager
                        .getContainer((IContainer) element);
                if (vContainer != null) {
                    return vContainer.getRbCount() > 0 ? true : false;
                } else if (((IContainer) element).members().length > 0) {
                    return true;
                }
            } catch (CoreException e) {
            }
        }
        if (element instanceof VirtualResourceBundle) {
            return true;
        }
        return false;
    }

    @Override
    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        TapiJIPreferences.removePropertyChangeListener(this);
        vcManager.reset();
        unregisterAllResourceBundleListner();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (StructuredViewer) viewer;
    }

    @Override
    // TODO remove ResourceChangelistner and add ResourceBundleChangelistner
    public void resourceChanged(final IResourceChangeEvent event) {

        final IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
            @Override
            public boolean visit(IResourceDelta delta) throws CoreException {
                final IResource res = delta.getResource();

                if (!RBFileUtils.isResourceBundleFile(res)) {
                    return true;
                }

                switch (delta.getKind()) {
                case IResourceDelta.REMOVED:
                    recountParenthierarchy(res.getParent());
                    break;
                // TODO remove unused VirtualResourceBundles and
                // VirtualContainer from vcManager
                case IResourceDelta.ADDED:
                    checkListner(res);
                    break;
                case IResourceDelta.CHANGED:
                    if (delta.getFlags() != IResourceDelta.MARKERS) {
                        return true;
                    }
                    break;
                }

                refresh(res);

                return true;
            }
        };

        try {
            event.getDelta().accept(visitor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void resourceBundleChanged(ResourceBundleChangedEvent event) {
        ResourceBundleManager rbmanager = ResourceBundleManager
                .getManager(event.getProject());

        switch (event.getType()) {
        case ResourceBundleChangedEvent.ADDED:
        case ResourceBundleChangedEvent.DELETED:
            IResource res = rbmanager.getRandomFile(event.getBundle());
            IContainer hostContainer;

            if (res == null) {
                try {
                    hostContainer = event.getProject()
                            .getFile(event.getBundle()).getParent();
                } catch (Exception e) {
                    refresh(null);
                    return;
                }
            } else {
                VirtualProject vProject = (VirtualProject) vcManager
                        .getContainer(res.getProject());
                if (vProject != null && vProject.isFragment()) {
                    IProject hostProject = vProject.getHostProject();
                    hostContainer = ResourceUtils.getCorrespondingFolders(
                            res.getParent(), hostProject);
                } else {
                    hostContainer = res.getParent();
                }
            }

            recountParenthierarchy(hostContainer);
            refresh(null);
            break;
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(TapiJIPreferences.NON_RB_PATTERN)) {
            vcManager.reset();

            refresh(root);
        }
    }

    // TODO problems with remove a hole ResourceBundle
    private void recountParenthierarchy(IContainer parent) {
        if (parent.isDerived()) {
            return; // Don't recount the 'bin' folder
        }

        VirtualContainer vContainer = vcManager.getContainer(parent);
        if (vContainer != null) {
            vContainer.recount();
        }

        if ((parent instanceof IFolder)) {
            recountParenthierarchy(parent.getParent());
        }
    }

    private void refresh(final IResource res) {
        if (refresh == null || refresh.getResult() != null) {
            refresh = new UIJob("refresh viewer") {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    if (viewer != null && !viewer.getControl().isDisposed()) {
                        if (res != null) {
                            viewer.refresh(res.getProject(), true); // refresh(res);
                        } else {
                            viewer.refresh();
                        }
                    }
                    return Status.OK_STATUS;
                }
            };
        }
        refresh.schedule();
    }

    private void registerResourceBundleListner(IProject p) {
        listenedProjects.add(p);

        ResourceBundleManager rbmanager = ResourceBundleManager.getManager(p);
        for (String rbId : rbmanager.getResourceBundleIdentifiers()) {
            rbmanager.registerResourceBundleChangeListener(rbId, this);
        }
    }

    private void unregisterAllResourceBundleListner() {
        for (IProject p : listenedProjects) {
            ResourceBundleManager rbmanager = ResourceBundleManager
                    .getManager(p);
            for (String rbId : rbmanager.getResourceBundleIdentifiers()) {
                rbmanager.unregisterResourceBundleChangeListener(rbId, this);
            }
        }
    }

    private void checkListner(IResource res) {
        ResourceBundleManager rbmanager = ResourceBundleManager.getManager(res
                .getProject());
        String rbId = ResourceBundleManager.getResourceBundleId(res);
        rbmanager.registerResourceBundleChangeListener(rbId, this);
    }
}
