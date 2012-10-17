package org.eclipse.ui.editors.text;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.texteditor.DocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipselabs.tapiji.translator.rap.helpers.managers.RBLockManager;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.DBUtils;
import org.eclipselabs.tapiji.translator.rap.helpers.utils.UserUtils;
import org.eclipselabs.tapiji.translator.rap.model.user.PropertiesFile;
import org.eclipselabs.tapiji.translator.rap.model.user.ResourceBundle;
import org.eclipselabs.tapiji.translator.rap.model.user.User;

/**
 * Simple text editor, which operates on a file and uses a text widget as interface. 
 * 
 * @author Matthias Lettmayer
 *
 */
public class TextEditor extends EditorPart implements ITextEditor {

	public static final String ID = "org.eclipse.ui.editors.text.TextEditor";
		
	private File file;
	private ResourceBundle rb;
	private Text textField;
	private boolean editable = true;
	private boolean dirty = false;
    private DocumentProvider documentProvider;
    private String fileContent;
    private IStatusLineManager statusLineManager;
    
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
		
		if (rb == null) {
			PropertiesFile propsFile = DBUtils.getPropertiesFile(file.getAbsolutePath());
			rb = propsFile != null ? propsFile.getResourceBundle() : null;
		}
		if (statusLineManager == null)
			statusLineManager = site.getActionBars().getStatusLineManager();
	}
	
	@Override
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean value) {		
		dirty = value;
		
		if (dirty == true && rb != null) {
			// try to lock RB, returns current user if locking was successfully
			User ownerOfLock = RBLockManager.INSTANCE.tryLock(rb.getId());
			User currentUser = UserUtils.getUser();
			// if RB is locked by another user and UIThread hasn't disabled editor yet
			if (! ownerOfLock.equals(currentUser)) {
				// undo typed key
				doRevertToSaved();
				// avoid that editor will be dirty
				return;
			} 
		} else if (dirty == false && rb != null && RBLockManager.INSTANCE.isLocked(rb.getId())) {
			RBLockManager.INSTANCE.release(rb.getId());				
		}
		
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
				if (textField.getText().equals(fileContent)) {
					if (dirty)
						setDirty(false);
				} else {
					if (!dirty)		
						setDirty(true);
				}
			}
		});
	}

	@Override
	public void setFocus() {
		textField.setFocus();
	}
	
	public void setText(final String text) {
		if (textField != null) {	
			Display display = textField.getDisplay();
			// only set text widget, which belongs to current running UIThread (saved in display)
			if (display.equals(Display.getCurrent())) {
				textField.setText(text);
			}
		}
	}
	
	public String getText() {
		if (textField != null)
			return textField.getText();
		else if (file != null)
			return readFile();
		else 
			return null;
	}
		
	public DocumentProvider getDocumentProvider() {
		if (documentProvider == null) {
			documentProvider = new DocumentProvider(this);
		}
		return documentProvider;
	}
	
	private String readFile() {			
		try {
			fileContent = FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return fileContent;
	}
	
	private void writeFile() {
		String content = textField.getText();		
		try {
			FileUtils.writeStringToFile(file, content);
			fileContent = content;
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
		textField.setEditable(editable);
	}

	public boolean isEnabled() {
		return textField.isEnabled();
	}
	
    public void setEnabled(boolean enabled) {
    	textField.setEnabled(enabled);
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

	public void addKeyistener(KeyListener keyListener) {
		textField.addKeyListener(keyListener);
	}
	
	public ResourceBundle getResourceBundle() {
		return rb;
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
