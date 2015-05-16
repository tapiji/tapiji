package org.eclipselabs.e4.tapiji.editor.ui.editor.i18n;


import javax.annotation.PostConstruct;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;


public class TestClass {


    @PostConstruct
    public void createControl(Composite parent) {
        final Composite parentComp = new Composite(parent, SWT.BORDER);
        parentComp.setLayout(new GridLayout(2, false));
        parentComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        final Label labelSearch = new Label(parentComp, SWT.NONE);
        labelSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        labelSearch.setText("Search expression:");

        Text inputFilter = new Text(parentComp, SWT.BORDER | SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);
        inputFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        // inputFilter.addModifyListener(this);

        Label labelScale = new Label(parentComp, SWT.NONE);
        labelScale.setText("Precision:");
        labelScale.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, false, false, 1, 1));

        Scale fuzzyScaler = new Scale(parentComp, SWT.NONE);
        fuzzyScaler.setMaximum(100);
        fuzzyScaler.setMinimum(0);
        fuzzyScaler.setIncrement(1);
        fuzzyScaler.setPageIncrement(5);
        // fuzzyScaler.addListener(SWT.Selection, this);
        fuzzyScaler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    }

}
