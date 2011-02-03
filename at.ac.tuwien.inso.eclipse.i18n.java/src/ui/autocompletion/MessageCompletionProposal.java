package ui.autocompletion;

import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import at.ac.tuwien.inso.eclipse.i18n.util.ImageUtils;

public class MessageCompletionProposal implements IJavaCompletionProposal {

	private int offset = 0;
	private int length = 0;
	private String content = "";
	private boolean messageAccessor = false;
	
	public MessageCompletionProposal (int offset, int length, String content, boolean messageAccessor) {
		this.offset = offset;
		this.length = length;
		this.content = content;
		this.messageAccessor = messageAccessor;
	}
	
	@Override
	public void apply(IDocument document) {
		try {
			document.replace(offset, length, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAdditionalProposalInfo() {
		// TODO Auto-generated method stub
		return "Inserts the property key '" + content + "' of the resource-bundle 'at.test.messages'";
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayString() {
		return content;
	}

	@Override
	public Image getImage() {
		// TODO Auto-generated method stub
		if (messageAccessor)
			return ImageUtils.getImage(ImageUtils.IMAGE_RESOURCE_BUNDLE);
		return ImageUtils.getImage(ImageUtils.IMAGE_PROPERTIES_FILE);
	}

	@Override
	public Point getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return new Point (offset+content.length()+1, 0);
	}

	@Override
	public int getRelevance() {
		// TODO Auto-generated method stub
		return 99;
	}

}
