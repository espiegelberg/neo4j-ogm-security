package com.mile24.example.ogm.typeconversion;

import org.junit.Assert;
import org.junit.Test;

import com.mile24.example.ogm.typeconversion.SecureStringPropertyConverter;

public class SecureStringPropetyConverterTest {

	@Test
	public void validateSecurity() {

		String plaintext = "For authorized eyes only";
		SecureStringPropertyConverter converter = new SecureStringPropertyConverter();
		
		String encryptedValue = converter.toGraphProperty(plaintext);
		Assert.assertNotEquals(plaintext, encryptedValue);
		
		String decryptedValue = converter.toEntityAttribute(encryptedValue);
		Assert.assertNotEquals(encryptedValue, decryptedValue);
		
		Assert.assertEquals(decryptedValue, plaintext);

	}

}