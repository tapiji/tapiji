package org.eclipse.babel.core.configuration;

/**
 * Interface to TapiJI preference page.
 * 
 * @author Alexej Strelzow
 */
public interface IConfiguration {

	boolean getAuditSameValue();
	boolean getAuditMissingValue();
	boolean getAuditMissingLanguage();
	boolean getAuditRb();
	boolean getAuditResource();
	String getNonRbPattern();
	
//	convertStringToList(String)
//	convertListToString(List<CheckItem>)
//	addPropertyChangeListener(IPropertyChangeListener)
//	removePropertyChangeListener(IPropertyChangeListener)
	
}
