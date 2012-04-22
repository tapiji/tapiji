/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Reiterer - initial API and implementation
 ******************************************************************************/
package ui;

import org.eclipse.babel.tapiji.tools.core.builder.InternationalizationNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IStructuredDocumentContextResolverFactory;
import org.eclipse.jst.jsf.context.resolver.structureddocument.IWorkspaceContextResolver;
import org.eclipse.jst.jsf.context.resolver.structureddocument.internal.ITextRegionContextResolver;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContext;
import org.eclipse.jst.jsf.context.structureddocument.IStructuredDocumentContextFactory;
import org.eclipse.jst.jsp.core.internal.regions.DOMJSPRegionContexts;

import util.ELUtils;
import auditor.JSFResourceBundleDetector;

/**
 * This class creates hovers for ISymbols in an el expression that have a
 * detailedDescription.
 */
public class JSFELMessageHover implements ITextHover {

	private String expressionValue = "";
	private IProject project = null;

	public final String getHoverInfo(final ITextViewer textViewer,
	        final IRegion hoverRegion) {
		String bundleName = JSFResourceBundleDetector.resolveResourceBundleId(
		        textViewer.getDocument(), JSFResourceBundleDetector
		                .getBundleVariableName(expressionValue));
		String resourceKey = JSFResourceBundleDetector
		        .getResourceKey(expressionValue);

		return ELUtils.getResource(project, bundleName, resourceKey);
	}

	public final IRegion getHoverRegion(final ITextViewer textViewer,
	        final int documentPosition) {
		final IStructuredDocumentContext context = IStructuredDocumentContextFactory.INSTANCE
		        .getContext(textViewer, documentPosition);

		IWorkspaceContextResolver workspaceResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		        .getWorkspaceContextResolver(context);

		project = workspaceResolver.getProject();

		if (project != null) {
			if (!InternationalizationNature.hasNature(project))
				return null;
		} else
			return null;

		final ITextRegionContextResolver symbolResolver = IStructuredDocumentContextResolverFactory.INSTANCE
		        .getTextRegionResolver(context);

		if (!symbolResolver.getRegionType().equals(
		        DOMJSPRegionContexts.JSP_VBL_CONTENT))
			return null;
		expressionValue = symbolResolver.getRegionText();

		return new Region(symbolResolver.getStartOffset(),
		        symbolResolver.getLength());

	}

}
