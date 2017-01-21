package org.eclipse.e4.tapiji.rcp.translator.ui.handler.window;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;


public class SaveHandler {

    @Execute
    public void execute(final EPartService partService, final MPart part) {
        partService.savePart(part, false);
        part.setDirty(false);
    }

    @CanExecute
    public boolean canExecute(@Optional final EPartService partService) {
        if (partService != null) {
            return partService.getActivePart().isDirty();
        }
        return false;
    }
}
