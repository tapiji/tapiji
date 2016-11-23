package org.eclipselabs.e4.tapiji.translator.ui.window;


import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;


public class SaveAllHandler {

    private static final String TAG = SaveAllHandler.class.getSimpleName();

    @Execute
    public void execute(final EPartService partService) {
        System.out.println("Execute: " + TAG);
        partService.getDirtyParts().forEach(part-> {
        	partService.savePart(part, false);
        	part.setDirty(false);
        });
    }

    @CanExecute
    public boolean canExecute(@Optional final EPartService partService) {
        if (partService != null) {
            return !partService.getDirtyParts().isEmpty();
        }
        return false;
    }
}
