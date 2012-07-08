package org.eclipse.jface.text;


import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * Simple Document class which only hold a String object.
 * 
 * @author Matthias Lettmayer
 *
 */
public class TextDocument implements IDocument {

	private String content;
	private TextEditor owner;
	
	public TextDocument(TextEditor owner) {
		this.owner = owner;
		content = owner.getText();
	}
	
	@Override
	public String get() {
		return content;
	}
	
	@Override
	public void set(String text) {
		content = text;
		owner.setText(text);
	}

	@Override
	public int getLength() {
		return content.length();
	}
}
