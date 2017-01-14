package org.eclipse.jface.text.source;


import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SourceViewer extends TextViewer {

	public SourceViewer(Composite parent, int styles) {
		super(parent, styles);
		new Text(parent, styles);
		// TODO Auto-generated constructor stub
	}


	
}
