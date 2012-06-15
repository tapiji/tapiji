package org.eclipselabs.tapiji.translator.rap.core;

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
