package at.ac.tuwien.inso.eclipse.rbe.model.bundle;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public interface IBundleGroup {

	boolean isKey(String key);

	IBundle getBundle(Locale locale);

	void removeKey(String key);

	IBundleEntry getBundleEntry(Locale locale, String key);

	void addBundleEntry(Locale locale, IBundleEntry bundleEntry);

	Iterator iterator();

	boolean containsKey(String key);

	Collection<IBundleEntry> getBundleEntries(String key);

	void addBundle(Locale locale, IBundle bundleFile);

	Set<String> getKeys();

	//void addListener(IDeltaListener iDeltaListener);

	int getBundleCount();

	void addKey(String newKey);

	int getSize();

	void copyKey(String origItemKey, String newItemKey);

	void commentKey(String id);

	void uncommentKey(String id);

	void renameKey(String oldItemKey, String newItemKey);

}
