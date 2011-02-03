package at.ac.tuwien.inso.eclipse.rbe.model.bundle;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public interface IBundle {

	Locale getLocale();

	void setLocale(Locale locale);

	void setBundleGroup(IBundleGroup bundleGroup);

	String getComment();

	IBundleEntry getEntry(String key);

	Iterator iterator();

	IBundleGroup getBundleGroup();

	Set<String> getKeys();

}
