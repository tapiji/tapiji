package org.eclipse.jface.text;

import javax.swing.text.BadLocationException;

import org.eclipse.e4.babel.logger.Log;

public class Document  implements IDocument {

	private static final String TAG = null;
	private String text;
	
	public Document() {
	}
	
	@Override
	public String get() {
		return this.text;
	}
	
	@Override
	public void set(String text) {
		 Log.d(TAG, "set" + text);
		this.text = text;
	}

	@Override
	public int getLength() {
		return text.length();
	}

	@Override
	public String get(int offset, int length) throws BadLocationException {
		return null;
	}

	@Override
	public int getLineOfOffset(int offset) throws BadLocationException {
		return 0;
	}

	@Override
	public long getModificationStamp() {
		return 0;
	}
}
