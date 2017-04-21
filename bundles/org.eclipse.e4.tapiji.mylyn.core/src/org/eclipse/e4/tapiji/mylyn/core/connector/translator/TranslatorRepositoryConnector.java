package org.eclipse.e4.tapiji.mylyn.core.connector.translator;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;


public class TranslatorRepositoryConnector extends AbstractRepositoryConnector {

    @Override
    public boolean canCreateNewTask(@NonNull TaskRepository repository) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canCreateTaskFromKey(@NonNull TaskRepository repository) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public @NonNull String getConnectorKind() {
        return TranslatorConnectorConstants.CONNECTOR_KIND;
    }

    @Override
    public @NonNull String getLabel() {
        // TODO Auto-generated method stub
        return "TEST";
    }

    @Override
    public @Nullable String getRepositoryUrlFromTaskUrl(@NonNull String taskUrl) {
        System.out.println("getTaskIdFromTaskUrl");
        return "http::BLUB";
    }

    @Override
    public @NonNull TaskData getTaskData(@NonNull TaskRepository repository, @NonNull String taskId, @NonNull IProgressMonitor monitor) throws CoreException {
        //  List<TaskData> data = MylynMockHelper.getTaskDataList(new ReviewTaskAttributeMapper(repository));
        return null;
    }

    @Override
    public @Nullable String getTaskIdFromTaskUrl(@NonNull String taskUrl) {
        System.out.println("getTaskIdFromTaskUrl");
        return "http::BLUB";
    }

    @Override
    public @Nullable String getTaskUrl(@NonNull String repositoryUrl, @NonNull String taskIdOrKey) {
        System.out.println("getTaskUrl");
        return "http::BLUB";
    }

    @Override
    public boolean hasTaskChanged(@NonNull TaskRepository taskRepository, @NonNull ITask task, @NonNull TaskData taskData) {
        System.out.println("hasTaskChanged");
        return false;
    }

    @Override
    public @NonNull IStatus performQuery(@NonNull TaskRepository repository, @NonNull IRepositoryQuery query, @NonNull TaskDataCollector collector, @Nullable ISynchronizationSession session, @NonNull IProgressMonitor monitor) {
        System.out.println("performQuery");

        return Status.OK_STATUS;
    }

    @Override
    public void updateRepositoryConfiguration(@NonNull TaskRepository taskRepository, @NonNull IProgressMonitor monitor) throws CoreException {
        System.out.println("updateRepositoryConfiguration");
    }

    @Override
    public void updateTaskFromTaskData(@NonNull TaskRepository taskRepository, @NonNull ITask task, @NonNull TaskData taskData) {
        System.out.println("updateTaskFromTaskData");
    }

}
