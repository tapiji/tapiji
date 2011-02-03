package at.ac.tuwien.inso.eclipse.rbe.model.tree;

import java.util.Map;
import java.util.Set;

import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleGroup;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.updater.IKeyTreeUpdater;

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
