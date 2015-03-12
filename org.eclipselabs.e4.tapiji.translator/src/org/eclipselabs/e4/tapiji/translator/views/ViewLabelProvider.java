package org.eclipselabs.e4.tapiji.translator.views;


import javax.annotation.PreDestroy;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;


public final class ViewLabelProvider extends LabelProvider {

    private TreeViewer treeViewer;

    public ViewLabelProvider(final TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    @Override
    public String getText(Object element) {
        return "HALLO";
    }

    @Focus
    public void Focus() {
        treeViewer.getControl().setFocus();
    }

    @Override
    @PreDestroy
    public void dispose() {

    }
}
