package org.eclipse.babel.tapiji.tools.java.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.builder.InternationalizationNature;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.java.auditor.ResourceAuditVisitor;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.CreateResourceBundleProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.InsertResourceBundleReferenceProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.MessageCompletionProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.NewResourceBundleEntryProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.NoActionProposal;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class MessageCompletionProposalComputer implements
	IJavaCompletionProposalComputer {

    public MessageCompletionProposalComputer() {

    }

    @Override
    public List<ICompletionProposal> computeCompletionProposals(
	    ContentAssistInvocationContext context, IProgressMonitor monitor) {

	List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();
	ResourceAuditVisitor csav;

	if (!InternationalizationNature
		.hasNature(((JavaContentAssistInvocationContext) context)
			.getCompilationUnit().getResource().getProject()))
	    return completions;

	try {
	    JavaContentAssistInvocationContext javaContext = ((JavaContentAssistInvocationContext) context);
	    CompletionContext coreContext = javaContext.getCoreContext();

	    int tokenStart = coreContext.getTokenStart();
	    int tokenEnd = coreContext.getTokenEnd();
	    int tokenOffset = coreContext.getOffset();
	    boolean isStringLiteral = coreContext.getTokenKind() == CompletionContext.TOKEN_KIND_STRING_LITERAL;

	    if (coreContext.getTokenKind() == CompletionContext.TOKEN_KIND_NAME
		    && (tokenEnd + 1) - tokenStart > 0)
		return completions;

	    if (isStringLiteral)
		tokenStart++;

	    if (tokenStart < 0) {
		tokenStart = tokenOffset;
		tokenEnd = tokenOffset;
	    }

	    tokenEnd = Math.max(tokenEnd, tokenStart);

	    String fullToken = "";

	    if (tokenStart < tokenEnd)
		fullToken = context.getDocument().get(tokenStart,
			tokenEnd - tokenStart);

	    // Check if the string literal is up to be written within the
	    // context of a resource-bundle accessor method
	    ResourceBundleManager manager = ResourceBundleManager
		    .getManager(javaContext.getCompilationUnit().getResource()
			    .getProject());

	    IResource resource = javaContext.getCompilationUnit().getResource();

	    csav = new ResourceAuditVisitor(null, manager);

	    CompilationUnit cu = ASTutils.getCompilationUnit(resource);

	    cu.accept(csav);

	    if (csav.getKeyAt(new Long(tokenOffset)) != null && isStringLiteral) {
		completions.addAll(getResourceBundleCompletionProposals(
			tokenStart, tokenEnd, tokenOffset, isStringLiteral,
			fullToken, manager, csav, resource));
	    } else if (csav.getRBReferenceAt(new Long(tokenOffset)) != null
		    && isStringLiteral) {
		completions.addAll(getRBReferenceCompletionProposals(
			tokenStart, tokenEnd, fullToken, isStringLiteral,
			manager, resource));
	    } else {
		completions.addAll(getBasicJavaCompletionProposals(tokenStart,
			tokenEnd, tokenOffset, fullToken, isStringLiteral,
			manager, csav, resource));
	    }
	    if (completions.size() == 1)
		completions.add(new NoActionProposal());

	} catch (Exception e) {
	    Logger.logError(e);
	}
	return completions;
    }

    private Collection<ICompletionProposal> getRBReferenceCompletionProposals(
	    int tokenStart, int tokenEnd, String fullToken,
	    boolean isStringLiteral, ResourceBundleManager manager,
	    IResource resource) {
	List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();
	boolean hit = false;

	// Show a list of available resource bundles
	List<String> resourceBundles = manager.getResourceBundleIdentifiers();
	for (String rbName : resourceBundles) {
	    if (rbName.startsWith(fullToken)) {
		if (rbName.equals(fullToken))
		    hit = true;
		else
		    completions.add(new MessageCompletionProposal(tokenStart,
			    tokenEnd - tokenStart, rbName, true));
	    }
	}

	if (!hit && fullToken.trim().length() > 0)
	    completions.add(new CreateResourceBundleProposal(fullToken,
		    resource, tokenStart, tokenEnd));

	return completions;
    }

    protected List<ICompletionProposal> getBasicJavaCompletionProposals(
	    int tokenStart, int tokenEnd, int tokenOffset, String fullToken,
	    boolean isStringLiteral, ResourceBundleManager manager,
	    ResourceAuditVisitor csav, IResource resource) {
	List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();

	if (fullToken.length() == 0) {
	    // If nothing has been entered
	    completions.add(new InsertResourceBundleReferenceProposal(
		    tokenStart, tokenEnd - tokenStart, manager, resource, csav
			    .getDefinedResourceBundles(tokenOffset)));
	    completions.add(new NewResourceBundleEntryProposal(resource,
		    tokenStart, tokenEnd, fullToken, isStringLiteral, false,
		    manager, null));
	} else {
	    completions.add(new NewResourceBundleEntryProposal(resource,
		    tokenStart, tokenEnd, fullToken, isStringLiteral, false,
		    manager, null));
	}
	return completions;
    }

    protected List<ICompletionProposal> getResourceBundleCompletionProposals(
	    int tokenStart, int tokenEnd, int tokenOffset,
	    boolean isStringLiteral, String fullToken,
	    ResourceBundleManager manager, ResourceAuditVisitor csav,
	    IResource resource) {

	List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();
	IRegion region = csav.getKeyAt(new Long(tokenOffset));
	String bundleName = csav.getBundleReference(region);
	IMessagesBundleGroup bundleGroup = manager
		.getResourceBundle(bundleName);

	if (fullToken.length() > 0) {
	    boolean hit = false;
	    // If a part of a String has already been entered
	    for (String key : bundleGroup.getMessageKeys()) {
		if (key.toLowerCase().startsWith(fullToken)) {
		    if (!key.equals(fullToken))
			completions.add(new MessageCompletionProposal(
				tokenStart, tokenEnd - tokenStart, key, false));
		    else
			hit = true;
		}
	    }
	    if (!hit) {
		completions.add(new NewResourceBundleEntryProposal(resource,
			tokenStart, tokenEnd, fullToken, isStringLiteral, true,
			manager, bundleName));

		// TODO: reference to existing resource
	    }
	} else {
	    for (String key : bundleGroup.getMessageKeys()) {
		completions.add(new MessageCompletionProposal(tokenStart,
			tokenEnd - tokenStart, key, false));
	    }
	    completions.add(new NewResourceBundleEntryProposal(resource,
		    tokenStart, tokenEnd, fullToken, isStringLiteral, true,
		    manager, bundleName));

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

    }

    @Override
    public void sessionStarted() {

    }

}
