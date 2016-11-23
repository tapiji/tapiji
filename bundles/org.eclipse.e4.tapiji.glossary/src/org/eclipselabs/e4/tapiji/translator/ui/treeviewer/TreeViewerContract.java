package org.eclipselabs.e4.tapiji.translator.ui.treeviewer;


import org.eclipse.jface.viewers.TreeViewer;
import org.eclipselabs.e4.tapiji.translator.core.api.IGlossaryService;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.ui.BasePresenter;


public interface TreeViewerContract {

    interface View {

        void showHideTranslationColumn(String languageCode);

        TreeViewer getTreeViewer();

        void updateView(Glossary glossary);

        void setColumnEditable(boolean isEditable);

        void enableFuzzyMatching(boolean enable);

        void setSearchString(String text);

        void setMatchingPrecision(float value);

        void setReferenceLanguage(String referenceLanguage);

        void addSelection(Term term);

        void registerTreeMenu(String menuId);
    }

    interface Presenter extends BasePresenter<View> {

        IGlossaryService getGlossary();

    }
}
