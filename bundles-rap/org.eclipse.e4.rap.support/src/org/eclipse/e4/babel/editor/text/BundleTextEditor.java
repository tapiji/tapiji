package org.eclipse.e4.babel.editor.text;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.e4.babel.editor.text.file.IPropertyResource;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public final class BundleTextEditor extends Composite {
	private CTabItem tab;

	public BundleTextEditor(final CTabFolder parent, MDirtyable dirty, IPropertyResource sourceViewerDocument) {
		super(parent, 0);

	}

	@Override
	public void dispose() {

		super.dispose();
	}

	public IPropertyResource getSourceViewerDocument() {
		return null;
	}

	public SourceViewer getSourceViewer() {
		return null;
	}

	public void setTab(CTabItem createTab) {
		this.tab = createTab;
	}

	public CTabItem getTab() {
		return this.tab;
	}
}
