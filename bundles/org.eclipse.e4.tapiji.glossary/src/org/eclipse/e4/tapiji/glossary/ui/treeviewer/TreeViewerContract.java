package org.eclipse.e4.tapiji.glossary.ui.treeviewer;


import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.tapiji.glossary.model.Glossary;
import org.eclipse.e4.tapiji.glossary.model.Term;
import org.eclipse.e4.tapiji.glossary.ui.BasePresenter;
import org.eclipse.jface.viewers.TreeViewer;


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
