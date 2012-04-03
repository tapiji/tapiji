package org.eclipse.babel.tapiji.tools.java.ui.autocompletion;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.dialogs.CreateResourceBundleEntryDialog;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


public class NewResourceBundleEntryProposal implements IJavaCompletionProposal {

	private int startPos;
	private int endPos;
	private String value;
	private boolean bundleContext;
	private ResourceBundleManager manager;
	private IResource resource;
	private String bundleName;
	private String reference;

	public NewResourceBundleEntryProposal(IResource resource, int startPos, boolean bundleContext, 
			ResourceBundleManager manager, String bundleName) {
		
		CompilationUnit cu = ASTutils.getCompilationUnit(resource);
		
		StringLiteral string = ASTutils.getStringAtPos(cu, startPos);
				
		this.startPos = string.getStartPosition()+1;
		this.endPos = string.getLength()-2;
		this.bundleContext = bundleContext;
		this.manager = manager;
		this.value = string.getLiteralValue();
		this.resource = resource;
		this.bundleName = bundleName;
	}

	@Override
	public void apply(IDocument document) {
		
		CompilationUnit cu = ASTutils.getCompilationUnit(resource);
		
		StringLiteral lit = ASTutils.getStringAtPos(cu, startPos);
		
		CreateResourceBundleEntryDialog dialog = new CreateResourceBundleEntryDialog(
				Display.getDefault().getActiveShell(),
				manager,
				bundleContext ? value : "",
				bundleContext ? "" : value,
				bundleName == null ? "" : bundleName,
				"");
		if (dialog.open() != InputDialog.OK)
			return;
		
		
		String resourceBundleId = dialog.getSelectedResourceBundle();
		String key = dialog.getSelectedKey();
		
		try {
			if (!bundleContext)
				reference = ASTutils.insertNewBundleRef(document, resource, startPos, endPos, resourceBundleId, key);
			else {
				document.replace(startPos, endPos, key);
				reference = key + "\"";
			}
			ResourceBundleManager.refreshResource(resource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getAdditionalProposalInfo() {
		if (value != null && value.length() > 0) {
			return "Exports the focused string literal into a Java Resource-Bundle. This action results " + 
					"in a Resource-Bundle reference!";
		} else
			return "";
	}

	@Override
	public IContextInformation getContextInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayString() {
		String displayStr = "";
		if (bundleContext)
			displayStr = "Create a new resource-bundle-entry";
		else
			displayStr = "Create a new localized string literal";
		
		if (value != null && value.length() > 0)
			displayStr += " for '" + value + "'";
		
		return displayStr;
	}

	@Override
	public Image getImage() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
				ISharedImages.IMG_OBJ_ADD).createImage();
	}

	@Override
	public Point getSelection(IDocument document) {
		int refLength = reference == null ? 0 : reference.length() -1;
		return new Point (startPos + refLength, 0);
	}

	@Override
	public int getRelevance() {
		// TODO Auto-generated method stub
		if (this.value.trim().length() == 0)
			return 1096;
		else
			return 1096;
	}

}
