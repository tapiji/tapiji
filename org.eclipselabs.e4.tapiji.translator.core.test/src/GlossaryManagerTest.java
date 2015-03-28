import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipselabs.e4.tapiji.logger.Log;
import org.eclipselabs.e4.tapiji.translator.core.internal.GlossaryManager;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Info;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.junit.BeforeClass;
import org.junit.Test;


public final class GlossaryManagerTest {

    private static final String TAG = GlossaryManagerTest.class.getSimpleName();

    private Info info;

    private Term term;
    private Term term2;

    private static IGlossaryService glossaryService;

    @BeforeClass
    public static void setup() {
        final IEclipseContext context = EclipseContextFactory.create();
        context.set(IEventBroker.class, new EventBrokerMock());

        glossaryService = ContextInjectionFactory.make(GlossaryManager.class, context);
    }


    private void initializeTestData() {
        info = Info.newInstance();
        info.translations = new ArrayList<String>();
        info.translations.add("default");

        // Hello world
        term = Term.newInstance();
        term.translations.add(Translation.newInstance("default", "Hallo Welt!"));
        term.translations.add(Translation.newInstance("de", "Hallo Welt!"));
        term.translations.add(Translation.newInstance("en", "Hello World!"));
        term.parentTerm = null;

        term2 = Term.newInstance();
        term2.translations.add(Translation.newInstance("default", "Welt!"));
        term2.translations.add(Translation.newInstance("de", "Welt!"));
        term2.translations.add(Translation.newInstance("en", "World!"));
        term2.parentTerm = null;

        final Glossary glossary = new Glossary();
        glossary.info = info;
        glossary.terms.add(term);
        glossary.terms.add(term2);

        glossaryService.updateGlossary(glossary);
    }


    @Test
    public void addTermTest() {

        initializeTestData();
        Log.d(TAG, glossaryService.getGlossary().toString());

        assertEquals(2, glossaryService.getGlossary().terms.size());

    }

    @Test
    public void removeTermTest() {
        initializeTestData();
        glossaryService.removeTerm(term);
        assertEquals(1, glossaryService.getGlossary().terms.size());

        glossaryService.removeTerm(term);
        assertEquals(1, glossaryService.getGlossary().terms.size());

        glossaryService.removeTerm(term2);
        assertEquals(0, glossaryService.getGlossary().terms.size());

        assertEquals(0, glossaryService.getGlossary().terms.size());
    }

    @Test
    public void evictGlossary() {
        initializeTestData();

        glossaryService.evictGlossary();

        assertEquals(0, glossaryService.getGlossary().terms.size());
        assertEquals("Default", glossaryService.getGlossary().info.getTranslations()[0]);
    }


    @Test
    public void saveGlossaryTest() {


    }

    @Test
    public void loadGlossaryTest() {

    }

}
