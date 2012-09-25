/*******************************************************************************
 * Copyright (c) 2012 Martin Reiterer, Alexej Strelzow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 *     Alexej Strelzow - moved SWT code to FilePreferencePage
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.preferences;

public class CheckItem {
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

    public boolean equals(CheckItem item) {
	return name.equals(item.getName());
    }
}
