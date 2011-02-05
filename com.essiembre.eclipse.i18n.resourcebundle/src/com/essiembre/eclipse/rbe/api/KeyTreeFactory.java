/*
 * Copyright (C) 2011, 
 * 
 * This file is part of Essiembre ResourceBundle Editor.
 * 
 * Essiembre ResourceBundle Editor is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * Essiembre ResourceBundle Editor is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Essiembre ResourceBundle Editor; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
package com.essiembre.eclipse.rbe.api;

import at.ac.tuwien.inso.eclipse.rbe.model.bundle.IBundleGroup;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTree;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.updater.IKeyTreeUpdater;

import com.essiembre.eclipse.rbe.model.tree.KeyTree;
import com.essiembre.eclipse.rbe.model.tree.updater.FlatKeyTreeUpdater;
import com.essiembre.eclipse.rbe.model.tree.updater.GroupedKeyTreeUpdater;

public class KeyTreeFactory {
	
	public static IKeyTree createKeyTree(IBundleGroup iBundleGroup, IKeyTreeUpdater updater) {
		return new KeyTree(iBundleGroup, updater);
	}
	
	public static FlatKeyTreeUpdater createFlatKeyTreeUpdater() {
		return new FlatKeyTreeUpdater();
	}
	
	public static GroupedKeyTreeUpdater createGroupedKeyTreeUpdater() {
		return new GroupedKeyTreeUpdater(".");
	}
}
