package org.eclipselabs.tapiji.translator.rap.extResources;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
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

public class TextEditor extends EditorPart {

	public static final String ID = "org.eclipselabs.tapiji.translator.rap.wrappers.TextEditor";
	
	private File file;
	private Text textField;
	
	private boolean dirty = false;
    
	public TextEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		monitor.beginTask("Saving file...", 1);
		writeFile();
		setDirty(false);
		monitor.done();
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if ((input instanceof TextEditorInput)) {
			file = (File) input.getAdapter(File.class);
		} else if ((input instanceof IFileEditorInput)) {
			IFileEditorInput ifei = (IFileEditorInput) input;			
			IFile ifile = ifei.getFile();
			file = ifile.getRawLocation().makeAbsolute().toFile();
		} else {		
			throw new RuntimeException("Input not of type " + TextEditorInput.class.getName() + 
					" or " + IFileEditorInput.class.getName());
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
		// TODO Auto-generated method stub
	}
	
	public void setText(String text) {
		if (textField != null)
			textField.setText(text);
	}
	
	public String getText() {
		if (textField != null)
			return textField.getText();
		else if (file != null)
			return readFile();
		else 
			return null;
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
}
