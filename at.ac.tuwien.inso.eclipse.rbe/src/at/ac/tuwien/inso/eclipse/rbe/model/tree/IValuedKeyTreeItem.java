package at.ac.tuwien.inso.eclipse.rbe.model.tree;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public interface IValuedKeyTreeItem {

	public void initValues (Map<Locale, String> values);

	public void addValue (Locale locale, String value);
	
	public String getValue (Locale locale);
	
	public Collection<String> getValues ();

	public void setInfo(Object info);

	public Object getInfo();
	
	public Collection<Locale> getLocales ();

	public String getId();
}
