package org.eclipselabs.e4.tapiji.translator.ui.glossary;


import java.io.File;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipselabs.e4.tapiji.translator.ui.BasePresenter;
import org.eclipselabs.e4.tapiji.translator.ui.treeviewer.TreeViewerContract;


public interface GlossaryContract {

    interface View {

        TreeViewerContract.View getTreeViewerView();

        void showHideFuzzyMatching(boolean isVisible);
    }

    interface Presenter extends BasePresenter<View> {

        float getFuzzyPrecission(int value);

        void saveGlossary();

        void openGlossary(File file);

        IEclipseContext getContext();
    }
}
