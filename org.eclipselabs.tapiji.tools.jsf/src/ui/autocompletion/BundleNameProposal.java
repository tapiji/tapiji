/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package ui.autocompletion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.babel.tapiji.tools.core.builder.InternationalizationNature;
import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IDOMContextResolver;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IStructuredDocumentContextResolverFactory;
import org.eclipse.jst.jsf.context.resolver.structureddocument.ITaglibContextResolver;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IWorkspaceContextResolver;
import org.eclipse.jst.jsf.context.resolver.structureddocument.internal.ITextRegionContextResolver;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContext;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContextFactory;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;


public class BundleNameProposal implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
				.getContext(viewer, offset);

		IWorkspaceContextResolver workspaceResolver = IStructuredDocumentContextResolverFactory.INSTANCE
				.getWorkspaceContextResolver(context);

		IProject project = workspaceResolver.getProject();
		IResource resource = workspaceResolver.getResource();

		if (project != null) {
			if (!InternationalizationNature.hasNature(project))
				return proposals.toArray(new ICompletionProposal[proposals
						.size()]);
		} else
			return proposals.toArray(new ICompletionProposal[proposals.size()]);

		addBundleProposals(proposals, context, offset, viewer.getDocument(), 
				resource);
		
		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	private void addBundleProposals(List<ICompletionProposal> proposals, 
			final IStructuredDocumentContext context, 
			int startPos, 
			IDocument document,
			IResource resource) {
		final ITextRegionContextResolver resolver = IStructuredDocumentContextResolverFactory.INSTANCE
				.getTextRegionResolver(context);

		if (resolver != null) {
			final String regionType = resolver.getRegionType();
			startPos = resolver.getStartOffset()+1;
			
			if (regionType != null
					&& regionType
							.equals(DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE)) {

				final ITaglibContextResolver tlResolver = IStructuredDocumentContextResolverFactory.INSTANCE
						.getTaglibContextResolver(context);

				if (tlResolver != null) {
					Attr attr = getAttribute(context);
					String startString = attr.getValue();
					
					int length = startString.length();
					
					if (attr != null) {
						Node tagElement = attr.getOwnerElement();
						if (tagElement == null) 
							return;
						
						String nodeName = tagElement.getNodeName();
						if (nodeName.substring(nodeName.indexOf(":")+1).toLowerCase().equals("loadbundle")) {
							ResourceBundleManager manager = ResourceBundleManager.getManager(resource.getProject());
							for (String id : manager.getResourceBundleIdentifiers()) {
								if (id.startsWith(startString) && id.length() != startString.length()) {
									proposals.add(new ui.autocompletion.MessageCompletionProposal(
											startPos, length, id, true));
								}
							}
						}
					}
				}
			}
		}
	}

	private Attr getAttribute(IStructuredDocumentContext context) {
		final IDOMContextResolver domResolver = IStructuredDocumentContextResolverFactory.INSTANCE
				.getDOMContextResolver(context);

		if (domResolver != null) {
			final Node curNode = domResolver.getNode();

			if (curNode instanceof Attr) {
				return (Attr) curNode;
			}
		}
		return null;

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
