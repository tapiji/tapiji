package org.eclipse.babel.editor.api;

import org.eclipse.babel.editor.MessagesEditor;
import org.eclipse.babel.editor.i18n.I18NPage;
import org.eclipse.babel.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;

public class EditorUtil {
	
    public static IKeyTreeNode getSelectedKeyTreeNode (IWorkbenchPage page) {
        MessagesEditor editor = (MessagesEditor)page.getActiveEditor();
        if (editor.getSelectedPage() instanceof I18NPage) {
            I18NPage p = (I18NPage) editor.getSelectedPage();
            ISelection selection = p.getSelection();
            if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
                return (IKeyTreeNode) ((IStructuredSelection) selection).getFirstElement();
            }
        }
        return null;
    }
}
