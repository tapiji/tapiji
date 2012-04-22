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
package org.eclipselabs.tapiji.translator.core;

import java.io.File;

public class LoadGlossaryEvent {

	private boolean newGlossary = false;
	private File glossaryFile;
	
	public LoadGlossaryEvent (File glossaryFile) {
		this.glossaryFile = glossaryFile;
	}
	
	public File getGlossaryFile() {
		return glossaryFile;
	}
	
	public void setNewGlossary(boolean newGlossary) {
		this.newGlossary = newGlossary;
	}
	
	public boolean isNewGlossary() {
		return newGlossary;
	}
	
	public void setGlossaryFile(File glossaryFile) {
		this.glossaryFile = glossaryFile;
	}
	
}
