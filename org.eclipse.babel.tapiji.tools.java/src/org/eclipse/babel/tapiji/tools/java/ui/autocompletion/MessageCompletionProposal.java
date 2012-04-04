package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.util.ImageUtils;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class MessageCompletionProposal implements IJavaCompletionProposal {

    private int offset = 0;
    private int length = 0;
    private String content = "";
    private boolean messageAccessor = false;

    public MessageCompletionProposal(int offset, int length, String content,
	    boolean messageAccessor) {
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
	    Logger.logError(e);
	}
    }

    @Override
    public String getAdditionalProposalInfo() {
	return "Inserts the resource key '" + this.content + "'";
    }

    @Override
    public IContextInformation getContextInformation() {
	return null;
    }

    @Override
    public String getDisplayString() {
	return content;
    }

    @Override
    public Image getImage() {
	if (messageAccessor)
	    return ImageUtils.getImage(ImageUtils.IMAGE_RESOURCE_BUNDLE);
	return ImageUtils.getImage(ImageUtils.IMAGE_PROPERTIES_FILE);
    }

    @Override
    public Point getSelection(IDocument document) {
	return new Point(offset + content.length() + 1, 0);
    }

    @Override
    public int getRelevance() {
	return 99;
    }

}
