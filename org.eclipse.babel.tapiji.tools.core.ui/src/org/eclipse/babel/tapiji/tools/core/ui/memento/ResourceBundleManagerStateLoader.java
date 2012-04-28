/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexej Strelzow - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.memento;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.model.IResourceDescriptor;
import org.eclipse.babel.tapiji.tools.core.model.ResourceDescriptor;
import org.eclipse.babel.tapiji.tools.core.model.manager.IStateLoader;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.util.FileUtils;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * Loads the state of the {@link ResourceBundleManager}.<br>
 * <br>
 * 
 * @author Alexej Strelzow
 */
public class ResourceBundleManagerStateLoader implements IStateLoader {

	private static final String TAG_INTERNATIONALIZATION = "Internationalization";
	private static final String TAG_EXCLUDED = "Excluded";
	private static final String TAG_RES_DESC = "ResourceDescription";
	private static final String TAG_RES_DESC_ABS = "AbsolutePath";
	private static final String TAG_RES_DESC_REL = "RelativePath";
	private static final String TAB_RES_DESC_PRO = "ProjectName";
	private static final String TAB_RES_DESC_BID = "BundleId";
	
	private HashSet<IResourceDescriptor> excludedResources;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadState() {

		excludedResources = new HashSet<IResourceDescriptor>();
		FileReader reader = null;
		try {
			reader = new FileReader(FileUtils.getRBManagerStateFile());
			loadManagerState(XMLMemento.createReadRoot(reader));
		} catch (Exception e) {
			Logger.logError(e);
		}
		
//		changelistener = new RBChangeListner();
//		ResourcesPlugin.getWorkspace().addResourceChangeListener(
//		        changelistener,
//		        IResourceChangeEvent.PRE_DELETE
//		                | IResourceChangeEvent.POST_CHANGE);
	}
	

	private void loadManagerState(XMLMemento memento) {
		IMemento excludedChild = memento.getChild(TAG_EXCLUDED);
		for (IMemento excluded : excludedChild.getChildren(TAG_RES_DESC)) {
			IResourceDescriptor descriptor = new ResourceDescriptor();
			descriptor.setAbsolutePath(excluded.getString(TAG_RES_DESC_ABS));
			descriptor.setRelativePath(excluded.getString(TAG_RES_DESC_REL));
			descriptor.setProjectName(excluded.getString(TAB_RES_DESC_PRO));
			descriptor.setBundleId(excluded.getString(TAB_RES_DESC_BID));
			excludedResources.add(descriptor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<IResourceDescriptor> getExcludedResources() {
		return excludedResources;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState() {
		if (excludedResources == null) {
			return;
		}
		XMLMemento memento = XMLMemento
		        .createWriteRoot(TAG_INTERNATIONALIZATION);
		IMemento exclChild = memento.createChild(TAG_EXCLUDED);

		Iterator<IResourceDescriptor> itExcl = excludedResources.iterator();
		while (itExcl.hasNext()) {
			IResourceDescriptor desc = itExcl.next();
			IMemento resDesc = exclChild.createChild(TAG_RES_DESC);
			resDesc.putString(TAB_RES_DESC_PRO, desc.getProjectName());
			resDesc.putString(TAG_RES_DESC_ABS, desc.getAbsolutePath());
			resDesc.putString(TAG_RES_DESC_REL, desc.getRelativePath());
			resDesc.putString(TAB_RES_DESC_BID, desc.getBundleId());
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(FileUtils.getRBManagerStateFile());
			memento.save(writer);
		} catch (Exception e) {
			Logger.logError(e);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e) {
				Logger.logError(e);
			}
		}
	}

}
