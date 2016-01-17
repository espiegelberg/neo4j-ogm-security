package com.mile24.example.neo4j.ogm.typeconversion;

import org.junit.Assert;
import org.junit.Test;

import com.mile24.example.neo4j.ogm.typeconversion.SecureStringPropertyConverter;

public class SecureStringPropetyConverterTest {

	private SecureStringPropertyConverter converter = new SecureStringPropertyConverter();

	public static final String SAMPLE_TEXT = "For authorized eyes only";
	
	@Test
	public void validateSecurity() {

		String encryptedValue = converter.toGraphProperty(SAMPLE_TEXT);
		Assert.assertNotEquals(SAMPLE_TEXT, encryptedValue);
		
		String decryptedValue = converter.toEntityAttribute(encryptedValue);
		Assert.assertNotEquals(encryptedValue, decryptedValue);
		
		Assert.assertEquals(decryptedValue, SAMPLE_TEXT);

		String value1 = "value1";
		String plaintext1 = converter.toEntityAttribute(value1);
		Assert.assertEquals(value1, plaintext1);
		
		String value2 = "ENC(Y+7MdqpeAELqdNqi5+viz+lnApN+qflbpMROaGqQIIlEWcyz6HOfYw==)";
		String plaintext2 = converter.toEntityAttribute(value2);
		Assert.assertEquals(SAMPLE_TEXT, plaintext2);
		
	}

}