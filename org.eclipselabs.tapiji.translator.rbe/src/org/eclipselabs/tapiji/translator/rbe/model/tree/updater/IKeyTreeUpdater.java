package org.eclipselabs.tapiji.translator.rbe.model.tree.updater;

import org.eclipselabs.tapiji.translator.rbe.model.tree.IKeyTree;

public interface IKeyTreeUpdater {

	void addKey(IKeyTree keyTree, String key);

	void removeKey(IKeyTree keyTree, String key);

}
