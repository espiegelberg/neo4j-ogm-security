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

	private BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
	
	public SecureStringPropertyConverter() {

		textEncryptor.setPassword("myPassword");

	}
	
	/**
	 * Encrypt the value before it is persisted to Neo4j.
	 */
	public String toGraphProperty(String value) {

		String encryptedText = textEncryptor.encrypt(value);

		return encryptedText;
		
	}

	/**
	 * Decrypt the value retrieved from Neo4j.
	 */
	public String toEntityAttribute(String value) {

		String result = value;
		
//		boolean encrypted = isEncrypted(value);
//		if (encrypted) {
//			result = value.substring(4, result.length() - 1);
//			//result = result.substring(0, );
//		}
		
		result = textEncryptor.decrypt(result);
		
		return result;
	}

	protected boolean isEncrypted(String value) {
		
		boolean result = false;
		
		if (value.startsWith("ENC(") && value.endsWith(")")) {
			result = true;
		}
		
		return result;
	}
	
}