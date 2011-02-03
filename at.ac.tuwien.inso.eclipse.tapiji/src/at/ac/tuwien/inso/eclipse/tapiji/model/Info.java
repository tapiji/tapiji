package at.ac.tuwien.inso.eclipse.tapiji.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType (XmlAccessType.FIELD)
public class Info implements Serializable {

	private static final long serialVersionUID = 8607746669906026928L;

	@XmlElementWrapper (name = "locales")
	@XmlElement (name = "locale")
	public List<String> translations;
	
	public Info () {
		this.translations = new ArrayList<String>();
		
		// Add the default Locale
		this.translations.add("Default");
	}
	
	public String[] getTranslations () {
		return translations.toArray(new String [translations.size()]);
	}
	
}
