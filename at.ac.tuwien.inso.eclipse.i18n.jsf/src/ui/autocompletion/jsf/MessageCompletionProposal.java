package ui.autocompletion.jsf;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IStructuredDocumentContextResolverFactory;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IWorkspaceContextResolver;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContext;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContextFactory;

import ui.autocompletion.NewResourceBundleEntryProposal;

import at.ac.tuwien.inso.eclipse.i18n.builder.InternationalizationNature;
import at.ac.tuwien.inso.eclipse.i18n.model.manager.ResourceBundleManager;
import auditor.JSFResourceBundleDetector;

public class MessageCompletionProposal implements IContentAssistProcessor  {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		List <ICompletionProposal> proposals = new ArrayList<ICompletionProposal> ();
		
		final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
			.getContext(viewer, offset);
	
		IWorkspaceContextResolver workspaceResolver =  IStructuredDocumentContextResolverFactory.INSTANCE
			.getWorkspaceContextResolver(context);
	
		IProject project = workspaceResolver.getProject();
		IResource resource = workspaceResolver.getResource();
		
		if (project != null) {
			if (!InternationalizationNature.hasNature(project))
				return proposals.toArray(new CompletionProposal[proposals.size()]);
		} else
			return proposals.toArray(new CompletionProposal[proposals.size()]);
		
		// Compute proposals
		String expression = getProposalPrefix (viewer.getDocument(), offset); 
		String bundleId = JSFResourceBundleDetector.resolveResourceBundleId(viewer.getDocument(), 
				JSFResourceBundleDetector.getBundleVariableName(expression));
		String key = JSFResourceBundleDetector.getResourceKey(expression);
		
		if (expression.trim().length() > 0 && 
			bundleId.trim().length() > 0 &&
			isNonExistingKey (project, bundleId, key)) {
			// Add 'New Resource' proposal
			int startpos = offset - key.length();
			int length = key.length();
			
			proposals.add(new NewResourceBundleEntryProposal(resource, key, startpos, length, ResourceBundleManager.getManager(project)
					, bundleId));
		}
		
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	private String getProposalPrefix(IDocument document, int offset) {
		String content = document.get().substring(0, offset);
		int expIntro = content.lastIndexOf("#{");
		
		return content.substring(expIntro+2, offset);
	}

	protected boolean isNonExistingKey (IProject project, String bundleId, String key) {
		ResourceBundleManager manager = ResourceBundleManager.getManager(project);
		
		return !manager.isResourceExisting(bundleId, key);
	}
	
	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

}
