package org.eclipse.e4.tapiji.mylyn.core.internal.connector.translator;


import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;


public class TranslatorTaskAttributeMapper extends TaskAttributeMapper {

    public TranslatorTaskAttributeMapper(@NonNull TaskRepository taskRepository) {
        super(taskRepository);
    }

}
