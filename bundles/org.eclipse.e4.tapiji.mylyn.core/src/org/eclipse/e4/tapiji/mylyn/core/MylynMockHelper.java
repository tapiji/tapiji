package org.eclipse.e4.tapiji.mylyn.core;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.e4.tapiji.mylyn.core.connector.translator.TranslatorConnectorConstants;
import org.eclipse.e4.tapiji.mylyn.core.internal.connector.translator.TranslatorTaskAttributeMapper;
import org.eclipse.e4.tapiji.mylyn.core.internal.connector.translator.TranslatorTaskSchema;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;


public class MylynMockHelper {

    static List<TaskData> tasks;
    static {

    }

    public static List<TaskData> getTaskDataList(TranslatorTaskAttributeMapper mapper) {

        TaskData taskData = new TaskData(mapper, TranslatorConnectorConstants.CONNECTOR_KIND, "http://ww.tapiji.cm", "1");

        //taskData.getRoot().createAttribute(TaskAttribute.COMMENT_NEW).getMetaData().setType(TaskAttribute.TYPE_LONG_RICH_TEXT).setReadOnly(false);
        TaskAttribute attribute = taskData.getRoot().createAttribute(TaskAttribute.SUMMARY);
        attribute.getMetaData().setReadOnly(false).setType(TaskAttribute.TYPE_SHORT_RICH_TEXT).setLabel("Summary:");

        attribute = taskData.getRoot().createAttribute(TaskAttribute.COMMENT_NEW);
        attribute.getMetaData().setReadOnly(false).setType(TaskAttribute.TYPE_LONG_RICH_TEXT).setLabel("New Comment:");

        TranslatorTaskSchema schema = TranslatorTaskSchema.getInstance();
        schema.initialize(taskData);

        TaskCommentMapper commentMapper = new TaskCommentMapper();
        commentMapper.setText("sdadsdadadasdasdasdsd");
        commentMapper.setNumber(1);
        commentMapper.setCommentId("1");
        commentMapper.setAuthor(createPerson("dsdasdasd", new TaskRepository(TranslatorConnectorConstants.CONNECTOR_KIND, "http://ww.tapiji.cm")));
        commentMapper.setCreationDate(new Date(1254 * 1000));

        TaskAttribute commentAttribute = taskData.getRoot().createAttribute(TaskAttribute.PREFIX_COMMENT + 1);
        commentMapper.applyTo(commentAttribute);

        setAttributeValue(taskData, schema.CREATED, Long.toString(new Date(11111111 * 1000).getTime()));
        tasks = new ArrayList<>();
        tasks.add(taskData);
        tasks.add(new TaskData(mapper, TranslatorConnectorConstants.CONNECTOR_KIND, "http://ww.tapiji.cm", "2"));
        return tasks;
    }

    private static IRepositoryPerson createPerson(String userName, TaskRepository repository) {
        IRepositoryPerson person = repository.createPerson(userName);
        person.setName(userName);
        return person;
    }

    /**
     * Convenience method to set the value of a given Attribute in the given {@link TaskData}.
     */
    private static void setAttributeValue(TaskData data, Field attr, String value) {
        TaskAttribute attribute = data.getRoot().getAttribute(attr.getKey());
        setAttributeValue(attribute, value);
    }

    /**
     * Helper method for setting attribute values (mostly because nulls aren't allowed in
     * attribute values).
     */
    private static void setAttributeValue(TaskAttribute attribute, String value) {
        if (value != null) {
            attribute.setValue(value);
        }
    }

}
