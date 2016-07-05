package org.eclipselabs.e4.tapiji.translator.ui.window;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;


public class SaveHandler {

    private static final String TAG = SaveHandler.class.getSimpleName();

    @Execute
    public void execute(final EPartService partService, final MPart part) {
        System.out.println("Execute: " + TAG);
        partService.savePart(part, false);
    }

    @CanExecute
    public boolean canExecute(@Optional final EPartService partService) {
        if (partService != null) {
            return partService.getActivePart().isDirty();
        }
        return false;
    }
}
