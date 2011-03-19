package org.eclipselabs.tapiji.translator.rbe.model.bundle;

import java.util.Locale;

public interface IBundleEntry {

	String getValue();

	IBundle getBundle();

	void setBundle(IBundle bundle);

	void setLocale(Locale locale);

	String getKey();

	String getComment();

	boolean isCommented();

}
