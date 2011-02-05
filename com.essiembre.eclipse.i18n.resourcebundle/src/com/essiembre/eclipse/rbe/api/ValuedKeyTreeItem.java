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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.essiembre.eclipse.rbe.model.tree.KeyTreeItem;


import at.ac.tuwien.inso.eclipse.rbe.model.tree.IKeyTree;
import at.ac.tuwien.inso.eclipse.rbe.model.tree.IValuedKeyTreeItem;

public class ValuedKeyTreeItem extends KeyTreeItem implements IValuedKeyTreeItem {

	private Map<Locale, String> values = new HashMap<Locale, String>();
	private Object info;

	public ValuedKeyTreeItem(IKeyTree keyTree, String id, String name) {
		super(keyTree, id, name);
	}
	
	public void initValues (Map<Locale, String> values) {
		this.values = values;
	}

	public void addValue (Locale locale, String value) {
		values.put(locale, value);
	}
	
	public String getValue (Locale locale) {
		return values.get(locale);
	}
	
	public Collection<String> getValues () {
		return values.values();
	}

	public void setInfo(Object info) {
		this.info = info;
	}

	public Object getInfo() {
		return info;
	}
	
	public Collection<Locale> getLocales () {
		List<Locale> locs = new ArrayList<Locale> ();
		for (Locale loc : values.keySet()) {
			locs.add(loc);
		}
		return locs;
	}
}
