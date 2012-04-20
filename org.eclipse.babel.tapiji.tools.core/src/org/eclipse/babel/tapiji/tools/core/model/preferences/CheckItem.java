/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.model.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class CheckItem{
	boolean checked;
	String name;
	
	public CheckItem(String item, boolean checked) {
		this.name = item;
		this.checked = checked;
	}
	
	public String getName() {
		return name;
	}

	public boolean getChecked() {
		return checked;
	}
	
	public TableItem toTableItem(Table  table){
		TableItem item = new TableItem(table, SWT.NONE);
		item.setText(name);
		item.setChecked(checked);
		return item;
	}
	
	public boolean equals(CheckItem item){
		return name.equals(item.getName());
	}
}
