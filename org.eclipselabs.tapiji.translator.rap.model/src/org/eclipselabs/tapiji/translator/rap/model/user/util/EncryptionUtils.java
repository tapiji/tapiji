package org.eclipselabs.tapiji.translator.rap.model.user.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class EncryptionUtils {
	public static String encryptPassword(String password) {		
		MessageDigest sha = null;
		try {
			sha = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sha.update(password.getBytes());
		byte[] digest = sha.digest();
		return DatatypeConverter.printBase64Binary(digest);
	}
}
