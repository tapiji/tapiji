import static org.junit.Assert.assertEquals;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.eclipselabs.e4.tapiji.translator.model.Glossary;
import org.eclipselabs.e4.tapiji.translator.model.Info;
import org.eclipselabs.e4.tapiji.translator.model.Term;
import org.eclipselabs.e4.tapiji.translator.model.Translation;
import org.junit.BeforeClass;
import org.junit.Test;


public class GlossaryTest {

    private static final String GLOSSARY_XML = "./glossary-jaxb.xml";

    private static Info info;

    private static Glossary glossary;

    private static Term term2;

    @BeforeClass
    public static void setup() {
        info = Info.newInstance();
        info.translations = new ArrayList<String>();
        info.translations.add("default");
        info.translations.add("de");
        info.translations.add("en");

        // Hello world
        final Term term = Term.newInstance();
        term.translations.add(Translation.newInstance("default", "Hallo Welt!"));
        term.translations.add(Translation.newInstance("de", "Hallo Welt!"));
        term.translations.add(Translation.newInstance("en", "Hello World!"));
        term.parentTerm = null;

        // Hello world 2
        final Term subTerms = Term.newInstance();
        subTerms.translations.add(Translation.newInstance("default", "Hallo Welt!"));
        subTerms.translations.add(Translation.newInstance("de", "Hallo Welt!"));
        subTerms.translations.add(Translation.newInstance("en", "Hello World!"));
        term.subTerms.add(subTerms);

        // Hello World 3
        term2 = Term.newInstance();
        term2.translations.add(Translation.newInstance("default", "Hallo Welt!"));
        term2.translations.add(Translation.newInstance("de", "Hallo Welt!"));
        term2.translations.add(Translation.newInstance("en", "Hello World!"));
        term2.parentTerm = null;

        glossary = new Glossary();
        glossary.info = info;
        glossary.terms.add(term);
        glossary.terms.add(term2);
    }

    @Test
    public void writeGlossaryToFileTest() throws JAXBException, IOException {
        final JAXBContext context = JAXBContext.newInstance(Glossary.class);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(glossary, new FileWriter(GLOSSARY_XML));
    }

    @Test
    public void readGlossaryFromFileTest() throws JAXBException, IOException {
        final JAXBContext context = JAXBContext.newInstance(Glossary.class);
        final Unmarshaller unMarshaller = context.createUnmarshaller();
        final Glossary loadedGlossary = (Glossary) unMarshaller.unmarshal(new FileReader(GLOSSARY_XML));
        assertEquals(loadedGlossary.toString(), "hello world");
    }

    @Test
    public void termSizeTest() {
        assertEquals(glossary.getAllTerms().length, 2);
    }
}
