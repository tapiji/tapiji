package org.eclipse.babel.editor.tree.actions;

import org.eclipse.babel.core.message.tree.internal.KeyTreeNode;
import org.eclipse.babel.editor.internal.AbstractMessagesEditor;
import org.eclipse.babel.editor.refactoring.RenameKeyProcessor;
import org.eclipse.babel.editor.refactoring.RenameKeyWizard;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;

public class RenameKeyAction extends AbstractRenameKeyAction {

    public RenameKeyAction(AbstractMessagesEditor editor, TreeViewer treeViewer) {
        super(editor, treeViewer);
    }

    @Override
    public void run() {
        KeyTreeNode node = getNodeSelection();

        // Rename single item
        RenameKeyProcessor refactoring = new RenameKeyProcessor(node,
                getBundleGroup());

        RefactoringWizard wizard = new RenameKeyWizard(node, refactoring);
        try {
            RefactoringWizardOpenOperation operation = new RefactoringWizardOpenOperation(
                    wizard);
            operation.run(getShell(), "Introduce Indirection");
        } catch (InterruptedException exception) {
            // Do nothing
        }
    }
}
