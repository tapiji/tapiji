package org.eclipselabs.e4.tapiji.translator.model;


import java.io.File;
import javax.xml.bind.JAXBException;


public interface IGlossaryService {

  void unregisterGlossaryListener(ILoadGlossaryListener listener);

  void registerGlossaryListener(ILoadGlossaryListener listener);

  void saveGlossary(File file) throws Exception;

  void loadGlossary(File file) throws JAXBException;

  void setGlossary(Glossary glossary);

  Glossary getGlossary();

  void loadGlossaryEvent(File file);

  void newGlossaryEvent(File file);

}
