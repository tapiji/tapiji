package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class ValueOfResourceBundleProposal implements IJavaCompletionProposal {

	private int offset = 0;
	private int length = 0;
	private String content = "";
	private String matchingString = "";
	
	public ValueOfResourceBundleProposal (int offset, int length, String content, String matchingString) {
		this.offset = offset;
		this.length = length;
		this.content = content;
		this.matchingString = matchingString;
	}
	
	@Override
	public void apply(IDocument document) {
		try {
			String inplaceContent = "myResources.getString(\"" + content + "\")";
			// TODO prüfe öffnende und schließende klammern
			document.replace(offset-1, length+2, inplaceContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public String getAdditionalProposalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayString() {
		// TODO Auto-generated method stub
		return "Insert localized reference to '" + matchingString + "'";
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRelevance() {
		// TODO Auto-generated method stub
		return 98;
	}

}
