package org.eclipse.babel.editor.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.babel.core.message.tree.KeyTreeNode;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IKeyTreeNode;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IMessagesBundleGroup;
import org.eclipselabs.tapiji.translator.rbe.babel.bundle.IValuedKeyTreeNode;


public class ValuedKeyTreeNode extends KeyTreeNode implements IValuedKeyTreeNode {

    public ValuedKeyTreeNode(IKeyTreeNode parent, String name, String messageKey,
            IMessagesBundleGroup messagesBundleGroup) {
        super(parent, name, messageKey, messagesBundleGroup);
    }

    private Map<Locale, String> values = new HashMap<Locale, String>();
    private Object info;

    public void initValues (Map<Locale, String> values) {
        this.values = values;
    }

    public void addValue (Locale locale, String value) {
        values.put(locale, value);
    }
    
    public String getValue (Locale locale) {
        return values.get(locale);
    }
    
    public Collection<String> getValues () {
        return values.values();
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public Object getInfo() {
        return info;
    }
    
    public Collection<Locale> getLocales () {
        List<Locale> locs = new ArrayList<Locale> ();
        for (Locale loc : values.keySet()) {
            locs.add(loc);
        }
        return locs;
    }
    
}
