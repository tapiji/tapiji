package org.eclipselabs.e4.tapiji.translator.ui.treeviewer;

import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.TreeViewerContract.View;

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
        view.bla();
    }



    
    
    
    
    
}
