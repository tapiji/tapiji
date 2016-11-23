package org.eclipse.e4.tapiji.glossary.ui.glossary;


import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.tapiji.glossary.core.api.IGlossaryService;
import org.eclipse.e4.tapiji.glossary.ui.glossary.GlossaryContract.View;


@Creatable
@Singleton
public final class GlossaryPresenter implements GlossaryContract.Presenter {

    @Inject
    private IGlossaryService glossaryService;

    @Inject
    private IEclipseContext eclipseContext;

    private GlossaryContract.View view;

    @Override
    public void init() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void openGlossary(File file) {
        glossaryService.openGlossary(file);
    }

    @Override
    public void saveGlossary() {
        glossaryService.saveGlossary();
    }

    @Override
    public boolean hasGlossaryTerms() {
        return glossaryService.getGlossary().terms.isEmpty();
    }

    @Override
    public float getFuzzyPrecission(int value) {
        return 1f - (Float.parseFloat(String.valueOf(value)) / 100.f);
    }

    @Override
    public IEclipseContext getContext() {
        return eclipseContext;
    }
}
