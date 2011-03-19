package org.eclipselabs.tapiji.translator.rbe.model.tree;

import java.util.Map;
import java.util.Set;

import org.eclipselabs.tapiji.translator.rbe.model.bundle.IBundleGroup;
import org.eclipselabs.tapiji.translator.rbe.model.tree.updater.IKeyTreeUpdater;


public interface IKeyTree {

	IBundleGroup getBundleGroup();

	IKeyTreeItem getKeyTreeItem(String oldKey);

	Map getKeyItemsCache();

	Set getRootKeyItems();

	void selectKey(String key);

	String getSelectedKey();

	IKeyTreeUpdater getUpdater();

	void setUpdater(IKeyTreeUpdater updater);

}
