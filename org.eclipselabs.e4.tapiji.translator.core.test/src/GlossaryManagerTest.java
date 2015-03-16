import java.util.ArrayList;
import org.eclipselabs.e4.tapiji.translator.core.internal.GlossaryManager;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Info;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.eclipselabs.e4.tapiji.translator.model.interfaces.IGlossaryService;
import org.junit.BeforeClass;
import org.junit.Test;



public class GlossaryManagerTest {

    private static Info info;

    private static Glossary glossary;

    private static Term term2;

    private static IGlossaryService manager;

    @BeforeClass
    public static void setup() {
        info = new Info();
        info.translations = new ArrayList<String>();
        info.translations.add("default");

        // Hello world
        final Term term = Term.newInstance();
        term.translations.add(Translation.newInstance("default", "Hallo Welt!"));
        term.translations.add(Translation.newInstance("de", "Hallo Welt!"));
        term.translations.add(Translation.newInstance("en", "Hello World!"));
        term.parentTerm = null;

        manager = new GlossaryManager();
    }

    @Test
    public void addTermTest() {
        Term term = Term.newInstance();
        manager.addTerm(term);
    }

    @Test
    public void removeTermTest() {
        Term term = Term.newInstance();
        manager.removeTerm(term);
    }

    @Test
    public void clearGlossaryTest() {
        manager.evictGlossary();
    }

    @Test
    public void saveGlossaryTest() {



    }

    @Test
    public void loadGlossaryTest() {

    }

}
