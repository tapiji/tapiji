package org.eclipse.jface.text;

/**
 * Simple Document class which only hold a String object.
 * 
 * @author Matthias Lettmayer
 *
 */
public class Document {

	private String content;
	
	public Document(String source) {
		content = source;
	}
	
	public String get() {
		return content;
	}
	
	public void set(String text) {
		content = text;
	}
}
