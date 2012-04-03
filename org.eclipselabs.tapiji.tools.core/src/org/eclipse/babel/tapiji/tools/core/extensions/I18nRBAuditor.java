package org.eclipse.babel.tapiji.tools.core.extensions;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 */
public abstract class I18nRBAuditor extends I18nAuditor{
	
	/**
	 * Mark the end of a audit and reset all problemlists
	 */
	public abstract void resetProblems();
	
	/**
	 * Returns the list of missing keys or no specified Resource-Bundle-key
	 * refernces. Each list entry describes the textual position on which this
	 * type of error has been detected. 
	 * @return The list of positions of no specified RB-key refernces
	 */
	public abstract List<ILocation> getUnspecifiedKeyReferences();
	 
	/**
	 * Returns the list of same Resource-Bundle-value refernces. Each list entry
	 * describes the textual position on which this type of error has been
	 * detected. 
	 * @return The list of positions of same RB-value refernces
	 */
	public abstract Map<ILocation, ILocation> getSameValuesReferences();
	 
	/**
	 * Returns the list of missing Resource-Bundle-languages compared with the
	 * Resource-Bundles of the hole project. Each list entry describes the
	 * textual position on which this type of error has been detected.
	 * @return
	 */
	public abstract List<ILocation> getMissingLanguageReferences();
	 
	 
	 //public abstract List<ILocation> getUnusedKeyReferences();
}
