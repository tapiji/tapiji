package org.eclipse.e4.tapiji.git.ui.panel.left.stash;


import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.utils.ColorUtils;
import org.eclipse.e4.tapiji.utils.FontUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


@Creatable
public class StashView implements StashContract.View {

    public void createPartControl(Composite parent, ScrolledComposite scrolledComposite) {

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
        composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        Composite composite = new Composite(composite_1, SWT.NONE);
        composite.setBackground(new Color(Display.getCurrent(), 220, 220, 220));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_composite = new GridLayout(2, false);
        composite.setLayout(gl_composite);

        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel.setBounds(0, 0, 55, 15);
        lblNewLabel.setFont(FontUtils.createFont(lblNewLabel, "Segoe UI", 10, SWT.BOLD));
        lblNewLabel.setText("Stash");

        Label lblNewLabel_1 = new Label(composite, SWT.NONE);
        lblNewLabel_1.setBackground(ColorUtils.getSystemColor(SWT.COLOR_TRANSPARENT));
        lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblNewLabel_1.setBounds(0, 0, 55, 15);
        lblNewLabel_1.setText("1");
        lblNewLabel_1.setFont(FontUtils.createFont(lblNewLabel_1, "Segoe UI", 8, SWT.BOLD));

        TableViewer tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        lblNewLabel.addListener(SWT.MouseDown, listener -> {
            ((GridData) table.getLayoutData()).exclude = !((GridData) table.getLayoutData()).exclude;
            table.setVisible(!((GridData) table.getLayoutData()).exclude);
            scrolledComposite.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            parent.layout(true, true);
            scrolledComposite.layout(true, true);
        });
    }
}
