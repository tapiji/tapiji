package org.eclipse.e4.tapiji.mylyn.core.internal.connector.translator;


import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;


public class TranslatorTaskDataHandler extends AbstractTaskDataHandler {

    @Override
    public TaskAttributeMapper getAttributeMapper(@NonNull TaskRepository repository) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean initializeTaskData(@NonNull TaskRepository repository, @NonNull TaskData data, @Nullable ITaskMapping initializationData, @Nullable IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RepositoryResponse postTaskData(@NonNull TaskRepository repository, @NonNull TaskData taskData, @Nullable Set<TaskAttribute> oldAttributes, @Nullable IProgressMonitor monitor) throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

}
