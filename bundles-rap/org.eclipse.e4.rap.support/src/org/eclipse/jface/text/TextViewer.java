package org.eclipse.jface.text;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Point;
import org.eclipse.e4.babel.logger.Log;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

public class TextViewer implements ITextViewer, ITextViewerExtension6 {

	protected static final String TAG = TextViewer.class.getSimpleName();
	private Text text;
	private IDocument document;
	private StyledText widget;

	public TextViewer(Composite parent, int styles) {
		text = new Text(parent, SWT.HORIZONTAL);
		widget= new StyledText(text, document);
	}

	@Override
	public IUndoManager getUndoManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StyledText getTextWidget() {
		return widget;
	}

	@Override
	public void setUndoManager(IUndoManager undoManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activatePlugins() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetPlugins() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTextListener(ITextListener listener) {
			text.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent event) {

					document.set(((Text)event.getSource()).getText());
					Log.d(TAG, "TEXT: " + ((Text)event.getSource()).getText());
						listener.textChanged(null);
				}
			});
		
	}

	@Override
	public void removeTextListener(ITextListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocument(IDocument document) {
		if(document.get() != null) {
			text.setText(document.get());
		}
		this.document = document;
	}

	@Override
	public IDocument getDocument() {
		return document;
	}

	@Override
	public void setEditable(boolean editable) {
		text.setEnabled(editable);
	}

	@Override
	public boolean isEditable() {
		return text.getEnabled();
	}

	@Override
	public void setDocument(IDocument document, int modelRangeOffset, int modelRangeLength) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setVisibleRegion(int offset, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetVisibleRegion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRegion getVisibleRegion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean overlapsWithVisibleRegion(int offset, int length) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void invalidateTextPresentation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextColor(java.awt.Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTextColor(java.awt.Color color, int offset, int length, boolean controlRedraw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDefaultPrefixes(String[] defaultPrefixes, String contentType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIndentPrefixes(String[] indentPrefixes, String contentType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelectedRange(int offset, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public java.awt.Point getSelectedRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISelectionProvider getSelectionProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void revealRange(int offset, int length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTopIndex(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTopIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTopIndexStartOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBottomIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBottomIndexEndOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTopInset() {
		// TODO Auto-generated method stub
		return 0;
	}

}
