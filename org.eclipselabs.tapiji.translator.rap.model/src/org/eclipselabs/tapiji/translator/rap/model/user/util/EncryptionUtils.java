package org.eclipselabs.tapiji.translator.rap.model.user.util;

import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptionUtils {
	public static final String PBE_STRING_ENCRYPTOR_ID = "org.eclipselabs.tapiji.translator.stringEncryptor";
	public static final String PBE_STRING_ENCRYPTOR_PASS = "Gfe7t174iuulBYNrsp5Jkcv" +
			"nx6Ho9fdtNLHcvL" +
			"wHb6w0ADwlMRmSyaKLYOPdauWy";

	private static PBEStringEncryptor encryptor;
	
	private static PBEStringEncryptor initStringEncryptor() {
		// create string encryptor
		StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
    	strongEncryptor.setPassword(PBE_STRING_ENCRYPTOR_PASS);    	
    	return strongEncryptor;
	}
	
	public static PBEStringEncryptor getStringEcryptor() {		
		if (encryptor == null)
    		encryptor = initStringEncryptor();
    	return encryptor;
	}
}
