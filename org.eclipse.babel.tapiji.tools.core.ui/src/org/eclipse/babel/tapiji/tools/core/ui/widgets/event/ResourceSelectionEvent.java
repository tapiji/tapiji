/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.core.ui.widgets.event;

public class ResourceSelectionEvent {

	private String selectionSummary;
	private String selectedKey;
	
	public ResourceSelectionEvent (String selectedKey, String selectionSummary) {
		this.setSelectionSummary(selectionSummary);
		this.setSelectedKey(selectedKey);
	}

	public void setSelectedKey (String key) {
		selectedKey = key;
	}
	
	public void setSelectionSummary(String selectionSummary) {
		this.selectionSummary = selectionSummary;
	}

	public String getSelectionSummary() {
		return selectionSummary;
	}

	public String getSelectedKey() {
		return selectedKey;
	}
	
	
	
}
