package com.essiembre.eclipse.rbe.api;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipselabs.tapiji.translator.rbe.model.tree.IKeyTreeItem;

import com.essiembre.eclipse.rbe.ui.editor.ResourceBundleEditor;
import com.essiembre.eclipse.rbe.ui.editor.i18n.I18nPage;

public class EditorUtil {
	
	public static IKeyTreeItem getSelectedKeyTreeItem (IWorkbenchPage page) {
		ResourceBundleEditor editor = (ResourceBundleEditor)page.getActiveEditor();
		if (editor.getSelectedPage() instanceof I18nPage) {
			I18nPage p = (I18nPage) editor.getSelectedPage();
			ISelection selection = p.getSelection();
			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
				return (IKeyTreeItem) ((IStructuredSelection) selection).getFirstElement();
			}
		}
		return null;
	}
}
