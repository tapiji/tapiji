package org.eclipselabs.e4.tapiji.translator.views.widgets;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;


public interface ITreeViewerWidget {

    TreeViewer getTreeViewer();

    void updateView(Glossary glossary);

    void setColumnEditable(boolean isEditable);

    void enableFuzzyMatching(boolean enable);

    void setSearchString(String text);
}
