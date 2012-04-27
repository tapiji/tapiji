/*******************************************************************************
 * Copyright (c) 2012 TapiJI.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.babel.tapiji.tools.java.ui;

import org.eclipse.babel.tapiji.tools.core.model.manager.ResourceBundleManager;
import org.eclipse.babel.tapiji.tools.java.ui.util.ASTutilsUI;
import org.eclipse.babel.tapiji.tools.java.visitor.ResourceAuditVisitor;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class ConstantStringHover implements IJavaEditorTextHover {

	IEditorPart editor = null;
	ResourceAuditVisitor csf = null;
	ResourceBundleManager manager = null;

	@Override
	public void setEditor(IEditorPart editor) {
		this.editor = editor;
		initConstantStringAuditor();
	}

	protected void initConstantStringAuditor() {
		// parse editor content and extract resource-bundle access strings

		// get the type of the currently loaded resource
		ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editor
		        .getEditorInput());

		if (typeRoot == null) {
			return;
		}

		CompilationUnit cu = ASTutilsUI.getCompilationUnit(typeRoot);

		if (cu == null) {
			return;
		}

		manager = ResourceBundleManager.getManager(cu.getJavaElement()
		        .getResource().getProject());

		// determine the element at the position of the cursur
		csf = new ResourceAuditVisitor(null, manager.getProject().getName());
		cu.accept(csf);
	}

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		initConstantStringAuditor();
		if (hoverRegion == null) {
			return null;
		}

		// get region for string literals
		hoverRegion = getHoverRegion(textViewer, hoverRegion.getOffset());

		if (hoverRegion == null) {
			return null;
		}

		String bundleName = csf.getBundleReference(hoverRegion);
		String key = csf.getKeyAt(hoverRegion);

		String hoverText = manager.getKeyHoverString(bundleName, key);
		if (hoverText == null || hoverText.equals("")) {
			return null;
		} else {
			return hoverText;
		}
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		if (editor == null) {
			return null;
		}

		// Retrieve the property key at this position. Otherwise, null is
		// returned.
		return csf.getKeyAt(Long.valueOf(offset));
	}

}
