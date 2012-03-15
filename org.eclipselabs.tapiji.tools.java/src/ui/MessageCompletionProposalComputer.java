package ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipselabs.tapiji.tools.core.builder.InternationalizationNature;
import org.eclipselabs.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;

import ui.autocompletion.CreateResourceBundleProposal;
import ui.autocompletion.InsertResourceBundleReferenceProposal;
import ui.autocompletion.MessageCompletionProposal;
import ui.autocompletion.NewResourceBundleEntryProposal;
import ui.autocompletion.NoActionProposal;
import auditor.ResourceAuditVisitor;

public class MessageCompletionProposalComputer implements
		IJavaCompletionProposalComputer {

	private ResourceAuditVisitor csav;
	private IJavaElement je;
	private IJavaElement javaElement;
	private CompilationUnit cu;
	
	public MessageCompletionProposalComputer() {
		
	}

	
	
	@Override
	public List<ICompletionProposal> computeCompletionProposals(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();
		
		if (!InternationalizationNature.hasNature(((JavaContentAssistInvocationContext) context)
				.getCompilationUnit().getResource().getProject()) )
			return completions;
		
		try {
			CompletionContext coreContext = ((JavaContentAssistInvocationContext) context)
					.getCoreContext();
			int offset = coreContext.getOffset();
			
			if (javaElement == null)
				javaElement = ((JavaContentAssistInvocationContext) context)
								.getCompilationUnit().getElementAt(offset);
			
			String stringLiteralStart = new String(coreContext.getToken());

			// Check if the string literal is up to be written within the context
			// of a resource-bundle accessor method
			ResourceBundleManager manager = ResourceBundleManager.getManager(javaElement.getResource().getProject());

			if (csav == null) {
				csav = new ResourceAuditVisitor(null, 
						manager);
				je = JavaCore.create(javaElement.getResource());
			}	

			if (je instanceof ICompilationUnit) {
				// get the type of the currently loaded resource
				if (cu == null) {
					ITypeRoot typeRoot = ((ICompilationUnit) je);
					
					if (typeRoot == null)
						return null;
		
					// get a reference to the shared AST of the loaded CompilationUnit
					cu = SharedASTProvider.getAST(typeRoot,
					// do not wait for AST creation
							SharedASTProvider.WAIT_YES, null);
					cu.accept(csav);
				}
			
				//StringLiteralAnalyzer sla = new StringLiteralAnalyzer(javaElement.getResource());
				if (csav.getKeyAt(new Long(offset)) != null) {	
					completions.addAll(getResourceBundleCompletionProposals(offset, stringLiteralStart, manager, csav, javaElement.getResource()));
				} else if (csav.getRBReferenceAt(new Long(offset)) != null) {
					completions.addAll(getRBReferenceCompletionProposals(offset, stringLiteralStart, manager, je.getResource()));
				} else {
					completions.addAll(getBasicJavaCompletionProposals(offset, stringLiteralStart, manager, csav, je.getResource()));
				}
				if (completions.size() == 1)
					completions.add(new NoActionProposal());
			} else
				return null;
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return completions;
	}
	
	private Collection<ICompletionProposal> getRBReferenceCompletionProposals(
			int offset, String stringLiteralStart, ResourceBundleManager manager, IResource resource) {
		List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();
		int posStart = offset - stringLiteralStart.length();
		boolean hit = false;
		
		// Show a list of available resource bundles
		List<String> resourceBundles = manager.getResourceBundleIdentifiers();
		for (String rbName : resourceBundles) {
			if (rbName.startsWith(stringLiteralStart)) {
				if (rbName.equals(stringLiteralStart))
					hit = true;
				else
					completions.add (new MessageCompletionProposal (posStart, stringLiteralStart.length(), rbName, true));	
			}
		}
		
		if (!hit && stringLiteralStart.trim().length() > 0)
			completions.add(new CreateResourceBundleProposal(stringLiteralStart, resource, posStart, posStart + stringLiteralStart.length()));
		
		return completions;
	}

	protected List<ICompletionProposal> getBasicJavaCompletionProposals (
			int offset, String stringLiteralStart, 
			ResourceBundleManager manager, ResourceAuditVisitor csav, IResource resource) {
		List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();
		
		if (stringLiteralStart.length() == 0) {
			// If nothing has been entered
			completions.add(new InsertResourceBundleReferenceProposal(
					offset - stringLiteralStart.length(),
					stringLiteralStart.length(), 
					manager, resource, csav.getDefinedResourceBundles(offset)));
			completions.add(new NewResourceBundleEntryProposal(resource, stringLiteralStart, offset - stringLiteralStart.length(),
					stringLiteralStart.length(), false, manager, null/*, csav.getDefinedResourceBundles(offset)*/));
		} else {
//			if (!"Hallo Welt".equals(stringLiteralStart)) {
//				// If a part of a String has already been entered
//				if ("Hallo Welt".startsWith(stringLiteralStart) ) {
//					completions.add(new ValueOfResourceBundleProposal(offset - stringLiteralStart.length(),
//							stringLiteralStart.length(), "hello.world", "Hallo Welt!"));
//				}
				completions.add(new NewResourceBundleEntryProposal(resource, stringLiteralStart, offset - stringLiteralStart.length(),
						stringLiteralStart.length(), false, manager, null/*, csav.getDefinedResourceBundles(offset)*/));
			
//			}
		}
		return completions;
	}
	
	protected List<ICompletionProposal> getResourceBundleCompletionProposals (
			int offset, String stringLiteralStart, ResourceBundleManager manager,
			ResourceAuditVisitor csav, IResource resource) {
		List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();
		IRegion region = csav.getKeyAt(new Long(offset));
		String bundleName = csav.getBundleReference(region);
		int posStart = offset - stringLiteralStart.length();
		IMessagesBundleGroup bundleGroup = manager.getResourceBundle(bundleName);
		
		if (stringLiteralStart.length() > 0) {
			boolean hit = false;
			// If a part of a String has already been entered	
			for (String key : bundleGroup.getMessageKeys()) {
				if (key.toLowerCase().startsWith(stringLiteralStart.toLowerCase())) {
					if (!key.equals(stringLiteralStart))
						completions.add(new MessageCompletionProposal(posStart, 
							stringLiteralStart.length(), key, false));
					else
						hit = true;
				}
			}
			if (!hit)
				completions.add(new NewResourceBundleEntryProposal(resource, stringLiteralStart, offset - stringLiteralStart.length(),
						stringLiteralStart.length(), true, manager, bundleName/*, csav.getDefinedResourceBundles(offset)*/));
		} else {
			for (String key : bundleGroup.getMessageKeys()) {
				completions.add (new MessageCompletionProposal (posStart, stringLiteralStart.length(), key, false));
			}
			completions.add(new NewResourceBundleEntryProposal(resource, stringLiteralStart, offset - stringLiteralStart.length(),
					stringLiteralStart.length(), true, manager, bundleName/*, csav.getDefinedResourceBundles(offset)*/));
			
		}
		return completions;
	}

	@Override
	public List computeContextInformation(
			ContentAssistInvocationContext context, IProgressMonitor monitor) {
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void sessionEnded() {
		csav = null;
		javaElement = null;
		cu = null;
	}

	@Override
	public void sessionStarted() {
	}

}
