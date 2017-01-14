package org.eclipse.swt.custom;

import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class StyledText {

	private static final long serialVersionUID = -5801418312829430710L;

	private Text text;

	public StyledText(Text text) {
		this.text = text;
	}

	public void setLayoutData(GridData textViewStyleData) {
		text.setLayoutData(textViewStyleData);
	}

	public void addFocusListener(FocusListener focusListener) {
		text.addFocusListener(focusListener);
	}

	public void addTraverseListener(TraverseListener traverseListener) {
		text.addTraverseListener(traverseListener);
	}

	public void addKeyListener(KeyListener keyListener) {
		text.addKeyListener(keyListener);
	}

	public String getText() {
		return text.getText();
	}

	public int getCaretOffset() {
		return 0;
	}

	public void setFocus() {
		text.setFocus();
	}

	public boolean isVisible() {
		return text.isVisible();
	}

	public void setSelection(int caretPosition) {
		text.setSelection(caretPosition);
	}

	public void setVisible(boolean visible) {
		text.setVisible(visible);
	}

	public GridData getLayoutData() {
		return (GridData) text.getLayoutData();
	}

	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
	}

	public void setEditable(boolean editable) {
		text.setEditable(editable);
	}

	public void setBackground(Color color) {
		text.setBackground(color);
	}
}
