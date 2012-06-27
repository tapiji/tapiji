package org.eclipselabs.tapiji.translator.rap.babel.editor.api;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipselabs.tapiji.translator.rap.babel.core.message.tree.IKeyTreeNode;
import org.eclipselabs.tapiji.translator.rap.babel.editor.i18n.I18NPage;
import org.eclipselabs.tapiji.translator.rap.babel.editor.internal.MessagesEditor;

/**
 * Util class for editor operations.
 * <br><br>
 * 
 * @author Alexej Strelzow
 */
public class EditorUtil {
	
	/**
	 * @param page The {@link IWorkbenchPage}
	 * @return The selected {@link IKeyTreeNode} of the page.
	 */
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
