package org.eclipselabs.tapiji.translator.rap;

import java.awt.event.TextEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class TestEditor extends EditorPart {

	public static final String ID = "org.eclipselabs.tapiji.translator.rap.TestEditor";
	
	private File file;
	
	public TestEditor() {
		// TODO Auto-generated constructor stub
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
		if (! (input instanceof TestEditorInput)) {
			throw new RuntimeException("Input not of type " + TestEditorInput.class.getName());
		}
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		file = (File) input.getAdapter(File.class);
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
		Text text = new Text(parent, SWT.HORIZONTAL);
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			
			String line = "";		
			while ((line = reader.readLine()) != null) {
				text.setText(text.getText() + line + "\n");
			}
			
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
