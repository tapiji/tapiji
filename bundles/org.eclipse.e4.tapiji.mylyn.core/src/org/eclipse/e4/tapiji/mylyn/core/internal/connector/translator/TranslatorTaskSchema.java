package org.eclipse.e4.tapiji.mylyn.core.internal.connector.translator;


import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;


public class TranslatorTaskSchema extends AbstractTaskSchema {

    private static final TranslatorTaskSchema INSTANCE = new TranslatorTaskSchema();

    private final DefaultTaskSchema parent = DefaultTaskSchema.getInstance();

    public final Field DESCRIPTION = inheritFrom(parent.SUMMARY).create();

    public final Field CREATED = inheritFrom(parent.DATE_CREATION).create();

    public final Field MODIFIED = inheritFrom(parent.DATE_MODIFICATION).create();

    public final Field KIND = inheritFrom(parent.TASK_KIND).create();

    private TranslatorTaskSchema() {
        super();
    }

    public static TranslatorTaskSchema getInstance() {
        return INSTANCE;
    }
}
