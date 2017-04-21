package org.eclipse.e4.tapiji.mylyn.ui.review.tasks;


import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.eclipse.e4.tapiji.mylyn.core.connector.translator.TranslatorConnectorConstants;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;


public class TasksView implements TasksContract.View {

    private TasksContract.Presenter presenter;

    @Inject
    public TasksView(TasksPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void onPostConstruct(final Composite parent, Shell shell) {

        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        AbstractRepositoryConnector bb = TasksUi.getRepositoryConnector(TranslatorConnectorConstants.CONNECTOR_KIND);

        bb.performQuery(new TaskRepository(TranslatorConnectorConstants.CONNECTOR_KIND, ""), null, new TaskDataCollector() {

            @Override
            public void accept(TaskData taskData) {
                @NonNull
                List<TaskAttribute> attributes = taskData.getAttributeMapper().getAttributesByType(taskData, TaskAttribute.COMMENT_NEW);

                attributes.stream().forEach(attr -> {
                    System.out.println("ATTRIBUTE: " + attr.toString());
                });
            }

        }, null, null);
        bb.canCreateRepository();
        //        TreeViewer viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        //        viewer.setContentProvider(new TasksContentProvider());
        //        viewer.getTree().setHeaderVisible(true);
        //
        //        TreeViewerColumn mainColumn = new TreeViewerColumn(viewer, SWT.NONE);
        //        mainColumn.getColumn().setText("Name");
        //        mainColumn.getColumn().setWidth(300);
        //        mainColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new TasksNameLabelProvider()));
        //
        //        TreeViewerColumn modifiedColumn = new TreeViewerColumn(viewer, SWT.NONE);
        //        modifiedColumn.getColumn().setText("Last Modified");
        //        modifiedColumn.getColumn().setWidth(100);
        //        modifiedColumn.getColumn().setAlignment(SWT.RIGHT);
        //        modifiedColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new TasksModifiedLabelProvider()));
        //
        //        viewer.setInput(File.listRoots());
    }

    @PreDestroy
    public void onDestroy() {

    }
}
