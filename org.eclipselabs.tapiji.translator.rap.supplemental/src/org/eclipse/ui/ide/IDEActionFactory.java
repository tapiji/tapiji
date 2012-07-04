package org.eclipse.ui.ide;

import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

/**
 * IDEActionFactory, only copied parts which are needed in babel editor, to get it working in RAP
 * 
 * @author Matthias Lettmayer
 *
 */
public class IDEActionFactory {
	 
	/**
     * IDE-specific workbench action (id: "bookmark", commandId: "org.eclipse.ui.edit.addBookmark"): Add bookmark.
     * This action is a {@link RetargetAction}. This action maintains its enablement state.
     */
    public static final ActionFactory BOOKMARK = new ActionFactory("bookmark", //$NON-NLS-1$
    		IWorkbenchCommandConstants.EDIT_ADD_BOOKMARK) { 
        /* (non-javadoc) method declared on ActionFactory */
        public IWorkbenchAction create(IWorkbenchWindow window) {
            if (window == null) {
                throw new IllegalArgumentException();
            }
            RetargetAction action = new RetargetAction(getId(), IDEWorkbenchMessages.Workbench_addBookmark);
            action.setToolTipText(IDEWorkbenchMessages.Workbench_addBookmarkToolTip);
            window.getPartService().addPartListener(action);
            action.setActionDefinitionId(getCommandId());
            return action;
        }
    };

}
