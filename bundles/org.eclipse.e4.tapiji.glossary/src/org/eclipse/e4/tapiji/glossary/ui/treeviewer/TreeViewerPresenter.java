package org.eclipse.e4.tapiji.glossary.ui.treeviewer;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.tapiji.glossary.model.Glossary;
import org.eclipse.e4.tapiji.glossary.ui.treeviewer.TreeViewerContract.View;

@Creatable
@Singleton
public class TreeViewerPresenter implements TreeViewerContract.Presenter{

    @Inject 
    private IGlossaryService glossaryService;
    
    private TreeViewerContract.View view;
    
    @Override
    public void init() {

        
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setView(View view) {
        this.view = view;

    }



    @Override
    public IGlossaryService getGlossary() {
        return glossaryService;
    }

    public void updateGlossary(final Glossary glossary) {
        new Job("Update Glossary") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                glossaryService.updateGlossary(glossary);
                return Status.OK_STATUS;
            }
        }.schedule();
    }
}
