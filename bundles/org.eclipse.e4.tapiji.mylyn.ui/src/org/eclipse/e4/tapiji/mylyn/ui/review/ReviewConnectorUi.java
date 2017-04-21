package org.eclipse.e4.tapiji.mylyn.ui.review;


import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;


public class ReviewConnectorUi extends AbstractRepositoryConnectorUi {

    @Override
    public @NonNull String getConnectorKind() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull ITaskRepositoryPage getSettingsPage(@Nullable TaskRepository repository) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull IWizard getQueryWizard(@NonNull TaskRepository repository, @Nullable IRepositoryQuery query) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @NonNull IWizard getNewTaskWizard(@NonNull TaskRepository repository, @Nullable ITaskMapping selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasSearchPage() {
        // TODO Auto-generated method stub
        return false;
    }

}
