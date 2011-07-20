package ui.autocompletion;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.tools.core.ui.dialogs.ResourceBundleEntrySelectionDialog;

import util.ASTutils;

public class InsertResourceBundleReferenceProposal implements IJavaCompletionProposal {

	private int offset = 0;
	private int length = 0;
	private ResourceBundleManager manager;
	private IResource resource;
	private String reference;
	
	public InsertResourceBundleReferenceProposal (int offset, int length, ResourceBundleManager manager,
			IResource resource, Collection<String> availableBundles) {
		this.offset = offset;
		this.length = length;
		this.manager = manager;
		this.resource = resource;
	}
	
	@Override
	public void apply(IDocument document) {
		ResourceBundleEntrySelectionDialog dialog = new ResourceBundleEntrySelectionDialog(
				Display.getDefault().getActiveShell(),
				manager, "");
		if (dialog.open() != InputDialog.OK)
			return;
		
		String resourceBundleId = dialog.getSelectedResourceBundle();
		String key = dialog.getSelectedResource();
		Locale locale = dialog.getSelectedLocale();
		
		reference = ASTutils.insertExistingBundleRef(document, resource, offset, length, resourceBundleId, key, locale);
	}

	@Override
	public String getAdditionalProposalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public String getDisplayString() {
		return "Insert reference to a localized string literal";
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJ_ADD).createImage();
	}

	@Override
	public Point getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return new Point (offset + reference.length(), 0);
	}

	@Override
	public int getRelevance() {
		// TODO Auto-generated method stub
		if (this.length == 0)
			return 1097;
		else
			return 97;
	}

}
