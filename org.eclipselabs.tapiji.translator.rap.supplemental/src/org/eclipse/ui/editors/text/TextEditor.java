package org.eclipse.ui.editors.text;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.DocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Simple text editor, which operates on a file and uses a text widget as interface. 
 * 
 * @author Matthias Lettmayer
 *
 */
public class TextEditor extends EditorPart implements ITextEditor {

	public static final String ID = "org.eclipselabs.tapiji.translator.rap.wrappers.TextEditor";
	
	private File file;
	private Text textField;
	private boolean editable = true;
	private boolean dirty = false;
    private DocumentProvider documentProvider;
	
	public TextEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (monitor != null)
			monitor.beginTask("Saving file...", 1);
		writeFile();
		setDirty(false);
		if (monitor != null)
			monitor.done();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if ((input instanceof IFileEditorInput)) {
			IFileEditorInput ifei = (IFileEditorInput) input;			
			IFile ifile = ifei.getFile();
			file = ifile.getRawLocation().makeAbsolute().toFile();
		} else {		
			throw new RuntimeException("Input not of type " + IFileEditorInput.class.getName());
		}
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	private void setDirty(boolean value) {
		dirty = value;
		firePropertyChange( PROP_DIRTY );
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		textField = new Text(parent, SWT.HORIZONTAL);
		textField.setText(readFile());
		
		textField.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent event) {
				if (!dirty) {
					setDirty(true);
				}
			}
		});		
	}

	@Override
	public void setFocus() {
		textField.setFocus();
	}
	
	private void setText(String text) {
		if (textField != null)
			textField.setText(text);
	}
	
	private String getText() {
		if (textField != null)
			return textField.getText();
		else if (file != null)
			return readFile();
		else 
			return null;
	}
	
	public DocumentProvider getDocumentProvider() {
		if (documentProvider == null) {
			documentProvider = new DocumentProvider(getText());
		}
		return documentProvider;
	}
	
	private String readFile() {		
		String content = null;		
		try {
			content = FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return content;
	}
	
	private void writeFile() {
		String content = textField.getText();		
		try {
			FileUtils.writeStringToFile(file, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void selectAndReveal(int selectionIndex, int selectionLength) {
		textField.setSelection(selectionIndex, selectionIndex+selectionLength);
		textField.setFocus();		
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public void doRevertToSaved() {
		textField.setText(readFile());		
	}

	@Override
	public void close(boolean save) {
		if (save)
			doSave(null);
	}

	@Override
	public void setAction(String actionID, IAction action) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActionActivationCode(String actionId,
			char activationCharacter, int activationKeyCode,
			int activationStateMask) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeActionActivationCode(String actionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean showsHighlightRangeOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void showHighlightRangeOnly(boolean showHighlightRangeOnly) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHighlightRange(int offset, int length, boolean moveCursor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IRegion getHighlightRange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetHighlightRange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISelectionProvider getSelectionProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAction getAction(String actionId) {
		// TODO Auto-generated method stub
		return null;
	}
}
