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
package org.eclipselabs.tapiji.translator.rap.babel.editor.tree;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.tree.IKeyTreeNode;

public interface IKeyTreeContributor {

	void contribute(final TreeViewer treeViewer);

	IKeyTreeNode getKeyTreeNode(String key);

	IKeyTreeNode[] getRootKeyItems();
}
