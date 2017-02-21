package org.eclipse.e4.tapiji.git.ui.panel.left.tag;


import java.util.List;
import javax.inject.Inject;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.resource.ITapijiResourceProvider;
import org.eclipse.e4.tapiji.resource.TapijiResourceConstants;
import org.eclipse.e4.tapiji.utils.ColorUtils;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


@Creatable
public class TagView implements TagContract.View {

    @Inject
    UISynchronize sync;

    @Inject
    Shell shell;

    @Inject
    TagPresenter presenter;

    @Inject
    ITapijiResourceProvider resourceProvider;

    private Composite composite;

    private Table table;

    private Label lblTagCnt;

    public void createPartControl(Composite parent, ScrolledComposite scrolledComposite) {
        presenter.setView(this);
        GridLayout gl_parent = new GridLayout(1, false);
        gl_parent.horizontalSpacing = 0;
        gl_parent.verticalSpacing = 0;
        gl_parent.marginWidth = 0;
        gl_parent.marginHeight = 0;
        parent.setLayout(gl_parent);

        Composite composite_1 = new Composite(parent, SWT.NONE);
        GridLayout gl_composite_1 = new GridLayout(1, false);
        gl_composite_1.verticalSpacing = 0;
        gl_composite_1.horizontalSpacing = 0;
        gl_composite_1.marginWidth = 0;
        gl_composite_1.marginHeight = 0;
        composite_1.setLayout(gl_composite_1);
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite composite = new Composite(composite_1, SWT.NONE);
        //composite.setBackground(new Color(Display.getCurrent(), 220, 220, 220));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_composite = new GridLayout(2, false);
        composite.setLayout(gl_composite);

        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel.setBounds(0, 0, 55, 15);
        lblNewLabel.setFont(FontUtils.createFont(lblNewLabel, "Segoe UI", 10, SWT.BOLD));
        lblNewLabel.setText("Tags");

        lblTagCnt = new Label(composite, SWT.NONE);
        lblTagCnt.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblTagCnt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblTagCnt.setBounds(0, 0, 55, 15);
        lblTagCnt.setText("1");
        lblTagCnt.setFont(FontUtils.createFont(lblTagCnt, "Segoe UI", 8, SWT.BOLD));

        TableViewer tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
        table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        lblNewLabel.addListener(SWT.MouseDown, listener -> {
            ((GridData) table.getLayoutData()).exclude = !((GridData) table.getLayoutData()).exclude;
            table.setVisible(!((GridData) table.getLayoutData()).exclude);
            scrolledComposite.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            parent.layout(true, true);
        });
    }

    @Override
    public TagPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showTags(List<String> tags) {
        sync.asyncExec(() -> {
            table.removeAll();
            table.clearAll();
            if (!tags.isEmpty()) {
                tags.stream().forEach(tag -> {
                    TableItem item = new TableItem(table, SWT.NONE);
                    item.setText(tag);
                    item.setImage(resourceProvider.loadImage(TapijiResourceConstants.IMG_TAG));
                });
                lblTagCnt.setText(String.valueOf(tags.size()));
            }
        });
    }

    @Override
    public void showError(GitServiceException exception) {
        sync.asyncExec(() -> {
            MessageDialog.openError(shell, "Error: ", exception.getMessage());
        });
    }

}
