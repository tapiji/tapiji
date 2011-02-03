package at.ac.tuwien.inso.eclipse.rbe.model.tree.updater;

import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTree;

public interface IKeyTreeUpdater {

	void addKey(IKeyTree keyTree, String key);

	void removeKey(IKeyTree keyTree, String key);

}
