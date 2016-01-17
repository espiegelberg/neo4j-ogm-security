package com.mile24.example.neo4j.ogm.typeconversion;

import org.jasypt.util.text.BasicTextEncryptor;
import org.neo4j.ogm.typeconversion.AttributeConverter;

/**
 * <p>A custom <code>AttributeConverter</code> demonstrating integration with the Jasypt 
 * security library. This is a trivial sample whereas real life usage can be as complex as your 
 * application requires.
 * </p>
 * <p>
 * Annotate your domain entities with @Convert(SecureStringPropertyConverter.class) to seamlessly 
 * provide transparent encryption.
 * </p>
 * 
 * 
 * @author eric <at> miletwentyfour.com
 */
public class SecureStringPropertyConverter implements AttributeConverter<String, String> {

	public static final String ENCRYPTED_TOKEN_START = "ENC(";
	public static final String ENCRYPTED_TOKEN_END = ")";
	
	private BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	
	public SecureStringPropertyConverter() {

		textEncryptor.setPassword("my-password-change-me");

	}
	
	/**
	 * Encrypt the value before it is persisted to the database.
	 */
	public String toGraphProperty(String value) {

		StringBuilder sb = new StringBuilder(ENCRYPTED_TOKEN_START);
		
		String encryptedText = textEncryptor.encrypt(value);
		sb.append(encryptedText);
		sb.append(ENCRYPTED_TOKEN_END);
		
		return sb.toString();
		
	}

	/**
	 * Decrypt the database value to the value set on the object model.
	 */
	public String toEntityAttribute(String value) {

		String result = value;
		
		boolean encrypted = isEncrypted(value);
		
		if (encrypted) {
			result = value.substring(4, result.length() - 1);
			result = textEncryptor.decrypt(result);
		}
		
		return result;
	}

	/**
	 * Determine if a value is encrypted or not.
	 * 
	 * This is a convenience to help facilitate scenarios where 
	 * <code>SecureStringPropertyConverter</code> is applied to properties who's values 
	 * are not all encrypted within the database, for example.
	 * 
	 * @param value
	 * @return True if the value is determined to be encrypted, otherwise false.
	 */
	protected boolean isEncrypted(String value) {
		
		boolean result = false;
		
		if (value.startsWith(ENCRYPTED_TOKEN_START) && value.endsWith(ENCRYPTED_TOKEN_END)) {
			result = true;
		}
		
		return result;
	}
	
}