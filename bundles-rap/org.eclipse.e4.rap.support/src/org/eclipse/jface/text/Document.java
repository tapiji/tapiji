package org.eclipse.jface.text;

import javax.swing.text.BadLocationException;

public class Document  implements IDocument {

	private String text;
	
	public Document() {
	}
	
	@Override
	public String get() {
		return this.text;
	}
	
	@Override
	public void set(String text) {
		this.text = text;
	}

	@Override
	public int getLength() {
		return text.length();
	}

	@Override
	public String get(int offset, int length) throws BadLocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLineOfOffset(int offset) throws BadLocationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getModificationStamp() {
		return 0;
	}
}
