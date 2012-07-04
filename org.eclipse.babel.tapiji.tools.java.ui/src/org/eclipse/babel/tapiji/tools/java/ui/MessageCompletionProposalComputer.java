/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.babel.core.message.IMessagesBundleGroup;
import org.eclipse.babel.tapiji.tools.core.Logger;
import org.eclipse.babel.tapiji.tools.core.ui.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.core.ui.builder.InternationalizationNature;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.CreateResourceBundleProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.InsertResourceBundleReferenceProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.MessageCompletionProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.NewResourceBundleEntryProposal;
import org.eclipse.babel.tapiji.tools.java.ui.autocompletion.NoActionProposal;
import org.eclipse.babel.tapiji.tools.java.ui.util.ASTutilsUI;
import org.eclipse.babel.tapiji.tools.java.util.ASTutils;
import org.eclipse.babel.tapiji.tools.java.visitor.ResourceAuditVisitor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class MessageCompletionProposalComputer implements
        IJavaCompletionProposalComputer {

	private ResourceAuditVisitor csav;
	private IResource resource;
	private CompilationUnit cu;
	private ResourceBundleManager manager;

	public MessageCompletionProposalComputer() {

	}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(
	        ContentAssistInvocationContext context, IProgressMonitor monitor) {

		List<ICompletionProposal> completions = new ArrayList<ICompletionProposal>();

		if (!InternationalizationNature
		        .hasNature(((JavaContentAssistInvocationContext) context)
		                .getCompilationUnit().getResource().getProject())) {
			return completions;
		}

		try {
			JavaContentAssistInvocationContext javaContext = ((JavaContentAssistInvocationContext) context);
			CompletionContext coreContext = javaContext.getCoreContext();

			int tokenStart = coreContext.getTokenStart();
			int tokenEnd = coreContext.getTokenEnd();
			int tokenOffset = coreContext.getOffset();
			boolean isStringLiteral = coreContext.getTokenKind() == CompletionContext.TOKEN_KIND_STRING_LITERAL;

			if (coreContext.getTokenKind() == CompletionContext.TOKEN_KIND_NAME
			        && (tokenEnd + 1) - tokenStart > 0) {
				return completions;
			}

			if (cu == null) {
				manager = ResourceBundleManager.getManager(javaContext
				        .getCompilationUnit().getResource().getProject());

				resource = javaContext.getCompilationUnit().getResource();

				csav = new ResourceAuditVisitor(null, manager.getProject()
				        .getName());

				cu = ASTutilsUI.getCompilationUnit(resource);

				cu.accept(csav);
			}
			
			if (tokenStart < 0) {
				// is string literal in front of cursor?
				StringLiteral strLit = ASTutils.getStringLiteralAtPos(cu, tokenOffset-1);
				if (strLit != null) {
					tokenStart = strLit.getStartPosition();
					tokenEnd = tokenStart + strLit.getLength() - 1;
					tokenOffset = tokenOffset-1;
					isStringLiteral = true;
				} else {
					tokenStart = tokenOffset;
					tokenEnd = tokenOffset;
				}
			}
			
			if (isStringLiteral) {
				tokenStart++;
			}

			tokenEnd = Math.max(tokenEnd, tokenStart);

			String fullToken = "";

			if (tokenStart < tokenEnd) {
				fullToken = context.getDocument().get(tokenStart,
				        tokenEnd - tokenStart);
			}

			// Check if the string literal is up to be written within the
			// context of a resource-bundle accessor method
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
			if (completions.size() == 1) {
				completions.add(new NoActionProposal());
			}

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
				if (rbName.equals(fullToken)) {
					hit = true;
				} else {
					completions.add(new MessageCompletionProposal(tokenStart,
					        tokenEnd - tokenStart, rbName, true));
				}
			}
		}

		if (!hit && fullToken.trim().length() > 0) {
			completions.add(new CreateResourceBundleProposal(fullToken,
			        resource, tokenStart, tokenEnd));
		}

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
			        tokenStart, tokenEnd - tokenStart, manager.getProject()
			                .getName(), resource, csav
			                .getDefinedResourceBundles(tokenOffset)));
			completions.add(new NewResourceBundleEntryProposal(resource,
			        tokenStart, tokenEnd, fullToken, isStringLiteral, false,
			        manager.getProject().getName(), null));
		} else {
			completions.add(new NewResourceBundleEntryProposal(resource,
			        tokenStart, tokenEnd, fullToken, isStringLiteral, false,
			        manager.getProject().getName(), null));
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
					if (!key.equals(fullToken)) {
						completions.add(new MessageCompletionProposal(
						        tokenStart, tokenEnd - tokenStart, key, false));
					} else {
						hit = true;
					}
				}
			}
			if (!hit) {
				completions.add(new NewResourceBundleEntryProposal(resource,
				        tokenStart, tokenEnd, fullToken, isStringLiteral, true,
				        manager.getProject().getName(), bundleName));

				// TODO: reference to existing resource
			}
		} else {
			for (String key : bundleGroup.getMessageKeys()) {
				completions.add(new MessageCompletionProposal(tokenStart,
				        tokenEnd - tokenStart, key, false));
			}
			completions.add(new NewResourceBundleEntryProposal(resource,
			        tokenStart, tokenEnd, fullToken, isStringLiteral, true,
			        manager.getProject().getName(), bundleName));

		}
		return completions;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void sessionEnded() {
		cu = null;
		csav = null;
		resource = null;
		manager = null;
	}

	@Override
	public void sessionStarted() {

	}

	@Override
	public List<IContextInformation> computeContextInformation(
	        ContentAssistInvocationContext arg0, IProgressMonitor arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
