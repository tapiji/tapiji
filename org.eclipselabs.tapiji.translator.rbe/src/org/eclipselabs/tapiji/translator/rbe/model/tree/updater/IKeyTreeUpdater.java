package org.eclipselabs.tapiji.translator.rbe.model.tree.updater;

import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IKeyTreeContributor;

public interface IKeyTreeUpdater {

	void addKey(IKeyTreeContributor keyTree, String key);

	void removeKey(IKeyTreeContributor keyTree, String key);

}
