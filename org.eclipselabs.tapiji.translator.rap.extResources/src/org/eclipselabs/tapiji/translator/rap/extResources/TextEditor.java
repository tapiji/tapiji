package org.eclipselabs.tapiji.translator.rap.extResources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
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
	
	public TextEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		textField = new Text(parent, SWT.HORIZONTAL);
		textField.setText(readFile());
		
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
			return "";
	}
	
	private String readFile() {
		String content = "";		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));		
			
			String line = "";
			while ((line = reader.readLine()) != null) {
				content = content + line + "\n";
			}			
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
}
