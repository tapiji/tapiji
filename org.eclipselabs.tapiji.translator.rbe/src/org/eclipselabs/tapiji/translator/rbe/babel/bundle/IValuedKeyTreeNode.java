package org.eclipselabs.tapiji.translator.rbe.babel.bundle;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;


public interface IValuedKeyTreeNode extends IKeyTreeNode{
    
    public void initValues (Map<Locale, String> values);

    public void addValue (Locale locale, String value);
    
    public String getValue (Locale locale);
    
    public Collection<String> getValues ();

    public void setInfo(Object info);

    public Object getInfo();
    
    public Collection<Locale> getLocales ();

}
